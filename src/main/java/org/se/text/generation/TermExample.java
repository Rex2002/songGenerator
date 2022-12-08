package org.se.text.generation;

import java.io.IOException;

import org.se.text.analysis.*;
import org.se.text.analysis.dict.Dict;

/**
 * @author Val Richter
 */
public class TermExample {
	private TermExample() {
	}

	public static TermCollection getExample() throws IOException {
		String content = FileReader.main("test.txt");
		return Analyzer.analyze(content, Dict.getDefault());
	}
}
