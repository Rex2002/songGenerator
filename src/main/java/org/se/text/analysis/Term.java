package org.se.text.analysis;

import java.util.*;

import org.se.text.analysis.model.DisplayableParent;
import org.se.text.analysis.model.Numerus;
import org.se.text.metric.Hyphenizer;

/**
 * @author Val Richter
 */
public class Term implements DisplayableParent {
	protected int frequency;
	protected String radix;
	protected String word;
	protected Integer syllableAmount;
	protected Numerus numerus;

	public Term(String word) {
		this.frequency = 1;
		this.radix = word;
		this.word = word;
		this.syllableAmount = 1;
		this.numerus = Numerus.SINGULAR;
	}

	public Term(String radix, String word, Numerus numerus) {
		this.frequency = 1;
		this.radix = radix;
		this.word = word;
		this.numerus = numerus;
		this.syllableAmount = Hyphenizer.CountSyllabes(word);
	}

	public void increaseFrequency() {
		this.frequency++;
	}

	public int hashData() {
		return this.hashCode();
	}

	@Override
	public String toStringHelper() {
		return " frequency='" + getFrequency() + "'" + ", radix='" + getRadix() + "'" + ", word='" + getWord() + "'" + ", syllableAmount='"
				+ getSyllableAmount() + "'" + ", numerus='" + getNumerus() + "'";
	}

	@Override
	public String toString() {
		return "{" + toStringHelper() + "}";
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

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
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
