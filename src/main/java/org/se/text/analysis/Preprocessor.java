package org.se.text.analysis;

import java.util.*;

import org.se.PartialProgressTask;
import org.se.text.analysis.model.Sentence;

public class Preprocessor extends PartialProgressTask<List<Sentence>> {
	protected String text;

	public Preprocessor(String text) {
		super(1);
		this.text = text;
	}

	@Override
	protected List<Sentence> call() throws Exception {
		String wordSplitter = "-_";
		boolean splitLastWord = false;
		String sentenceEnds = ".!?";
		String otherPunctuation = ",;:-()[]{}";
		List<Sentence> sentences = new ArrayList<>();
		Sentence currentSentence = new Sentence();
		char[] chars = text.toCharArray();
		StringBuilder currentWord = new StringBuilder();

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			// ignore whitespace
			if (Character.isWhitespace(c)) {
				if (!currentWord.isEmpty() && !splitLastWord) {
					currentSentence.add(currentWord.toString());
					currentWord.delete(0, currentWord.length());
				}
			}
			// End sentence at specific punctuation (e.g. at .!?)
			else if (sentenceEnds.indexOf(c) != -1) {
				if (!currentWord.isEmpty()) {
					currentSentence.add(currentWord.toString());
					currentWord.delete(0, currentWord.length());
				}
				splitLastWord = false;
				Sentence copiedSentence = new Sentence(currentSentence);
				sentences.add(copiedSentence);
				currentSentence.clear();
			}
			// Current word gets split
			else if (!currentWord.isEmpty() && !splitLastWord && wordSplitter.indexOf(c) != -1) {
				splitLastWord = true;
			}
			// punctuation that doesn't end a sentence
			else if (otherPunctuation.indexOf(c) != -1) {
				if (!currentWord.isEmpty()) {
					currentSentence.add(currentWord.toString());
					currentWord.delete(0, currentWord.length());
				}
				splitLastWord = false;
			}
			// otherwise we just have another character for the word
			else {
				currentWord.append(c);
				splitLastWord = false;
			}

			updateProgress((double) i / chars.length);
		}

		if (!currentWord.isEmpty()) {
			currentSentence.add(currentWord.toString());
		}
		if (!currentSentence.isEmpty()) {
			sentences.add(currentSentence);
		}
		return sentences;
	}
}
