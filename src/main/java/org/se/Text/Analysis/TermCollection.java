package org.se.Text.Analysis;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Val Richter
 */
public class TermCollection {
	public HashMap<String, TermVariations> terms;

	public TermCollection() {
		this.terms = new HashMap<String, TermVariations>();
	}

	public TermCollection(ArrayList<TermVariations> terms) {
		this.terms = new HashMap<String, TermVariations>();
		for (TermVariations term : terms)
			this.terms.put(term.getLemma(), term);
	}

	public TermCollection(HashMap<String, TermVariations> map) {
		this.terms = map;
	}

	public HashMap<String, TermVariations> getTerms() {
		return this.terms;
	}

	public void setTerms(HashMap<String, TermVariations> terms) {
		this.terms = terms;
	}

	public TermCollection terms(HashMap<String, TermVariations> terms) {
		setTerms(terms);
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
		return Objects.equals(terms, termCollection.terms);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(terms);
	}

	@Override
	public String toString() {
		return "{" +
				" terms='" + getTerms() + "'" +
				"}";
	}

	public void add(TermVariations variations) {
		if (has(variations)) {
			this.terms.get(variations.getLemma()).add(variations);
		} else {
			terms.put(variations.getLemma(), variations);
		}
	}

	public void add(Term t) {
		TermVariations v = new TermVariations(t);
		if (has(v)) {
			terms.get(v.getLemma()).add(t);
		} else {
			terms.put(v.getLemma(), v);
		}
	}

	public Boolean has(TermVariations variations) {
		return terms.containsKey(variations.getLemma());
	}

	public Boolean has(Term t) {
		return terms.containsKey(t.getLemma());
	}

	public List<Term> query(GrammaticalCase grammaticalCase, Gender gender, Boolean isPlural, Integer syllableMin,
			Integer syllableMax) {
		List<Term> existing = new ArrayList<Term>();
		List<Term> created = new ArrayList<Term>();

		this.terms.values().forEach(x -> {
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

	public List<Term> queryBy(Predicate<? super Term> f) {
		return TermCollection.queryBy(terms, f);
	}

	public List<Term> queryBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(terms, minSyllableAmount, maxSyllableAmount);
	}

	public List<Term> queryBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(terms, syllableAmount);
	}

	public List<Term> queryBy(GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(terms, grammaticalCase);
	}

	public List<Term> queryBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(terms, onlyPluralTerms);
	}

	public List<Term> queryBy(Gender gender) {
		return TermCollection.queryBy(terms, gender);
	}

	public List<Term> mostCommonTerms() {
		return TermCollection.mostCommonTerms(terms);
	}

	public Term getRandomTerm() {
		return TermCollection.getRandomTerm(terms.values().stream().collect(Collectors.toList()));
	}

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
}
