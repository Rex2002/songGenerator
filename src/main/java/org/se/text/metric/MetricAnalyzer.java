package org.se.text.metric;

import java.util.*;

public class MetricAnalyzer {
	public static int metricsGet(String content, List<String> terms) {
		// Find Average length for sentences and hyphen in order to determine text speed
		int averageH = averageHyphen(terms);
		int averageS = averageSentence(content);
		int bpm = 0;

		/*
		 * Define bpm for several sentence/hyphen length combinations
		 * 1-9 Words is short sentence
		 * 10-18 words is average long sentence
		 * 18+ words is long sentence
		 * Hyphen
		 * 1 hyphen is short word
		 * 2/3 hyphen is averae word
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
			bpm = 180;
		}
		if (averageH == 2 || averageH == 3 && averageS <= 9) {
			bpm = 160;
		}
		if (averageH == 2 || averageH == 3 && averageS >= 9 && averageS <= 18) {
			bpm = 140;
		}
		if (averageH == 2 || averageH == 3 && averageS >= 19) {
			bpm = 120;
		}
		if (averageH >= 4 && averageS <= 9) {
			bpm = 100;
		}
		if (averageH >= 4 && averageS >= 9 && averageS <= 18) {
			bpm = 80;
		}
		if (averageH >= 4 && averageS >= 19) {
			bpm = 60;
		}
		return bpm;
	}

	public static int averageHyphen(List<String> terms) {
		int totalHyphens = 0;
		for (String t : terms) {
			totalHyphens += Hyphenizer.CountSyllabes(t);
		}

		int averageH = totalHyphens / terms.size();
		return averageH;
	}

	public static int averageSentence(String content) {
		String c = content;
		int[] termAndSentences = WordCounter.countWords(c);
		int wordsTotal = termAndSentences[0];
		int cTcSTotal = termAndSentences[1];
		int sentenceAverage = wordsTotal / cTcSTotal;
		return sentenceAverage;
	}
}
