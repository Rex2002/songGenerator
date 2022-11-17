package org.se.Text.Analysis;

import java.util.ArrayList;

public class TermCollection {
	public ArrayList<TermVariations> terms;

	public TermCollection() {
		this.terms = new ArrayList<TermVariations>();
	}

	public TermCollection(ArrayList<TermVariations> terms) {
		this.terms = terms;
	}

	public void add(TermVariations variations) {
		terms.add(variations);
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
