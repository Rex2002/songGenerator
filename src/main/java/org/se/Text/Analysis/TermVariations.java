package org.se.Text.Analysis;

import java.util.*;

public class TermVariations {
	public HashMap<Integer, Term> variations;
	public Integer frequency;
	String lemma;

	// public HashMap<Term> getVariations(@Nullable GrammaticalCase grammaticalCase, @Nullable Gender gender, @Nullable Boolean isPlural, @Nullable Integer syllableMin, @Nullable Integer syllableMax) {}

	// Gets the term of the specified variation if it's stored and otherwise creates it based on simple rules
	// Careful, created variations might be pretty bad
	// public Term createVariation(GrammaticalCase grammaticalCase, Boolean isPlural) {}

	public TermVariations() {
		this.variations = new HashMap<Integer, Term>();
		this.frequency = 0;
		this.lemma = "";
	}

	public TermVariations(Term term) {
		this.variations = new HashMap<Integer, Term>();
		this.variations.put(term.hashData(), term);
		this.frequency = 1;
		this.lemma = "";
	}

	public TermVariations(List<Term> terms) {
		this.variations = new HashMap<Integer, Term>();
		for (Term term : terms) {
			variations.put(term.hashData(), term);
		}
		this.frequency = terms.size();
		this.lemma = terms.get(0).lemma;
	}

	public String getLemma() {
		return this.lemma;
	}

	@Override
	public int hashCode() {
		return this.getLemma().hashCode();
	}

	public void add(Term term) {
		this.variations.put(term.hashData(), term);
	}

	public boolean hasType(GrammaticalCase grammaticalCase, Boolean isPlural) {
		int hash = Term.hashData(grammaticalCase, isPlural);
		return variations.containsKey(hash);
	}

	public Term getTerm(GrammaticalCase grammaticalCase, Boolean iPlural) {
		int hash = Term.hashData(grammaticalCase, iPlural);
		if (variations.containsKey(hash)) return this.variations.get(hash);
		// TODO
		Term newTerm = new Term(this.lemma);
		this.variations.put(hash, newTerm); // Should we have this here or not?
		return newTerm;
	}
}
