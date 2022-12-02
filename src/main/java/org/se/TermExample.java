package org.se;

import java.io.IOException;
import java.nio.file.*;
import org.se.Text.Analysis.*;

/**
 * @author Val Richter
 */
public class TermExample {
	public static TermCollection getExample() throws IOException {
		return Analyzer.analyze(Path.of("test2.txt"));
	}
}
