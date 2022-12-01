package org.se.model;

import java.util.List;


/**
 * @author Benjamin Frahm
 */
abstract public class PitchedPlayable extends MidiPlayable{
    protected Chord[] chords;
    protected Chord[] inflatedChords;
    protected MusicalKey key;

    public PitchedPlayable(int trackNo, int bar, MusicalKey key, List<String> chord) {
        super(trackNo, bar);
        this.key = key;
        parseChordString(chord);
        inflateChordList();
    }

    public void parseChordString(List<String> chords){
        this.chords = new Chord[chords.size()];
        for (int i = 0; i < chords.size(); i++) {
            int stair = Integer.parseInt(String.valueOf(chords.get(i).charAt(0)));
            String modifier = chords.get(i).substring(1);
            this.chords[i] = new Chord(MusicalKey.getNotesInKey(key.getBaseNote())[stair], modifier);
        }
    }

    private void inflateChordList(){
        inflatedChords = new Chord[4];
        for(int chordNo = 0; chordNo < chords.length; chordNo ++){
            for(int i = 0; i < 4/chords.length; i ++){
                inflatedChords[4/chords.length*chordNo+i] = chords[chordNo];
            }
        }
    }
    // parseChordString
    // inflateChordList
    // constructor: MusicalKey-Attribute, chords
}
