package org.se;

import java.io.IOException;

import org.se.text.analysis.*;

public class Main {
	static int counter = 0;

	public static void main(String[] args) throws IOException {

		// TermCollection tc = Analyzer.analyze(Path.of("bibel.txt"));
		// tc.iterNouns(vars -> {
		// if (vars.getFrequency() > 1) {
		// System.out.println(vars);
		// counter++;
		// }
		// });
		// System.out.println("\n\n");
		System.out.println("Example:");
		TermCollection example = TermExample.getExample();
		// example.iterNouns(vars -> {
		// System.out.println(vars);
		// counter++;
		// });
		example.iterVerbs(vars -> {
			System.out.println(vars);
			counter++;
		});
		System.out.println(counter);

		System.out.println("stell".compareTo("stemm"));
		System.out.println("l".compareTo("m"));

		// TermCollection tc = TermExample.getExample();
		// List<Term> res =
		// tc.query(GrammaticalCase.Nominative,Gender.female,false,0,100);
		// res.forEach(t -> System.out.println(String.join(" ", t.words)));
	}
}