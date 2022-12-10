package org.se.music.logic;

import org.se.Settings;
import org.se.music.Config;
import org.se.music.logic.playables.ChordContainer;
import org.se.music.logic.playables.MidiPlayable;
import org.se.music.model.*;
import org.se.text.analysis.TermCollection;
import org.se.text.generation.SongTextGenerator;

import java.util.*;

/**
 * Generates a full song based on the provided inputs
 * @author Malte Richert
 * @author Benjamin Frahm
 */
public class StructureGenerator {
	private static Structure structure;
	private static final Map<Integer, Integer> trackMapping = new HashMap<>();

	public static MidiSequence generateStructure(Settings settings, Map<String, Integer> metrics, TermCollection terms) {
		Random ran = new Random();
		structure = Config.getStructures().get(0);//ran.nextInt(Config.getStructures().size()));
		structure.setGenre(settings.getGenre());
		structure.setKey(new MusicalKey());
		if (settings.getTempo() != null) {
			structure.setTempo(settings.getTempo());
		} else {
			structure.setTempo(metrics.get("tempo"));
		}
		System.out.println(structure);

		SongTextGenerator textGenerator = new SongTextGenerator();
		HashMap<String,List<String[][]>> songText = textGenerator.generateSongText(structure, terms);

		MidiSequence seq = initMidiSequence(structure);

		structure.getPart(structure.getBasePartKey()).fillRandomly(structure.getKey(), trackMapping, structure.getGenre() == Genre.POP ? 4 : 12, songText.get(structure.getBasePartKey()).get(0));

		for (String partName : structure.getParts().keySet()) {
			if (partName.equals(structure.getBasePartKey())) {
				continue;
			}
			Part part = structure.getPart(partName);
			List<List<String>> progression;
			if (part.getRandomizationLevel() == 0) {
				progression = structure.getPart(structure.getBasePartKey()).getChords();
			} else if (part.getRandomizationLevel() == 1) {
				List<String> reqChords = getImportantChords(structure.getPart(structure.getBasePartKey()).getChords());
				List<List<List<String>>> matchingProgressions = ChordContainer.getMatchingProgressions(reqChords);
				progression = matchingProgressions.get(ran.nextInt(matchingProgressions.size()));
			} else {
				progression = Config.getChordProgressions().get(ran.nextInt(Config.getChordProgressions().size()));
			}
			part.fillPart(progression, structure.getKey(), trackMapping, structure.getGenre() == Genre.POP ? 4 : 12, songText.get(partName).get(0));
		}

		seq.setEnd(calculateLength());
		int barOffset = 0;
		for (String partName : structure.getOrder()) {
			for (MidiPlayable m : structure.getPart(partName).getMidiPlayables()) {
				m.setBar(m.getBar() + barOffset);
				seq.addMidiPlayable(m);
				m.setBar(m.getBar() - barOffset);
			}
			seq.addText(barOffset * 4, 0, partName);
			if(partContainsVocal(structure.getPart(partName))){
				Part p = structure.getPart(partName);
				MidiText t;
				for(int bar = 0; bar < p.getLength(); bar += 1){
					t = new MidiText(trackMapping.get(Config.getInstrumentMapping().get("vocals")), bar + barOffset,
							songText.get(partName).get(0)[bar][0]);
					System.out.println("adding midi text @ " + t.getBar());
					seq.addMidiText(t);
				}
			}
			for (MidiText t : structure.getPart(partName).getMidiTexts()) {
				t.setBar(t.getBar() + barOffset);
				seq.addMidiText(t);
				t.setBar(t.getBar() - barOffset);
			}
			barOffset += structure.getPart(partName).getLength();
		}
		return seq;
	}

	public static MidiSequence initMidiSequence(Structure s) {
		int currentTrackNo = 0;
		for (String partName : s.getParts().keySet()) {
			for (InstrumentEnum instrument : s.getParts().get(partName).getReqInsts()) {
				if (!trackMapping.containsKey(Config.getInstrumentMapping().get(instrument.toString()))) {
					trackMapping.put(Config.getInstrumentMapping().get(instrument.toString()), currentTrackNo);
					if (instrument.equals(InstrumentEnum.chords) || instrument.equals(InstrumentEnum.chords2)) {
						currentTrackNo++;
					}
					currentTrackNo++;
				}
			}
			for (InstrumentEnum instrument : s.getParts().get(partName).getOptInsts()) {
				if (!trackMapping.containsKey(Config.getInstrumentMapping().get(instrument.toString()))) {
					trackMapping.put(Config.getInstrumentMapping().get(instrument.toString()), currentTrackNo);
					if (instrument.equals(InstrumentEnum.chords) || instrument.equals(InstrumentEnum.chords2)) {
						currentTrackNo++;
					}
					currentTrackNo++;
				}
			}
		}

		MidiSequence seq = new MidiSequence(currentTrackNo);
		List<Integer> drumInstrs = getDrumInstrNo();
		for (Integer instr : trackMapping.keySet()) {
			if (drumInstrs.contains(instr)) {
				seq.setInstrument(instr, trackMapping.get(instr), true);
			} else {
				seq.setInstrument(instr, trackMapping.get(instr));
			}
			seq.setKey(structure.getKey().getBase(), trackMapping.get(instr));
			// seq.addNote(60, 0, 24, trackMapping.get(instr));
		}
		seq.setBPM(structure.getTempo());
		return seq;
	}

	private static int calculateLength() {
		int length = 0;
		for (String i : structure.getOrder()) {
			length += structure.getParts().get(i).getLength();
		}
		return length;
	}

	/**
	 * Returns most important chords by ordering: 0,4,3,2,1,5,6
	 * @param chords any list of chords in chord_progression template format
	 * @return list of two most important of the input chords
	 */
	private static List<String> getImportantChords(List<List<String>> chords) {
		Map<String, Integer> chordImportanceMap = Map.of("0", 0, "4", 1, "3", 2, "2", 3, "1", 4, "5", 5, "6", 6);
		List<String> importantChords = new ArrayList<>();
		for (List<String> bar : chords) {
			for (String chord : bar) {
				if (importantChords.contains(chord)) {
					continue;
				}
				if (importantChords.isEmpty()) {
					importantChords.add(chord);
				} else if (chordImportanceMap.get(chord.substring(0, 1)) < chordImportanceMap.get(importantChords.get(0).substring(0, 1))) {
					importantChords.add(0, chord);
					importantChords.remove(importantChords.size() - 1);
				} else if (importantChords.size() < 2
						|| chordImportanceMap.get(chord.substring(0, 1)) < chordImportanceMap.get(importantChords.get(1).substring(0, 1))) {
							if (importantChords.size() > 1) {
								importantChords.remove(1);
							}
							importantChords.add(1, chord);

						}
			}
		}
		return importantChords;
	}

	public static List<Integer> getDrumInstrNo() {
		List<Integer> d = new ArrayList<>();
		for (InstrumentEnum instr : InstrumentEnum.values()) {
			if (instr.toString().startsWith("drum")) {
				d.add(Config.getInstrumentMapping().get(instr.toString()));
			}
		}
		return d;
	}

	public static boolean partContainsVocal(Part part) {
		for (InstrumentEnum instr : part.getReqInsts()) {
			if (instr.toString().startsWith("vocals")) {
				return true;
			}
		}
		return false;
	}
}
