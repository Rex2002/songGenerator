package org.se.text.analysis;

import java.io.IOException;
import java.util.*;

import org.se.text.analysis.dict.Dict;
import org.se.text.analysis.model.*;

public class Test {
	public static void main(String[] args) throws IOException {
		Dict dict = Dict.getDefault();
		String content = FileReader.main("test3.txt");
		TermCollection terms = Analyzer.analyze(content, dict);
		terms.flatIter(term -> System.out.println(term));

		// List<NounTerm> res = terms.query(GrammaticalCase.ACCUSATIVE, Gender.MALE, Numerus.PLURAL);
		// System.out.println(res);
	}
}
