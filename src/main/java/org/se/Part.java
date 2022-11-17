package org.se;

import java.util.ArrayList;
import java.util.List;

public class Part {
    private int length;
    private List<Chord> chords;
    private List<InstrumentEnum> instruments;

    Part(int length){
        this.length=length;
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
        //implementation of variation still has to be designed
    }

    private void selectChords(MusicalKey key, Genre genre, String someKindOfRestriction){
        //implementation of restriction still has to be designed
    }
}
