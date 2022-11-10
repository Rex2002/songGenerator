package org.se;

import java.io.IOException;
import java.nio.file.Path;

import org.se.Text.Analysis.Analyzer;

public class Main {
    public static void main(String[] args) throws IOException {
		Analyzer.analyze(Path.of("C:\\Users\\Administrator\\Documents\\Studium\\Software Engineering\\SongGenerator\\lorem.txt"));
    }
}