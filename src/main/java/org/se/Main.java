package org.se;

import org.se.logic.Config;
import org.se.logic.StructureGenerator;
import org.se.model.Genre;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Config.loadConfig();
        //these are test values that will eventually be passed by UI and TextAnalyzer
        Map<String, Object> settings = Map.of("genre", Genre.POP, "nsfw", false, "tempo", 120);
        Map<String, Integer> metrics = Map.of("tempo", 70);
        StructureGenerator.generateStructure(settings, metrics);





    }
}