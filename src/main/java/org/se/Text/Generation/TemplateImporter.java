package org.se.text.generation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.se.text.generation.PopTemplate;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

public class TemplateImporter {
	YAMLFactory yaml = new YAMLFactory();
	ObjectMapper mapper = new ObjectMapper(yaml);

	// private static List<PopTemplate> =;

	public List<PopTemplate> getTemplate(Structure.Genre genre) {
		if (genre == Structure.Genre.pop) {
			// load Templates from yml
			try {
				YAMLParser yamlParser = yaml.createParser(new File("./popTemplate.yml"));

				return mapper.readValues(yamlParser, PopTemplate.class).readAll();

			} catch (IOException e) {
				System.out.println("Encountered exception while trying to read popTemplate.");
				e.printStackTrace();

				return List.of();
			}

		}
		return List.of();
	}
}
