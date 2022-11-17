package org.se.Text.Analysis;

import java.util.*;

public class TermCollection {
	public HashMap<String, TermVariations> terms;

	public TermCollection() {
		this.terms = new HashMap<String, TermVariations>();
	}

	public TermCollection(ArrayList<TermVariations> terms) {
		this.terms = new HashMap<String, TermVariations>();
		for (TermVariations term : terms) this.terms.put(term.getLemma(), term);
	}

	public TermCollection(HashMap<String, TermVariations> map) {
		this.terms = map;
	}

	public HashMap<String,TermVariations> getTerms() {
		return this.terms;
	}

	public void setTerms(HashMap<String,TermVariations> terms) {
		this.terms = terms;
	}

	public TermCollection terms(HashMap<String,TermVariations> terms) {
		setTerms(terms);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof TermCollection)) {
			return false;
		}
		TermCollection termCollection = (TermCollection) o;
		return Objects.equals(terms, termCollection.terms);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(terms);
	}

	@Override
	public String toString() {
		return "{" +
			" terms='" + getTerms() + "'" +
			"}";
	}


	public void add(TermVariations variations) {
		if (has(variations)) {
			this.terms.get(variations.getLemma()).add(variations);
		} else {
			terms.put(variations.getLemma(), variations);
		}
	}

	public void add(Term t) {
		TermVariations v = new TermVariations(t);
		if (has(v)) {
			terms.get(v.getLemma()).add(t);
		} else {
			terms.put(v.getLemma(), v);
		}
	}

	public Boolean has(TermVariations variations) {
		return terms.containsKey(variations.getLemma());
	}

	public Boolean has(Term t) {
		return terms.containsKey(t.getLemma());
	}

	// public ArrayList<TermVariations> query(@Nullable GrammaticalCase grammaticalCase, @Nullable Gender gender, @Nullable Boolean isPlural, @Nullable Integer syllableMin, @Nullable Integer syllableMax) {}

	// public ArrayList<TermVariations> queryBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
	// 	return TermCollection.queryBySyllableRange(minSyllableAmount, maxSyllableAmount, terms);
	// }

	// public ArrayList<TermVariations> queryBySyllableAmount(Integer syllableAmount) {
	// 	return TermCollection.queryBySyllableAmount(syllableAmount, terms);
	// }

	// public ArrayList<TermVariations> queryByGrammaticalCase(GrammaticalCase grammaticalCase) {
	// 	return TermCollection.queryByGrammaticalCase(grammaticalCase, terms);
	// }

	// public ArrayList<TermVariations> queryByPlural(Boolean onlyPluralTerms) {
	// 	return TermCollection.queryByPlural(onlyPluralTerms, terms);
	// }

	// public ArrayList<TermVariations> queryByGender(Gender gender) {
	// 	return TermCollection.queryByGender(gender, terms);
	// }

	// public ArrayList<TermVariations> mostCommonTerms() {
	// 	return TermCollection.mostCommonTerms(terms);
	// }

	// public Term getRandomTerm() {
	// 	return TermCollection.getRandomTerm(terms);
	// }


	// public static ArrayList<TermVariations> queryBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount, ArrayList<TermVariations> terms) {}

	// public static ArrayList<TermVariations> queryBySyllableAmount(Integer syllableAmount, ArrayList<TermVariations> terms) {}

	// public static ArrayList<TermVariations> queryByGrammaticalCase(GrammaticalCase grammaticalCase, ArrayList<TermVariations> terms) {}

	// public static ArrayList<TermVariations> queryByPlural(Boolean onlyPluralTerms, ArrayList<TermVariations> terms) {}

	// public static ArrayList<TermVariations> queryByGender(Gender gender, ArrayList<TermVariations> terms) {}


	// public static ArrayList<TermVariations> mostCommonTerms(ArrayList<TermVariations> terms) {}

	// public static Term getRandomTerm(ArrayList<TermVariations> terms) {}
}
