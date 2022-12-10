package org.se.text.analysis;

import java.util.*;

import org.se.text.analysis.model.Gender;
import org.se.text.analysis.model.GrammaticalCase;
import org.se.text.analysis.model.Numerus;

/**
 * @author Val Richter
 */
public class NounTerm extends Term {
	public GrammaticalCase grammaticalCase;
	public Gender gender;
	public boolean changeableGender = false;

	public NounTerm(String radix, String word, Numerus numerus, GrammaticalCase grammaticalCase, Gender gender, boolean changeableGender) {
		super(capitalize(radix), word, numerus);
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
		this.changeableGender = changeableGender;
	}

	public NounTerm(String radix, String word, Numerus numerus, GrammaticalCase grammaticalCase, Gender gender) {
		super(capitalize(radix), word, numerus);
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
	}

	public static String capitalize(String s) {
		if (s.isEmpty()) return s;
		else return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	@Override
	public int hashData() {
		return NounTerm.hashData(gender, grammaticalCase, numerus);
	}

	public static int hashData(Gender gender, GrammaticalCase grammaticalCase, Numerus numerus) {
		int genderNum = gender.ordinal();
		int caseNum = grammaticalCase.ordinal();
		int pluralNum = numerus.ordinal();
		return genderNum * 100 + caseNum * 10 + pluralNum;
	}

	@Override
	public String toString() {
		return "{" + super.toStringHelper() + ", grammaticalCase='" + getGrammaticalCase() + "'" + ", gender='" + getGender() + "'" + "}";
	}

	// Boilerplate:

	public GrammaticalCase getGrammaticalCase() {
		return this.grammaticalCase;
	}

	public Gender getGender() {
		return this.gender;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof NounTerm nounTerm)) {
			return false;
		}
		return super.equals(nounTerm) && Objects.equals(grammaticalCase, nounTerm.grammaticalCase) && Objects.equals(gender, nounTerm.gender);
	}

	@Override
	public int hashCode() {
		return Objects.hash(frequency, radix, word, syllableAmount, numerus, grammaticalCase, gender);
	}
}
