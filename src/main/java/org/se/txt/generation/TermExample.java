package org.se.txt.generation;

import java.io.IOException;

import org.se.text.TextMain;
import org.se.txt.analysis.*;

/**
 * @author Val Richter
 */
public class TermExample {
	private TermExample() {
	}

	public static TermCollection getExample() throws IOException {
		return TextMain.analyze("test3.txt");
	}
}
