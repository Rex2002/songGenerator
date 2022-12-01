package org.se;

import java.io.IOException;
import java.nio.file.Path;
import org.se.Text.Analysis.*;

public class Main {
	static int counter = 0;

	public static void main(String[] args) throws IOException {

		TermCollection tc = Analyzer.analyze(Path.of("bibel.txt"));
		tc.iterNouns(vars -> {
			if (vars.getFrequency() > 1) {
				counter++;
				vars.forEach(term -> System.out.print(term.show() + ", "));
				System.out.println(vars.getFrequency());
			}
		});
		System.out.println(counter);

		// TermCollection tc = TermExample.getExample();
		// List<Term> res =
		// tc.query(GrammaticalCase.Nominative,Gender.female,false,0,100);
		// res.forEach(t -> System.out.println(String.join(" ", t.words)));
	}
}