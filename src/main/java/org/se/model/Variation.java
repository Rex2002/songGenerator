package org.se.model;

import java.util.List;

public class Variation {
    private final List<Chord> reqChords;
    private final List<String> reqInstruments;
    private final List<String> optInstruments;

    public Variation(List<Chord> reqChords, List<String> reqInstruments, List<String> optInstruments) {
        this.reqChords = reqChords;
        this.reqInstruments = reqInstruments;
        this.optInstruments = optInstruments;
    }

    public List<Chord> getReqChords() {
        return reqChords;
    }
    public List<String> getReqInstruments() {
        return reqInstruments;
    }
    public List<String> getOptInstruments() {
        return optInstruments;
    }
}
