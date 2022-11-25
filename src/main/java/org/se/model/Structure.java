package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

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

    @Deprecated
    private static List<HashMap> structures_pop;
    private static Random ran = new Random();

    @JsonCreator
    public Structure(@JsonProperty("order") List<String> order, @JsonProperty("parts") Map<String, Part> parts,
                     @JsonProperty("basePart") String basePartKey){
        this.order = order;
        this.parts = parts;
        this.basePartKey = basePartKey;
    }
    @Deprecated
    public Structure(MusicalKey key, Genre genre){
        this.key = key;
        this.genre = genre;
        HashMap template;
        if (genre == Genre.POP) {
            template = structures_pop.get(ran.nextInt(structures_pop.size()));
        }
        else{ // TODO add blues template
            template = structures_pop.get(ran.nextInt(structures_pop.size()));
        }
        // TODO fill parts-list with structure from template
        // 1. find all unique parts and generate them according to their specifications
        // 2. assemble parts according to the order"
        // 3. write as midi sequence

    }

    public Map<String, Part> getParts() {
        return parts;
    }
    public MusicalKey getKey() {
        return key;
    }
    public Genre getGenre() {
        return genre;
    }
    public int getTempo() {
        return tempo;
    }
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
}
