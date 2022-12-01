package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * @author Malte Richert
 */
public class Structure {
    @JsonProperty
    private Map<String, Part> parts;
    @JsonProperty
    private List<String> order;
    @JsonProperty
    private String basePartKey;
    private MusicalKey key;
    private Genre genre;
    private int tempo;

    @JsonCreator
    public Structure(@JsonProperty("order") List<String> order, @JsonProperty("parts") Map<String, Part> parts,
                     @JsonProperty("basePart") String basePartKey){
        this.order = order;
        this.parts = parts;
        this.basePartKey = basePartKey;
    }

    public Map<String, Part> getParts() {
        return parts;
    }

    public Part getPart(String partName){ return parts.get(partName);}
    public MusicalKey getKey() {
        return key;
    }
    public Genre getGenre() {
        return genre;
    }
    public int getTempo() {
        return tempo;
    }

    public List<String> getOrder() {return order;}

    public String getBasePartKey() {
        return basePartKey;
    }

    public void setKey(MusicalKey key) {
        this.key = key;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    @Override
    public String toString() {
        return "Structure{" +
                "\nparts=" + parts +
                ", \norder=" + order +
                ", \nbasePartKey='" + basePartKey + '\'' +
                ", \nkey=" + key +
                ", \ngenre=" + genre +
                ", \ntempo=" + tempo +
                '}';
    }
}
