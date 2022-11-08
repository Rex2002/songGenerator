package org.se;

public class Main {
    public static void main(String[] args) {
		SongGenerator generator = new SongGenerator();
		String s = generator.loadFile("C:\\path\\to\\file");
        System.out.println(s);
    }
}