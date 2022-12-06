package org.se;

import java.io.IOException;
import java.nio.file.*;

import org.se.text.analysis.*;

/**
 * @author Val Richter
 */
public class TermExample {
	private TermExample() {
	}

	public static TermCollection getExample() throws IOException {
		return Analyzer.analyze(Path.of("test3.txt"));
	}
}
