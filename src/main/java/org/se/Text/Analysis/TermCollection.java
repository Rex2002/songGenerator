package org.se.Text.Analysis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Val Richter
 */
public class TermCollection {
	public Map<String, TermVariations> nouns;
	public Map<String, TermVariations> verbs;

	public TermCollection() {
		this.nouns = new HashMap<String, TermVariations>();
		this.verbs = new HashMap<String, TermVariations>();
	}

	public TermCollection(ArrayList<TermVariations> nouns, ArrayList<TermVariations> verbs) {
		this.nouns = new HashMap<String, TermVariations>();
		this.verbs = new HashMap<String, TermVariations>();
		for (TermVariations term : nouns) {
			this.nouns.put(term.getRadix(), term);
		}
		for (TermVariations term : verbs) {
			this.verbs.put(term.getRadix(), term);
		}
	}

	public TermCollection(Map<String, TermVariations> nouns, Map<String, TermVariations> verbs) {
		this.nouns = nouns;
		this.verbs = verbs;
	}

	public void addNouns(TermVariations variations) {
		if (hasNoun(variations)) {
			nouns.get(variations.getRadix()).add(variations);
		} else {
			nouns.put(variations.getRadix(), variations);
		}
	}

	public void addVerbs(TermVariations variations) {
		if (hasVerb(variations)) {
			verbs.get(variations.getRadix()).add(variations);
		} else {
			verbs.put(variations.getRadix(), variations);
		}
	}

	public void addNoun(NounTerm t) {
		TermVariations v = new TermVariations(t);
		if (hasNoun(v)) {
			nouns.get(v.getRadix()).add(t);
		} else {
			nouns.put(v.getRadix(), v);
		}
	}

	public void addVerb(NounTerm t) {
		TermVariations v = new TermVariations(t);
		if (hasVerb(v)) {
			verbs.get(v.getRadix()).add(t);
		} else {
			verbs.put(v.getRadix(), v);
		}
	}

	public Boolean hasNoun(TermVariations variations) {
		return nouns.containsKey(variations.getRadix());
	}

	public Boolean hasNoun(NounTerm t) {
		return nouns.containsKey(t.getRadix());
	}

	public Boolean hasVerb(TermVariations variations) {
		return verbs.containsKey(variations.getRadix());
	}

	public Boolean hasVerb(NounTerm t) {
		return verbs.containsKey(t.getRadix());
	}

	// Iterators

	public void iter(Consumer<? super TermVariations> f) {
		iterNouns(f);
		iterVerbs(f);
	}

	public void iterNouns(Consumer<? super TermVariations> f) {
		nouns.forEach((key, variations) -> {
			f.accept(variations);
		});
	}

	public void iterVerbs(Consumer<? super TermVariations> f) {
		verbs.forEach((key, variations) -> {
			f.accept(variations);
		});
	}

	public void flatIter(Consumer<? super NounTerm> f) {
		flatIterNouns(f);
		flatIterVerbs(f);
	}

	public void flatIterNouns(Consumer<? super NounTerm> f) {
		nouns.forEach((key, variations) -> {
			variations.forEach(f);
		});
	}

	public void flatIterVerbs(Consumer<? super NounTerm> f) {
		verbs.forEach((key, variations) -> {
			variations.forEach(f);
		});
	}

	// Query Functions

	public List<NounTerm> query(GrammaticalCase grammaticalCase, Gender gender, Boolean isPlural, Integer syllableMin,
			Integer syllableMax) {
		List<NounTerm> existing = new ArrayList<NounTerm>();
		List<NounTerm> created = new ArrayList<NounTerm>();

		nouns.values().forEach(x -> {
			Optional<NounTerm> res = x.getTerm(gender, grammaticalCase, isPlural);
			if (res.isPresent()) {
				NounTerm t = res.get();
				if (syllableMin <= t.syllables.length && t.syllables.length <= syllableMax) {
					if (x.hasType(gender, grammaticalCase, isPlural))
						existing.add(t);
					else
						created.add(t);
				}
			}
		});

		existing.addAll(created);
		return existing;
	}

	public List<NounTerm> queryNounsBy(Predicate<? super NounTerm> f) {
		return TermCollection.queryBy(nouns, f);
	}

	public List<NounTerm> queryVerbsBy(Predicate<? super NounTerm> f) {
		return TermCollection.queryBy(verbs, f);
	}

