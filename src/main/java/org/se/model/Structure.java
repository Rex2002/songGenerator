package org.se.model;

import java.util.List;

public class Structure {
    private List<Part> parts;
    private MusicalKey key;
    private Genre genre;

    Structure(MusicalKey key, Genre genre){
        this.key = key;
        this.genre = genre;
        //fill parts-list with structure from template
    }

    public List<Part> getParts() {
        return parts;
    }
    public MusicalKey getKey() {
        return key;
    }
    public Genre getGenre() {
        return genre;
    }
}
