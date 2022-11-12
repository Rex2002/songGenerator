package org.se;
import javax.sound.midi.*;
import java.io.*;
import java.util.Random;

public class MidiTest {

    public static Random ran = new Random();
    public static int[] keyM = {0,2,3,5,7,8,10,12};
    public static void main(String[] args) throws InterruptedException {
        MidiSequence m = new MidiSequence( 2);
        m.setKey("C", "M", 0);
        m.setKey("C", "M", 1);


        m.setBPM(150, 0);
        m.setEnd(40, 0);
        m.setInstrument(1, 0);
        m.setTrackName("testTrack");

        m.setBPM(150, 1);
        m.setEnd(40, 1);
        m.setInstrument(1, 1);

        m.addText(0, 0, "epic triangle showdown ");
        for(int i = 0; i<12; i++){
            m.addChord(new Chord(60, "maj"), 24 * 4 * i,12,0);
            m.addChord(new Chord(69, "m"), 24 * 4 * i + 24,24,0);
            m.addChord(new Chord(67, "maj"), 24 * 4 * i + 48,12,0);
            m.addChord(new Chord(62, "maj"), 24 * 4 * i + 72,24,0);
        //    m.addNote(30+i/24, i, 24, 0);
        }
        int noteOffset = 0;
        int baseNote = 60;
        for (int i = 0; i< 12 * 8; i+=1) {
            if (ran.nextBoolean()){
                if (!(ran.nextBoolean() && ran.nextBoolean())) {
                    noteOffset = keyM[ran.nextInt(keyM.length)];
                }
                m.addNote(baseNote + noteOffset, i*12, 8L * ran.nextInt(1,4),1);
            }

        }
        m.createFile("midi test");
    }

    public static void testTrack() throws InterruptedException{
        System.out.println("midifile begin ");
        try {
//****  Create a new MIDI sequence with 24 ticks per beat  ****
            Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,24);

//****  Obtain a MIDI track from the sequence  ****
            Track t = s.createTrack();

//****  General MIDI sysex -- turn on General MIDI sound set  ****
            byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
            SysexMessage sm = new SysexMessage();
            sm.setMessage(b, 6);
            MidiEvent me = new MidiEvent(sm, 0);
            t.add(me);

//****  set tempo (meta event)  ****
            MetaMessage mt = new MetaMessage();
            byte[] bt = {0x07, (byte) 0xA1, 0x20};
            mt.setMessage(0x51 ,bt, 3);
            me = new MidiEvent(mt,(long)0);
            t.add(me);

//****  set track name (meta event)  ****
            mt = new MetaMessage();
            String TrackName = new String("midifile track");
            mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
            me = new MidiEvent(mt,(long)0);
            t.add(me);

//****  set omni on  ****
            ShortMessage mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7D,0x00);
            me = new MidiEvent(mm,(long)0);
            t.add(me);

//****  set poly on  ****
            mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7F,0x00);
            me = new MidiEvent(mm,(long)0);
            t.add(me);

//****  set instrument to Piano  ****
            mm = new ShortMessage();
            mm.setMessage(0xC0, 0x4F, 0x00);
            me = new MidiEvent(mm,(long)0);
            t.add(me);

//****  note on - middle C  ****
            mm = new ShortMessage();
            mm.setMessage(0x90,0x4C,0x60);
            me = new MidiEvent(mm,(long)1);
            t.add(me);

            mm = new ShortMessage();
            mm.setMessage(0x90,0x3C,0x60);
            me = new MidiEvent(mm,(long)1);
            t.add(me);

//****  note off - middle C - 120 ticks later  ****
            mm = new ShortMessage();
            mm.setMessage(0x80,0x4C,0x40);
            me = new MidiEvent(mm,(long)121);
            t.add(me);

            mm = new ShortMessage();
            mm.setMessage(0x80,0x3C,0x40);
            me = new MidiEvent(mm,(long)25);
            t.add(me);

//****  set end of track (meta event) 19 ticks later  ****
            mt = new MetaMessage();
            byte[] bet = {}; // empty array
            mt.setMessage(0x2F,bet,0);
            me = new MidiEvent(mt, (long)140);
            t.add(me);

//****  write the MIDI sequence to a MIDI file  ****
            File f = new File("midifile.mid");
            MidiSystem.write(s,1,f);
        } //try
        catch(Exception e)
        {
            System.out.println("Exception caught " + e);
        } //catch
        System.out.println("midifile end ");

//        playMidiSounds();
    } //main

    public static void playMidiSounds() throws InterruptedException{
        try{
            Synthesizer midiSynth = MidiSystem.getSynthesizer();
            midiSynth.open();

            //get and load default instrument and channel lists
            Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
            MidiChannel[] mChannels = midiSynth.getChannels();
            midiSynth.loadAllInstruments(midiSynth.getDefaultSoundbank());

            int noteOffset = 0;
            int baseNote = 46 + ran.nextInt(12);
            mChannels[0].programChange(14);
            for (int i = 0; i< 100; i++) {
                if (!(ran.nextBoolean() && ran.nextBoolean())) {
                    noteOffset = keyM[ran.nextInt(keyM.length)];
                }

                mChannels[0].noteOn(baseNote + noteOffset, 100);
                Thread.sleep(250 + 250*ran.nextInt(3));
                mChannels[0].noteOff(noteOffset + noteOffset);
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