	public List<NounTerm> queryNounsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(nouns, minSyllableAmount, maxSyllableAmount);
	}

	public List<NounTerm> queryVerbsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(verbs, minSyllableAmount, maxSyllableAmount);
	}

	public List<NounTerm> queryNounsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(nouns, syllableAmount);
	}

	public List<NounTerm> queryVerbsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(verbs, syllableAmount);
	}

	public List<NounTerm> queryNounsBy(GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(nouns, grammaticalCase);
	}

	public List<NounTerm> queryVerbsBy(GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(verbs, grammaticalCase);
	}

	public List<NounTerm> queryNounsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(nouns, onlyPluralTerms);
	}

	public List<NounTerm> queryVerbsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(verbs, onlyPluralTerms);
	}

	public List<NounTerm> queryNounsBy(Gender gender) {
		return TermCollection.queryBy(nouns, gender);
	}

	public List<NounTerm> queryVerbsBy(Gender gender) {
		return TermCollection.queryBy(verbs, gender);
	}

	public List<NounTerm> mostCommonNouns() {
		return TermCollection.mostCommonTerms(nouns);
	}

	public List<NounTerm> mostCommonVerbs() {
		return TermCollection.mostCommonTerms(verbs);
	}

	public NounTerm getRandomNoun() {
		return TermCollection.getRandomTerm(nouns.values().stream().collect(Collectors.toList()));
	}

	public NounTerm getRandomVerb() {
		return TermCollection.getRandomTerm(verbs.values().stream().collect(Collectors.toList()));
	}

	// Static Query Functions

	public static List<NounTerm> queryBySyllableRange(Map<String, TermVariations> terms, Integer minSyllableAmount,
			Integer maxSyllableAmount) {
		return TermCollection.queryBy(terms,
				x -> minSyllableAmount <= x.syllables.length && x.syllables.length <= maxSyllableAmount);
	}

	public static List<NounTerm> queryBySyllableAmount(Map<String, TermVariations> terms, Integer syllableAmount) {
		return TermCollection.queryBy(terms, x -> x.syllables.length == syllableAmount);
	}

	public static List<NounTerm> queryBy(Map<String, TermVariations> terms, GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(terms, x -> x.grammaticalCase == grammaticalCase);
	}

	public static List<NounTerm> queryBy(Map<String, TermVariations> terms, Boolean onlyPluralTerms) {
		return TermCollection.queryBy(terms, x -> x.isPlural == onlyPluralTerms);
	}

	public static List<NounTerm> queryBy(Map<String, TermVariations> terms, Gender gender) {
		return TermCollection.queryBy(terms, x -> x.gender == gender);
	}

	public static List<NounTerm> queryBy(Map<String, TermVariations> terms, Predicate<? super NounTerm> f) {
		List<NounTerm> res = new ArrayList<NounTerm>();
		terms.values().forEach(x -> res.addAll(x.queryBy(f)));
		res.sort(new TermComp(terms));
		return res;
	}

	public static List<NounTerm> mostCommonTerms(Map<String, TermVariations> terms) {
		List<NounTerm> res = terms.values().stream().map(x -> x.variations.values().stream())
				.flatMap(Function.identity())
				.collect(Collectors.toList());
		;
		res.sort(new TermComp(terms));
		return res.subList(0, 10);
	}

	public static NounTerm getRandomTerm(List<TermVariations> terms) {
		Random rand = new Random();
		int i = rand.nextInt(terms.size());
		Collection<NounTerm> ts = terms.get(i).variations.values();
		int j = rand.nextInt(ts.size());
		return ts.stream().collect(Collectors.toList()).get(j);
	}

	// Boilerplate:

	public Map<String, TermVariations> getNouns() {
		return this.nouns;
	}

	public void setNouns(Map<String, TermVariations> nouns) {
		this.nouns = nouns;
	}

	public Map<String, TermVariations> getVerbs() {
		return this.verbs;
	}

	public void setVerbs(Map<String, TermVariations> verbs) {
		this.verbs = verbs;
	}

	public TermCollection nouns(Map<String, TermVariations> nouns) {
		setNouns(nouns);
		return this;
	}

	public TermCollection verbs(Map<String, TermVariations> verbs) {
		setVerbs(verbs);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
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
		return "{" +
				" nouns='" + getNouns() + "'" +
				", verbs='" + getVerbs() + "'" +
				"}";
	}

}
