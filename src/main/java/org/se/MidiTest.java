package org.se;
import javax.sound.midi.*;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class MidiTest {

    public static Random ran = new Random();
    public static int[] keyM = {0,2,3,5,7,8,10,12};
    public static void main(String[] args) throws InterruptedException {
        DrumBeat k = new DrumBeat(0);
        DrumBeat k1 = new DrumBeat(1);
        Chord c = new Chord(0,"maj");

        //System.exit(0);
        MidiSequence m = new MidiSequence( 2);
        m.setBPM(150);
        m.setEnd(40);
        m.setTrackName("testTrack");

//        m.setKey("C", "M", 0);
//        m.setInstrument(1, 0);
//        m.setKey("C", "M", 1);
//        m.setInstrument(1, 1);
        m.addBeat(k1, 1);
        m.addBeat(k, 2);
//
//        m.addText(0, 0, "epic triangle showdown ");
//        for(int i = 0; i<12; i++){
//            m.addChord(new Chord(60, "maj"), 24 * 4 * i,12,0);
//            m.addChord(new Chord(69, "m"), 24 * 4 * i + 24,24,0);
//            m.addChord(new Chord(67, "maj"), 24 * 4 * i + 48,12,0);
//            m.addChord(new Chord(62, "maj"), 24 * 4 * i + 72,24,0);
//        //    m.addNote(30+i/24, i, 24, 0);
//        }
//        int noteOffset = 0;
//        int baseNote = 60;
//        for (int i = 0; i< 12 * 8; i+=1) {
//            if (ran.nextBoolean()){
//                if (!(ran.nextBoolean() && ran.nextBoolean())) {
//                    noteOffset = keyM[ran.nextInt(keyM.length)];
//                }
//                m.addNote(baseNote + noteOffset, i*12, 8L * ran.nextInt(1,4),1);
//            }
//
//        }
        m.createFile("midi test");
    }

    public static void playMidiSounds() throws InterruptedException{
        try{
            Synthesizer midiSynth = MidiSystem.getSynthesizer();
            midiSynth.open();
            for(Instrument i : midiSynth.getAvailableInstruments()) {
                System.out.println(i.getName());
                System.out.println(i.getPatch().getProgram());
            }
            //get and load default instrument and channel lists
            Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();

            MidiChannel[] mChannels = midiSynth.getChannels();
            midiSynth.loadAllInstruments(midiSynth.getDefaultSoundbank());

            int noteOffset = 0;
            int baseNote = 46 + ran.nextInt(12);
            mChannels[10].programChange(116);

            for (int i = 0; i< 128; i++) {
                if (!(ran.nextBoolean() && ran.nextBoolean())) {
                    noteOffset = keyM[ran.nextInt(keyM.length)];
                }

                mChannels[10].noteOn(baseNote + noteOffset, 100);
                Thread.sleep(250 + 250*ran.nextInt(3));
                mChannels[10].noteOff(noteOffset + noteOffset);
            }
//
//            mChannels[0].noteOn(60, 100);//On channel 0, play note number 60 with velocity 100
//            Thread.sleep(1000);
//            mChannels[1].noteOn(65, 100);
//            Thread.sleep(1000); // wait time in milliseconds to control duration
//            mChannels[0].noteOff(60);//turn of the note
//            mChannels[1].noteOn(65, 100);


        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

}
