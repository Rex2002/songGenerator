package org.se.text.analysis;

import java.io.IOException;
import java.util.*;
import org.se.text.analysis.model.*;

public class Test {
	public static void main(String[] args) throws IOException {
		TermCollection terms = Analyzer.analyze("test3.txt");
		System.out.println(terms);

		// List<NounTerm> res = terms.query(GrammaticalCase.ACCUSATIVE, Gender.MALE, Numerus.PLURAL);
		// System.out.println(res);
	}
}
