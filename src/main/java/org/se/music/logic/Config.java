package org.se.music.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.se.music.model.*;

/**
 * @author Malte Richert
 * @reviewer Benjamin Frahm
 */

public class Config {
	private static List<Structure> structuresPop, structuresBlues;
	private static List<List<List<String>>> chordProgressionsPop, chordProgressionsBlues;
	private static HashMap<String, Integer> instrumentMapping;
	private static Genre genreFlag;

	public static void loadConfig() {
		YAMLFactory yaml = new YAMLFactory();
		ObjectMapper mapper = new ObjectMapper(yaml);

		// load drum program config into BeatContainer
		try {
			BeatContainer.setDrumPrograms(mapper.readValue(new File("./src/main/resources/music/drum_prog_no.yml"), HashMap.class));
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read drum_prog config.");
			e.printStackTrace();
		}

		// load beat templates into BeatContainer
		try {
			YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/music/beat_templates_pop.yml"));
			List<BeatData> beatDataList = mapper.readValues(yamlParser, BeatData.class).readAll();

			List<Beat> outBeats = new ArrayList<>();
			for (BeatData beatData : beatDataList) {
				outBeats.add(beatData.toBeat());
			}
			BeatContainer.setDrumBeats(outBeats);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Beat template.");
			e.printStackTrace();
		}

		// load structure template
		try {
			YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/music/structure_templates_pop.yml"));
			structuresPop = mapper.readValues(yamlParser, Structure.class).readAll();
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Structure template for pop.");
			e.printStackTrace();
		}
		try {
			YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/music/structure_templates_blues.yml"));
			structuresBlues = mapper.readValues(yamlParser, Structure.class).readAll();
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Structure template for blues.");
			e.printStackTrace();
		}

		// load chord modifiers into Chord class
		try {
			Chord.setChordModifiers(mapper.readValue(new File("./src/main/resources/music/chord_modifiers.yml"), HashMap.class));
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Chord modifiers from config.");
			e.printStackTrace();
		}

		// load chord progressions
		try {
			chordProgressionsPop = mapper.readValue(new File("./src/main/resources/music/chord_progressions_pop.yml"), ArrayList.class);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Chord progressions for pop from template.");
			e.printStackTrace();
		}
		try {
			chordProgressionsBlues = mapper.readValue(new File("./src/main/resources/music/chord_progressions_blues.yml"), ArrayList.class);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Chord progressions for blues from template.");
			e.printStackTrace();
		}

		// load instrument mappings
		try {
			instrumentMapping = mapper.readValue(new File("./src/main/resources/music/instrument_mapping.yml"), HashMap.class);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Instrument mappings from config.");
			e.printStackTrace();
		}
	}

	public static List<Structure> getStructures() {
		return genreFlag == Genre.BLUES ? structuresBlues : structuresPop;
	}

	public static List<List<List<String>>> getChordProgressions() {
		return genreFlag == Genre.BLUES ? chordProgressionsBlues : chordProgressionsPop;
	}

	public static HashMap<String, Integer> getInstrumentMapping() {
		return instrumentMapping;
	}

	public static void setGenreFlag(Genre genre) {
		genreFlag = genre;
	}
}