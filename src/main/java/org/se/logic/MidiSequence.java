package org.se.logic;

import org.se.model.Chord;
import org.se.model.MidiPlayable;
import org.se.model.MusicalKey;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MidiSequence {
    private static final int PPQ_RESOLUTION = 24;
    private Sequence seq;
    private Track[] t;
    private boolean endIsSet = false;

    public MidiSequence (int trackNumber){
        try {
            seq = new Sequence(Sequence.PPQ, PPQ_RESOLUTION);

            for(int i =0; i<trackNumber; i++){
                seq.createTrack();
            }
            t = seq.getTracks();
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    /**
     * the formula to get the midi-format speed-values is 60_000_000 / BPM = microsecondsPerQuarter = mps.
     * The mps value is stored in three bytes as part of a meta-midi-message.
     * the weird line byte[] bt = ... does not have to be understood.
     * @param BPM specifies the BPM for the final file
     */
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
            MidiEvent me = new MidiEvent(mt, 0);
            for(Track track : t){
                track.add(me);
            }

        }catch (InvalidMidiDataException e){
            e.printStackTrace();
        }

    }

    /**
     * for detailed information about the key-value mapping for the key see: <a href="https://www.recordingblogs.com/wiki/midi-key-signature-meta-message">...</a>
     * @param key - the musical key
     * @param scale - the scale, minor or major, 0 -> major, 1 -> minor
     * @param trackNumber - the track number, for which the key is meant to be set
     */
    public void setKey(String key, String scale, int trackNumber) {

        if (!(MusicalKey.musicalKeyMinor.containsKey(key) || MusicalKey.musicalKeyMajor.containsKey(key))){
            throw new RuntimeException("illegal key" + key);
        }
        byte s = (byte) (Objects.equals(scale, "min") ? 1 :0);
        byte k = s==1 ? MusicalKey.musicalKeyMinor.get(key) : MusicalKey.musicalKeyMajor.get(key);
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
        if (endIsSet){
            System.out.println("end is already set");
            return;
        }
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
        }
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

    @Deprecated
    public void addChord(Chord chord, long start, long length, int trackNumber){
        for (int modifier: chord.getChordModifier()) {
            addNote(chord.getBaseNote() + modifier, start, length, trackNumber);
        }
    }

    public void addMidiPlayable(MidiPlayable m){
        HashMap<Integer, ArrayList<ArrayList<Integer>>> content = m.getContent();
        int bar = m.getBar();
        int track = m.getTrackNo();
        for(int instrNo : content.keySet()){
            for(ArrayList<Integer> o : content.get(instrNo)){
                addNote(instrNo,bar * 96L +o.get(0).longValue(), o.get(1).longValue(), track);
            }
        }
    }

    @Deprecated
    public void addBeat(BeatContainer beat, int bar){
        HashMap<Integer, ArrayList<ArrayList<Integer>>> beatContent = beat.getContent();
        for(int drumNo : beatContent.keySet()){
            for(ArrayList<Integer> o : beatContent.get(drumNo)){
                addNote(drumNo,bar * 96L +o.get(0).longValue(), o.get(1).longValue(), 0);
            }
        }
    }

    public void createFile(String filename){
        if (!endIsSet){
            return;
        }
        try{
            File f = new File(filename+".mid");
            MidiSystem.write(seq,1,f);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}