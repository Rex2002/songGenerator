package org.se.Text.Analysis;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Val Richter
 */
public class TermCollection {
	public HashMap<String, TermVariations> nouns;
	public HashMap<String, TermVariations> verbs;

	public TermCollection() {
		this.nouns = new HashMap<String, TermVariations>();
		this.verbs = new HashMap<String, TermVariations>();
	}

	public TermCollection(ArrayList<TermVariations> nouns, ArrayList<TermVariations> verbs) {
		this.nouns = new HashMap<String, TermVariations>();
		this.verbs = new HashMap<String, TermVariations>();
		for (TermVariations term : nouns) {
			this.nouns.put(term.getLemma(), term);
		}
		for (TermVariations term : verbs) {
			this.verbs.put(term.getLemma(), term);
		}
	}

	public TermCollection(HashMap<String, TermVariations> nouns, HashMap<String, TermVariations> verbs) {
		this.nouns = nouns;
		this.verbs = verbs;
	}

	public void addNouns(TermVariations variations) {
		if (hasNoun(variations)) {
			nouns.get(variations.getLemma()).add(variations);
		} else {
			nouns.put(variations.getLemma(), variations);
		}
	}

	public void addVerbs(TermVariations variations) {
		if (hasVerb(variations)) {
			verbs.get(variations.getLemma()).add(variations);
		} else {
			verbs.put(variations.getLemma(), variations);
		}
	}

	public void addNoun(Term t) {
		TermVariations v = new TermVariations(t);
		if (hasNoun(v)) {
			nouns.get(v.getLemma()).add(t);
		} else {
			nouns.put(v.getLemma(), v);
		}
	}

	public void addVerb(Term t) {
		TermVariations v = new TermVariations(t);
		if (hasVerb(v)) {
			verbs.get(v.getLemma()).add(t);
		} else {
			verbs.put(v.getLemma(), v);
		}
	}

	public Boolean hasNoun(TermVariations variations) {
		return nouns.containsKey(variations.getLemma());
	}

	public Boolean hasNoun(Term t) {
		return nouns.containsKey(t.getLemma());
	}

	public Boolean hasVerb(TermVariations variations) {
		return verbs.containsKey(variations.getLemma());
	}

	public Boolean hasVerb(Term t) {
		return verbs.containsKey(t.getLemma());
	}

	public List<Term> query(GrammaticalCase grammaticalCase, Gender gender, Boolean isPlural, Integer syllableMin,
			Integer syllableMax) {
		List<Term> existing = new ArrayList<Term>();
		List<Term> created = new ArrayList<Term>();

		nouns.values().forEach(x -> {
			Term t = x.getTerm(gender, grammaticalCase, isPlural);
			if (syllableMin <= t.syllables.length && t.syllables.length <= syllableMax) {
				if (x.hasType(gender, grammaticalCase, isPlural))
					existing.add(t);
				else
					created.add(t);
			}
		});

		existing.addAll(created);
		return existing;
	}

	// Query Functions

	public List<Term> queryNounsBy(Predicate<? super Term> f) {
		return TermCollection.queryBy(nouns, f);
	}

	public List<Term> queryVerbsBy(Predicate<? super Term> f) {
		return TermCollection.queryBy(verbs, f);
	}

	public List<Term> queryNounsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(nouns, minSyllableAmount, maxSyllableAmount);
	}

	public List<Term> queryVerbsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(verbs, minSyllableAmount, maxSyllableAmount);
	}

	public List<Term> queryNounsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(nouns, syllableAmount);
	}

	public List<Term> queryVerbsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(verbs, syllableAmount);
	}

	public List<Term> queryNounsBy(GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(nouns, grammaticalCase);
	}

	public List<Term> queryVerbsBy(GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(verbs, grammaticalCase);
	}

	public List<Term> queryNounsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(nouns, onlyPluralTerms);
	}

	public List<Term> queryVerbsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(verbs, onlyPluralTerms);
	}

	public List<Term> queryNounsBy(Gender gender) {
		return TermCollection.queryBy(nouns, gender);
	}

	public List<Term> queryVerbsBy(Gender gender) {
		return TermCollection.queryBy(verbs, gender);
	}

	public List<Term> mostCommonNouns() {
		return TermCollection.mostCommonTerms(nouns);
	}

	public List<Term> mostCommonVerbs() {
		return TermCollection.mostCommonTerms(verbs);
	}

	public Term getRandomNoun() {
		return TermCollection.getRandomTerm(nouns.values().stream().collect(Collectors.toList()));
	}

	public Term getRandomVerb() {
		return TermCollection.getRandomTerm(verbs.values().stream().collect(Collectors.toList()));
	}

	// Static Query Functions

	public static List<Term> queryBySyllableRange(HashMap<String, TermVariations> terms, Integer minSyllableAmount,
			Integer maxSyllableAmount) {
		return TermCollection.queryBy(terms,
				x -> minSyllableAmount <= x.syllables.length && x.syllables.length <= maxSyllableAmount);
	}

	public static List<Term> queryBySyllableAmount(HashMap<String, TermVariations> terms, Integer syllableAmount) {
		return TermCollection.queryBy(terms, x -> x.syllables.length == syllableAmount);
	}

	public static List<Term> queryBy(HashMap<String, TermVariations> terms, GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(terms, x -> x.grammaticalCase == grammaticalCase);
	}

	public static List<Term> queryBy(HashMap<String, TermVariations> terms, Boolean onlyPluralTerms) {
		return TermCollection.queryBy(terms, x -> x.isPlural == onlyPluralTerms);
	}

	public static List<Term> queryBy(HashMap<String, TermVariations> terms, Gender gender) {
		return TermCollection.queryBy(terms, x -> x.gender == gender);
	}

	public static List<Term> queryBy(HashMap<String, TermVariations> terms, Predicate<? super Term> f) {
		List<Term> res = new ArrayList<Term>();
		terms.values().forEach(x -> res.addAll(x.queryBy(f)));
		res.sort(new Comp(terms));
		return res;
	}

	public static List<Term> mostCommonTerms(HashMap<String, TermVariations> terms) {
		List<Term> res = terms.values().stream().map(x -> x.variations.values().stream()).flatMap(Function.identity())
				.collect(Collectors.toList());
		;
		res.sort(new Comp(terms));
		return res.subList(0, 10);
	}

	public static Term getRandomTerm(List<TermVariations> terms) {
		Random rand = new Random();
		int i = rand.nextInt(terms.size());
		Collection<Term> ts = terms.get(i).variations.values();
		int j = rand.nextInt(ts.size());
		return ts.stream().collect(Collectors.toList()).get(j);
	}

	// Boilerplate:

	public HashMap<String, TermVariations> getNouns() {
		return this.nouns;
	}

	public void setNouns(HashMap<String, TermVariations> nouns) {
		this.nouns = nouns;
	}

	public HashMap<String, TermVariations> getVerbs() {
		return this.verbs;
	}

	public void setVerbs(HashMap<String, TermVariations> verbs) {
		this.verbs = verbs;
	}

	public TermCollection nouns(HashMap<String, TermVariations> nouns) {
		setNouns(nouns);
		return this;
	}

	public TermCollection verbs(HashMap<String, TermVariations> verbs) {
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
