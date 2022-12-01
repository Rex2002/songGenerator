package org.se.Text.Analysis.dict;

import java.util.Objects;

import org.se.Text.Analysis.Gender;
import org.se.Text.Analysis.GrammaticalCase;
import org.se.Text.Analysis.Numerus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Declination {
	@JsonProperty
	String radix;
	@JsonProperty
	GrammaticalCase grammaticalCase;
	@JsonProperty
	Gender gender;
	@JsonProperty
	Numerus numerus;
	@JsonProperty
	boolean toUmlaut;

	@JsonCreator
	public Declination(@JsonProperty("radix") String radix,
			@JsonProperty("grammaticalCase") GrammaticalCase grammaticalCase,
			@JsonProperty("gender") Gender gender,
			@JsonProperty("numerus") Numerus numerus,
			@JsonProperty("toUmlaut") boolean toUmlaut) {
		this.radix = radix;
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
		this.numerus = numerus;
		this.toUmlaut = toUmlaut;
		// System.out.println(this);
	}

	public Declination() {
	}

	public String getRadix() {
		return this.radix;
	}

	public void setRadix(String radix) {
		this.radix = radix;
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

	public Numerus getNumerus() {
		return this.numerus;
	}

	public void setNumerus(Numerus numerus) {
		this.numerus = numerus;
	}

	public boolean isToUmlaut() {
		return this.toUmlaut;
	}

	public boolean getToUmlaut() {
		return this.toUmlaut;
	}

	public void setToUmlaut(boolean toUmlaut) {
		this.toUmlaut = toUmlaut;
	}

	public Declination radix(String radix) {
		setRadix(radix);
		return this;
	}

	public Declination grammaticalCase(GrammaticalCase grammaticalCase) {
		setGrammaticalCase(grammaticalCase);
		return this;
	}

	public Declination gender(Gender gender) {
		setGender(gender);
		return this;
	}

	public Declination numerus(Numerus numerus) {
		setNumerus(numerus);
		return this;
	}

	public Declination toUmlaut(boolean toUmlaut) {
		setToUmlaut(toUmlaut);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Declination)) {
			return false;
		}
		Declination declination = (Declination) o;
		return Objects.equals(radix, declination.radix) && Objects.equals(grammaticalCase, declination.grammaticalCase)
				&& Objects.equals(gender, declination.gender) && Objects.equals(numerus, declination.numerus)
				&& toUmlaut == declination.toUmlaut;
	}

	@Override
	public int hashCode() {
		return Objects.hash(radix, grammaticalCase, gender, numerus, toUmlaut);
	}

	@Override
	public String toString() {
		return "{" +
				" radix='" + getRadix() + "'" +
				", grammaticalCase='" + getGrammaticalCase() + "'" +
				", gender='" + getGender() + "'" +
				", numerus='" + getNumerus() + "'" +
				", toUmlaut='" + isToUmlaut() + "'" +
				"}";
	}

}
