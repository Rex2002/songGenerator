package org.se.model;

import java.util.List;

public class Variation {
    private final List<List<String>> reqChords;
    private final List<InstrumentEnum> reqInstruments;
    private final List<InstrumentEnum> optInstruments;

    public Variation(List<List<String>> reqChords, List<InstrumentEnum> reqInstruments, List<InstrumentEnum> optInstruments) {
        this.reqChords = reqChords;
        this.reqInstruments = reqInstruments;
        this.optInstruments = optInstruments;
    }

    public List<List<String>> getReqChords() {
        return reqChords;
    }
    public List<InstrumentEnum> getReqInstruments() {
        return reqInstruments;
    }
    public List<InstrumentEnum> getOptInstruments() {
        return optInstruments;
    }
}
