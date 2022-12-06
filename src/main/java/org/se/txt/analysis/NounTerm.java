package org.se.txt.analysis;

import java.util.*;

/**
 * @author Val Richter
 */
public class NounTerm extends Term {
	public GrammaticalCase grammaticalCase;
	public Gender gender;

	public NounTerm(String radix, String word, Integer syllableAmount, Numerus numerus, GrammaticalCase grammaticalCase, Gender gender) {
		super(radix, word, syllableAmount, numerus);
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
	}

	public NounTerm(String word) {
		// TODO: Change hardcoded defaults to programmatically determined ones
		super(word);
		this.grammaticalCase = GrammaticalCase.NOMINATIVE;
		this.gender = Gender.FEMALE;
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

	public void setGrammaticalCase(GrammaticalCase grammaticalCase) {
		this.grammaticalCase = grammaticalCase;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof NounTerm)) {
			return false;
		}
		NounTerm nounTerm = (NounTerm) o;
		return super.equals(nounTerm) && Objects.equals(grammaticalCase, nounTerm.grammaticalCase) && Objects.equals(gender, nounTerm.gender);
	}

	@Override
	public int hashCode() {
		return Objects.hash(frequency, radix, word, syllableAmount, numerus, grammaticalCase, gender);
	}
}
