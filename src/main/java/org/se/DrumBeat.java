package org.se;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;


public class DrumBeat {
    public static HashMap<String, Integer> drumPrograms;
    public static ArrayList drumBeats;

    private LinkedHashMap beat;
    private LinkedHashMap instruments;

    static{
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        try {
            drumPrograms = mapper.readValue(new File("./src/main/resources/drum_prog_no.yml"), HashMap.class);

            drumBeats = mapper.readValue(new File("./src/main/resources/drum_patterns.yml"), ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String t: drumPrograms.keySet() ) {
            System.out.println(t);
        }

    }

    public DrumBeat(int beatNo){
        System.out.println(drumBeats.toString());
        beat = (LinkedHashMap) drumBeats.get(beatNo);
        instruments = (LinkedHashMap) (beat.get("instruments"));
    }

    public HashMap<Integer, ArrayList> getContent(){
        HashMap<Integer, ArrayList> content = new HashMap<>();
        for(Object o : instruments.keySet()){
            content.put(drumPrograms.get(o.toString()), (ArrayList) instruments.get(o));
        }
        return content;
    }
}
