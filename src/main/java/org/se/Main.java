package org.se;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
		SongGenerator generator = new SongGenerator();
		String s = generator.loadFile(Path.of("C:\\Users\\Administrator\\Documents\\Studium\\Software Engineering\\SongGenerator\\lorem.txt"));
		Term[] t = generator.analyzeText(s);
        // System.out.println(s);
    }
}