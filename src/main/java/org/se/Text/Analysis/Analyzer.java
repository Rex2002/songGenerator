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
		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
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
