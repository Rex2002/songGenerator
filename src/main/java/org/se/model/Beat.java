package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;


/**
 * @author Malte Richert
 */

public class Beat {
    @JsonProperty
    Map<String, List<List<Integer>>> mainPattern;
    @JsonProperty
    Map<String, List<List<Integer>>> bigFill;
    @JsonProperty
    Map<String, List<List<Integer>>> smallFill;

    @JsonCreator
    public Beat(@JsonProperty("mainPattern") Map<String, List<List<Integer>>> mainPattern,
                @JsonProperty("bigFill") Map<String, List<List<Integer>>> bigFill,
                @JsonProperty("smallFill") Map<String, List<List<Integer>>> smallFill) {

        this.mainPattern = mainPattern;
        this.bigFill = bigFill;
        this.smallFill = smallFill;
    }

    public Map<String, List<List<Integer>>> getMainPattern() {
        return mainPattern;
    }

    public Map<String, List<List<Integer>>> getBigFill() {
        return bigFill;
    }

    public Map<String, List<List<Integer>>> getSmallFill() {
        return smallFill;
    }
}