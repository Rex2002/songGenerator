package org.se;

import java.io.IOException;
import java.nio.file.Path;
import org.se.Text.Analysis.*;

public class Main {
	public static void main(String[] args) throws IOException {

		TermCollection tc = Analyzer.analyze(Path.of("test.txt"));
		System.out.println(tc);
		System.out.println(tc.nouns.size());

		// TermCollection tc = TermExample.getExample();
		// List<Term> res =
		// tc.query(GrammaticalCase.Nominative,Gender.female,false,0,100);
		// res.forEach(t -> System.out.println(String.join(" ", t.words)));
	}
}