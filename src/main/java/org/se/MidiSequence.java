package org.se;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class MidiSequence {
    private String key = "Am";
    private Sequence seq;
    private Track[] t;
    private int drumTrack;
    //potentially turn into Track array to handle multiple tracks
    private final int PPQresolution = 24;
    private boolean endIsSet = false;

    public MidiSequence(int trackNumber){
        this(trackNumber+1, trackNumber);
    }
    public MidiSequence (int trackNumber, int drumTrackNumber){

        try {
            seq = new Sequence(Sequence.PPQ, PPQresolution);
            t = new Track[trackNumber];
            for(int i =0; i<t.length; i++){
                t[i] = seq.createTrack();
            }
            drumTrack = drumTrackNumber;
            setInstrument(116, drumTrack);
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void setBPM(int BPM){
        // speed calculation:
        // midi tempo message consists of three bytes, which contain the number of microseconds per quarter (mpq)
        // 60_000_000 / mpq = BPM
        // mpq = 60_000_000 / BPM
        // masking is done to split the value to its three 2-byte pairs
        try {
            BPM = 60_000_000 / BPM;
            MetaMessage mt = new MetaMessage();
        // byte[] bt = {0x07, (byte) 0xA1, 0x20}; 120 BPM
            byte[] bt = {(byte)((BPM & 0xFF0000) >> 16),(byte)((BPM & 0x00FF00) >> 8), (byte)((BPM & 0x0000FF))};
            mt.setMessage(0x51, bt, 3);
            MidiEvent me = new MidiEvent(mt, (long) 0);
            for(Track track : t){
                track.add(me);
            }

        }catch (InvalidMidiDataException e){
            e.printStackTrace();
        }

    }

    public void setKey(String key, String scale, int trackNumber) {
        // s: 0 -> major, 1 -> minor
        // for detailed information about the key-value mapping for the key see: https://www.recordingblogs.com/wiki/midi-key-signature-meta-message
        if (!(MusicalKey.musicalKeyMinor.containsKey(key))){
            throw new RuntimeException("illegal key");
        }
        byte s = (byte) (Objects.equals(scale, "m") ? 1 :0);
        byte k = s==1 ? MusicalKey.musicalKeyMinor.get(key) : MusicalKey.musicalKeyMajor.get(key);
        System.out.println("s: " + s);
        System.out.println("k: " + k);
        try {
            MetaMessage mt = new MetaMessage();

            mt.setMessage(0x59, new byte[]{k, s}, 0x02);
            MidiEvent me = new MidiEvent(mt, 0);
            t[trackNumber].add(me);
        }catch (InvalidMidiDataException e) {
            e.printStackTrace();

        }
    }

    public void setTrackName(String name) {
        try {
            MetaMessage mt = new MetaMessage();
            mt.setMessage(0x03, name.getBytes(), name.length());
        }catch (InvalidMidiDataException e){
            e.printStackTrace();
        }
    }

    public void setInstrument(int instrument, int trackNumber){
        try{
            ShortMessage mm = new ShortMessage();

            mm.setMessage(0xC0, instrument, 0x00);
            MidiEvent me = new MidiEvent(mm, 0);
           t[trackNumber].add(me);

        }catch (InvalidMidiDataException e){
            e.printStackTrace();
        }
    }

    public void setEnd(int bars){
        // length of the track in bars, converted to ticks with 4 quarters per bar and 24 ticks per quarter
        try {
            MetaMessage mt = new MetaMessage();
            byte[] bet = {}; // empty array
            mt.setMessage(0x2F,bet,0);
            MidiEvent me = new MidiEvent(mt, (long)bars*24*4);
            for(Track track : t){
                track.add(me);
            }

        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            return;
        }
        endIsSet = true;
    }

    public void addText(int position, int trackNumber, String text){
        try{
            MetaMessage mt = new MetaMessage();
            mt.setMessage(0x01, text.getBytes(), text.length());
            MidiEvent me = new MidiEvent(mt, position* 24L);
            t[trackNumber].add(me);
        }catch (InvalidMidiDataException e){
            e.printStackTrace();
            return;
        }
        return;
    }

    public void addNote(int note, long start, long length, int trackNumber){
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
        }
    }

    public void addChord(Chord chord, long start, long length, int trackNumber){
        //System.out.println("chord: " + chord.getBaseNote() + ", modifiers: " + Arrays.toString(chord.getChordModifier()));

        for (int modifier: chord.getChordModifier()) {
            addNote(chord.getBaseNote() + modifier, start, length, trackNumber);
        }
    }

    public void addBeat(DrumBeat beat, int bar){
        HashMap<Integer, ArrayList> beatContent = beat.getContent();
        for(int drumNo : beatContent.keySet()){
            System.out.println(beatContent.get(drumNo));
            for(Object o : beatContent.get(drumNo)){
                addNote(drumNo,bar* 96L +((Integer) ((ArrayList<?>) o).get(0)).longValue(), ((Integer) ((ArrayList<?>) o).get(1)).longValue(), drumTrack);
            }

        }

    }

    public void createFile(String filename){
        if (!endIsSet){
            return;
        }
        try{
            File f = new File("midi-file.mid");
            MidiSystem.write(seq,1,f);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
