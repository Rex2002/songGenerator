package org.se.Text.Analysis;

import java.util.ArrayList;
import java.util.List;

public class Term {
	public String[] words;
	public Integer[] syllables;
	public Boolean isPlural;
	public GrammaticalCase grammaticalCase;
	public Gender gender;
	public String lemma;

	public Term(String[] words) {
		this.words = words;

		String[] pluralEndings = {"en", "s"};
		String word = words[-1];
		for (String pe : pluralEndings) {
			if (word.endsWith(pe)) {
				word = word.substring(0, word.length()-pe.length());

				break;
			}
		}

		ArrayList<Integer> syllables = new ArrayList<Integer>();
		Integer lastEndCounter = 0;
		for (String w : words) {
			List<Integer> wordSyllables = Term.syllables(w);
			wordSyllables = wordSyllables.stream().map(start -> start + lastEndCounter).toList();
			syllables.addAll(wordSyllables);
		}
		syllables.toArray(this.syllables);

		this.isPlural = Term.isPlural(words[-1]);
		this.grammaticalCase = Term.getCase(words[0], this.isPlural);
		this.gender = Term.getGender(words[0], this.grammaticalCase);

		List<String> lemmas = new ArrayList<String>();
		for (String w : words) {
			String s = Term.getLemma(w, this.gender, this.grammaticalCase, this.isPlural);
			lemmas.add(s);
		}
		this.lemma = String.join(" ", lemmas);
	}

	public Term(String word) {
		String[] words = {word};
		new Term(words);
	}

	public String toString() {
		return String.join(" ", words);
	}

	public String[] syllableStrings() {
		String words = String.join("", this.words);
		String[] res = new String[syllables.length];
		for (int i = 0; i < syllables.length; i++) {
			Integer start = syllables[i];
			Integer end = i + 1 == syllables.length ? words.length() : syllables[i+1];
			res[i] = words.substring(start, end);
		}
		return res;
	}

	public int hashData() {
		return Term.hashData(grammaticalCase, isPlural);
	}

	public static int hashData(GrammaticalCase grammaticalCase, Boolean isPlural) {
		int caseNum = grammaticalCase.ordinal();
		int pluralNum = isPlural ? 1 : 0;
		return caseNum * 10 + pluralNum;
	}

	private static List<Integer> syllables(String word) {
		List<Integer> res = new ArrayList<Integer>();
		res.add(0);
		// TODO
		return res;
	}

	private static Boolean isPlural(String word) {
		// TODO
		return false;
	}

	private static GrammaticalCase getCase(String word, Boolean isPlural) {
		// TODO
		return GrammaticalCase.Nominative;
	}

	private static Gender getGender(String word, GrammaticalCase grammaticalCase) {
		// TODO
		return Gender.female;
	}

	private static String getLemma(String word, Gender gender, GrammaticalCase grammaticalCase, Boolean isPlural) {
		// TODO
		return word;
	}
}
