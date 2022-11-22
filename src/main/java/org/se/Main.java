package org.se;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.se.Text.Analysis.Analyzer;
import org.se.Text.Analysis.Gender;
import org.se.Text.Analysis.GrammaticalCase;
import org.se.Text.Analysis.Term;
import org.se.Text.Analysis.TermCollection;

public class Main {
    public static void main(String[] args) throws IOException {

		TermCollection tc = TermExample.getExample();
		List<Term> res = tc.query(GrammaticalCase.Nominative,Gender.female,false,0,100);
		res.forEach(t -> System.out.println(String.join(" ", t.words)));
    }
}