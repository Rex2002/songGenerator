package org.se.txt.analysis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Val Richter
 */
public class TermCollection {
	public Map<String, TermVariations<NounTerm>> nouns;
	public Map<String, TermVariations<VerbTerm>> verbs;
	static final Random rand = new Random();

	public TermCollection() {
		this.nouns = new HashMap<>();
		this.verbs = new HashMap<>();
	}

	public TermCollection(List<TermVariations<NounTerm>> nouns, List<TermVariations<VerbTerm>> verbs) {
		this.nouns = new HashMap<>();
		this.verbs = new HashMap<>();
		for (TermVariations<NounTerm> term : nouns) {
			this.nouns.put(term.getRadix(), term);
		}
		for (TermVariations<VerbTerm> term : verbs) {
			this.verbs.put(term.getRadix(), term);
		}
	}

	public TermCollection(Map<String, TermVariations<NounTerm>> nouns, Map<String, TermVariations<VerbTerm>> verbs) {
		this.nouns = nouns;
		this.verbs = verbs;
	}

	public void addNouns(TermVariations<NounTerm> variations) {
		if (hasNoun(variations)) {
			nouns.get(variations.getRadix()).add(variations);
		} else {
			nouns.put(variations.getRadix(), variations);
		}
	}

	public void addVerbs(TermVariations<VerbTerm> variations) {
		if (hasVerb(variations)) {
			verbs.get(variations.getRadix()).add(variations);
		} else {
			verbs.put(variations.getRadix(), variations);
		}
	}

	public void addNoun(NounTerm t) {
		TermVariations<NounTerm> v = new TermVariations<>(t);
		if (hasNoun(v)) {
			nouns.get(v.getRadix()).add(t);
		} else {
			nouns.put(v.getRadix(), v);
		}
	}

	public void addVerb(VerbTerm t) {
		TermVariations<VerbTerm> v = new TermVariations<>(t);
		if (hasVerb(v)) {
			verbs.get(v.getRadix()).add(t);
		} else {
			verbs.put(v.getRadix(), v);
		}
	}

	public boolean hasNoun(TermVariations<NounTerm> variations) {
		return nouns.containsKey(variations.getRadix());
	}

	public boolean hasNoun(NounTerm t) {
		return nouns.containsKey(t.getRadix());
	}

	public boolean hasVerb(TermVariations<VerbTerm> variations) {
		return verbs.containsKey(variations.getRadix());
	}

	public boolean hasVerb(VerbTerm t) {
		return verbs.containsKey(t.getRadix());
	}

	// Iterators

	public void iter(Consumer<? super TermVariations<? extends Term>> f) {
		iterNouns(f);
		iterVerbs(f);
	}

	public void iterNouns(Consumer<? super TermVariations<NounTerm>> f) {
		nouns.forEach((key, variations) -> f.accept(variations));
	}

	public void iterVerbs(Consumer<? super TermVariations<VerbTerm>> f) {
		verbs.forEach((key, variations) -> f.accept(variations));
	}

	public void flatIter(Consumer<? super Term> f) {
		flatIterNouns(f);
		flatIterVerbs(f);
	}

	public void flatIterNouns(Consumer<? super NounTerm> f) {
		nouns.forEach((key, variations) -> variations.forEach(f));
	}

	public void flatIterVerbs(Consumer<? super VerbTerm> f) {
		verbs.forEach((key, variations) -> variations.forEach(f));
	}

	// Query Functions

	public List<NounTerm> query(GrammaticalCase grammaticalCase, Gender gender, Numerus numerus, Integer syllableMin, Integer syllableMax) {
		List<NounTerm> existing = new ArrayList<>();
		List<NounTerm> created = new ArrayList<>();

		nouns.values().forEach(x -> {
			Optional<NounTerm> res = TermVariations.getTerm(x, gender, grammaticalCase, numerus);
			if (res.isPresent()) {
				NounTerm t = res.get();
				if (syllableMin <= t.syllableAmount && t.syllableAmount <= syllableMax) {
					if (TermVariations.hasType(x, gender, grammaticalCase, numerus)) existing.add(t);
					else created.add(t);
				}
			}
		});

		existing.addAll(created);
		return existing;
	}

	public List<NounTerm> queryNounsBy(Predicate<? super NounTerm> f) {
		return TermCollection.queryBy(nouns, f);
	}

	public List<VerbTerm> queryVerbsBy(Predicate<? super VerbTerm> f) {
		return TermCollection.queryBy(verbs, f);
	}

