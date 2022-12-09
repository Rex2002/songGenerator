package org.se.text.analysis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.se.text.analysis.dict.*;
import org.se.text.analysis.model.*;

/**
 * @author Val Richter
 */
public class Analyzer {
	private Analyzer() {
	}

	public static TermCollection analyze(String text, Dict dict) {
		List<Sentence> sentences = Analyzer.preprocess(text);
		List<List<Tag>> tags = Analyzer.tag(sentences, dict);
		return Analyzer.buildTerms(tags, dict);
	}

	public static String readFile(Path filepath) throws IOException {
		return Files.readString(filepath, StandardCharsets.UTF_8);
	}

	static List<Sentence> preprocess(String text) {
		// I'm sure there must be a better way to do this
		// Maybe there's something you could do with streams to make this more readable
		// but Java certainly doesn't make it easy to do any of this shit ffs
		// I hate Java so fucking much, I'd rather be using C at this point
		// aaaaaaaaaaaarrrrrgggggghhhhhhhhh
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
		}

		if (!currentWord.isEmpty()) {
			currentSentence.add(currentWord.toString());
		}
		if (!currentSentence.isEmpty()) {
			sentences.add(currentSentence);
		}
		return sentences;
	}

	static int capitalizedCount(String str) {
		int count = 0;
		for (char c : str.toCharArray()) {
			if (Character.isUpperCase(c)) {
				count++;
			}
		}
		return count;
	}

	static List<List<Tag>> tag(List<Sentence> sentences, Dict dict) {
		List<List<Tag>> tags = new ArrayList<>();

		for (Sentence sentence : sentences) {
			List<Tag> currentTags = new ArrayList<>();

			for (int i = 0; i < sentence.size(); i++) {
				String word = sentence.get(i);
				Tag tag;

				// If it's not the first word in the sentence and is capitalized, it's a noun
				// this check can only be trusted on, if not every word is capitalized
				if (i != 0 && Analyzer.capitalizedCount(word) == 1) {
					tag = new Tag(word, TagType.Noun);
				} // Otherwise, let the dictionary tag the word
				else {
					tag = dict.tagWord(word);
				}

				currentTags.add(tag);
			}
			tags.add(currentTags);
		}
		return tags;
	}

	static TermCollection buildTerms(List<List<Tag>> tags, Dict dict) {
		Map<String, TermVariations<NounTerm>> nounVariations = new HashMap<>();
		Map<String, TermVariations<VerbTerm>> verbVariations = new HashMap<>();

		for (List<Tag> sentenceTags : tags) {
			for (Tag t : sentenceTags) {
				if (!t.is(TagType.Other)) {

					if (t.is(TagType.Noun)) {
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

			}
		}
		return new TermCollection(dict, nounVariations, verbVariations);
	}

}
