package org.se.music.model;

import java.util.List;
import java.util.Map;

/**
 * Class modelling a parsed drumbeat.
 * Parsing is done by the BeatData-class
 * @author Malte Richert
 * @reviewer Benjamin Frahm
 */

public class Beat {
	Map<String, List<List<Integer>>> mainPattern;
	Map<String, List<List<Integer>>> bigFill;
	Map<String, List<List<Integer>>> smallFill;

	public Beat(Map<String, List<List<Integer>>> mainPattern,
			Map<String, List<List<Integer>>> bigFill,
			Map<String, List<List<Integer>>> smallFill) {

		this.mainPattern = mainPattern;
		this.bigFill = bigFill;
		this.smallFill = smallFill;
	}

	public Map<String, List<List<Integer>>> getMainPattern() {
		return mainPattern;
	}

	public Map<String, List<List<Integer>>> getBigFill() {
		return bigFill;
	}

	public Map<String, List<List<Integer>>> getSmallFill() {
		return smallFill;
	}
}