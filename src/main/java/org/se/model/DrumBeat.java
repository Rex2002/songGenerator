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


    static{
        YAMLFactory yaml = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yaml);
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        try {
            YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/beat_templates_pop.yml"));
            drumBeats = mapper.readValues(yamlParser, Beat.class).readAll();
            drumPrograms = mapper.readValue(new File("./src/main/resources/drum_prog_no.yml"), HashMap.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String t: drumPrograms.keySet() ) {
            System.out.println(t);
        }

    }

    public DrumBeat(int beatNo){

    }

    public HashMap<Integer, ArrayList> getContent(){
        HashMap<Integer, ArrayList> content = new HashMap<>();
        // returns a hashmap, that has the drum instrument as key and another arraylist that contains playtime and duration in the bar of the drum
        return content;
    }
}
