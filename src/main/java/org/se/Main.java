package org.se;

import org.se.music.logic.Config;
import org.se.music.logic.StructureGenerator;
import org.se.music.model.Genre;

import java.util.Map;

/**
 * @author Malte Richert
 * @author Benjamin Frahm
 */

public class Main {
	public static void main(String[] args) {
		Config.loadConfig();
		// these are test values that will eventually be passed by UI and TextAnalyzer
		Map<String, Object> settings = Map.of("genre", Genre.BLUES, "nsfw", false, "tempo", 120);
		Map<String, Integer> metrics = Map.of("tempo", 70);
		StructureGenerator.generateStructure(settings, metrics);
	}
}