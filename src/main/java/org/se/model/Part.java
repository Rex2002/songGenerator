package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Part {
    @JsonProperty
    private int length;
    @JsonProperty
    private List<InstrumentEnum> reqInsts;
    @JsonProperty
    private List<InstrumentEnum> optInsts;
    @JsonProperty
    private boolean isBasePart;
    @JsonProperty
    private int randomizationLevel;
    private List<Chord> chords;
    private Genre genre;
    private Beat beat;

    @JsonCreator
    public Part(@JsonProperty("length") int length, @JsonProperty("req") List<InstrumentEnum> reqInsts,
                @JsonProperty("opt") List<InstrumentEnum> optInsts,
                @JsonProperty(value = "isBasePart", defaultValue = "false") boolean isBasePart,
                @JsonProperty(value = "randomizationLevel", defaultValue = "0") int randomizationLevel) {
        this.length = length;
        this.reqInsts = reqInsts;
        this.optInsts = optInsts;
        this.isBasePart = isBasePart;
        this.randomizationLevel = randomizationLevel;
    }

    public void fillPart(Part basePart, MusicalKey key, Variation variation){

    }

    private void selectChords(MusicalKey key, List<Chord> reqChords){

    }
    private void selectBeat(){
        //selects beat from template
    }


    public int getLength() {
        return length;
    }
    public List<InstrumentEnum> getReqInsts() {
        return reqInsts;
    }
    public List<InstrumentEnum> getOptInsts() {
        return optInsts;
    }
    public boolean isBasePart() {
        return isBasePart;
    }
    public int getRandomizationLevel() {
        return randomizationLevel;
    }
    public List<Chord> getChords() {
        return chords;
    }
    public Genre getGenre() {
        return genre;
    }
    public Beat getBeat() {
        return beat;
    }

    public void setChords(List<Chord> chords) {
        this.chords = chords;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    public void setBeat(Beat beat) {
        this.beat = beat;
    }
}
