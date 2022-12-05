package org.se.Text.Generation;

import java.io.IOException;
import java.nio.file.*;
import org.se.Text.Analysis.*;

/**
 * @author Val Richter
 */
public class TermExample {
	public static TermCollection getExample() throws IOException {
		return Analyzer.analyze(Path.of("test.txt"));
	}
}
