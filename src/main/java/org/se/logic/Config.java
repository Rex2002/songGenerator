package org.se.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.se.model.Beat;
import org.se.model.Chord;
import org.se.model.Structure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    private static List<Structure> structures;
    private static List<List<List<Integer>>> chordProgressions;
    private static HashMap<String, Integer> instrumentMapping;

    public static void loadConfig(){
        YAMLFactory yaml = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yaml);

        //load beat templates into BeatContainer
        try {
            YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/beat_templates_pop.yml"));
            BeatContainer.setDrumBeats(mapper.readValues(yamlParser, Beat.class).readAll());
            BeatContainer.setDrumPrograms(mapper.readValue(new File("./src/main/resources/drum_prog_no.yml"), HashMap.class));
        } catch (IOException e) {
            System.out.println("Encountered exception while trying to read Beat template.");
            e.printStackTrace();
        }

        //load structure template
        try{
            YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/structure_templates_pop.yml"));
            structures = mapper.readValues(yamlParser, Structure.class).readAll();
        } catch (IOException e) {
            System.out.println("Encountered exception while trying to read Structure template.");
            e.printStackTrace();
        }

        //load chord modifiers into Chord class
        try {
            Chord.setChordModifiers(mapper.readValue(new File("./src/main/resources/chord_modifiers.yml"), HashMap.class));
        } catch (IOException e) {
            System.out.println("Encountered exception while trying to read Chord modifiers from config.");
            e.printStackTrace();
        }

        //load chord progressions
        try{
            chordProgressions = mapper.readValue(new File("./src/main/resources/chord_progressions_pop.yml"), ArrayList.class);
        } catch (IOException e) {
            System.out.println("Encountered exception while trying to read Chord progressions from template.");
            e.printStackTrace();
        }

        //load instrument mappings
        try{
            instrumentMapping = mapper.readValue(new File("./src/main/resources/instrument_mapping.yml"), HashMap.class);
        } catch (IOException e) {
            System.out.println("Encountered exception while trying to read Instrument mappings from config.");
            e.printStackTrace();
        }
    }

    public static List<Structure> getStructures() {
        return structures;
    }
    public static List<List<List<Integer>>> getChordProgressions() {
        return chordProgressions;
    }
    public static HashMap<String, Integer> getInstrumentMapping() {
        return instrumentMapping;
    }
}
