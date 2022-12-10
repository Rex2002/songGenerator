package org.se;

import org.se.music.logic.Config;
import org.se.music.logic.StructureGenerator;
import org.se.music.model.Genre;
import org.se.text.analysis.Analyzer;
import org.se.text.analysis.FileReader;
import org.se.text.analysis.TermCollection;
import org.se.text.analysis.dict.Dict;
import org.se.text.metric.MetricAnalyzer;
import java.io.IOException;
import java.util.Map;

/**
 * @author Malte Richert
 * @author Benjamin Frahm
 */

public class Main {
	public static void main(String[] args) throws IOException {
		Dict dictionary = Dict.getDefault();

		// these are test values that will eventually be passed by UI and TextAnalyzer
		String filepath = args.length > 0 ? args[0] : "test.txt";
		Settings settings = new Settings(filepath, Genre.POP, 12);
		Config.loadConfig(settings.getGenre());

		String content = FileReader.main(settings.getFilepath());
		TermCollection terms = Analyzer.analyze(content, dictionary);
		int metrics = MetricAnalyzer.metricsGet(content, terms);
		StructureGenerator.generateStructure(settings, Map.of("tempo", metrics), terms);
	}
}

/**
 * Map<String, Object> settings = Map.of("genre", Genre.POP, "nsfw", false, "tempo", 120);
 * Map<String, Integer> metrics = Map.of("tempo", 70);
 * StructureGenerator.generateStructure(settings, metrics);
 */