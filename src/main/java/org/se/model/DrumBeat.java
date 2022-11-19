package org.se.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class DrumBeat {
    public static HashMap<String, Integer> drumPrograms;
    public static List<Beat> drumBeats;

    private Beat beat;
    private HashMap<String, ArrayList<ArrayList<Integer>>> beatShape;


    static{
        YAMLFactory yaml = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yaml);

        try {
            YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/beat_templates_pop.yml"));
            drumBeats = mapper.readValues(yamlParser, Beat.class).readAll();
            drumPrograms = mapper.readValue(new File("./src/main/resources/drum_prog_no.yml"), HashMap.class);
            System.out.println(drumPrograms);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String t: drumPrograms.keySet() ) {
            System.out.println(t);
        }

    }

    public DrumBeat(int beatNo){
        beat = drumBeats.get(beatNo);
    }

    public void setFill(int bigSmall){
        // sets if the beat itself or its fill versions are selected. 0 is small fill, 1 is big fill
        if (bigSmall == 0){
            beatShape = beat.smallFill;
        }
        else if (bigSmall == 1){
            beatShape = beat.bigFill;
        }
        else{
            beatShape = beat.mainPattern;
        }
    }

    public HashMap<Integer, ArrayList<ArrayList<Integer>>> getContent(){
        if(beatShape == null){
            beatShape = beat.mainPattern;
        }
        HashMap<Integer, ArrayList<ArrayList<Integer>>> content = new HashMap<>();
        for(String instr : beatShape.keySet()){
            content.put(drumPrograms.get(instr), beatShape.get(instr));
        }
        // returns a hashmap, that has the drum instrument as key and another arraylist that contains playtime and duration in the bar of the drum
        return content;
    }
}
