package org.se.logic;

import org.se.model.Chord;
import org.se.model.MidiPlayable;
import org.se.model.MusicalKey;

import java.util.*;

/**
 * @author Malte Richert
 */

public class BassContainer extends MidiPlayable {
    private final MusicalKey key;
    private final Chord[] chords;
    private final Chord[] nextChords;
    public BassContainer(int trackNo, int bar, MusicalKey key, List<String> currentChords, List<String> nextChords) {
        super(trackNo, bar);
        this.key = key;
        this.chords = parseChordString(currentChords);
        this.nextChords = parseChordString(nextChords);
        setContent();
    }

    public Chord[] parseChordString(List<String> chords){
        Chord[] parsedChords = new Chord[chords.size()];
        for (int i = 0; i < chords.size(); i++) {
            int stair = Integer.parseInt(String.valueOf(chords.get(i).charAt(0)));
            String modifier = chords.get(i).substring(1);
            parsedChords[i] = new Chord(MusicalKey.getNotesInKey(key.getBaseNote())[stair], modifier);
        }
        return parsedChords;
    }

    private void setContent() {
        //creates steady eighth bass line on root notes with scalar transitions

        for (int count = 0; count < 8; count++){
            int indexInChord = count % (8/chords.length);
            int chordIndex = count / (8/chords.length);
            int rootNote = chords[chordIndex].getRootNote() % 12 + 36; // gets root of chord and translates it to octave starting with 36


            // get #eights each chord is played
            int chordEights = 8 / chords.length;

            // get distance to next chord's root
            Chord nextChord;
            if (chordIndex + 1 < chords.length) {
                nextChord = chords[chordIndex + 1];
            } else {
                nextChord = nextChords[0];
            }
            int[] scale = MusicalKey.getNotesInKey(key.getBaseNote());
            Map<Integer, Integer> descaler = new HashMap<>();
            for (int index = 0; index < scale.length; index++) {
                descaler.put(scale[index]%12, index);
            }
            int distance = (descaler.get(nextChord.getRootNote()%12) - descaler.get(rootNote%12)) %7;
            if (distance > 3){
                distance -= 7;
            } else if (distance < -3) {
                distance += 7;
            }

            // play rootNote for chordEights-distance-1 eights
            if (chordEights > indexInChord + (Math.abs(distance) -1)){
                addNoteToContent(count, rootNote);
                continue;
            }
            // play transition steps
            if (distance == 2 || (distance == 3 && chordEights-indexInChord == 2)){
                addNoteToContent(count,scale[(rootNote+1)%7]%12+36);
                continue;
            }
            if (distance == -2 || (distance == -3 && chordEights-indexInChord == 2)) {
                addNoteToContent(count, scale[(rootNote-1)%7]%12+36);
                continue;
            }
            if (distance == 3){
                addNoteToContent(count, scale[(rootNote+2)%7]%12+36);
                continue;
            }
            if (distance == -3){
                addNoteToContent(count, scale[(rootNote-2)%7]%12+36);
            }
        }
    }
    private void addNoteToContent(int count, int pitch){
        List<Integer> posAndLen = new ArrayList<>();
        posAndLen.add(count * 12);
        posAndLen.add(12);
        if (super.getContent().containsKey(pitch)) {
            super.getContent().get(pitch).add(posAndLen);
        } else {
            List<List<Integer>> posAndLenList = new ArrayList<>();
            posAndLenList.add(posAndLen);
            super.getContent().put(pitch, posAndLenList);
        }
    }
}
