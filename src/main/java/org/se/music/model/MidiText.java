package org.se.music.model;

public class MidiText {
	private int bar;
	private final int relativeOffset, trackNo;
	private final String text;
	private final boolean isChordName;

	public MidiText(int trackNo, int bar, String text) {
		this.bar = bar;
		this.text = text;
		this.relativeOffset = 0;
		this.trackNo = trackNo;
		this.isChordName = false;
	}

	public MidiText(int trackNo, int bar, Chord chord, int relativeOffset) {
		this.bar = bar;
		this.isChordName = true;
		this.relativeOffset = relativeOffset;
		this.trackNo = trackNo;
		this.text = chord.getNoteName();
	}

	public int getPos() {
		return bar * 4 + (isChordName ? relativeOffset : 2);
	}

	public String getText() {
		return text;
	}

	public int getBar() {
		return bar;
	}

	public void setBar(int bar) {
		this.bar = bar;
	}

	public int getTrackNo() {
		return trackNo;
	}
}
