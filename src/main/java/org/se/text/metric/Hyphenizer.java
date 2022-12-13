package org.se.text.metric;

/**
 * @author Jakob Kautz
 */
public class Hyphenizer {
	private static final String VOWELS = "aeiouäöü";

	public static int countSyllables(String terms) {
		int count = 0;
		terms = terms.toLowerCase();

		for (int i = 0; i < terms.length(); i++) { // traversing till length of string
			if (terms.charAt(i) == '\"' || terms.charAt(i) == '\'' || terms.charAt(i) == '-' || terms.charAt(i) == ',' || terms.charAt(i) == ')'
					|| terms.charAt(i) == '(') {
				// if at any point, we encounter any such expression, we substring the string from start till that point and further.
				terms = terms.substring(0, i) + terms.substring(i + 1);
			}
		}

		boolean isPrevVowel = false;

		for (int j = 0; j < terms.length(); j++) {
			if (VOWELS.indexOf(terms.substring(j, j + 1)) != -1) {
				// checking if character is a vowel and if the last letter of the word is 'e' or not
				if (isVowel(terms.charAt(j)) && !((terms.charAt(j) == 'e') && (j == terms.length() - 1))) {
					if (!isPrevVowel) {
						count++;
						isPrevVowel = true;
					}
				} else {
					isPrevVowel = false;
				}
			} else {
				count++;
				break;
			}
		}
		return count;
	}

	public static boolean isVowel(char c) {
		return (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
	}
}
