package org.se;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import org.se.Text.Analysis.*;

public class TermExample {
	private static GrammaticalCase parseCase(String s) {
		s = s.toLowerCase();
		if (s.startsWith("nom")) return GrammaticalCase.Nominative;
		else if (s.startsWith("gen")) return GrammaticalCase.Genitive;
		else if (s.startsWith("dat")) return GrammaticalCase.Dative;
		else return GrammaticalCase.Accusative;
	}

	private static Gender parseGender(String s) {
		s = s.toLowerCase();
		return s == "m" ? Gender.male : s == "f" ? Gender.female : Gender.neutral;
	}

	private static Boolean parseIsPlural(String s) {
		s = s.toLowerCase();
		if (s == "t" || s == "p") return true;
		else return false;
	}

	private static List<Integer> parseSyllables(String s) {
		List<Integer> l = new ArrayList<Integer>();
		Stream.of(s.split("-")).map(n -> Integer.decode(n)).forEach(n -> l.add(n));;
		return l;
	}

	public static TermCollection getExample() throws IOException {
		TermCollection c = new TermCollection();

		String csvFile = Files.readString(Path.of("", "example.csv"));
		Stream.of(csvFile.split("\r?\n")).skip(1).map(s -> s.split(",")).forEach(row -> {
			Term t = new Term(row[1]);
			t.setLemma(row[0]);
			t.setGender(TermExample.parseGender(row[2]));
			t.setGrammaticalCase(TermExample.parseCase(row[3]));
			t.setIsPlural(TermExample.parseIsPlural(row[4]));
			t.setSyllables(TermExample.parseSyllables(row[5]).toArray(t.syllables));
			TermVariations v = new TermVariations(t);
			c.add(v);
		});;

		return c;
	}
}
