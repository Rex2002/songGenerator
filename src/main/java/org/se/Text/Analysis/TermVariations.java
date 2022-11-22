package org.se.Text.Analysis;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TermVariations {
	public HashMap<Integer, Term> variations;
	public Integer frequency;
	public String lemma;

	// public HashMap<Term> getVariations(@Nullable GrammaticalCase grammaticalCase,
	// @Nullable Gender gender, @Nullable Boolean isPlural, @Nullable Integer
	// syllableMin, @Nullable Integer syllableMax) {}

	// Gets the term of the specified variation if it's stored and otherwise creates
	// it based on simple rules
	// Careful, created variations might be pretty bad
	// public Term createVariation(GrammaticalCase grammaticalCase, Boolean
	// isPlural) {}

	public TermVariations() {
		this.variations = new HashMap<Integer, Term>();
		this.frequency = 0;
		this.lemma = "";
	}

	public TermVariations(Term term) {
		this.variations = new HashMap<Integer, Term>();
		this.variations.put(term.hashData(), term);
		this.frequency = 1;
		this.lemma = term.lemma;
	}

	public TermVariations(List<Term> terms) {
		this.variations = new HashMap<Integer, Term>();
		for (Term term : terms) {
			variations.put(term.hashData(), term);
		}
		this.frequency = terms.size();
		this.lemma = terms.get(0).lemma;
	}

	public String getLemma() {
		return this.lemma;
	}

	public TermVariations(HashMap<Integer, Term> variations, Integer frequency, String lemma) {
		this.variations = variations;
		this.frequency = frequency;
		this.lemma = lemma;
	}

	public HashMap<Integer, Term> getVariations() {
		return this.variations;
	}

	public void setVariations(HashMap<Integer, Term> variations) {
		this.variations = variations;
	}

	public Integer getFrequency() {
		return this.frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public TermVariations variations(HashMap<Integer, Term> variations) {
		setVariations(variations);
		return this;
	}

	public TermVariations frequency(Integer frequency) {
		setFrequency(frequency);
		return this;
	}

	public TermVariations lemma(String lemma) {
		setLemma(lemma);
		return this;
	}

	@Override
	public int hashCode() {
		return this.lemma.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof TermVariations)) {
			return false;
		}
		TermVariations termVariations = (TermVariations) o;
		return Objects.equals(variations, termVariations.variations)
				&& Objects.equals(frequency, termVariations.frequency) && Objects.equals(lemma, termVariations.lemma);
	}

	@Override
	public String toString() {
		return "{" +
				" variations='" + getVariations() + "'" +
				", frequency='" + getFrequency() + "'" +
				", lemma='" + getLemma() + "'" +
				"}";
	}

	public void add(Term term) {
		if (this.lemma.isEmpty())
			this.lemma = term.lemma;

		if (this.has(term))
			this.variations.get(term.hashCode()).increaseFrequency();
		else
			this.variations.put(term.hashData(), term);
	}

	public boolean has(Term term) {
		return this.variations.containsKey(term.hashCode());
	}

	public boolean hasType(Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural) {
		int hash = Term.hashData(gender, grammaticalCase, isPlural);
		return variations.containsKey(hash);
	}

	public List<Term> queryBy(Predicate<? super Term> f) {
		return this.variations.values().stream().filter(f).collect(Collectors.toList());
	}

	public List<Term> queryBy(Gender gender) {
		return this.queryBy(x -> x.gender == gender);
	}

	public List<Term> queryBy(GrammaticalCase grammaticalCase) {
		return this.queryBy(x -> x.grammaticalCase == grammaticalCase);
	}

	public List<Term> queryBy(Boolean isPlural) {
		return this.queryBy(x -> x.isPlural == isPlural);
	}

	public List<Term> queryBySyllableRange(int minSyllableAmount, int maxSyllableAmount) {
		return this.queryBy(x -> minSyllableAmount <= x.syllables.length && x.syllables.length >= maxSyllableAmount);
	}

	public Term getTerm(Gender gender, GrammaticalCase grammaticalCase, Boolean iPlural) {
		int hash = Term.hashData(gender, grammaticalCase, iPlural);
		if (variations.containsKey(hash))
			return this.variations.get(hash);
		// TODO
		Term newTerm = new Term(this.lemma);
		this.variations.put(hash, newTerm); // Should we have this here or not?
		return newTerm;
	}

	public void add(TermVariations variations) {
		variations.variations.forEach((key, term) -> {
			if (!this.variations.containsKey(key)) {
				this.variations.put(key, term);
			} else {
				this.variations.get(key).increaseFrequency();
			}
			this.increaseFrequency();
		});
	}

	public void increaseFrequency() {
		this.frequency++;
	}
}
