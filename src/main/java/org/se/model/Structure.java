package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class Structure {
    @JsonProperty
    private Map<String, Map<String,Object>> parts;
    @JsonProperty
    private List<String> order;
    private MusicalKey key;
    private Genre genre;

    @Deprecated
    private static List<HashMap> structures_pop;
    private static Random ran = new Random();

    @JsonCreator
    public Structure(@JsonProperty("order") List<String> order,
                     @JsonProperty("parts") Map<String, Map<String,Object>> parts){
        this.order = order;
        this.parts = parts;
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

    public Map<String, Map<String, Object>> getParts() {
        return parts;
    }
    public MusicalKey getKey() {
        return key;
    }
    public Genre getGenre() {
        return genre;
    }
}
