package org.se.text;

import java.io.IOException;
import java.util.*;
import org.se.music.logic.*;
import org.se.music.model.Genre;
import org.se.text.analysis.Analyzer;
import org.se.text.analysis.FileReader;
import org.se.text.analysis.TermCollection;
import org.se.text.analysis.dict.Dict;
import org.se.text.generation.SongTextGenerator;
import org.se.text.metric.MetricAnalyzer;

public class Test {
	public static void main(String[] args) throws IOException {
		String filepath = args.length > 0 ? args[0] : "test.txt";
		Map<String, Object> settings = Map.of("genre", Genre.POP, "nsfw", false, "tempo", 120);
		Dict dictionary = Dict.getDefault();

		Config.loadConfig();
		Config.getStructures().get(0).setGenre(Genre.POP);

		String content = FileReader.main(filepath);
		TermCollection terms = Analyzer.analyze(content, dictionary);
		int metrics = MetricAnalyzer.metricsGet(content, terms);
		StructureGenerator.generateStructure(settings, Map.of("tempo", metrics));
		SongTextGenerator textGenerator = new SongTextGenerator();
		List<String[]> songText = textGenerator.generateSongText(Config.getStructures().get(0), terms);

		System.out.println("\n\n\n");
		System.out.println("Songtext:");

		for (String[] strophe : songText) {
			for (String line : strophe) {
				System.out.println(line);
			}
			System.out.println("\n");
		}

		System.out.println("Metrics:");
		System.out.println(metrics);
	}
}
