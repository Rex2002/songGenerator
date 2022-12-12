package org.se.text.analysis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.se.text.analysis.dict.Dict;
import org.se.text.analysis.model.Gender;
import org.se.text.analysis.model.GrammaticalCase;
import org.se.text.analysis.model.Numerus;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class TermVariations<T extends Term> {
	private Map<Integer, T> variations;
	private Integer frequency;
	private String radix;
	private Random rand = new Random();

	public TermVariations(T term) {
		this.variations = new HashMap<>();
		this.variations.put(term.hashData(), term);
		this.frequency = 1;
		this.radix = term.radix;
	}

	public void forEach(Consumer<? super T> f) {
		variations.forEach((key, term) -> f.accept(term));
	}

	public void add(T term) {
		if (this.radix.isEmpty()) this.radix = term.radix;

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
		return this.variations.values().stream().filter(f).toList();
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

	public Term getRandomTerm() {
		int i = rand.nextInt(variations.size());
		Object[] arr = variations.values().toArray();
		return (Term) arr[i];
	}

	// Static Functions specifically for Nouns

	public static Optional<NounTerm> getTerm(TermVariations<NounTerm> nounVariations, Gender gender, GrammaticalCase grammaticalCase,
			Numerus numerus) {
		int hash = NounTerm.hashData(gender, grammaticalCase, numerus);
		if (nounVariations.variations.containsKey(hash)) {
			return Optional.of(nounVariations.variations.get(hash));
		}
		return Optional.empty();
	}

	// Same as getTerm, except it allows the program to automatically create the
	// queried variation if necessary
	// Automatically created variations can be very wrong and should be avoided if
	// possible
	public static Optional<NounTerm> createTerm(TermVariations<NounTerm> nounVariations, Gender gender, GrammaticalCase grammaticalCase,
			Numerus numerus, Dict dict) {
		Optional<NounTerm> res = getTerm(nounVariations, gender, grammaticalCase, numerus);
		if (res.isPresent()) return res;

		return dict.createNounTerm(nounVariations, gender, grammaticalCase, numerus);
	}

	// Boilerplate:

	public String getRadix() {
		return this.radix;
	}

	public Map<Integer, T> getVariations() {
		return this.variations;
	}

	public Integer getFrequency() {
		return this.frequency;
	}

	@Override
	public int hashCode() {
		return this.radix.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermVariations)) {
			return false;
		}
		TermVariations<T> termVariations = (TermVariations<T>) o;
		return Objects.equals(variations, termVariations.variations) && Objects.equals(frequency, termVariations.frequency)
				&& Objects.equals(radix, termVariations.radix);
	}

	@Override
	public String toString() {
		return "{" + " variations='" + getVariations() + "'" + ", frequency='" + getFrequency() + "'" + ", radix='" + getRadix() + "'" + "}";
	}
}
