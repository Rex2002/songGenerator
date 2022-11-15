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

	static ArrayList<ArrayList<Tag>> tag(ArrayList<ArrayList<String>> sentences) {
		ArrayList<ArrayList<Tag>> tags = new ArrayList<ArrayList<Tag>>();
		return tags;
	}

	static TermCollection buildTerms(ArrayList<ArrayList<Tag>> tags) {
		return new TermCollection();
	}

}
