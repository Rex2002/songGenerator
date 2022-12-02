package org.se.Text.Analysis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.se.Text.Analysis.dict.Dict;

/**
 * @author Val Richter
 */
public class TermVariations<T extends Term> {
	public Map<Integer, T> variations;
	public Integer frequency;
	public String radix;

	// public Map<T> getVariations(@Nullable GrammaticalCase grammaticalCase,
	// @Nullable Gender gender, @Nullable Numerus numerus, @Nullable Integer
	// syllableMin, @Nullable Integer syllableMax) {}

	// Gets the term of the specified variation if it's stored and otherwise creates
	// it based on simple rules
	// Careful, created variations might be pretty bad
	// public T createVariation(GrammaticalCase grammaticalCase, Numerus
	// numerus) {}

	public TermVariations() {
		this.variations = new HashMap<Integer, T>();
		this.frequency = 0;
		this.radix = "";
	}

	public TermVariations(T term) {
		this.variations = new HashMap<Integer, T>();
		this.variations.put(term.hashData(), term);
		this.frequency = 1;
		this.radix = term.radix;
	}

	public TermVariations(List<T> terms) {
		this.variations = new HashMap<Integer, T>();
		for (T term : terms) {
			variations.put(term.hashData(), term);
		}
		this.frequency = terms.size();
		this.radix = terms.get(0).radix;
	}

	public TermVariations(Map<Integer, T> variations, Integer frequency, String radix) {
		this.variations = variations;
		this.frequency = frequency;
		this.radix = radix;
	}

	public void forEach(Consumer<? super T> f) {
		variations.forEach((key, term) -> {
			f.accept(term);
		});
	}

	public void add(T term) {
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

	public boolean has(T term) {
		return this.variations.containsKey(term.hashData());
	}

	public List<T> queryBy(Predicate<? super T> f) {
		return this.variations.values().stream().filter(f).collect(Collectors.toList());
	}

	public List<T> queryBySyllableRange(int minSyllableAmount, int maxSyllableAmount) {
		return this.queryBy(x -> minSyllableAmount <= x.syllableAmount && x.syllableAmount >= maxSyllableAmount);
	}

	public void add(TermVariations<T> variations) {
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

	// Static Functions specifically for Nouns

	public static Optional<NounTerm> getTerm(TermVariations<NounTerm> nounVariations, Gender gender,
			GrammaticalCase grammaticalCase,
			Numerus numerus) {
		int hash = NounTerm.hashData(gender, grammaticalCase, numerus);
		if (nounVariations.variations.containsKey(hash)) {
			return Optional.of(nounVariations.variations.get(hash));
		}
		return Optional.empty();
	}

	// Same as getTerm, except it allows the program to automatically create the
	// queried variation if necessary
	// Automatically created variations can be very wrong and should avoided if
	// possible
	public static NounTerm createTerm(TermVariations<NounTerm> nounVariations, Gender gender,
			GrammaticalCase grammaticalCase,
			Numerus numerus, Dict dict) {
		Optional<NounTerm> res = getTerm(nounVariations, gender, grammaticalCase, numerus);
		if (res.isPresent()) {
			return res.get();
		}

		return dict.createNounTerm(nounVariations, gender, grammaticalCase, numerus);
	}

	public static boolean hasType(TermVariations<NounTerm> nounVariations, Gender gender,
			GrammaticalCase grammaticalCase,
			Numerus numerus) {
		int hash = NounTerm.hashData(gender, grammaticalCase, numerus);
		return nounVariations.variations.containsKey(hash);
	}

	// Boilerplate:

	public String getRadix() {
		return this.radix;
	}

	public Map<Integer, T> getVariations() {
		return this.variations;
	}

	public void setVariations(Map<Integer, T> variations) {
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

	public TermVariations<T> variations(Map<Integer, T> variations) {
		setVariations(variations);
		return this;
	}

	public TermVariations<T> frequency(Integer frequency) {
		setFrequency(frequency);
		return this;
	}

	public TermVariations<T> radix(String radix) {
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
		TermVariations<T> termVariations = (TermVariations<T>) o;
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
}
