package org.se.Text.Analysis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.se.Text.Analysis.dict.Dict;

/**
 * @author Val Richter
 */
public class TermVariations {
	public Map<Integer, NounTerm> variations;
	public Integer frequency;
	public String radix;

	// public Map<Term> getVariations(@Nullable GrammaticalCase grammaticalCase,
	// @Nullable Gender gender, @Nullable Boolean isPlural, @Nullable Integer
	// syllableMin, @Nullable Integer syllableMax) {}

	// Gets the term of the specified variation if it's stored and otherwise creates
	// it based on simple rules
	// Careful, created variations might be pretty bad
	// public Term createVariation(GrammaticalCase grammaticalCase, Boolean
	// isPlural) {}

	public TermVariations() {
		this.variations = new HashMap<Integer, NounTerm>();
		this.frequency = 0;
		this.radix = "";
	}

	public TermVariations(NounTerm term) {
		this.variations = new HashMap<Integer, NounTerm>();
		this.variations.put(term.hashData(), term);
		this.frequency = 1;
		this.radix = term.radix;
	}

	public TermVariations(List<NounTerm> terms) {
		this.variations = new HashMap<Integer, NounTerm>();
		for (NounTerm term : terms) {
			variations.put(term.hashData(), term);
		}
		this.frequency = terms.size();
		this.radix = terms.get(0).radix;
	}

	public TermVariations(Map<Integer, NounTerm> variations, Integer frequency, String radix) {
		this.variations = variations;
		this.frequency = frequency;
		this.radix = radix;
	}

	public void forEach(Consumer<? super NounTerm> f) {
		variations.forEach((key, term) -> {
			f.accept(term);
		});
	}

	public String getRadix() {
		return this.radix;
	}

	public Map<Integer, NounTerm> getVariations() {
		return this.variations;
	}

	public void setVariations(Map<Integer, NounTerm> variations) {
		this.variations = variations;
	}

	public Integer getFrequency() {
		return this.frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public void setRadix(String radix) {
		this.radix = radix;
	}

	public TermVariations variations(Map<Integer, NounTerm> variations) {
		setVariations(variations);
		return this;
	}

	public TermVariations frequency(Integer frequency) {
		setFrequency(frequency);
		return this;
	}

	public TermVariations radix(String radix) {
		setRadix(radix);
		return this;
	}

	@Override
	public int hashCode() {
		return this.radix.hashCode();
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
				&& Objects.equals(frequency, termVariations.frequency) && Objects.equals(radix, termVariations.radix);
	}

	@Override
	public String toString() {
		return "{" +
				" variations='" + getVariations() + "'" +
				", frequency='" + getFrequency() + "'" +
				", radix='" + getRadix() + "'" +
				"}";
	}

	public void add(NounTerm term) {
		if (this.radix.isEmpty()) {
			this.radix = term.radix;
		}

		if (this.has(term)) {
			this.variations.get(term.hashData()).increaseFrequency();
		} else {
			this.variations.put(term.hashData(), term);
		}

		increaseFrequency();
	}

	public boolean has(NounTerm term) {
		return this.variations.containsKey(term.hashCode());
	}

	public boolean hasType(Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural) {
		int hash = NounTerm.hashData(gender, grammaticalCase, isPlural);
		return variations.containsKey(hash);
	}

	public List<NounTerm> queryBy(Predicate<? super NounTerm> f) {
		return this.variations.values().stream().filter(f).collect(Collectors.toList());
	}

	public List<NounTerm> queryBy(Gender gender) {
		return this.queryBy(x -> x.gender == gender);
	}

	public List<NounTerm> queryBy(GrammaticalCase grammaticalCase) {
		return this.queryBy(x -> x.grammaticalCase == grammaticalCase);
	}

	public List<NounTerm> queryBy(Boolean isPlural) {
		return this.queryBy(x -> x.isPlural == isPlural);
	}

	public List<NounTerm> queryBySyllableRange(int minSyllableAmount, int maxSyllableAmount) {
		return this.queryBy(x -> minSyllableAmount <= x.syllables.length && x.syllables.length >= maxSyllableAmount);
	}

	public Optional<NounTerm> getTerm(Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural) {
		int hash = NounTerm.hashData(gender, grammaticalCase, isPlural);
		if (variations.containsKey(hash)) {
			return Optional.of(this.variations.get(hash));
		}
		return Optional.empty();
	}

	public NounTerm createTerm(Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural, Dict dict) {
		Optional<NounTerm> res = getTerm(gender, grammaticalCase, isPlural);
		if (res.isPresent()) {
			return res.get();
		}

		return dict.createNounTerm(this, gender, grammaticalCase, isPlural);
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
