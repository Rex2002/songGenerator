package org.se.music.logic;

import java.util.*;

import org.se.music.model.MusicalKey;
import org.se.music.model.PitchedPlayable;

/**
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */
public class ChordContainer extends PitchedPlayable {
	private final boolean isBassTrack;

	public ChordContainer(int trackNo, int bar, MusicalKey key, List<String> chords, boolean isBassTrack) {
		super(trackNo, bar, key, chords);
		this.isBassTrack = isBassTrack;
		setContent();
	}

	public ChordContainer(int trackNo, int bar, MusicalKey key, List<String> chords) {
		this(trackNo, bar, key, chords, false);
	}

	public static List<List<List<String>>> getMatchingProgressions(List<String> reqChords) {
		List<List<List<String>>> returnProgressions = new ArrayList<>();
		boolean flag;
		for (List<List<String>> progression : Config.getChordProgressions()) {
			flag = true;
			List<String> l = progression.stream().flatMap(Collection::stream).toList();
			for (String chord : reqChords) {
				if (!l.contains(chord)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				returnProgressions.add(progression);
			}
		}
		return returnProgressions;
	}

	private void setContent() {
		Map<Integer, List<List<Integer>>> content = new HashMap<>();
		inflateChordList();
		for (int count = 0; count < 4; count++) {
			List<Integer> singleChord = inflatedChords[count].getChord();
			if (isBassTrack) {
				singleChord = List.of(inflatedChords[count].getRootNote() - 24);
			}
			for (Integer note : singleChord) {
				List<Integer> posAndLen;
				if (isBassTrack && count == 3) {
					posAndLen = List.of(count * 24 + 12, 12);
				} else {
					posAndLen = List.of(count * 24, 24);
				}
				if (content.containsKey(note)) {
					content.get(note).add(posAndLen);
				} else {
					List<List<Integer>> posAndLenList = new ArrayList<>();
					posAndLenList.add(posAndLen);
					content.put(note, posAndLenList);
				}
			}
		}
		super.setContent(content);
	}
}
