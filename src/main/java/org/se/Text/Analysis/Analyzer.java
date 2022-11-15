package org.se.Text.Analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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
		// I hate Java so fucking much, I'd rather be using C at this point aaaaaaaaaaaarrrrrgggggghhhhhhhhh
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
			if (Character.isWhitespace(c)) {
				if (currentWord != "" && !splitLastWord) {
					currentSentence.add(currentWord);
					currentWord = "";
				}
			}
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
			else if (currentWord != "" && wordSplitter.indexOf(c) != -1) {
				splitLastWord = true;
			}
			else if (otherPunctuation.indexOf(c) != -1) {
				if (currentWord != "") {
					currentSentence.add(currentWord);
					currentWord = "";
				}
				splitLastWord = false;
			} else {
				currentWord += c;
				splitLastWord = false;
			}
		}

		if (currentWord != "") currentSentence.add(currentWord);
		if (!currentSentence.isEmpty()) sentences.add(currentSentence);
		return sentences;
	}

	static int caitalizedCount(String str) {
		int count = 0;
		for (char c : str.toCharArray()) {
			if (Character.isUpperCase(c)) count++;
		}
		return count;
	}

	static boolean hasSuffix(String str, String[] suffixes) {
		for (String p : suffixes) {
			if (str.endsWith(p)) return true;
		}
		return false;
	}

	static boolean hasPrefix(String str, String[] prefixes) {
		for (String p : prefixes) {
			if (str.startsWith(p)) return true;
		}
		return false;
	}

	static ArrayList<ArrayList<Tag>> tag(ArrayList<ArrayList<String>> sentences) {
		ArrayList<ArrayList<Tag>> tags = new ArrayList<ArrayList<Tag>>();
		String[] nounSuffixes = {"ung", "heit", "keit"};
		for (ArrayList<String> sentence : sentences) {
			ArrayList<Tag> currentTags = new ArrayList<Tag>();
			for (int i = 0; i < sentence.size(); i++) {
				String word = sentence.get(i);
				Tag tag;
				if (i != 0 && Analyzer.caitalizedCount(word) == 1) tag = Tag.Noun;
				else if (Analyzer.hasSuffix(word, nounSuffixes)) tag = Tag.Noun;
				// TODO: Add dictionary lookup
				else tag = Tag.Other;
				currentTags.add(tag);
			}
			tags.add(currentTags);
		}
		return tags;
	}

	static TermCollection buildTerms(ArrayList<ArrayList<Tag>> tags) {
		TermCollection TC = new TermCollection();
		// TODO
		return TC;
	}

}
