package org.se.text.generation;

import java.io.IOException;

import org.se.text.analysis.*;

/**
 * @author Val Richter
 */
public class TermExample {
	private TermExample() {
	}

	public static TermCollection getExample() throws IOException {
		return Analyzer.analyze("test3.txt");
	}
}
