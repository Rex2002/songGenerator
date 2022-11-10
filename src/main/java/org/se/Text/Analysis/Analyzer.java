package org.se.Text.Analysis;

import java.util.ArrayList;

public class Analyzer {
	String text = "";
	ArrayList<ArrayList<Word>> sentences = new ArrayList<ArrayList<Word>>();
	ArrayList<ArrayList<Tag>> tags = new ArrayList<ArrayList<Tag>>();

	public Analyzer(String text) {
		this.text = text;
	}

	public TermCollection analyze() {
		this.preprocess();
		this.tag();
		return this.buildTerms();
	}

	void preprocess() {

	}

	void tag() {

	}

	TermCollection buildTerms() {
		return new TermCollection();
	}

}
