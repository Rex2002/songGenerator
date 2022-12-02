package org.se.Text.Analysis.dict;

import java.util.*;

/**
 * @author Val Richter
 */
public class WordStemmer {
	private String stem = "";
	private Declination declinatedSuffix = new Declination();
	private List<WordWithData> prefixes = new LinkedList<WordWithData>();
	private List<WordWithData> suffixes = new LinkedList<WordWithData>();
	final String baseKey;

	// Constructors:

	public WordStemmer() {
		this.baseKey = "radix";
	}

	public WordStemmer(String stem) {
		this.baseKey = "radix";
		this.stem = stem;
	}

	public WordStemmer(String baseKey, String stem) {
		this.baseKey = baseKey;
		this.stem = stem;
	}

	public WordStemmer(String baseKey, String stem, Declination declinatedSuffix) {
		this.baseKey = baseKey;
		this.stem = stem;
		this.declinatedSuffix = declinatedSuffix;
	}

	public WordStemmer(String stem, List<WordWithData> prefixes, List<WordWithData> suffixes) {
		this.baseKey = "radix";
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

	public WordStemmer(String stem, Declination declinatedSuffix, List<WordWithData> prefixes,
			List<WordWithData> suffixes, String baseKey) {
		this.stem = stem;
		this.declinatedSuffix = declinatedSuffix;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.baseKey = baseKey;
	}

	// Actual Logic:

	public static WordStemmer[] radicalize(String s, List<Declination> declinatedSuffixes, WordList suffixes,
			WordList prefixes,
			int minStemLength, WordList diphtongs, WordList umlautChanges, String baseKey) {
		WordStemmer[] x = findDeclinatedSuffixes(s, declinatedSuffixes, umlautChanges, minStemLength, diphtongs,
				baseKey);
		for (WordStemmer w : x) {
			w.removeSuffixes(suffixes, minStemLength, diphtongs);
			w.removePrefixes(prefixes, minStemLength, diphtongs);
		}
		return x;
	}

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
			currentStringPart = chars[i] + currentStringPart;

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
		List<WordWithData> occuredPrefixes = new LinkedList<>();
		String currentStringPart = new String();
		char[] chars = s.toCharArray();
		int j = 0;
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

	// TODO: Optimize declinatedSuffixes storage
	// Currently all declinatedSuffixes are stored in a list
	// where many objects have the same radix
	// specifically this means, that we have many duplicate calculations
	// This could be optimized by storing a list of radixes
	// mapping to a list of Declinations
	// where no duplicate radixes are stored and the result is flattened
	public static WordStemmer[] findDeclinatedSuffixes(String s, List<Declination> declinatedSuffixes,
			WordList umlautChanges,
			int minStemLength,
			WordList diphtongs,
			String baseKey) {
		List<WordStemmer> l = new ArrayList<>();

		// Check all suffixs if they apply to the stem
		for (Declination suffix : declinatedSuffixes) {
			String scopy = s;
			if (scopy.endsWith(suffix.getRadix())) {
				// Update Umlaut sequences if necessary
				if (suffix.getToUmlaut()) {
					char[] chars = scopy.toCharArray();
					int len = chars.length - suffix.getRadix().length();

					// Update umlaute if necessary
					// Check every character if it's part of an umlaut sequence
					for (int i = 0; i < len; i++) {
						// Go through all umlaut sounds, to check if the current character is part of a
						// umlaut sequence
						for (WordWithData umlaut : umlautChanges) {
							char[] withUmlaut = umlaut.get("with").toCharArray();
							boolean comparison = true;
							// Check if the current slice is the correct umlaut sequence
							for (int j = 0; comparison && j < withUmlaut.length && j + i < len; j++) {
								if (withUmlaut[j] != chars[i + j]) {
									comparison = false;
								}
							}
							// If the comparison was correct, update the umlaut sequence
							// break to stop checking for other umlaut sequences
							if (comparison) {
								char[] withoutUmlaut = umlaut.get().toCharArray();
								for (int j = 0; comparison && j < withoutUmlaut.length && j + i < len; j++) {
									chars[i + j] = withoutUmlaut[j];
								}
								break;
							}
						}
					}

					scopy = chars.toString();
				}

				l.add(new WordStemmer(baseKey, scopy, suffix));
			}
		}

		WordStemmer[] res = {};
		return l.toArray(res);
	}

	// Boilerplate

	public String getStem() {
		return this.stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public Declination getDeclinatedSuffix() {
		return this.declinatedSuffix;
	}

	public void setDeclinatedSuffix(Declination declinatedSuffix) {
		this.declinatedSuffix = declinatedSuffix;
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

	public WordStemmer declinatedSuffix(Declination declinatedSuffix) {
		setDeclinatedSuffix(declinatedSuffix);
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
		return Objects.equals(stem, wordStemmer.stem) && Objects.equals(declinatedSuffix, wordStemmer.declinatedSuffix)
				&& Objects.equals(prefixes, wordStemmer.prefixes) && Objects.equals(suffixes, wordStemmer.suffixes)
				&& Objects.equals(baseKey, wordStemmer.baseKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stem, declinatedSuffix, prefixes, suffixes, baseKey);
	}

	@Override
	public String toString() {
		return "{" +
				" stem='" + getStem() + "'" +
				", declinatedSuffix='" + getDeclinatedSuffix() + "'" +
				", prefixes='" + getPrefixes() + "'" +
				", suffixes='" + getSuffixes() + "'" +
				", baseKey='" + getBaseKey() + "'" +
				"}";
	}

}
