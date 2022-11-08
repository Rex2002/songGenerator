package org.se;

public class Word {
	String word;
	Integer[] syllables = {0}; // List of indices
	WordType type = WordType.Other;



	public Word(String s) {
		word = s;
	}

	public Lemma lemmatize() {
		return new Lemma("Test");
	}

	public Boolean isNSFW() {
		return false;
	}
}
