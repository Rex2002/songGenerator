package org.se.music.model;

import java.util.*;

/**
 * Provided an instance of the Theme class, this class can be used to
 * create a MidiPlayable modeling a variation of the theme.
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class ThemeVariation extends MidiPlayable {

	Theme theme;
	Map<Integer, List<List<Integer>>> transposedContent;
	String[][] text;
	static Random ran = new Random();

	public ThemeVariation(Theme theme, int trackNo, int bar, String[][] text) {
		super(trackNo, bar);
		this.theme = theme;
		this.text = text;
		setContent(theme.deepCopy());
		createVariation();
	}

	@SuppressWarnings("unused")
	public ThemeVariation(Theme theme, int trackNo, int bar, boolean variationFlag) {
		super(trackNo, bar);
		this.theme = theme;
		setContent(theme.deepCopy());
	}

	private void createVariation() {
		Map<Integer, List<List<Integer>>> themeContent = theme.transposedContent;
		int pos, posNextSmaller, posNextBigger, newNote, newLength;
		List<Integer> posAndLength;
		for (int bar = 0; bar < theme.getLengthInBars(); bar++) {
			while (getNoteCountInBar(bar) < Integer.parseInt(text[bar][1])) {
				pos = ran.nextInt(16) * 6 + bar * 96;
				posNextSmaller = getPosNext(pos, false);
				posNextBigger = getPosNext(pos, true);
				if (themeContent.containsKey(posNextBigger) && themeContent.containsKey(posNextSmaller)) {
					posAndLength = new ArrayList<>();
					if (posNextBigger == pos) {
						continue;
					}
					if (posNextBigger == pos + 6) {
						int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(), themeContent.get(pos + 6).get(0).get(0));
						newNote = gamut[(MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(pos + 6).get(0).get(0)) + (7 - ran.nextInt(2)))
								% 7];
						newLength = 6;
						posAndLength.add(pos);
						posAndLength.add(newLength);

						addPosAndLengthToContent(newNote, posAndLength);
					}

					else if (posNextBigger - posNextSmaller >= 48
							&& themeContent.get(posNextBigger).get(0).get(0).equals(themeContent.get(posNextSmaller).get(0).get(0))
							&& ran.nextInt(2) == 0) {
								System.out.println("run");
								int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(),
										themeContent.get(posNextBigger).get(0).get(0));
								int index = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextBigger).get(0).get(0));
								if (index >= 4) {
									newNote = gamut[index - 2];
									newLength = 12;
									posAndLength.add(posNextBigger - 24);
									posAndLength.add(newLength);
									addPosAndLengthToContent(newNote, posAndLength);
									newNote = gamut[index - 1];
								} else {
									newNote = gamut[index + 2];
									newLength = 12;
									posAndLength.add(posNextBigger - 24);
									posAndLength.add(newLength);
									addPosAndLengthToContent(newNote, posAndLength);
									newNote = gamut[index + 1];
								}
								posAndLength.set(0, posNextBigger - 36);
								addPosAndLengthToContent(newNote, posAndLength);
								posAndLength.set(0, posNextBigger - 12);
								addPosAndLengthToContent(newNote, posAndLength);
							} else {
								int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(),
										themeContent.get(posNextBigger).get(0).get(0));

								int index1 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextBigger).get(0).get(0));
								int index2 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextSmaller).get(0).get(0));
								int newIndexInGamut = (index1 + index2) / 2;
								newLength = posNextBigger - pos;
								posAndLength.add(pos);
								posAndLength.add(newLength);
								newNote = gamut[newIndexInGamut];

								addPosAndLengthToContent(newNote, posAndLength);
							}
				}
			}
		}
	}

	private void addPosAndLengthToContent(int newNote, List<Integer> posAndLength) {
		List<Integer> posAndLengthCopy = new ArrayList<>(posAndLength);
		if (getContent().containsKey(newNote)) {
			getContent().get(newNote).add(posAndLengthCopy);
		} else {
			List<List<Integer>> posAndLengthList = new ArrayList<>();
			posAndLengthList.add(posAndLengthCopy);
			getContent().put(newNote, posAndLengthList);
		}
	}

	public int getPosNext(int posToFind, boolean bigger) {
		int foundPos = bigger ? theme.getLengthInBars() * 96 : 0;
		for (Integer pos : theme.transposedContent.keySet()) {
			if ((!bigger && pos < posToFind && foundPos < pos) || (bigger && pos >= posToFind && foundPos > pos)) {
				foundPos = pos;
			}
		}
		return foundPos;
	}

	public int getNoteCountInBar(int bar) {
		int count = 0;

		setTransposedContent();
		for (Integer pos : transposedContent.keySet()) {
			if (bar * 96 <= pos && pos < (bar + 1) * 96) {
				for (List<Integer> ignored : transposedContent.get(pos)) {
					count++;
				}
			}
		}
		return count;
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
}
