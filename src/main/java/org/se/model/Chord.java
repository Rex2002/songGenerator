package org.se.model;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Benjamin Frahm
 */

public class Chord {
    private final int rootNote;
    private final ArrayList<Integer> chordModifier;
    public static HashMap<String, ArrayList<Integer>> chordModifiers;

    public Chord(int rootNote, String chordMod){
        if (0 <= rootNote && rootNote < 128){
            this.rootNote = rootNote;
        }
        else {
            System.out.println("invalid rootNote, change to 60 (C)");
            this.rootNote = 60;
        }

        if (chordModifiers.containsKey(chordMod)){
            this.chordModifier = chordModifiers.get(chordMod);
        }
        else{
            this.chordModifier = chordModifiers.get("maj");
        }
    }

    public int getRootNote() {
        return rootNote;
    }

    public ArrayList<Integer> getChordModifier(){
        return chordModifier;
    }

    public ArrayList<Integer> getChord(){
        ArrayList<Integer> k = new ArrayList<>();
        for (int modifier: chordModifier) {
            k.add(rootNote + modifier);
        }
        return k;

    }

    public static void setChordModifiers(HashMap<String, ArrayList<Integer>> chordModifiers) {
        Chord.chordModifiers = chordModifiers;
    }

    public static HashMap<String, ArrayList<Integer>> getChordModifiers() {
        return chordModifiers;
    }

    @Override
    public String toString() {
        return "Chord{" +
                "baseNote=" + rootNote +
                ", chordModifier=" + chordModifier +
                '}';
    }
}
