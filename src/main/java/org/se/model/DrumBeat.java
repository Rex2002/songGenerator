package org.se.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class DrumBeat extends MidiPlayable {
    public static HashMap<String, Integer> drumPrograms;
    public static List<Beat> drumBeats;


    static{
        YAMLFactory yaml = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yaml);

        try {
            YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/beat_templates_pop.yml"));
            drumBeats = mapper.readValues(yamlParser, Beat.class).readAll();
            drumPrograms = mapper.readValue(new File("./src/main/resources/drum_prog_no.yml"), HashMap.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DrumBeat(int beatNo, int bar, int trackNo){
        this(beatNo, bar, -1, trackNo);
    }
    public DrumBeat(int beatNo, int bar, int fill, int trackNo){
        super(trackNo, bar);        // 118 is drumset instrument
        Beat beat = drumBeats.get(beatNo);

        HashMap<String, ArrayList<ArrayList<Integer>>> beatShape;
        if (fill == 0){
            beatShape = beat.smallFill;
        }
        else if (fill == 1){
            beatShape = beat.bigFill;
        }
        else{
            beatShape = beat.mainPattern;
        }
        HashMap<Integer, ArrayList<ArrayList<Integer>>> c = new HashMap<>();
        for(String instr : beatShape.keySet()){
            c.put(drumPrograms.get(instr), beatShape.get(instr));
        }
        setContent(c);
    }
}
