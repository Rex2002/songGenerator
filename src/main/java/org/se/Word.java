package org.se;

public class Word {
	String word;
	Integer[] syllables; // List of indices
	WordType type;

	public Word(String s) {

	}

	public String lemmatize() {
		return "Test";
	}

	public Boolean isNSFW() {
		return false;
	}
}