	public List<NounTerm> queryNounsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(nouns, minSyllableAmount, maxSyllableAmount);
	}

	public List<VerbTerm> queryVerbsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(verbs, minSyllableAmount, maxSyllableAmount);
	}

	public List<NounTerm> queryNounsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(nouns, syllableAmount);
	}

	public List<VerbTerm> queryVerbsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(verbs, syllableAmount);
	}

	public List<NounTerm> queryNounsBy(GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(nouns, grammaticalCase);
	}

	public List<NounTerm> queryNounsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(nouns, onlyPluralTerms);
	}

	public List<VerbTerm> queryVerbsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(verbs, onlyPluralTerms);
	}

	public List<NounTerm> queryNounsBy(Gender gender) {
		return TermCollection.queryBy(nouns, gender);
	}

	public List<NounTerm> mostCommonNouns() {
		return TermCollection.mostCommonTerms(nouns);
	}

	public List<VerbTerm> mostCommonVerbs() {
		return TermCollection.mostCommonTerms(verbs);
	}

	public NounTerm getRandomNoun() {
		return TermCollection.getRandomTerm(nouns.values().stream().collect(Collectors.toList()));
	}

	public VerbTerm getRandomVerb() {
		return TermCollection.getRandomTerm(verbs.values().stream().collect(Collectors.toList()));
	}

	// Static Query Functions

	public static <T extends Term> List<T> queryBySyllableRange(Map<String, TermVariations<T>> terms, Integer minSyllableAmount,
			Integer maxSyllableAmount) {
		return TermCollection.queryBy(terms, x -> minSyllableAmount <= x.syllableAmount && x.syllableAmount <= maxSyllableAmount);
	}

	public static <T extends Term> List<T> queryBySyllableAmount(Map<String, TermVariations<T>> terms, Integer syllableAmount) {
		return TermCollection.queryBy(terms, x -> Objects.equals(x.syllableAmount, syllableAmount));
	}

	public static List<NounTerm> queryBy(Map<String, TermVariations<NounTerm>> terms, GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(terms, x -> x.grammaticalCase == grammaticalCase);
	}

	public static <T extends Term> List<T> queryBy(Map<String, TermVariations<T>> terms, Boolean onlyPluralTerms) {
		return TermCollection.queryBy(terms, x -> (x.getNumerus() == Numerus.PLURAL) == onlyPluralTerms);
	}

	public static List<NounTerm> queryBy(Map<String, TermVariations<NounTerm>> terms, Gender gender) {
		return TermCollection.queryBy(terms, x -> x.gender == gender);
	}

	public static <T extends Term> List<T> queryBy(Map<String, TermVariations<T>> terms, Predicate<? super T> f) {
		List<T> res = new ArrayList<>();
		terms.values().forEach(x -> res.addAll(x.queryBy(f)));
		res.sort(new TermComp<>(terms));
		return res;
	}

	public static <T extends Term> List<T> mostCommonTerms(Map<String, TermVariations<T>> terms) {
		List<T> res = terms.values().stream().map(x -> x.variations.values().stream()).flatMap(Function.identity()).collect(Collectors.toList());
		res.sort(new TermComp<>(terms));
		return res.subList(0, 10);
	}

	public static <T extends Term> T getRandomTerm(List<TermVariations<T>> terms) {
		int i = rand.nextInt(terms.size());
		Collection<T> ts = terms.get(i).variations.values();
		int j = rand.nextInt(ts.size());
		return ts.stream().collect(Collectors.toList()).get(j);
	}

	// Boilerplate:

	public Map<String, TermVariations<NounTerm>> getNouns() {
		return this.nouns;
	}

	public void setNouns(Map<String, TermVariations<NounTerm>> nouns) {
		this.nouns = nouns;
	}

	public Map<String, TermVariations<VerbTerm>> getVerbs() {
		return this.verbs;
	}

	public void setVerbs(Map<String, TermVariations<VerbTerm>> verbs) {
		this.verbs = verbs;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermCollection)) {
			return false;
		}
		TermCollection termCollection = (TermCollection) o;
		return Objects.equals(nouns, termCollection.nouns) && Objects.equals(verbs, termCollection.verbs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nouns, verbs);
	}

	@Override
	public String toString() {
		return "{" + " nouns='" + getNouns() + "'" + ", verbs='" + getVerbs() + "'" + "}";
	}

}
