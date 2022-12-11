package org.se.text.analysis.model;

import java.util.Objects;

/**
 * @author Val Richter
 */
public class Declination extends TermAffix {
	private GrammaticalCase grammaticalCase;
	private Gender gender;

	public Declination(String radix, GrammaticalCase grammaticalCase, Gender gender, Numerus numerus, AffixType type, boolean toUmlaut) {
		super(radix, numerus, type, toUmlaut);
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
	}

	public Declination() {
	}

	@Override
	public boolean grammaticallyEquals(TermAffix other) {
		if (!(other instanceof Declination declination)) return false;
		return super.grammaticallyEquals(declination) && grammaticalCase == declination.grammaticalCase && gender == declination.gender;
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

	public Declination gender(Gender gender) {
		setGender(gender);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Declination declination)) {
			return false;
		}
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
