package org.se.logic;

import org.se.model.Chord;
import org.se.model.MidiPlayable;

import java.util.*;

public class ChordContainer extends MidiPlayable {
    private final int baseNote;
    Chord[] chords;
    Chord[] inflatedChords;
    public ChordContainer(int trackNo, int bar, int baseNote, String[] chords) {
        super(trackNo, bar);
        this.chords = new Chord[chords.length];
        this.baseNote = baseNote;
        this.parseChordString(chords);
        setContent();

    }
    public void parseChordString(String[] chords){
        for (int i = 0; i < chords.length; i++) {
            int stair = Integer.parseInt(String.valueOf(chords[i].charAt(0)));
            String modifier = chords[i].substring(1);
            this.chords[i] = new Chord(baseNote + stair, modifier);
        }
    }

    private void inflateChordList(){
        // TODO someone needs to check that I think...
        inflatedChords = new Chord[4];
        for(int chordNo = 0; chordNo < chords.length; chordNo ++){
            for(int i = 0; i < 4/chords.length; i ++){
                inflatedChords[4/chords.length*chordNo+i] = chords[chordNo];
            }
        }
    }

    private void setContent(){
        HashMap<Integer, ArrayList<ArrayList<Integer>>> content = new HashMap<>();
        inflateChordList();
        for (Chord c:inflatedChords) {
            System.out.println(c.getChord());
        }
        for (int chordNo = 0; chordNo < inflatedChords.length; chordNo++){
            ArrayList<Integer> singleChord = inflatedChords[chordNo].getChord();
            for (Integer integer : singleChord) {
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(chordNo * 24);
                tmp.add(24);
                if (content.containsKey(integer)) {
                    content.get(integer).add(tmp);
                } else {
                    ArrayList<ArrayList<Integer>> tmp2 = new ArrayList<>();
                    tmp2.add(tmp);
                    content.put(integer, tmp2);
                }
            }
        }
        super.setContent(content);
    }
}
