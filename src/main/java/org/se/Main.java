package org.se;

import java.io.IOException;
import java.nio.file.Path;

import org.se.Text.Analysis.Term;
import org.se.Text.Analysis.Archive.Analyzer;

public class Main {
    public static void main(String[] args) throws IOException {
		Analyzer generator = new Analyzer();
		String s = generator.loadFile(Path.of("C:\\Users\\Administrator\\Documents\\Studium\\Software Engineering\\SongGenerator\\lorem.txt"));
		Term[] t = generator.analyzeText(s);
        // System.out.println(s);
    }
}