package org.se.text;

import java.io.IOException;

import org.se.text.analysis.Analyzer;
import org.se.text.analysis.FileReader;
import org.se.text.analysis.TermCollection;
import org.se.text.analysis.dict.Dict;

/**
 * @author Val Richter
 */
public class AnalysisTest {
	public static void main(String[] args) throws IOException {
		Dict dict = Dict.getDefault();
		String content = FileReader.main("src/test/resources/test2.txt");
		TermCollection terms = Analyzer.analyze(content, dict);
		terms.flatIter(System.out::println);
	}
}
