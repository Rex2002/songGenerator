package org.se.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MidiPlayable {
    private final int trackNo;
    private int bar;
    private Map<Integer, List<List<Integer>>> content = new HashMap<>();

    public MidiPlayable(int trackNo, int bar){
        this.bar = bar;
        this.trackNo = trackNo;
    }

    public void setContent(Map<Integer, List<List<Integer>>> content) {
        this.content = content;
    }

    public Map<Integer, List<List<Integer>>> getContent(){
        return content;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public int getBar() { return bar; }

    public void setBar(int bar){
        this.bar = bar;
    }
}
