package org.se.model;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Benjamin Frahm
 */

public class Chord {
    private final int baseNote;
    private final ArrayList<Integer> chordModifier;
    public static HashMap<String, ArrayList<Integer>> chordModifiers;

    public Chord(int baseNote, String chordMod){
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

    public ArrayList<Integer> getChord(){
        ArrayList<Integer> k = new ArrayList<>();
        for (int modifier: chordModifier) {
            k.add(baseNote + modifier);
        }
        return k;

    }

    public static void setChordModifiers(HashMap<String, ArrayList<Integer>> chordModifiers) {
        Chord.chordModifiers = chordModifiers;
    }

    public static HashMap<String, ArrayList<Integer>> getChordModifiers() {
        return chordModifiers;
    }
}
