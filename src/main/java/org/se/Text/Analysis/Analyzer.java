package org.se.Text.Analysis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.se.Text.Analysis.dict.Dict;

/**
 * @author Val Richter
 */
public class Analyzer {
	public static TermCollection analyze(Path filepath) throws IOException {
		Dict dict = new Dict(Path.of("", "./src/main/resources/dictionary"));
		String text = Analyzer.readFile(filepath);
		ArrayList<ArrayList<String>> sentences = Analyzer.preprocess(text);
		ArrayList<ArrayList<Tag>> tags = Analyzer.tag(sentences, dict);
		return Analyzer.buildTerms(tags, dict);
	}

	public static String readFile(Path filepath) throws IOException {
		return Files.readString(filepath, StandardCharsets.ISO_8859_1);
	}

	static ArrayList<ArrayList<String>> preprocess(String text) {
		// I'm sure there must be a better way to do this
		// Maybe there's something you could do with streams to make this more readable
		// but Java certainly doesn't make it easy to do any of this shit ffs
		// I hate Java so fucking much, I'd rather be using C at this point
		// aaaaaaaaaaaarrrrrgggggghhhhhhhhh
		String wordSplitter = "-_";
		Boolean splitLastWord = false;
		String sentenceEnds = ".!?";
		String otherPunctuation = ",;:-";
		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		ArrayList<String> currentSentence = new ArrayList<String>();
		char[] chars = text.toCharArray();
		String currentWord = "";

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			// ignore whitespace
			if (Character.isWhitespace(c)) {
				if (currentWord != "" && !splitLastWord) {
					currentSentence.add(currentWord);
					currentWord = "";
				}
			}
			// End sentence at specific punctuation (e.g. at .!?)
			else if (sentenceEnds.indexOf(c) != -1) {
				if (currentWord != "") {
					currentSentence.add(currentWord);
					currentWord = "";
				}
				splitLastWord = false;
				ArrayList<String> copiedSentence = new ArrayList<String>(currentSentence);
				sentences.add(copiedSentence);
				currentSentence.clear();
			}
			// Current word gets split
			else if (currentWord != "" && !splitLastWord && wordSplitter.indexOf(c) != -1) {
				splitLastWord = true;
			}
			// punctuation that doesn't end a sentence
			else if (otherPunctuation.indexOf(c) != -1) {
				if (currentWord != "") {
					currentSentence.add(currentWord);
					currentWord = "";
				}
				splitLastWord = false;
			}
			// otherwise we just have another character for the word
			else {
				currentWord += c;
				splitLastWord = false;
			}
		}

		if (currentWord != "") {
			currentSentence.add(currentWord);
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

	static ArrayList<ArrayList<Tag>> tag(ArrayList<ArrayList<String>> sentences, Dict dict) {
		ArrayList<ArrayList<Tag>> tags = new ArrayList<ArrayList<Tag>>();

		for (ArrayList<String> sentence : sentences) {
			ArrayList<Tag> currentTags = new ArrayList<Tag>();

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

	static TermCollection buildTerms(ArrayList<ArrayList<Tag>> tags, Dict dict) {
		Map<String, TermVariations> nounVariations = new HashMap<String, TermVariations>();
		Map<String, TermVariations> verbVariations = new HashMap<String, TermVariations>();

		for (ArrayList<Tag> sentenceTags : tags) {
			for (Tag t : sentenceTags) {
				if (!t.is(TagType.Other)) {
					Optional<NounTerm> term = dict.buildTerm(t);
					if (term.isPresent()) {
						// System.out.println(term.get());

						// TODO: There must be better syntax for this
						// maybe something similar to Rust's match syntax?
						Map<String, TermVariations> tmp;
						if (t.is(TagType.Noun)) {
							tmp = nounVariations;
						} else {
							tmp = verbVariations;
						}

						if (tmp.containsKey(term.get().radix)) {
							tmp.get(term.get().radix).add(term.get());
						} else {
							tmp.put(term.get().radix, new TermVariations(term.get()));
						}
					}
				}
			}
		}
		return new TermCollection(nounVariations, verbVariations);
	}

}
