package org.se;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    public Beat(HashMap<String, ArrayList<ArrayList<Integer>>> mainPattern,
                HashMap<String, ArrayList<ArrayList<Integer>>> bigFill,
                HashMap<String, ArrayList<ArrayList<Integer>>> smallFill) {

        this.mainPattern = mainPattern;
        this.bigFill = bigFill;
        this.smallFill = smallFill;
    }

    public HashMap<String, ArrayList<ArrayList<Integer>>> getMainPattern() {
        return mainPattern;
    }

    public void setMainPattern(HashMap<String, ArrayList<ArrayList<Integer>>> mainPattern) {
        this.mainPattern = mainPattern;
    }

    public HashMap<String, ArrayList<ArrayList<Integer>>> getBigFill() {
        return bigFill;
    }

    public void setBigFill(HashMap<String, ArrayList<ArrayList<Integer>>> bigFill) {
        this.bigFill = bigFill;
    }

    public HashMap<String, ArrayList<ArrayList<Integer>>> getSmallFill() {
        return smallFill;
    }

    public void setSmallFill(HashMap<String, ArrayList<ArrayList<Integer>>> smallFill) {
        this.smallFill = smallFill;
    }
}
