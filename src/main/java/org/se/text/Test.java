package org.se.text;

import java.io.IOException;
import java.util.*;
import org.se.music.logic.*;
import org.se.music.model.Genre;
import org.se.text.analysis.Analyzer;
import org.se.text.analysis.TermCollection;
import org.se.text.generation.SongTextGenerator;

public class Test {
	public static void main(String[] args) throws IOException {
		String filepath = args.length > 0 ? args[0] : "test.txt";
		Map<String, Object> settings = Map.of("genre", Genre.POP, "nsfw", false, "tempo", 120);
		Map<String, Integer> metrics = Map.of("tempo", 70);
		Config.loadConfig();
		Config.getStructures().get(0).setGenre(Genre.POP);

		TermCollection terms = Analyzer.analyze(filepath);
		StructureGenerator.generateStructure(settings, metrics);
		SongTextGenerator textGenerator = new SongTextGenerator();
		List<String[]> songText = textGenerator.generateSongText(Config.getStructures().get(0), terms);

		System.out.println("\n\n\nSongtext:\n");

		for (String[] strophe : songText) {
			for (String line : strophe) {
				System.out.println(line);
			}
			System.out.println("\n");
		}
	}
}
