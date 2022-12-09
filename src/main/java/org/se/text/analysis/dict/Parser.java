package org.se.text.analysis.dict;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.se.text.analysis.model.*;

/**
 * @author Val Richter
 */
public class Parser {
	private Parser() {
	}

	// List of classes that are supported for parsing
	static Class<?>[] supportedClasses = { Integer.class, Boolean.class, List.class, GrammaticalCase.class, Person.class, Tense.class, Gender.class,
			Numerus.class };

	public static <T> Optional<T> parse(String s, Class<T> cls) {
		// Why can't I do a switch statement on Class<T>,
		// ugghhhhhhh why Java, why!!!????
		if (cls == String.class) return Optional.ofNullable(cls.cast(s));
		else if (cls == GrammaticalCase.class) return Optional.ofNullable(cls.cast(parseGrammaticalCase(s)));
		else if (cls == Person.class) return Optional.ofNullable(cls.cast(parsePerson(s)));
		else if (cls == Tense.class) return Optional.ofNullable(cls.cast(parseTense(s)));
		else if (cls == Gender.class) return Optional.ofNullable(cls.cast(parseGender(s)));
		else if (cls == Numerus.class) return Optional.ofNullable(cls.cast(parseNumerus(s)));
		else if (cls == CompoundPart.class) return Optional.ofNullable(cls.cast(parseCompoundPart(s)));
		else if (cls == AffixType.class) return Optional.ofNullable(cls.cast(parseAffixType(s)));
		else if (cls == Boolean.class) return Optional.ofNullable(cls.cast(parseBool(s)));
		else if (cls == Integer.class) return Optional.ofNullable(cls.cast(parseInt(s)));
		else if (cls == List.class) return Optional.ofNullable(cls.cast(parseList(s)));

		return Optional.empty();
	}

	public static GrammaticalCase parseGrammaticalCase(String s) {
		s = s.toLowerCase();
		switch (s.charAt(0)) {
			case 'n':
				return GrammaticalCase.NOMINATIVE;

			case 'g':
				return GrammaticalCase.GENITIVE;

			case 'd':
				return GrammaticalCase.DATIVE;

			case 'a':
				return GrammaticalCase.ACCUSATIVE;

			default:
				return GrammaticalCase.NOMINATIVE;
		}
	}

	public static Person parsePerson(String s) {
		s = s.toLowerCase();
		switch (s.charAt(0)) {
			case '1':
				return Person.FIRST;
			case 'f':
				return Person.FIRST;

			case '2':
				return Person.SECOND;
			case 's':
				return Person.SECOND;

			case '3':
				return Person.THIRD;
			case 't':
				return Person.THIRD;

			default:
				return Person.FIRST;
		}
	}

	public static Tense parseTense(String s) {
		s = s.toLowerCase();
		if (s.startsWith("pr")) return Tense.PRESENT;
		else if (s.startsWith("pas")) return Tense.PAST;
		else return Tense.PARTICIPLE;
	}

	public static Gender parseGender(String s) {
		switch (s.toLowerCase().charAt(0)) {
			case 'm':
				return Gender.MALE;

			case 'f':
				return Gender.FEMALE;

			default:
				return Gender.NEUTRAL;
		}
	}

	public static Numerus parseNumerus(String s) {
		switch (s.toLowerCase().charAt(0)) {
			// t for true
			case 't':
				return Numerus.PLURAL;

			// p for plural
			case 'p':
				return Numerus.PLURAL;

			default:
				return Numerus.SINGULAR;
		}
	}

	public static CompoundPart parseCompoundPart(String s) {
		switch (s.toLowerCase().charAt(0)) {
			case 's':
				return CompoundPart.SUBTRACTION;

			default:
				return CompoundPart.ADDITION;
		}
	}

	public static AffixType parseAffixType(String s) {
		switch (s.toLowerCase().charAt(0)) {
			case 's':
				return AffixType.SUFFIX;
			default:
				return AffixType.PREFIX;
		}
	}

	public static Integer parseInt(String s) {
		return parseInt(s, 0);
	}

	public static Integer parseInt(String s, Integer def) {
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return def;
		}
	}

	public static boolean parseBool(String s) {
		return s.toLowerCase().startsWith("t");
	}

	public static String[] parseList(String s) {
		return s.split("-");
	}

	public static List<String[]> readCSV(Path filepath) throws IOException {
		if (!filepath.toString().endsWith(".csv") && !Files.exists(filepath)) {
			filepath = Path.of(filepath.toString() + ".csv");
		}

		String[] rows = Files.readString(filepath).split("\\r?\\n");
		List<String[]> res = new ArrayList<>();

		for (int i = 0; i < rows.length; i++) {
			res.add(rows[i].split(","));
		}

		return res;
	}

	public static void parseCSV(Path filepath, Consumer<? super WordWithData> forEachRow) throws IOException {
		if (!filepath.toString().endsWith(".csv") && !Files.exists(filepath)) {
			filepath = Path.of(filepath.toString() + ".csv");
		}

		String[] rows = Files.readString(filepath).split("\\r?\\n");
		String[] firstRow = rows[0].split(",");

		for (int i = 1; i < rows.length; i++) {
			String[] cols = rows[i].split(",");
			WordWithData row = new WordWithData();
			for (int j = 0; j < firstRow.length && j < cols.length; j++) {
				row.put(firstRow[j], cols[j]);
			}
			forEachRow.accept(row);
		}
	}

	// This function uses generics to set the attributes in a given class with the
	// parsed entries of a CSV file.
	// For this to work, the field in the CSV has to have the same name as the
	// corresponding attribute in the class. And the attribute has to be public
	// (either exactly the same name, or the same as the lowercase attribute's name)
	public static <T> void parseCSV(Path filepath, List<T> list, Class<T> cls) throws IOException {
		parseCSV(filepath, row -> {
			try {
				T t = cls.getDeclaredConstructor().newInstance();

				for (Field field : cls.getFields()) {
					String s = null;
					String fieldName = field.getName();
					if (row.containsKey(fieldName)) {
						s = row.get(fieldName);
					} else {
						String tmp = fieldName.toLowerCase();
						if (row.containsKey(tmp)) {
							s = row.get(tmp);
						}
					}

					if (s != null) {
						Optional<?> val = parse(s, field.getType());
						if (val.isPresent()) {
							field.set(t, val.get());
						}
					}
				}

				list.add(t);
			} catch (Exception e) {
				// TODO: Add sensible ErrorHandling
				System.err.println(e);
			}
		});
	}

	public static void readCSV(Path filepath, List<WordWithData> list) throws IOException {
		parseCSV(filepath, list::add);
	}

	public static void readCSV(Path filepath, WordList list) throws IOException {
		// TODO:
		// When inserting the rows normally via list::insert, no verbs are found
		// There must be something wrong with the WordList class
		// Since it only affects verbs, there might be some edge-case with some of the verbforms, that causes the error
		// Since the uncheckedInsert with additional sorting afterwards fixes the problem apparently,
		// I won't spend more time on trying to find the bug for now
		parseCSV(filepath, list::uncheckedInsert);
		list.sort();
	}
}
