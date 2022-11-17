package org.se;

import java.io.IOException;
import java.nio.file.Path;

import org.se.Text.Analysis.Analyzer;
import org.se.Text.Analysis.TermCollection;

public class Main {
    public static void main(String[] args) throws IOException {

		// Analyzer.analyze(Path.of("C:\\Users\\Administrator\\Documents\\Studium\\Software Engineering\\SongGenerator\\test.txt"));
		TermCollection c = TermExample.getExample();
		System.out.println(c.terms.size());
    }
}