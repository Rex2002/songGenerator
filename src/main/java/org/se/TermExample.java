package org.se;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import org.se.Text.Analysis.*;

/**
 * @author Val Richter
 */
public class TermExample {
	private static GrammaticalCase parseCase(String s) {
		s = s.toLowerCase();
		if (s.startsWith("nom"))
			return GrammaticalCase.Nominative;
		else if (s.startsWith("gen"))
			return GrammaticalCase.Genitive;
		else if (s.startsWith("dat"))
			return GrammaticalCase.Dative;
		else
			return GrammaticalCase.Accusative;
	}

	private static Numerus parseNumerus(String s) {
		s = s.toLowerCase();
		if (s.startsWith("p") || s.startsWith("t"))
			return Numerus.Plural;
		else
			return Numerus.Singular;
	}

	private static Gender parseGender(String s) {
		s = s.toLowerCase();
		return s.startsWith("m") ? Gender.Male : s.startsWith("f") ? Gender.Female : Gender.Neutral;
	}

	private static List<Integer> parseSyllables(String s) {
		List<Integer> l = new ArrayList<Integer>();
		Stream.of(s.split("-")).map(n -> Integer.decode(n)).forEach(n -> l.add(n));
		;
		return l;
	}

	public static TermCollection getExample() throws IOException {
		TermCollection c = new TermCollection();

		String csvFile = Files.readString(Path.of("", "example.csv"));
		Stream.of(csvFile.split("\r?\n")).skip(1).map(s -> s.split(",")).forEach(row -> {
			NounTerm t = new NounTerm(row[1]);
			t.setRadix(row[0]);
			t.setGender(TermExample.parseGender(row[2]));
			t.setGrammaticalCase(TermExample.parseCase(row[3]));
			t.setNumerus(TermExample.parseNumerus(row[4]));
			t.setSyllableAmount(TermExample.parseSyllables(row[5]).size());
			TermVariations v = new TermVariations(t);
			c.addNouns(v);
		});
		;

		return c;
	}
}
