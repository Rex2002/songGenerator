package org.se.music.logic.playables;

import org.se.music.model.Beat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Midi-playable model of a drum beat. The templates are taken from the drumBeats-list as Beat-Objects
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class BeatContainer extends MidiPlayable {
	public static Map<String, Integer> drumPrograms;
	public static List<Beat> drumBeats;

	public BeatContainer(int beatNo, int bar, int fill, int trackNo) {
		super(trackNo, bar); // 118 is drumset instrument
		Beat beat = drumBeats.get(beatNo);
		Map<String, List<Integer[]>> beatShape;
		if (fill == 0) {
			beatShape = beat.getSmallFill();
		} else if (fill == 1) {
			beatShape = beat.getBigFill();
		} else {
			beatShape = beat.getMainPattern();
		}
		Map<Integer, List<Integer[]>> c = new HashMap<>();
		for (String instr : beatShape.keySet()) {
			c.put(drumPrograms.get(instr), beatShape.get(instr));
		}
		setContent(c);
	}

	public static void setDrumPrograms(Map<String, Integer> drumPrograms) {
		BeatContainer.drumPrograms = drumPrograms;
	}

	public static List<Beat> getDrumBeats() {
		return drumBeats;
	}

	public static void setDrumBeats(List<Beat> drumBeats) {
		BeatContainer.drumBeats = drumBeats;
	}
}
