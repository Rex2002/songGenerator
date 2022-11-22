package org.se.Text.Analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Val Richter
 */
public class Analyzer {
	public static TermCollection analyze(Path filepath) throws IOException {
		String text = Analyzer.readFile(filepath);
		ArrayList<ArrayList<String>> sentences = Analyzer.preprocess(text);
		ArrayList<ArrayList<Tag>> tags = Analyzer.tag(sentences);
		return Analyzer.buildTerms(tags);
	}

	public static String readFile(Path filepath) throws IOException {
		return Files.readString(filepath);
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

		if (currentWord != "")
			currentSentence.add(currentWord);
		if (!currentSentence.isEmpty())
			sentences.add(currentSentence);
		return sentences;
	}

	static int capitalizedCount(String str) {
		int count = 0;
		for (char c : str.toCharArray()) {
			if (Character.isUpperCase(c))
				count++;
		}
		return count;
	}

	static boolean hasSuffix(String str, String[] suffixes) {
		for (String p : suffixes) {
			if (str.endsWith(p))
				return true;
		}
		return false;
	}

	static boolean hasPrefix(String str, String[] prefixes) {
		for (String p : prefixes) {
			if (str.startsWith(p))
				return true;
		}
		return false;
	}

	static ArrayList<ArrayList<Tag>> tag(ArrayList<ArrayList<String>> sentences) {
		ArrayList<ArrayList<Tag>> tags = new ArrayList<ArrayList<Tag>>();
		String[] nounSuffixes = { "ung", "heit", "keit" };
		for (ArrayList<String> sentence : sentences) {
			ArrayList<Tag> currentTags = new ArrayList<Tag>();
			for (int i = 0; i < sentence.size(); i++) {
				String word = sentence.get(i);
				TagType type;
				if (i != 0 && Analyzer.capitalizedCount(word) == 1)
					type = TagType.Noun;
				else if (Analyzer.hasSuffix(word, nounSuffixes))
					type = TagType.Noun;
				// TODO: Add dictionary lookup
				else
					type = TagType.Other;

				currentTags.add(new Tag(word, type));
			}
			tags.add(currentTags);
		}
		return tags;
	}

	static TermCollection buildTerms(ArrayList<ArrayList<Tag>> tags) {
		HashMap<String, TermVariations> variationsMap = new HashMap<String, TermVariations>();

		for (ArrayList<Tag> sentenceTags : tags) {
			for (Tag t : sentenceTags) {
				if (t.is(TagType.Noun)) {
					Term term = new Term(t.word);
					if (variationsMap.containsKey(term.lemma)) {
						variationsMap.get(term.lemma).add(term);
					} else {
						variationsMap.put(term.lemma, new TermVariations(term));
					}
				}
			}
		}
		return new TermCollection(variationsMap);
	}

}
