package org.se.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Chord {
    private int baseNote;
    private ArrayList<Integer> chordModifier;
    public static HashMap<String, ArrayList<Integer>> chordModifiers;
    static {
        /*chordModifiers.put("maj",new int[]{0,4,7});
        chordModifiers.put("m",new int[]{0,3,7});
        chordModifiers.put("maj7",new int[]{0,4,7,11});
        chordModifiers.put("m7",new int[]{0,3,7,11});
        chordModifiers.put("dim",new int[]{0,3,6});
        chordModifiers.put("add9",new int[]{0,4,7, 14}); */

        //below code is not tested, not sure whether relative path works that way
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
