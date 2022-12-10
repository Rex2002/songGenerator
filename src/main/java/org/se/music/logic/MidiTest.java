package org.se.music.logic;

import org.se.music.model.Genre;
import org.se.music.model.MidiPlayable;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * @deprecated
 * @author Benjamin Frahm
 */

public class MidiTest {

	public static Random ran = new Random();
	public static int[] keyM = { 0, 2, 3, 5, 7, 8, 10, 12 };

	public static void main(String[] args) {

		playMidiSeq();
		System.exit(0);
		Config.loadConfig(Genre.POP);

		MidiSequence m = new MidiSequence(10);
		int drumTrackNo = 9;
		m.setInstrument(118, drumTrackNo);
		m.setInstrument(1, 0);
		m.setBPM(80);
		m.setEnd(40);
		m.setTrackName("testTrack");
		testDrumBeats(m, drumTrackNo);
		addRandomMelody(m, 0);
		addRandomMelody(m, 1);
		// MidiPlayable midiPlayable = new ChordContainer(0,0,60,new String[]{"0maj7", "6min"});
		// m.addMidiPlayable(midiPlayable);
		// for(int i = 0; i<12; i++){
		// m.addChord(new Chord(60, "maj"), 24 * 4 * i,12,0);
		// m.addChord(new Chord(69, "m"), 24 * 4 * i + 24,24,0);
		// m.addChord(new Chord(67, "maj"), 24 * 4 * i + 48,12,0);
		// m.addChord(new Chord(62, "maj"), 24 * 4 * i + 72,24,0);
		//
		//
		// }
		//
		// m.addBeat(k1, 0);
		m.createFile("midi test");
	}

	public static void addRandomMelody(MidiSequence m, int trackNo) {
		int noteOffset = 0;
		int baseNote = 60;
		for (int i = 0; i < 12 * 8; i += 1) {
			if (ran.nextBoolean()) {
				if (!(ran.nextBoolean() && ran.nextBoolean())) {
					noteOffset = keyM[ran.nextInt(keyM.length)];
				}
				m.addNote(baseNote + noteOffset, i * 12, 8L * ran.nextInt(1, 4), trackNo);
			}
		}
	}

	public static void testDrumBeats(MidiSequence m, int drumTrackNo) {
		m.setInstrument(118, drumTrackNo);
		m.setEnd(BeatContainer.getDrumBeats().size() * 3 + 1);
		for (int i = 0; i < BeatContainer.getDrumBeats().size() - 1; i++) {
			MidiPlayable beat = new BeatContainer(i, 3 * i, drumTrackNo);
			m.addMidiPlayable(beat);
			beat = new BeatContainer(i, 3 * i + 1, 0, drumTrackNo);
			m.addMidiPlayable(beat);
			beat = new BeatContainer(i, 3 * i + 2, 1, drumTrackNo);
			m.addMidiPlayable(beat);
		}
	}

	public static void playMidiSeq() {
		try {
			Sequencer sequencer = MidiSystem.getSequencer(); // Get the default Sequencer
			if (sequencer == null) {
				System.err.println("Sequencer device not supported");
				return;
			}
			sequencer.open(); // Open device
			// Create sequence, the File must contain MIDI file data.
			System.out.println(System.getProperties());
			Sequence sequence = MidiSystem.getSequence(new File("./structureTest.mid"));
			sequencer.setSequence(sequence); // load it into sequencer
			sequencer.start(); // start the playback
			Thread.sleep(10000);
		} catch (MidiUnavailableException | InvalidMidiDataException | IOException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	@Deprecated
	public static void playMidiSounds() throws InterruptedException {
		try {
			Synthesizer midiSynth = MidiSystem.getSynthesizer();
			midiSynth.open();
			for (Instrument i : midiSynth.getAvailableInstruments()) {
				System.out.println(i.getName());
				System.out.println(i.getPatch().getProgram());
			}
			// get and load default instrument and channel lists
			// Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();

			MidiChannel[] mChannels = midiSynth.getChannels();
			midiSynth.loadAllInstruments(midiSynth.getDefaultSoundbank());

			int noteOffset = 0;
			int baseNote = 46 + ran.nextInt(12);
			mChannels[10].programChange(116);

			for (int i = 0; i < 128; i++) {
				if (!(ran.nextBoolean() && ran.nextBoolean())) {
					noteOffset = keyM[ran.nextInt(keyM.length)];
				}

				mChannels[10].noteOn(baseNote + noteOffset, 100);
				Thread.sleep(250 + 250 * ran.nextInt(3));
				mChannels[10].noteOff(noteOffset + noteOffset);
			}
			// mChannels[0].noteOn(60, 100);//On channel 0, play note number 60 with velocity 100
			// Thread.sleep(1000);
			// mChannels[1].noteOn(65, 100);
			// Thread.sleep(1000); // wait time in milliseconds to control duration
			// mChannels[0].noteOff(60);//turn of the note
			// mChannels[1].noteOn(65, 100);

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

}
