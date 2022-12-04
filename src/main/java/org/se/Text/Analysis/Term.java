package org.se.Text.Analysis;

import java.util.*;

/**
 * @author Val Richter
 */
public class Term implements DisplayableParent {
	public int frequency;
	public String radix;
	public String word;
	public Integer syllableAmount;
	public Numerus numerus;

	public Term(String word) {
		this.frequency = 1;
		this.radix = word;
		this.word = word;
		this.syllableAmount = 1;
		this.numerus = Numerus.Singular;
	}

	public Term(String radix, String word, Integer syllableAmount, Numerus numerus) {
		this.frequency = 1;
		this.radix = radix;
		this.word = word;
		this.syllableAmount = syllableAmount;
		this.numerus = numerus;
	}

	public void increaseFrequency() {
		this.frequency++;
	}

	public int hashData() {
		return this.hashCode();
	}

	// Removed, because we only store the amount of syllables
	// instead of a list of syllable starting indices
	// Keeping the code as a comment,
	// in case we should decide to change that decision again

	// public String[] syllableStrings() {
	// String word = String.join("", this.word);
	// String[] res = new String[syllableAmount.length];
	// for (int i = 0; i < syllableAmount.length; i++) {
	// Integer start = syllableAmount[i];
	// Integer end = i + 1 == syllableAmount.length ? word.length() :
	// syllableAmount[i + 1];
	// res[i] = word.substring(start, end);
	// }
	// return res;
	// }

	@Override
	public String toStringHelper() {
		return " frequency='" + getFrequency() + "'" +
				", radix='" + getRadix() + "'" +
				", word='" + getWord() + "'" +
				", syllableAmount='" + getSyllableAmount() + "'" +
				", numerus='" + getNumerus() + "'";
	}

	@Override
	public String toString() {
		return "{" +
				toStringHelper() +
				"}";
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	// Boilerplate:

	public String getRadix() {
		return this.radix;
	}

	public void setRadix(String radix) {
		this.radix = radix;
	}

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getSyllableAmount() {
		return this.syllableAmount;
	}

	public void setSyllableAmount(Integer syllableAmount) {
		this.syllableAmount = syllableAmount;
	}

	public Numerus getNumerus() {
		return this.numerus;
	}

	public void setNumerus(Numerus numerus) {
		this.numerus = numerus;
	}

	public Term frequency(int frequency) {
		setFrequency(frequency);
		return this;
	}

	public Term radix(String radix) {
		setRadix(radix);
		return this;
	}

	public Term word(String word) {
		setWord(word);
		return this;
	}

	public Term syllableAmount(Integer syllableAmount) {
		setSyllableAmount(syllableAmount);
		return this;
	}

	public Term numerus(Numerus numerus) {
		setNumerus(numerus);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Term)) {
			return false;
		}
		Term term = (Term) o;
		return frequency == term.frequency && Objects.equals(radix, term.radix) && Objects.equals(word, term.word)
				&& Objects.equals(syllableAmount, term.syllableAmount) && Objects.equals(numerus, term.numerus);
	}

	@Override
	public int hashCode() {
		return Objects.hash(frequency, radix, word, syllableAmount, numerus);
	}

}
