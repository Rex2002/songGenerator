package org.se.text.generation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.se.music.model.Genre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

public class TemplateImporter {
	YAMLFactory yaml = new YAMLFactory();
	ObjectMapper mapper = new ObjectMapper(yaml);

	/**
	 * returns all templates based on the genre
	 */
	public List<TextTemplate> getTemplate(Genre genre) {
		YAMLParser yamlParser;
			// load Templates from yml
			try {
				if(genre == Genre.POP) {
					yamlParser = yaml.createParser(new File("./src/main/resources/text/popTemplate.yml"));
				}else{
					yamlParser = yaml.createParser(new File("./src/main/resources/text/bluesTemplate.yml"));
				}
				return mapper.readValues(yamlParser, TextTemplate.class).readAll();

			} catch (IOException e) {
				System.out.println("Encountered exception while trying to read " + genre + "-Template.");
				e.printStackTrace();

				return List.of();
			}

	}
}
