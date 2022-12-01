package org.se.Text.Analysis;

import java.util.*;

/**
 * @author Val Richter
 */
public class NounTerm {
	public int frequency;
	public String radix;
	public String[] words;
	public Integer[] syllables = {};
	public Boolean isPlural;
	public GrammaticalCase grammaticalCase;
	public Gender gender;

	public NounTerm(String radix, String[] words, Integer[] syllables, Boolean isPlural,
			GrammaticalCase grammaticalCase,
			Gender gender) {
		this.frequency = 1;
		this.radix = radix;
		this.words = words;
		this.syllables = syllables;
		this.isPlural = isPlural;
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
		System.out.println(this);
	}

	public NounTerm(String radix, String word, Integer[] syllables, Boolean isPlural, GrammaticalCase grammaticalCase,
			Gender gender) {
		this.frequency = 1;
		this.radix = radix;
		String[] words = { word };
		this.words = words;
		this.syllables = syllables;
		this.isPlural = isPlural;
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
	}

	public NounTerm(String word) {
		this.frequency = 1;
		this.radix = word;
		String[] words = { word };
		this.words = words;
		Integer[] syllables = { 0 };
		this.syllables = syllables;
		this.isPlural = false;
		this.grammaticalCase = GrammaticalCase.Nominative;
		this.gender = Gender.Female;
	}

	public void increaseFrequency() {
		this.frequency++;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public String[] getWords() {
		return this.words;
	}

	public void setWords(String[] words) {
		this.words = words;
	}

	public Integer[] getSyllables() {
		return this.syllables;
	}

	public void setSyllables(Integer[] syllables) {
		this.syllables = syllables;
	}

	public Boolean isPlural() {
		return this.isPlural;
	}

	public Boolean getIsPlural() {
		return this.isPlural;
	}

	public Boolean isIsPlural() {
		return this.isPlural;
	}

	public Gender getGender() {
		return this.gender;
	}

	public String getRadix() {
		return this.radix;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setIsPlural(Boolean isPlural) {
		this.isPlural = isPlural;
	}

	public GrammaticalCase getGrammaticalCase() {
		return this.grammaticalCase;
	}

	public void setGrammaticalCase(GrammaticalCase grammaticalCase) {
		this.grammaticalCase = grammaticalCase;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setRadix(String radix) {
		this.radix = radix;
	}

	public NounTerm words(String[] words) {
		setWords(words);
		return this;
	}

	public NounTerm grammaticalCase(GrammaticalCase grammaticalCase) {
		setGrammaticalCase(grammaticalCase);
		return this;
	}

	public NounTerm gender(Gender gender) {
		setGender(gender);
		return this;
	}

	public NounTerm radix(String radix) {
		setRadix(radix);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof NounTerm)) {
			return false;
		}
		NounTerm term = (NounTerm) o;
		return Objects.equals(words, term.words) && Objects.equals(syllables, term.syllables)
				&& Objects.equals(isPlural, term.isPlural) && Objects.equals(grammaticalCase, term.grammaticalCase)
				&& Objects.equals(gender, term.gender) && Objects.equals(radix, term.radix);
	}

	@Override
	public int hashCode() {
		return Objects.hash(words, syllables, isPlural, grammaticalCase, gender, radix);
	}

	@Override
	public String toString() {
		return "{" +
				" words='" + getWords() + "'" +
				", syllables='" + getSyllables() + "'" +
				", isPlural='" + isIsPlural() + "'" +
				", grammaticalCase='" + getGrammaticalCase() + "'" +
				", gender='" + this.gender + "'" +
				", radix='" + this.radix + "'" +
				"}";
	}

	public String show() {
		return String.join(" ", this.words);
	}

	public String[] syllableStrings() {
		String words = String.join("", this.words);
		String[] res = new String[syllables.length];
		for (int i = 0; i < syllables.length; i++) {
			Integer start = syllables[i];
			Integer end = i + 1 == syllables.length ? words.length() : syllables[i + 1];
			res[i] = words.substring(start, end);
		}
		return res;
	}

	public int hashData() {
		return NounTerm.hashData(gender, grammaticalCase, isPlural);
	}

	public static int hashData(Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural) {
		int genderNum = gender.ordinal();
		int caseNum = grammaticalCase.ordinal();
		int pluralNum = isPlural ? 1 : 0;
		return genderNum * 100 + caseNum * 10 + pluralNum;
	}
}
