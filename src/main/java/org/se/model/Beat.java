package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class Beat {
    @JsonProperty
    HashMap<String, ArrayList<ArrayList<Integer>>> mainPattern;
    @JsonProperty
    HashMap<String, ArrayList<ArrayList<Integer>>> bigFill;
    @JsonProperty
    HashMap<String, ArrayList<ArrayList<Integer>>> smallFill;

    public Beat(){}
    @JsonCreator
    public Beat(@JsonProperty("mainPattern") HashMap<String, ArrayList<ArrayList<Integer>>> mainPattern,
                @JsonProperty("bigFill") HashMap<String, ArrayList<ArrayList<Integer>>> bigFill,
                @JsonProperty("smallFill") HashMap<String, ArrayList<ArrayList<Integer>>> smallFill) {

        this.mainPattern = mainPattern;
        this.bigFill = bigFill;
        this.smallFill = smallFill;
    }

    public HashMap<String, ArrayList<ArrayList<Integer>>> getMainPattern() {
        return mainPattern;
    }

    public HashMap<String, ArrayList<ArrayList<Integer>>> getBigFill() {
        return bigFill;
    }

    public HashMap<String, ArrayList<ArrayList<Integer>>> getSmallFill() {
        return smallFill;
    }
}