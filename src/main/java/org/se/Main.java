package org.se;

import java.io.IOException;
import java.nio.file.Path;

import org.se.text.analysis.*;
import org.se.text.analysis.dict.Dict;

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
		// System.out.println("Example:");
		TermCollection example = TermExample.getExample();
		example.iterNouns(vars -> {
			System.out.println(vars);
			counter++;
		});
		example.iterVerbs(vars -> {
			System.out.println(vars);
			counter++;
		});
		System.out.println(counter);

		// Dict d = new Dict(Path.of("", "./src/main/resources/dictionary"));

		// NounTerm t1 = TermVariations.createTerm(example.getNouns().get("Mann"),
		// Gender.MALE, GrammaticalCase.DATIVE, Numerus.PLURAL, d);
		// System.out.println(t1);

		// NounTerm t2 = TermVariations.createTerm(example.getNouns().get("Frau"),
		// Gender.FEMALE, GrammaticalCase.ACCUSATIVE, Numerus.PLURAL, d);
		// System.out.println(t2);
	}
}