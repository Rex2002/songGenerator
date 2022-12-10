package org.se.text.metric;

import org.se.text.analysis.TermCollection;

/**
 * @author Jakob Kautz
 */
public class MetricAnalyzer {
	static int totalHyphens = 0;

	public static int metricsGet(String content, TermCollection terms) {
		// Find average length for sentences and hyphen in order to determine text speed
		int averageH = averageHyphen(terms);
		int averageS = averageSentence(content);

		/*
		 * Define bpm for several sentence/hyphen length combinations
		 * 1-9 Words is short sentence
		 * 10-18 words is average long sentence
		 * 18+ words is long sentence
		 * Hyphen
		 * 1 hyphen is short word
		 * 2/3 hyphen is average word
		 * 4+ hyphen is long word
		 *
		 * bpm (hyphen sentence)
		 * 60bpm long long
		 * 80bpm long average
		 * 100 bpm long short
		 * 120 bpm average long
		 * 140 bpm average average
		 * 160 bpm average short
		 * 180 bpm short short
		 */
		if (averageH <= 1 && averageS <= 9) {
			return 180;
		}
		if ((averageH == 2 || averageH == 3) && averageS <= 9) {
			return 160;
		}
		if ((averageH == 2 || averageH == 3) && averageS <= 18) {
			return 140;
		}
		if (averageH == 2 || averageH == 3) {
			return 120;
		}
		if (averageH >= 4 && averageS <= 9) {
			return 100;
		}
		if (averageH >= 4 && averageS <= 18) {
			return 80;
		}
		if (averageH >= 4) {
			return 60;
		}
		return 120;
	}
	/*
	 * Berechnet durchschnittliche Silbenlänge
	 */
	public static int averageHyphen(TermCollection terms) {
		totalHyphens = 0;
		terms.flatIter(term -> totalHyphens += term.getSyllableAmount());

		return totalHyphens / terms.size();
	}

	/*
	 * Berechnet durchschnittliche Satzlänge
	 */
	public static int averageSentence(String content) {
		int[] termAndSentences = WordCounter.countWords(content);
		int wordsTotal = termAndSentences[0];
		int cTcSTotal = termAndSentences[1];
		return wordsTotal / cTcSTotal;
	}
}
