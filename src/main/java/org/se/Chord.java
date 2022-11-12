package org.se;

import java.util.HashMap;
import java.util.Map;

public class Chord {
    private int baseNote;
    private int[] chordModifier;
    public static final Map<String, int[]> chordModifiers = new HashMap<>();
    static {
        chordModifiers.put("maj",new int[]{0,4,7});
        chordModifiers.put("m",new int[]{0,3,7});
        chordModifiers.put("maj7",new int[]{0,4,7,11});
        chordModifiers.put("m7",new int[]{0,3,7,11});
        chordModifiers.put("dim",new int[]{0,3,6});
        chordModifiers.put("add9",new int[]{0,4,7, 14});

    }

    public Chord(int baseNote, String chordModifier){
        if (0 <= baseNote && baseNote < 128){
            this.baseNote = baseNote;
        }
        else {
            System.out.println("invalid baseNote, change to 60 (C)");
            this.baseNote = 60;
        }
        if (chordModifiers.containsKey(chordModifier)){
            this.chordModifier = chordModifiers.get(chordModifier);
        }
        else{
            this.chordModifier = chordModifiers.get("maj");
        }
    }

    public int getBaseNote() {
        return baseNote;
    }

    public int[] getChordModifier(){
        return chordModifier;
    }
}
