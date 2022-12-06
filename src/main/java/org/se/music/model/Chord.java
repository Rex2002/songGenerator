package org.se.music.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class Chord {
	private final int rootNote;
	private final String chordModifierString;
	private final ArrayList<Integer> chordModifier;
	public static HashMap<String, ArrayList<Integer>> chordModifiers;

	public Chord(int rootNote, String chordMod) {

		if (0 <= rootNote && rootNote < 128) {
			this.rootNote = rootNote;
		} else {
			System.out.println("invalid rootNote, change to 60 (C)");
			this.rootNote = 60;
		}

		if (chordModifiers.containsKey(chordMod)) {
			this.chordModifier = chordModifiers.get(chordMod);
			this.chordModifierString = chordMod;
		} else {
			this.chordModifier = chordModifiers.get("maj");
			this.chordModifierString = "maj";
		}
	}

	public int getRootNote() {
		return rootNote;
	}

	public ArrayList<Integer> getChordModifier() {
		return chordModifier;
	}

	public ArrayList<Integer> getChord() {
		ArrayList<Integer> k = new ArrayList<>();
		for (int modifier : chordModifier) {
			k.add(rootNote + modifier);
		}
		return k;

	}

	public static void setChordModifiers(HashMap<String, ArrayList<Integer>> chordModifiers) {
		Chord.chordModifiers = chordModifiers;
	}

	@Deprecated
	public static HashMap<String, ArrayList<Integer>> getChordModifiers() {
		return chordModifiers;
	}

	@Override
	public String toString() {
		return "Chord{" + "rootNote=" + rootNote + ", chordModifier=" + chordModifier + '}';
	}

	public String getNoteName() {
		// rooer -= 21; // see the explanation below.
		String[] notes = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
		// int octave = rootNote / 12 + 1;
		return notes[rootNote % 12] + chordModifierString;
	}

}