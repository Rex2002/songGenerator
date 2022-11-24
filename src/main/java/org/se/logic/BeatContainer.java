package org.se.logic;

import org.se.model.Beat;
import org.se.model.MidiPlayable;

import java.util.*;


public class BeatContainer extends MidiPlayable {
    public static HashMap<String, Integer> drumPrograms;
    public static List<Beat> drumBeats;

    public BeatContainer(int beatNo, int bar, int trackNo){
        this(beatNo, bar, -1, trackNo);
    }
    public BeatContainer(int beatNo, int bar, int fill, int trackNo){
        super(trackNo, bar);        // 118 is drumset instrument
        Beat beat = drumBeats.get(beatNo);
        HashMap<String, ArrayList<ArrayList<Integer>>> beatShape;
        if (fill == 0){
            beatShape = beat.getSmallFill();
        }
        else if (fill == 1){
            beatShape = beat.getBigFill();
        }
        else{
            beatShape = beat.getMainPattern();
        }
        HashMap<Integer, ArrayList<ArrayList<Integer>>> c = new HashMap<>();
        for(String instr : beatShape.keySet()){
            c.put(drumPrograms.get(instr), beatShape.get(instr));
        }
        setContent(c);
    }

    public static HashMap<String, Integer> getDrumPrograms() {
        return drumPrograms;
    }

    public static void setDrumPrograms(HashMap<String, Integer> drumPrograms) {
        BeatContainer.drumPrograms = drumPrograms;
    }

    public static List<Beat> getDrumBeats() {
        return drumBeats;
    }

    public static void setDrumBeats(List<Beat> drumBeats) {
        BeatContainer.drumBeats = drumBeats;
    }
}
