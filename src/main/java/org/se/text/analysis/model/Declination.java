package org.se.text.analysis.model;

import java.util.Objects;

import org.se.text.analysis.dict.TermEndings;

/**
 * @author Val Richter
 */
public class Declination extends TermEndings {
	public GrammaticalCase grammaticalCase;
	public Gender gender;

	public Declination(String radix, GrammaticalCase grammaticalCase, Gender gender, Numerus numerus, boolean toUmlaut) {
		super(radix, numerus, toUmlaut);
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
	}

	public Declination() {
	}

	public GrammaticalCase getGrammaticalCase() {
		return this.grammaticalCase;
	}

	public void setGrammaticalCase(GrammaticalCase grammaticalCase) {
		this.grammaticalCase = grammaticalCase;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Declination grammaticalCase(GrammaticalCase grammaticalCase) {
		setGrammaticalCase(grammaticalCase);
		return this;
	}

	public Declination gender(Gender gender) {
		setGender(gender);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Declination)) {
			return false;
		}
		Declination declination = (Declination) o;
		return Objects.equals(radix, declination.radix) && Objects.equals(grammaticalCase, declination.grammaticalCase)
				&& Objects.equals(gender, declination.gender) && Objects.equals(numerus, declination.numerus);
	}

	@Override
	public int hashCode() {
		return Objects.hash(radix, grammaticalCase, gender, numerus, toUmlaut);
	}

	@Override
	public String toString() {
		return "{" + super.toStringHelper() + ", grammaticalCase='" + getGrammaticalCase() + "'" + ", gender='" + getGender() + "'" + "}";
	}

}
