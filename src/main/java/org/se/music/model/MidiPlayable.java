package org.se.music.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the base class that is used to construct objects,
 * which can be interpreted and translated to actual midi by the MidiSequence-class
 * </p>
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public abstract class MidiPlayable {
	private final int trackNo;
	private int bar;
	private Map<Integer, List<List<Integer>>> content = new HashMap<>();

	public MidiPlayable(int trackNo, int bar) {
		this.bar = bar;
		this.trackNo = trackNo;
	}

	public void setContent(Map<Integer, List<List<Integer>>> content) {
		this.content = content;
	}

	public Map<Integer, List<List<Integer>>> getContent() {
		return content;
	}

	public int getTrackNo() {
		return trackNo;
	}

	public int getBar() {
		return bar;
	}

	public void setBar(int bar) {
		this.bar = bar;
	}

	public Map<Integer, List<List<Integer>>> deepCopy() {
		Map<Integer, List<List<Integer>>> deepCopy = new HashMap<>();
		for (Integer note : content.keySet()) {
			List<List<Integer>> k = new ArrayList<>();
			for (List<Integer> innerList : content.get(note)) {
				List<Integer> l = new ArrayList<>(innerList);
				k.add(l);
			}
			deepCopy.put(note, k);
		}
		return deepCopy;
	}
}
