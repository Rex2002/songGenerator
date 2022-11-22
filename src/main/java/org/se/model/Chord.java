package org.se.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Chord {
    private final int baseNote;
    private final ArrayList<Integer> chordModifier;
    public static HashMap<String, ArrayList<Integer>> chordModifiers;
    static {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            chordModifiers = mapper.readValue(new File("./src/main/resources/chord_modifiers.yml"), HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Chord(int baseNote, String chordMod
    ){
        if (0 <= baseNote && baseNote < 128){
            this.baseNote = baseNote;
        }
        else {
            System.out.println("invalid baseNote, change to 60 (C)");
            this.baseNote = 60;
        }

        if (chordModifiers.containsKey(chordMod)){
            this.chordModifier = chordModifiers.get(chordMod);
        }
        else{
            this.chordModifier = chordModifiers.get("maj");
        }
    }

    public int getBaseNote() {
        return baseNote;
    }

    public ArrayList<Integer> getChordModifier(){
        return chordModifier;
    }
}
