package org.se.text.analysis;

import java.util.*;

import org.se.PartialProgressTask;
import org.se.text.analysis.dict.*;
import org.se.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class Analyzer extends PartialProgressTask<TermCollection> {
	private final String text;
	private final Dict dict;

	public Analyzer(String text, Dict dict) {
		super(3);
		this.text = text;
		this.dict = dict;
	}

	@Override
	protected TermCollection call() throws Exception {
		updateMessage("Preprocessing Text...");
		List<Sentence> sentences = preprocess();
		procedureDone();

		updateMessage("Tagging Words in Text...");
		List<Tag> tags = tag(sentences);
		procedureDone();

		updateMessage("Building Terms from Text...");
		TermCollection res = buildTerms(tags);
		procedureDone();
		return res;
	}

	private List<Sentence> preprocess() {
		String wordSplitter = "-_";
		boolean splitLastWord = false;
		String sentenceEnds = ".!?";
		String otherPunctuation = ",;:-";
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

	private int capitalizedCount(String str) {
		int count = 0;
		for (char c : str.toCharArray()) {
			if (Character.isUpperCase(c)) {
				count++;
			}
		}
		return count;
	}

	private boolean isCapitalized(String word) {
		return capitalizedCount(word) == 1 && Character.isUpperCase(word.charAt(0));
	}

	private List<Tag> tag(List<Sentence> sentences) {
		List<Tag> tags = new ArrayList<>();

		int sentencesAmount = sentences.size();
		for (int i = 0; i < sentencesAmount; i++) {
			Sentence sentence = sentences.get(i);

			double wordsAmount = sentence.size();
			for (int j = 0; j < wordsAmount; j++) {
				String word = sentence.get(j);
				Tag tag;

				// If it's not the first word in the sentence and is capitalized, it's a noun
				// this check can only be trusted on, if not every word is capitalized
				if (j != 0 && isCapitalized(word)) {
					tag = new Tag(word, TagType.NOUN);
				} // Otherwise, let the dictionary tag the word
				else {
					tag = dict.tagWord(word);
				}

				tags.add(tag);
				updateProgress((i + (j / wordsAmount)) / sentencesAmount);
			}
		}
		return tags;
	}

	private TermCollection buildTerms(List<Tag> tags) {
		Map<String, TermVariations<NounTerm>> nounVariations = new HashMap<>();
		Map<String, TermVariations<VerbTerm>> verbVariations = new HashMap<>();

		int tagsAmount = tags.size();
		for (int i = 0; i < tagsAmount; i++) {
			Tag t = tags.get(i);
			if (!t.is(TagType.OTHER)) {

				if (t.is(TagType.NOUN)) {
					Optional<NounTerm> term = dict.buildNounTerm(t);
					if (term.isPresent()) {
						if (nounVariations.containsKey(term.get().getRadix())) {
							nounVariations.get(term.get().getRadix()).add(term.get());
						} else {
							nounVariations.put(term.get().getRadix(), new TermVariations<>(term.get()));
						}
					}
				} else {
					Optional<VerbTerm> term = dict.buildVerbTerm(t);
					if (term.isPresent()) {
						if (verbVariations.containsKey(term.get().getRadix())) {
							verbVariations.get(term.get().getRadix()).add(term.get());
						} else {
							verbVariations.put(term.get().getRadix(), new TermVariations<>(term.get()));
						}
					}
				}
			}
			updateProgress((i / tagsAmount));
		}
		return new TermCollection(dict, nounVariations, verbVariations);
	}

}
