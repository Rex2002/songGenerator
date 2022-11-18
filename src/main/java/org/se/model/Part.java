package org.se.model;

import java.util.ArrayList;
import java.util.List;

public class Part {
    private int length;
    private List<Chord> chords;
    private List<InstrumentEnum> instruments;
    private Genre genre;
    private Beat beat;

    Part(int length, Genre genre){
        this.length=length;
        this.genre=genre;
        chords = new ArrayList<>();
        instruments = new ArrayList<>();
    }

    public int getLength() {
        return length;
    }
    public List<Chord> getChords() {
        return chords;
    }
    public List<InstrumentEnum> getInstruments() {
        return instruments;
    }

    public void fillPart(Part basePart, MusicalKey key, int variation){
        //variation class = (List<Chord> reqChords, reqInsts, optInsts)
    }

    private void selectChords(MusicalKey key, List<Chord> reqChords){
        //implementation of restriction still has to be designed
    }
    private void selectBeat(){
        //selects beat from template
    }
}
