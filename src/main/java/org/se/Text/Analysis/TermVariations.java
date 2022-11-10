package org.se.Text.Analysis;

import java.util.ArrayList;

public class TermVariations {
	public ArrayList<Term> variations = new ArrayList<Term>();
	public Integer frequency;
	String lemma;

	// public ArrayList<Term> getVariations(@Nullable GrammaticalCase grammaticalCase, @Nullable Gender gender, @Nullable Boolean isPlural, @Nullable Integer syllableMin, @Nullable Integer syllableMax) {}

	// Gets the term of the specified variation if it's stored and otherwise creates it based on simple rules
	// Careful, created variations might be pretty bad
	// public Term createVariation(GrammaticalCase grammaticalCase, Boolean isPlural) {}

	public String getLemma() {
		return this.lemma;
	}
}
