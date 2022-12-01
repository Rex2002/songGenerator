package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;


/**
 * @author Malte Richert
 */
public class BeatData {
    @JsonProperty
    private Map<String, String> mainPattern;
    @JsonProperty
    private Map<String, String> bigFill;
    @JsonProperty
    private Map<String, String> smallFill;
    @JsonProperty
    private int resolution; //midi-length of smallest time-unit in template

    @JsonCreator
    public BeatData(@JsonProperty("mainPattern") Map<String, String> mainPattern,
                    @JsonProperty("bigFill") Map<String, String> bigFill,
                    @JsonProperty("smallFill") Map<String, String> smallFill,
                    @JsonProperty("resolution") int resolution) {
        this.mainPattern = mainPattern;
        this.bigFill = bigFill;
        this.smallFill = smallFill;
        this.resolution = resolution;
    }

    public Beat toBeat(){
        Beat beat = new Beat(new HashMap<>(), new HashMap<>(), new HashMap<>());

        for (Map<String, String> map : List.of(mainPattern, bigFill, smallFill)) {
            List<Integer> positions = new ArrayList<>();
            for (String instrument : map.keySet()) {
                for (int i = 0; i < map.get(instrument).length(); i++) {
                    String symbol = map.get(instrument).substring(i,i+1);
                    if (symbol.equals("x")){
                        positions.add(i*resolution);
                    }
                }
            }
            // sort positions-list and remove doubles
            positions = new HashSet<>(positions).stream().sorted().toList();

            for (String instrument : map.keySet()) {
                List<List<Integer>> notes = new ArrayList<>();
                for (int i = 0; i < map.get(instrument).length(); i++) {
                    String symbol = map.get(instrument).substring(i, i + 1);

                    if (symbol.equals("x")) {
                        int position = i * resolution;

                        int duration;
                        if (positions.indexOf(position) < positions.size()-1) {
                            duration = positions.get(positions.indexOf(position)+1) - position;
                        } else {
                            duration = 96 - position;
                        }
                        List<Integer> note = List.of(position, duration);
                        notes.add(note);
                    }
                }
                if (map == mainPattern) {
                    beat.getMainPattern().put(instrument, notes);
                } else if (map == bigFill) {
                    beat.getBigFill().put(instrument, notes);
                } else if (map == smallFill) {
                    beat.getSmallFill().put(instrument, notes);
                }
            }
        }
        return beat;
    }

}
