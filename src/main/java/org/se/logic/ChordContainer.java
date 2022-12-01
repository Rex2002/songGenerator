package org.se.logic;

import org.se.model.Chord;
import org.se.model.MidiPlayable;
import org.se.model.MusicalKey;

import java.util.*;

/**
 * @author Benjamin Frahm
 */
public class ChordContainer extends MidiPlayable {
    private final MusicalKey key;
    private final boolean isBassTrack;
    Chord[] chords;
    Chord[] inflatedChords;
    public ChordContainer(int trackNo, int bar, MusicalKey key, List<String> chords, boolean isBassTrack) {
        super(trackNo, bar);
        this.chords = new Chord[chords.size()];
        this.key = key;
        this.isBassTrack = isBassTrack;
        this.parseChordString(chords);
        setContent();
    }

    public ChordContainer(int trackNo, int bar, MusicalKey key, List<String> chords){
        this(trackNo,bar, key, chords, false);
    }

    public static List<List<List<String>>> getMatchingProgressions(List<String> reqChords) {
        List<List<List<String>>> returnProgressions = new ArrayList<>();
        boolean flag;
        for(List<List<String>> progression : Config.getChordProgressions()){
            flag = true;
            List<String> l = progression.stream().flatMap(Collection::stream).toList();
            for(String chord : reqChords){
                if(!l.contains(chord)){
                    flag = false;
                    break;
                }
            }
            if(flag){
                returnProgressions.add(progression);
            }
        }
        return returnProgressions;
    }

    public void parseChordString(List<String> chords){
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

    private void setContent(){
        Map<Integer, List<List<Integer>>> content = new HashMap<>();
        inflateChordList();
        for (int count = 0; count < 4; count++){
            List<Integer> singleChord = inflatedChords[count].getChord();
            for (Integer rootNote : singleChord) {
                List<Integer> posAndLen = new ArrayList<>();
                posAndLen.add(count * 24);
                posAndLen.add(24);
                if(isBassTrack){ rootNote = rootNote-24;}
                if (content.containsKey(rootNote)) {
                    content.get(rootNote).add(posAndLen);
                    if(isBassTrack){
                        break;
                    }
                } else {
                    List<List<Integer>> posAndLenList = new ArrayList<>();
                    posAndLenList.add(posAndLen);
                    if(isBassTrack){
                        content.put(rootNote, posAndLenList);
                        break;
                    }

                    content.put(rootNote, posAndLenList);
                }
            }
        }
        super.setContent(content);
    }
}
