package org.se;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		// Testing

		String text = FileReader.main("test.txt");
		System.out.println(text);

		System.out.println("\n\n\n---\n\n\n");

		text = FileReader.main("test.pdf");
		System.out.println(text);
	}
}