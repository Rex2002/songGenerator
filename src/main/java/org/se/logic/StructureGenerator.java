package org.se.logic;

import org.se.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StructureGenerator {
    private static Random ran = new Random();
    private static Structure structure;
    private static Map<String, Object> settings;
    private static Map<String, Integer> metrics;


    public static void main(String[] args) {
        //these are test values that will eventually be passed by UI and TextAnalyzer
        settings = Map.of("genre", Genre.POP, "nsfw", false, "tempo", 120);
        metrics = Map.of("tempo", 70);

        Config.loadConfig();

        structure = Config.getStructures().get(ran.nextInt(Config.getStructures().size()));
        structure.setGenre((Genre) settings.get("genre"));
        structure.setKey(new MusicalKey());
        if (settings.get("tempo") != null){
            structure.setTempo((int) settings.get("tempo"));
        } else {
            structure.setTempo(metrics.get("tempo"));
        }

        for (String key: structure.getParts().keySet()) {
            Part part = structure.getParts().get(key);

            Variation variation;
            if (part.getRandomizationLevel()==0){
                variation = new Variation(structure.getParts().get(structure.getBasePartKey()).getChords(), part.getReqInsts(), part.getOptInsts());
            } else if (part.getRandomizationLevel() == 1) {
                List<Chord> reqChords = new ArrayList<>();
                reqChords.add(structure.getParts().get(structure.getBasePartKey()).getChords().get(0));
                reqChords.add(structure.getParts().get(structure.getBasePartKey()).getChords().get(1));
                //TODO pick two most important chords instead of first two
                variation = new Variation(reqChords, part.getReqInsts(), part.getOptInsts());
            } else {
                variation = new Variation(new ArrayList<>(), part.getReqInsts(), part.getOptInsts());
            }
            //hatten wir schon ne Lösung wie wir herausfinden was der basePart ist?
            //Quick Fix: zusätzliche basePart property im Structure Template, die den key vom basePart enthält. Damit wäre isBasePart obsolet
            part.fillPart(structure.getParts().get(structure.getBasePartKey()), structure.getKey(),variation);
        }
    }
}
