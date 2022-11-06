package org.se;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiSequence {
    private String key = "Am";
    private Sequence seq;
    private Track[] t;
    //potentially turn into Track array to handle multiple tracks
    private final int PPQresolution = 24;
    private boolean endIsSet = false;

    public MidiSequence (String key, int trackNumber){
        this.key = key;

        try {
            seq = new Sequence(Sequence.PPQ, PPQresolution);
            t = new Track[trackNumber];
            for(int i =0; i<t.length; i++){
                t[i] = seq.createTrack();

            }
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public boolean setBPM(int BPM, int trackNumber){
        try {
            MetaMessage mt = new MetaMessage();
            byte[] bt = {0x07, (byte) 0xA1, 0x20};
            mt.setMessage(0x51, bt, 3);
            MidiEvent me = new MidiEvent(mt, (long) 0);
           t[trackNumber].add(me);

        }catch (InvalidMidiDataException e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean setTrackName(String name) {
        try {
            MetaMessage mt = new MetaMessage();
            mt.setMessage(0x03, name.getBytes(), name.length());
        }catch (InvalidMidiDataException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean setInstrument(int instrument, int trackNumber){
        try{
            ShortMessage mm = new ShortMessage();

            mm.setMessage(0xC0, instrument, 0x00);
            MidiEvent me = new MidiEvent(mm,(long)0);
           t[trackNumber].add(me);

        }catch (InvalidMidiDataException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean setEnd(int bars, int trackNumber){
        // length of the track in bars, converted to ticks with 4 quartes per bar and 24 ticks per quarter
        try {
            MetaMessage mt = new MetaMessage();
            byte[] bet = {}; // empty array
            mt.setMessage(0x2F,bet,0);
            MidiEvent me = new MidiEvent(mt, (long)bars*24*4);
           t[trackNumber].add(me);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            return false;
        }
        endIsSet = true;
        return true;


    }

    public boolean addNote(int note, long start, long length, int trackNumber){
        // currently start and length are in ticks
        try {
            // note on event (0x90)
            ShortMessage mm = new ShortMessage();
            mm.setMessage(0x90, note, 0x60);
            MidiEvent me = new MidiEvent(mm, start);
           t[trackNumber].add(me);

            // note off event (0x80)
            mm = new ShortMessage();
            mm.setMessage(0x80, note, 0x40);
            me = new MidiEvent(mm, start + length);
           t[trackNumber].add(me);
        }catch (InvalidMidiDataException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean createFile(String filename){
        if (!endIsSet){
            return false;
        }
        try{
            File f = new File("midifile.mid");
            MidiSystem.write(seq,1,f);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
