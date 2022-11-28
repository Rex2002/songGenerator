package org.se.Text.Analysis.dict;

import java.util.*;

public class WordStemmer {
	private String stem = "";
	private List<WordWithData> caseEndings = new LinkedList<WordWithData>();
	private List<WordWithData> prefixes = new LinkedList<WordWithData>();
	private List<WordWithData> suffixes = new LinkedList<WordWithData>();
	final String baseKey;

	// Constructors:

	public WordStemmer() {
		this.baseKey = "lemma";
	}

	public WordStemmer(String stem) {
		this.baseKey = "lemma";
		this.stem = stem;
	}

	public WordStemmer(String baseKey, String stem) {
		this.baseKey = baseKey;
		this.stem = stem;
	}

	public WordStemmer(String stem, List<WordWithData> prefixes, List<WordWithData> suffixes) {
		this.baseKey = "lemma";
		this.stem = stem;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
	}

	public WordStemmer(String baseKey, String stem, List<WordWithData> prefixes, List<WordWithData> suffixes) {
		this.baseKey = baseKey;
		this.stem = stem;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
	}

	public WordStemmer(String stem, List<WordWithData> caseEndings, List<WordWithData> prefixes,
			List<WordWithData> suffixes, String baseKey) {
		this.stem = stem;
		this.caseEndings = caseEndings;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.baseKey = baseKey;
	}

	// Actual Logic:

	public void removeSuffixes(WordList suffixes, int minStemLength, WordList diphtongs) {
		WordStemmer res = removeSuffixes(this.stem, suffixes, minStemLength, diphtongs, this.baseKey);
		this.stem = res.getStem();
		this.suffixes.addAll(0, res.getSuffixes()); // Append to the beginning
	}

	public void removePrefixes(WordList prefixes, int minStemLength, WordList diphtongs) {
		WordStemmer res = removePrefixes(this.stem, prefixes, minStemLength, diphtongs, this.baseKey);
		this.stem = res.getStem();
		this.prefixes.addAll(res.getPrefixes()); // Append to the end
	}

	// Returns a list of strings, where the strings are in order of appearance in
	// the string, meaning that the wordstem comes first
	public static WordStemmer removeSuffixes(String s, WordList suffixes, int minStemLength, WordList diphtongs,
			String baseKey) {
		List<WordWithData> occuredSuffixes = new LinkedList<WordWithData>();
		String currentStringPart = new String();
		char[] chars = s.toCharArray();
		int j = chars.length;

		for (int i = chars.length - 1; i > minStemLength; i--) {
			boolean isPartOfDiphtong = false;
			currentStringPart += chars[i];

			// Check if current character is part of a diphtong
			if (i > 0) {
				if (diphtongs.has(chars[i - 1] + "" + chars[i])) {
					// Check that the previous character isn't already part of a diphtong,
					// invalidating the current diphtong (e.g. "eie" would invalidate the current
					// diphtong "ie", because "ei" is also a diphtong)
					if (i - 1 <= 0 || !diphtongs.has(chars[i - 2] + "" + chars[i - 1])) {
						isPartOfDiphtong = true;
					}
				}
			}

			if (!isPartOfDiphtong && suffixes.has(currentStringPart.toString())) {
				occuredSuffixes.add(0, suffixes.get(currentStringPart).get());
				currentStringPart = "";
				j = i;
			}
		}

		String stem = s.substring(0, j);
		WordStemmer w = new WordStemmer(baseKey, stem);
		w.setSuffixes(occuredSuffixes);
		return w;
	}

	// Returns a list of strings, where the strings are in order of appearance in
	// the string, meaning that the wordstem comes last
	public static WordStemmer removePrefixes(String s, WordList prefixes, int minStemLength, WordList diphtongs,
			String baseKey) {
		List<WordWithData> occuredPrefixes = new LinkedList<WordWithData>();
		String currentStringPart = new String();
		char[] chars = s.toCharArray();
		int j = chars.length;
		boolean wasLastCharPartOfDiphtong = false;

		for (int i = 0; i < chars.length - minStemLength; i++) {
			boolean isPartOfDiphtong = false;
			currentStringPart += chars[i];

			if (i < chars.length - 1) {
				if (!wasLastCharPartOfDiphtong && diphtongs.has(chars[i] + "" + chars[i + 1])) {
					isPartOfDiphtong = true;
				}
			}

			if (!isPartOfDiphtong && prefixes.has(currentStringPart.toString())) {
				occuredPrefixes.add(prefixes.get(currentStringPart).get());
				currentStringPart = "";
				j = i + 1;
			}

			wasLastCharPartOfDiphtong = isPartOfDiphtong;
		}

		String stem = s.substring(j);
		WordStemmer w = new WordStemmer(baseKey, stem);
		w.setPrefixes(occuredPrefixes);
		return w;
	}

	public static WordStemmer removeCaseEndings(String s, WordList cases, int minStemLength, WordList diphtongs,
			String baseKey) {
		// TODO:
		return new WordStemmer();
	}

	// Boilerplate

	public String getStem() {
		return this.stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public List<WordWithData> getCaseEndings() {
		return this.caseEndings;
	}

	public void setCaseEndings(List<WordWithData> caseEndings) {
		this.caseEndings = caseEndings;
	}

	public List<WordWithData> getPrefixes() {
		return this.prefixes;
	}

	public void setPrefixes(List<WordWithData> prefixes) {
		this.prefixes = prefixes;
	}

	public List<WordWithData> getSuffixes() {
		return this.suffixes;
	}

	public void setSuffixes(List<WordWithData> suffixes) {
		this.suffixes = suffixes;
	}

	public String getBaseKey() {
		return this.baseKey;
	}

	public WordStemmer stem(String stem) {
		setStem(stem);
		return this;
	}

	public WordStemmer caseEndings(List<WordWithData> caseEndings) {
		setCaseEndings(caseEndings);
		return this;
	}

	public WordStemmer prefixes(List<WordWithData> prefixes) {
		setPrefixes(prefixes);
		return this;
	}

	public WordStemmer suffixes(List<WordWithData> suffixes) {
		setSuffixes(suffixes);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof WordStemmer)) {
			return false;
		}
		WordStemmer wordStemmer = (WordStemmer) o;
		return Objects.equals(stem, wordStemmer.stem) && Objects.equals(caseEndings, wordStemmer.caseEndings)
				&& Objects.equals(prefixes, wordStemmer.prefixes) && Objects.equals(suffixes, wordStemmer.suffixes)
				&& Objects.equals(baseKey, wordStemmer.baseKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stem, caseEndings, prefixes, suffixes, baseKey);
	}

	@Override
	public String toString() {
		return "{" +
				" stem='" + getStem() + "'" +
				", caseEndings='" + getCaseEndings() + "'" +
				", prefixes='" + getPrefixes() + "'" +
				", suffixes='" + getSuffixes() + "'" +
				", baseKey='" + getBaseKey() + "'" +
				"}";
	}

}