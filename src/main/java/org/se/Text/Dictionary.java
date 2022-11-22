package org.se.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Dictionary {
	WordList suffixes;
	WordList prefixes;
	WordList nouns;

	public Dictionary(WordList suffixes, WordList prefixes, WordList nouns) {
		this.suffixes = suffixes;
		this.prefixes = prefixes;
		this.nouns = nouns;
	}

	public boolean isSuffix(String s) {
		return suffixes.has(s);
	}

	public boolean isPrefix(String s) {
		return prefixes.has(s);
	}

	public boolean isNoun(String s) {
		return nouns.has(s);
	}

	public WordList loadFile(Path path, String delimiter) throws IOException {
		String[] words = Files.readString(path).split(delimiter);
		WordList list = new WordList();
		list.insertAll(words);
		return list;
	}

	public void loadSuffixes(Path path, String delimiter) throws IOException {
		suffixes = loadFile(path, delimiter);
	}

	public void loadPrefixes(Path path, String delimiter) throws IOException {
		prefixes = loadFile(path, delimiter);
	}

	public void loadNouns(Path path, String delimiter) throws IOException {
		nouns = loadFile(path, delimiter);
	}

	public WordList getSuffixes() {
		return this.suffixes;
	}

	public void setSuffixes(WordList suffixes) {
		this.suffixes = suffixes;
	}

	public WordList getPrefixes() {
		return this.prefixes;
	}

	public void setPrefixes(WordList prefixes) {
		this.prefixes = prefixes;
	}

	public WordList getNouns() {
		return this.nouns;
	}

	public void setNouns(WordList nouns) {
		this.nouns = nouns;
	}

	public Dictionary suffixes(WordList suffixes) {
		setSuffixes(suffixes);
		return this;
	}

	public Dictionary prefixes(WordList prefixes) {
		setPrefixes(prefixes);
		return this;
	}

	public Dictionary nouns(WordList nouns) {
		setNouns(nouns);
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
		return Objects.equals(suffixes, dictionary.suffixes) && Objects.equals(prefixes, dictionary.prefixes) && Objects.equals(nouns, dictionary.nouns);
	}

	@Override
	public int hashCode() {
		return Objects.hash(suffixes, prefixes, nouns);
	}

	@Override
	public String toString() {
		return "{" +
			" suffixes='" + getSuffixes() + "'" +
			", prefixes='" + getPrefixes() + "'" +
			", nouns='" + getNouns() + "'" +
			"}";
	}

}
