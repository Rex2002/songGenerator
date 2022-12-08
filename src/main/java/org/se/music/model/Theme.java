package org.se.music.model;

import java.util.*;

/**
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class Theme extends MidiPlayable {

	private final MusicalKey key;
	private Chord[][] chords;
	private Chord[] inflatedChords;
	HashMap<Integer, List<List<Integer>>> transposedContent;
	private final int length;

	public Theme(MusicalKey key, List<List<String>> chordProgression, int length) {
		super(0, 0);
		this.key = key;
		this.length = length;
		parseChordString(chordProgression);
		inflateChordList();
		setContent();
		setTransposedContent();

	}

	public void setContent() {
		Map<Integer, List<List<Integer>>> content = new HashMap<>();
		Random ran = new Random();
		for (int i = 0; i < 8 * length; i++) {
			if (ran.nextInt(8) == 0 || (i % 2 == 0 && ran.nextInt(4) != 0)) {
				Integer chordNote = inflatedChords[i / 2].getChord().get(ran.nextInt(inflatedChords[i / 2].getChord().size()));
				List<Integer> posAndLength = new ArrayList<>();
				posAndLength.add(i * 12);
				posAndLength.add(12 + 12 * ran.nextInt(3));
				if (content.containsKey(chordNote)) {
					content.get(chordNote).add(posAndLength);
				} else {
					List<List<Integer>> l = new ArrayList<>();
					l.add(posAndLength);
					content.put(chordNote, l);
				}
			}
		}
		super.setContent(content);
	}

	public void setTransposedContent() {
		transposedContent = new HashMap<>();
		for (int note : getContent().keySet()) {
			for (List<Integer> posAndLength : getContent().get(note)) {
				List<Integer> noteAndLength = new ArrayList<>();
				noteAndLength.add(note);
				noteAndLength.add(posAndLength.get(1));
				if (transposedContent.containsKey(posAndLength.get(0))) {
					transposedContent.get(posAndLength.get(0)).add(noteAndLength);
				} else {
					List<List<Integer>> tmp = new ArrayList<>();
					tmp.add(noteAndLength);
					transposedContent.put(posAndLength.get(0), tmp);
				}
			}
		}
	}

	public void parseChordString(List<List<String>> chordProgression) {
		chords = new Chord[chordProgression.size()][];
		for (int bar = 0; bar < chordProgression.size(); bar++) {
			chords[bar] = new Chord[chordProgression.get(bar).size()];
			for (int i = 0; i < chordProgression.get(bar).size(); i++) {
				int stair = Integer.parseInt(String.valueOf(chordProgression.get(bar).get(i).charAt(0)));
				String modifier = chordProgression.get(bar).get(i).substring(1);
				chords[bar][i] = new Chord(MusicalKey.getNotesInKey(key.getBaseNote())[stair], modifier);
			}
		}
	}

	private void inflateChordList() {
		inflatedChords = new Chord[chords.length * length];
		for (int barNo = 0; barNo < chords.length; barNo++) {
			for (int chordNo = 0; chordNo < chords[barNo].length; chordNo++) {
				for (int i = 0; i < 4 / chords[barNo].length; i++) {
					inflatedChords[4 * barNo + 4 / chords[barNo].length * chordNo + i] = chords[barNo][chordNo];
				}
			}
		}
	}

	public MusicalKey getKey() {
		return key;
	}

	public int getLengthInBars() {
		return length;
	}
}
