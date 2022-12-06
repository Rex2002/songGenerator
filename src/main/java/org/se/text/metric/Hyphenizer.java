package org.se.text.metric;

public class Hyphenizer {
	public static int CountSyllabes(String terms) {
		int count = 0;
		terms = terms.toLowerCase();

		for (int i = 0; i < terms.length(); i++) { // traversing till length of string
			if (terms.charAt(i) == '\"' || terms.charAt(i) == '\'' || terms.charAt(i) == '-' || terms.charAt(i) == ',' || terms.charAt(i) == ')'
					|| terms.charAt(i) == '(') {
				// if at any point, we encounter any such expression, we substring the string from start till that point and further.
				terms = terms.substring(0, i) + terms.substring(i + 1, terms.length());
			}
		}

		boolean isPrevVowel = false;

		for (int j = 0; j < terms.length(); j++) {
			if (terms.contains("a") || terms.contains("e") || terms.contains("i") || terms.contains("o") || terms.contains("u")) {
				// checking if character is a vowel and if the last letter of the word is 'e' or not
				if (isVowel(terms.charAt(j)) && !((terms.charAt(j) == 'e') && (j == terms.length() - 1))) {
					if (isPrevVowel == false) {
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
		if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
			return true;
		} else {
			return false;
		}

	}
}
