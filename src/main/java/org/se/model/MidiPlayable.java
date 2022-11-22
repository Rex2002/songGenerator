package org.se.model;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class MidiPlayable {
    private final int trackNo;
    private int bar;
    private HashMap<Integer, ArrayList<ArrayList<Integer>>> content;

    public MidiPlayable(int trackNo, int bar){
        this.bar = bar;
        this.trackNo = trackNo;
    }

    public void setContent(HashMap<Integer, ArrayList<ArrayList<Integer>>> content) {
        this.content = content;
    }

    public HashMap<Integer, ArrayList<ArrayList<Integer>>> getContent(){
        return content;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public int getBar() {
        return bar;
    }
}
