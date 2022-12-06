package org.se.text;

import java.io.IOException;

import org.se.text.analysis.Analyzer;
import org.se.text.analysis.FileReader;
import org.se.text.analysis.TermCollection;

public class TextMain {
	public static TermCollection analyze(String path) throws IOException {
		String text = FileReader.main(path);
		return Analyzer.analyze(text);
	}
}