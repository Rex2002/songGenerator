package org.se.text.generation;

public class Part {
	private int length;
	private Chords[] chords; // I think this is an array
	private Instrument[] instruments;

	enum Instrument {
		blasding,
		piano,
		cello
	}

	public int getLength() {
		return length;
	}

	public Chords[] getChords() {
		return chords;
	}

	public Instrument[] getInstruments() {
		return instruments;
	}

}
