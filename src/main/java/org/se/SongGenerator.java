package org.se;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

public class SongGenerator {
	Settings settings;


	public String loadFile(Path filepath) throws IOException {
		return Files.readString(filepath);
	}

	public Term[] analyzeText(String text) {
		HashMap<String, Integer> BoW = new HashMap<String, Integer>();
		String punctuation = ".,;!?:";
		Stream.of(Stream.of(text.split("")).filter(s -> !punctuation.contains(s)).reduce((a, b) -> a + b).get().split("\\s+")).forEach(word -> {
			word = word.toLowerCase();
			if (BoW.keySet().contains(word)) {
				BoW.put(word, BoW.get(word) + 1);
			} else {
				BoW.put(word, 1);
			}
		});

		Term terms[] = {new Term()};
		return terms;
	}
}
