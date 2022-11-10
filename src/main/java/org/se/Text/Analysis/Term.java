package org.se.Text.Analysis;

public class Term {
	public Integer[] syllables;
	public String[] words;
	public Boolean isPlural;
	public GrammaticalCase grammaticalCase;
	public Gender gender;

	public Term() {
		// TODO
	}

	public String toString() {
		return String.join(" ", words);
	}
}
