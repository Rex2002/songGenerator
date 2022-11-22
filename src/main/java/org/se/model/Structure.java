package org.se.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Structure {
    private List<Part> parts;
    private MusicalKey key;
    private Genre genre;

    private static List<HashMap> structures_pop;
    private static Random ran = new Random();

    static{
        YAMLFactory yaml = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yaml);
        try{
            YAMLParser parser = yaml.createParser(new File("./src/main/resources/structure_templates_pop.yml"));

            structures_pop = mapper.readValues(parser, HashMap.class).readAll();
            System.out.println(structures_pop);
            System.out.println(structures_pop.get(0).getClass());

        }catch (IOException e){
            e.printStackTrace();
        }
    }
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
