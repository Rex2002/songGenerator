package org.se.text.analysis;

import java.util.*;
import java.util.function.*;
import org.se.text.analysis.dict.Dict;
import org.se.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class TermCollection {
	private List<TermVariations<NounTerm>> nouns;
	private List<TermVariations<VerbTerm>> verbs;
	private final Dict dict;
	static final Random rand = new Random();

	public TermCollection(Dict dict) {
		this.nouns = new ArrayList<>();
		this.verbs = new ArrayList<>();
		this.dict = dict;
	}

	public TermCollection(Dict dict, List<TermVariations<NounTerm>> nouns, List<TermVariations<VerbTerm>> verbs) {
		this.nouns = nouns;
		this.verbs = verbs;
		this.dict = dict;
	}

	public TermCollection(Dict dict, Map<String, TermVariations<NounTerm>> nouns, Map<String, TermVariations<VerbTerm>> verbs) {
		this.nouns = new ArrayList<>();
		this.verbs = new ArrayList<>();
		for (TermVariations<NounTerm> noun : nouns.values()) {
			this.nouns.add(noun);
		}
		for (TermVariations<VerbTerm> verb : verbs.values()) {
			this.verbs.add(verb);
		}
		this.dict = dict;
	}

	public int size() {
		return nouns.size() + verbs.size();
	}

	// Iterators

	public void iter(Consumer<? super TermVariations<? extends Term<?>>> f) {
		iterNouns(f);
		iterVerbs(f);
	}

	public void iterNouns(Consumer<? super TermVariations<NounTerm>> f) {
		nouns.forEach(f);
	}

	public void iterVerbs(Consumer<? super TermVariations<VerbTerm>> f) {
		verbs.forEach(f);
	}

	public void flatIter(Consumer<? super Term<?>> f) {
		flatIterNouns(f);
		flatIterVerbs(f);
	}

	public void flatIterNouns(Consumer<? super NounTerm> f) {
		nouns.forEach(variations -> variations.forEach(f));
	}

	public void flatIterVerbs(Consumer<? super VerbTerm> f) {
		verbs.forEach(variations -> variations.forEach(f));
	}

	// Query Functions

	public List<NounTerm> query(GrammaticalCase grammaticalCase, Gender gender, Numerus numerus, Integer syllableMin, Integer syllableMax) {
		List<NounTerm> res = new ArrayList<>();

		nouns.forEach(x -> {
			Optional<NounTerm> t = TermVariations.createTerm(x, gender, grammaticalCase, numerus, dict);
			if (t.isPresent()) {
				int syllableAmount = t.get().getSyllableAmount();
				if (syllableMin <= syllableAmount && syllableAmount <= syllableMax) res.add(t.get());
			}
		});

		res.sort(new TermComp<>());
		return res;
	}

	public List<NounTerm> query(GrammaticalCase grammaticalCase, Gender gender, Numerus numerus) {
		return query(grammaticalCase, gender, numerus, 0, Integer.MAX_VALUE);
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
		return TermCollection.getRandomTerm(nouns.stream().toList());
	}

	public VerbTerm getRandomVerb() {
		return TermCollection.getRandomTerm(verbs.stream().toList());
	}

	// Static Query Functions

	public static <T extends Term<T>> List<T> queryBySyllableRange(List<TermVariations<T>> terms, Integer minSyllableAmount,
			Integer maxSyllableAmount) {
		return TermCollection.queryBy(terms, x -> minSyllableAmount <= x.syllableAmount && x.syllableAmount <= maxSyllableAmount);
	}

	public static <T extends Term<T>> List<T> queryBySyllableAmount(List<TermVariations<T>> terms, Integer syllableAmount) {
		return TermCollection.queryBy(terms, x -> Objects.equals(x.syllableAmount, syllableAmount));
	}

	public static List<NounTerm> queryBy(List<TermVariations<NounTerm>> terms, GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(terms, x -> x.getGrammaticalCase() == grammaticalCase);
	}

	public static <T extends Term<T>> List<T> queryBy(List<TermVariations<T>> terms, Boolean onlyPluralTerms) {
		return TermCollection.queryBy(terms, x -> (x.getNumerus() == Numerus.PLURAL) == onlyPluralTerms);
	}

	public static List<NounTerm> queryBy(List<TermVariations<NounTerm>> terms, Gender gender) {
		return TermCollection.queryBy(terms, x -> x.getGender() == gender);
	}

	public static <T extends Term<T>> List<T> queryBy(List<TermVariations<T>> terms, Predicate<? super T> f) {
		List<T> res = new ArrayList<>();
		terms.forEach(x -> res.addAll(x.queryBy(f)));
		res.sort(new TermComp<>());
		return res;
	}

	public static <T extends Term<T>> List<T> mostCommonTerms(List<TermVariations<T>> terms) {
		List<T> res = terms.stream().map(x -> x.getVariations().values().stream()).flatMap(Function.identity()).toList();
		res.sort(new TermComp<>());
		return res.subList(0, 10);
	}

	public static <T extends Term<T>> T getRandomTerm(List<TermVariations<T>> terms) {
		int i = rand.nextInt(terms.size());
		Collection<T> ts = terms.get(i).getVariations().values();
		int j = rand.nextInt(ts.size());
		return ts.stream().toList().get(j);
	}

	// Boilerplate:

	public List<TermVariations<NounTerm>> getNouns() {
		return this.nouns;
	}

	public void setNouns(List<TermVariations<NounTerm>> nouns) {
		this.nouns = nouns;
	}

	public List<TermVariations<VerbTerm>> getVerbs() {
		return this.verbs;
	}

	public void setVerbs(List<TermVariations<VerbTerm>> verbs) {
		this.verbs = verbs;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermCollection termCollection)) {
			return false;
		}
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
