package org.se.Text.Analysis;

import java.util.*;
import java.util.stream.Collectors;

public class Term {
	public int frequency;
	public String[] words;
	public Integer[] syllables = {};
	public Boolean isPlural;
	public GrammaticalCase grammaticalCase;
	public Gender gender;
	public String lemma;

	public Term(String[] words) {
		constructorHelper(words);
	}

	public Term(String word) {
		String[] words = { word };
		constructorHelper(words);
	}

	private void constructorHelper(String[] words) {
		this.frequency = 1;
		this.words = words;

		String[] pluralEndings = { "en", "s" };
		String word = words[words.length - 1];
		for (String pe : pluralEndings) {
			if (word.endsWith(pe)) {
				word = word.substring(0, word.length() - pe.length());

				break;
			}
		}

		ArrayList<Integer> syllables = new ArrayList<Integer>();
		Integer lastEndCounter = 0;
		for (String w : words) {
			List<Integer> wordSyllables = Term.syllables(w);
			wordSyllables = wordSyllables.stream().map(start -> start + lastEndCounter).collect(Collectors.toList());
			syllables.addAll(wordSyllables);
		}
		syllables.toArray(this.syllables);

		this.isPlural = Term.isWordPlural(words[words.length - 1]);
		this.grammaticalCase = Term.getCaseOf(words[0], this.isPlural);
		this.gender = Term.getGenderOf(words[0], this.grammaticalCase);

		List<String> lemmas = new ArrayList<String>();
		for (String w : words) {
			String s = Term.getLemmaOf(w, this.gender, this.grammaticalCase, this.isPlural);
			lemmas.add(s);
		}
		this.lemma = String.join(" ", lemmas);
	}

	public Term(String[] words, Integer[] syllables, Boolean isPlural, GrammaticalCase grammaticalCase, Gender gender,
			String lemma) {
		this.words = words;
		this.syllables = syllables;
		this.isPlural = isPlural;
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
		this.lemma = lemma;
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

	public String getLemma() {
		return this.lemma;
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

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public Term words(String[] words) {
		setWords(words);
		return this;
	}

	public Term grammaticalCase(GrammaticalCase grammaticalCase) {
		setGrammaticalCase(grammaticalCase);
		return this;
	}

	public Term gender(Gender gender) {
		setGender(gender);
		return this;
	}

	public Term lemma(String lemma) {
		setLemma(lemma);
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
		return Objects.equals(words, term.words) && Objects.equals(syllables, term.syllables)
				&& Objects.equals(isPlural, term.isPlural) && Objects.equals(grammaticalCase, term.grammaticalCase)
				&& Objects.equals(gender, term.gender) && Objects.equals(lemma, term.lemma);
	}

	@Override
	public int hashCode() {
		return Objects.hash(words, syllables, isPlural, grammaticalCase, gender, lemma);
	}

	@Override
	public String toString() {
		return "{" +
				" words='" + getWords() + "'" +
				", syllables='" + getSyllables() + "'" +
				", isPlural='" + isIsPlural() + "'" +
				", grammaticalCase='" + getGrammaticalCase() + "'" +
				", gender='" + this.gender + "'" +
				", lemma='" + this.lemma + "'" +
				"}";
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
		return Term.hashData(gender, grammaticalCase, isPlural);
	}

	public static int hashData(Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural) {
		int genderNum = gender.ordinal();
		int caseNum = grammaticalCase.ordinal();
		int pluralNum = isPlural ? 1 : 0;
		return genderNum * 100 + caseNum * 10 + pluralNum;
	}

	private static List<Integer> syllables(String word) {
		List<Integer> res = new ArrayList<Integer>();
		res.add(0);
		// TODO
		return res;
	}

	private static Boolean isWordPlural(String word) {
		// TODO
		return false;
	}

	private static GrammaticalCase getCaseOf(String word, Boolean isPlural) {
		// TODO
		return GrammaticalCase.Nominative;
	}

	private static Gender getGenderOf(String word, GrammaticalCase grammaticalCase) {
		// TODO
		return Gender.female;
	}

	private static String getLemmaOf(String word, Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural) {
		// TODO
		return word;
	}
}
