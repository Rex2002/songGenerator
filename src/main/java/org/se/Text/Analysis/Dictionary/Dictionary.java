package org.se.Text.Analysis.Dictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Dictionary {
	WordList nounSuffixes;
	WordList nounPrefixes;
	WordList nouns;
	WordList verbSuffixes;
	WordList verbPrefixes;
	WordList verbs;

	public Dictionary(WordList nounSuffixes, WordList nounPrefixes,
			WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
	}

	public Dictionary(Path filepath) throws IOException {
		this.nouns = new WordList();
		this.nounPrefixes = new WordList();
		this.nounSuffixes = new WordList();
		this.verbs = new WordList();
		this.verbPrefixes = new WordList();
		this.verbSuffixes = new WordList();

		String[] rows = Files.readString(filepath).split("\\r?\\n");
		String[] firstRow = rows[0].split(",");

		for (int i = 1; i < rows.length; i++) {
			String[] col = rows[0].split(",");
			HashMap<String, String> data = new HashMap<String, String>();
			for (int j = 0; j < col.length; j++) {
				data.put(firstRow[j], col[j]);
			}

			if (data.containsKey("type") && data.containsKey("lemma")) {
				switch (data.get("type")) {
					case "noun":
						this.nouns.insert(data.get("lemma"));
						break;

					case "nounSuffix":
						this.nounSuffixes.insert(data.get("lemma"));
						break;

					case "nounPrefix":
						this.nounPrefixes.insert(data.get("lemma"));
						break;

					case "verb":
						this.verbs.insert(data.get("lemma"));
						break;

					case "verbSuffix":
						this.verbSuffixes.insert(data.get("lemma"));
						break;

					case "verbPrefix":
						this.verbPrefixes.insert(data.get("lemma"));
						break;
				}
			}
		}
	}

	public Dictionary addDictionary(Dictionary dict) {
		this.nounSuffixes.insertAll(dict.getNounSuffixes());
		this.nounPrefixes.insertAll(dict.getNounPrefixes());
		this.nouns.insertAll(dict.getNouns());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbs.insertAll(dict.getVerbs());
		return this;
	}

	// Check if some String is of a certain type

	public boolean isNounSuffix(String s) {
		return this.nounSuffixes.has(s);
	}

	public boolean isNounPrefix(String s) {
		return this.nounPrefixes.has(s);
	}

	// TODO:
	// This function must have some actual logic
	public boolean isNoun(String s) {
		return this.nouns.has(s);
	}

	public boolean isVerbSuffix(String s) {
		return this.verbSuffixes.has(s);
	}

	public boolean isVerbPrefix(String s) {
		return this.verbPrefixes.has(s);
	}

	// TODO:
	// This function must have some actual logic
	public boolean isVerb(String s) {
		return this.verbs.has(s);
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

	public Dictionary nounSuffixes(WordList nounSuffixes) {
		setNounSuffixes(nounSuffixes);
		return this;
	}

	public Dictionary nounPrefixes(WordList nounPrefixes) {
		setNounPrefixes(nounPrefixes);
		return this;
	}

	public Dictionary nouns(WordList nouns) {
		setNouns(nouns);
		return this;
	}

	public Dictionary verbSuffixes(WordList verbSuffixes) {
		setVerbSuffixes(verbSuffixes);
		return this;
	}

	public Dictionary verbPrefixes(WordList verbPrefixes) {
		setVerbPrefixes(verbPrefixes);
		return this;
	}

	public Dictionary verbs(WordList verbs) {
		setVerbs(verbs);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Dictionary)) {
			return false;
		}
		Dictionary dictionary = (Dictionary) o;
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
