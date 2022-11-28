package org.se.Text.Analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class Dict {
	WordList nounSuffixes;
	WordList nounPrefixes;
	WordList nouns;
	WordList verbSuffixes;
	WordList verbPrefixes;
	WordList verbs;

	public Dict(WordList nounSuffixes, WordList nounPrefixes,
			WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
	}

	private static void readCSV(Path filepath, Consumer<? super HashMap<String, String>> forEachRow)
			throws IOException {
		String[] rows = Files.readString(filepath).split("\\r?\\n");
		String[] firstRow = rows[0].split(",");

		for (int i = 1; i < rows.length; i++) {
			String[] col = rows[0].split(",");
			HashMap<String, String> data = new HashMap<String, String>();
			for (int j = 0; j < col.length; j++) {
				data.put(firstRow[j], col[j]);
			}
			forEachRow.accept(data);
		}
	}

	private static WordList[] readDictionaryFromFiles(Path affixCSV, Path nounsCSV, Path verbsCSV) throws IOException {
		WordList nouns = new WordList("lemma");
		WordList nounPrefixes = new WordList("lemma");
		WordList nounSuffixes = new WordList("lemma");
		WordList verbs = new WordList("lemma");
		WordList verbPrefixes = new WordList("lemma");
		WordList verbSuffixes = new WordList("lemma");

		readCSV(affixCSV, data -> {
			switch (data.get("type")) {
				case "nounSuffix":
					nounSuffixes.insert(data);
					break;

				case "nounPrefix":
					nounPrefixes.insert(data);
					break;

				case "verbSuffix":
					verbSuffixes.insert(data);
					break;

				case "verbPrefix":
					verbPrefixes.insert(data);
					break;
			}
		});
		readCSV(nounsCSV, data -> {
			nouns.insert(data);
		});
		readCSV(verbsCSV, data -> {
			verbs.insert(data);
		});

		WordList[] res = { nounSuffixes, nounPrefixes, nouns, verbSuffixes, verbPrefixes, verbs };
		return res;
	}

	public Dict(Path affixCSV, Path nounsCSV, Path verbsCSV) throws IOException {
		WordList[] res = readDictionaryFromFiles(affixCSV, nounsCSV, verbsCSV);
		this.nounSuffixes = res[0];
		this.nounPrefixes = res[1];
		this.nouns = res[2];
		this.verbSuffixes = res[3];
		this.verbPrefixes = res[4];
		this.verbs = res[5];
	}

	public Dict(Path dirPath) throws IOException {
		Path affixCSV = dirPath.resolve("affixesDict.csv");
		Path nounsCSV = dirPath.resolve("nounsDict.csv");
		Path verbsCSV = dirPath.resolve("verbsDict.csv");
		WordList[] res = readDictionaryFromFiles(affixCSV, nounsCSV, verbsCSV);
		this.nounSuffixes = res[0];
		this.nounPrefixes = res[1];
		this.nouns = res[2];
		this.verbSuffixes = res[3];
		this.verbPrefixes = res[4];
		this.verbs = res[5];
	}

	public Dict addDictionary(Dict dict) {
		this.nounSuffixes.insertAll(dict.getNounSuffixes());
		this.nounPrefixes.insertAll(dict.getNounPrefixes());
		this.nouns.insertAll(dict.getNouns());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbs.insertAll(dict.getVerbs());
		return this;
	}

	// Make word into a term

	// Returns a list of strings, where the strings are in order of appearance in
	// the string, meaning that the wordstem comes first
	public static List<String> removeSuffixes(String s, WordList suffixes, int minStemLength) {
		List<String> res = new LinkedList<String>();
		String currentStringPart = new String();
		char[] chars = s.toCharArray();
		int j = chars.length;
		for (int i = chars.length - 1; i > minStemLength; i--) {
			currentStringPart += chars[i];
			if (suffixes.has(currentStringPart.toString())) {
				res.add(0, currentStringPart);
				currentStringPart = "";
				j = i;
			}
		}
		res.add(0, s.substring(0, j));
		return res;
	}

	// Returns a list of strings, where the strings are in order of appearance in
	// the string, meaning that the wordstem comes last
	public static List<String> removePrefixes(String s, WordList prefixes, int minStemLength) {
		List<String> res = new LinkedList<String>();
		String currentStringPart = new String();
		char[] chars = s.toCharArray();
		int j = chars.length;
		for (int i = 0; i < chars.length - minStemLength; i++) {
			currentStringPart += chars[i];
			if (prefixes.has(currentStringPart.toString())) {
				res.add(currentStringPart);
				currentStringPart = "";
				j = i + 1;
			}
		}
		res.add(s.substring(j));
		return res;
	}

	public boolean isNoun(String s) {
		List<String> suffixSplit = removeSuffixes(s, nounSuffixes, 2);
		List<String> prefixSplit = removePrefixes(suffixSplit.get(0), nounPrefixes, 2);
		String stem = prefixSplit.get(prefixSplit.size() - 1);

		// TODO: Include declination changes to the word

		return nouns.has(stem);
	}

	public boolean isVerb(String s) {
		List<String> suffixSplit = removeSuffixes(s, verbSuffixes, 2);
		List<String> prefixSplit = removePrefixes(suffixSplit.get(0), verbPrefixes, 2);
		String stem = prefixSplit.get(prefixSplit.size() - 1);

		// TODO: Include conjugation changes to the word

		return verbs.has(stem);
	}

	// TODO: Optimize by changing Tag-Class and storing the suffixes/prefixes that
	// have already been split in the Tag-object

	public Tag tagWord(String s) {
		if (this.isNoun(s)) {
			return new Tag(s, TagType.Noun);
		} else if (this.isVerb(s)) {
			return new Tag(s, TagType.Verb);
		} else {
			return new Tag(s, TagType.Other);
		}
	}

	public Term buildTerm(Tag t) {
		// TODO: Put some actual logic here
		return new Term(t.word);
	}

	// Getters, Setters & other Boilerplate

	public WordList getNounSuffixes() {
		return this.nounSuffixes;
	}

	public void setNounSuffixes(WordList nounSuffixes) {
		this.nounSuffixes = nounSuffixes;
	}

	public WordList getNounPrefixes() {
		return this.nounPrefixes;
	}

	public void setNounPrefixes(WordList nounPrefixes) {
		this.nounPrefixes = nounPrefixes;
	}

	public WordList getNouns() {
		return this.nouns;
	}

	public void setNouns(WordList nouns) {
		this.nouns = nouns;
	}

	public WordList getVerbSuffixes() {
		return this.verbSuffixes;
	}

	public void setVerbSuffixes(WordList verbSuffixes) {
		this.verbSuffixes = verbSuffixes;
	}

	public WordList getVerbPrefixes() {
		return this.verbPrefixes;
	}

	public void setVerbPrefixes(WordList verbPrefixes) {
		this.verbPrefixes = verbPrefixes;
	}

	public WordList getVerbs() {
		return this.verbs;
	}

	public void setVerbs(WordList verbs) {
		this.verbs = verbs;
	}

	public Dict nounSuffixes(WordList nounSuffixes) {
		setNounSuffixes(nounSuffixes);
		return this;
	}

	public Dict nounPrefixes(WordList nounPrefixes) {
		setNounPrefixes(nounPrefixes);
		return this;
	}

	public Dict nouns(WordList nouns) {
		setNouns(nouns);
		return this;
	}

	public Dict verbSuffixes(WordList verbSuffixes) {
		setVerbSuffixes(verbSuffixes);
		return this;
	}

	public Dict verbPrefixes(WordList verbPrefixes) {
		setVerbPrefixes(verbPrefixes);
		return this;
	}

	public Dict verbs(WordList verbs) {
		setVerbs(verbs);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Dict)) {
			return false;
		}
		Dict dictionary = (Dict) o;
		return Objects.equals(nounSuffixes, dictionary.nounSuffixes)
				&& Objects.equals(nounPrefixes, dictionary.nounPrefixes) && Objects.equals(nouns, dictionary.nouns)
				&& Objects.equals(verbSuffixes, dictionary.verbSuffixes)
				&& Objects.equals(verbPrefixes, dictionary.verbPrefixes) && Objects.equals(verbs, dictionary.verbs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nounSuffixes, nounPrefixes, nouns, verbSuffixes, verbPrefixes, verbs);
	}

	@Override
	public String toString() {
		return "{" +
				" nounSuffixes='" + getNounSuffixes() + "'" +
				", nounPrefixes='" + getNounPrefixes() + "'" +
				", nouns='" + getNouns() + "'" +
				", verbSuffixes='" + getVerbSuffixes() + "'" +
				", verbPrefixes='" + getVerbPrefixes() + "'" +
				", verbs='" + getVerbs() + "'" +
				"}";
	}

}
