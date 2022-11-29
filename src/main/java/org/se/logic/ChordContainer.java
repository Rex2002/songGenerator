package org.se.logic;

import org.se.model.Chord;
import org.se.model.MidiPlayable;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class ChordContainer extends MidiPlayable {
    private final int baseNote;
    private final boolean isBassTrack;
    Chord[] chords;
    Chord[] inflatedChords;
    public ChordContainer(int trackNo, int bar, int baseNote, List<String> chords, boolean isBassTrack) {
        super(trackNo, bar);
        this.chords = new Chord[chords.size()];
        this.baseNote = baseNote;
        this.isBassTrack = isBassTrack;
        this.parseChordString(chords);
        setContent();
    }

    public ChordContainer(int trackNo, int bar, int baseNote, List<String> chords){
        this(trackNo,bar, baseNote, chords, false);
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
            this.chords[i] = new Chord(baseNote + stair, modifier);
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
        HashMap<Integer, ArrayList<ArrayList<Integer>>> content = new HashMap<>();
        inflateChordList();
        for (int chordNo = 0; chordNo < inflatedChords.length; chordNo++){
            ArrayList<Integer> singleChord = inflatedChords[chordNo].getChord();
            for (Integer integer : singleChord) {
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(chordNo * 24);
                tmp.add(24);
                if(isBassTrack){ integer = integer-26;}
                if (content.containsKey(integer)) {
                    content.get(integer).add(tmp);
                    if(isBassTrack){
                        break;
                    }
                } else {
                    ArrayList<ArrayList<Integer>> tmp2 = new ArrayList<>();
                    tmp2.add(tmp);
                    if(isBassTrack){
                        content.put(integer, tmp2);
                        break;
                    }

                    content.put(integer, tmp2);
                }
            }
        }
        super.setContent(content);
    }
}
