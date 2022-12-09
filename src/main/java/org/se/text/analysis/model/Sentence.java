package org.se.text.analysis.model;

import java.util.*;

/**
 * @author Val Richter
 */
public class Sentence extends ArrayList<String> {
	public Sentence() {
	}

	public Sentence(int c) {
		super(c);
	}

	public Sentence(Collection<String> c) {
		super(c);
	}
}
