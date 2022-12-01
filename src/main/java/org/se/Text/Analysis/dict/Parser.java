package org.se.Text.Analysis.dict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.se.Text.Analysis.*;

public class Parser {
	public static GrammaticalCase parseGrammaticalCase(String s) {
		s = s.toLowerCase();
		switch (s.charAt(0)) {
			case 'n':
				return GrammaticalCase.Nominative;

			case 'g':
				return GrammaticalCase.Genitive;

			case 'd':
				return GrammaticalCase.Dative;

			case 'a':
				return GrammaticalCase.Accusative;

			default:
				return GrammaticalCase.Nominative;
		}
	}

	public static Gender parseGender(String s) {
		s = s.toLowerCase();
		return s.startsWith("m") ? Gender.Male : s.startsWith("f") ? Gender.Female : Gender.Neutral;
	}

	public static Numerus parseNumerus(String s) {
		switch (s.toLowerCase().charAt(0)) {
			// t for true
			case 't':
				return Numerus.Plural;

			// p for plural
			case 'p':
				return Numerus.Plural;

			default:
				return Numerus.Singular;
		}
	}

	public static Optional<Integer> parseInt(String s) {
		try {
			return Optional.of(Integer.parseInt(s.trim()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static boolean parseBool(String s) {
		return s.toLowerCase().startsWith("t");
	}

	public static String[] parseList(String s) {
		return s.split("-");
	}

	public static void readCSV(Path filepath, Consumer<? super WordWithData> forEachRow)
			throws IOException {
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

	public static void readCSV(Path filepath, List<WordWithData> list) throws IOException {
		readCSV(filepath, row -> list.add(row));
	}

	public static void readCSV(Path filepath, WordList list) throws IOException {
		readCSV(filepath, row -> list.insert(row));
	}
}
