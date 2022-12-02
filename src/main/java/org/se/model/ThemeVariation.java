package org.se.model;

import java.util.*;

public class ThemeVariation extends MidiPlayable {

    int minSyllablesPerBar = 7;
    Theme theme;
    Map<Integer, List<List<Integer>>> transposedContent;
    static Random ran = new Random();
    public ThemeVariation(Theme theme, int trackNo, int bar) {
        super(trackNo, bar);
        this.theme = theme;
        System.out.println("called theme variation");
        setContent(theme.deepCopy());
        createVariation();
    }

    private void createVariation(){


        Map<Integer, List<List<Integer>>> themeContent = theme.transposedContent;
        int pos, posNextSmaller, posNextBigger, newNote, newLength;
        // artificially scaled the gamut one octave up
        int[] gamut = MusicalKey.getNotesInKey(theme.getKey().getBaseNote());

        List<Integer> posAndLength;
        for(int bar = 0; bar < getLengthInBars(); bar++){
            while(getNoteCountInBar(bar) < minSyllablesPerBar){
                pos = ran.nextInt(16)*6 + bar*96;
                posNextSmaller = getPosNextSmaller(pos);
                posNextBigger = getPosNextBigger(pos);
                if(themeContent.containsKey(posNextBigger) && themeContent.containsKey(posNextSmaller)) {
                    posAndLength = new ArrayList<>();
                    if (posNextBigger == pos) {
                        newNote = gamut[MusicalKey.findIndexOfNoteInScale(gamut, (themeContent.get(pos).get(0).get(0))+gamut.length-1)% gamut.length];
                        newLength = themeContent.get(pos).get(0).get(1);
                        posAndLength.add(pos);
                        posAndLength.add(newLength);

                        if(getContent().containsKey(newNote)){
                            getContent().get(newNote).add(posAndLength);
                        }
                        else{
                            List<List<Integer>> posAndLengthList = new ArrayList<>();
                            posAndLengthList.add(posAndLength);
                            getContent().put(newNote, posAndLengthList);
                        }
                    }
                    if (posNextBigger == pos + 6) {
                        newNote = gamut[MusicalKey.findIndexOfNoteInScale(gamut, (themeContent.get(pos+6).get(0).get(0))+gamut.length-1)% gamut.length];
                        //newNote = themeContent.get(pos+6).get(0).get(0) - 3;
                        newLength = 6;
                        posAndLength.add(pos);
                        posAndLength.add(newLength);

                        if(getContent().containsKey(newNote)){
                            getContent().get(newNote).add(posAndLength);
                        }
                        else{
                            List<List<Integer>> posAndLengthList = new ArrayList<>();
                            posAndLengthList.add(posAndLength);
                            getContent().put(newNote, posAndLengthList);
                        }
                        // make note ...
                    } else {
                        int index1 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextBigger).get(0).get(0));
                        int index2 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextSmaller).get(0).get(0));
                        int newIndexInGamut = (index1 + index2) / 2;
                        newLength = posNextBigger - pos;
                        posAndLength.add(pos);
                        posAndLength.add(newLength);
                        newNote = gamut[newIndexInGamut];


                        if(getContent().containsKey(newNote)){
                            getContent().get(newNote).add(posAndLength);
                        }
                        else{
                            List<List<Integer>> posAndLengthList = new ArrayList<>();
                            posAndLengthList.add(posAndLength);
                            getContent().put(newNote, posAndLengthList);
                        }
                        // make note be in the middle of them (in the scale tones)
                    }
                }
            }
        }
    }

    public int getPosNextSmaller(int posToFind){
        int smallerPos = 0;
        for(Integer pos : theme.transposedContent.keySet()){
            if(pos < posToFind && smallerPos < pos){
                smallerPos = pos;
            }
        }
        return smallerPos;
    }

    public int getPosNextBigger(int posToFind){
        int biggerPos = getLengthInBars() * 96;
        for(Integer pos : theme.transposedContent.keySet()){
            if(pos >= posToFind && biggerPos > pos){
                biggerPos = pos;
            }
        }
        return biggerPos;
    }

    public int getNoteCountInBar(int bar){
        int count = 0;

        setTransposedContent();
        for(Integer pos : transposedContent.keySet()){
            if( bar * 96 <= pos && pos < (bar+1) * 96){
                for(List<Integer> ignored : transposedContent.get(pos)){
                    count++;
                }
            }
        }
        return count;
    }

    public void setTransposedContent() {
        transposedContent = new HashMap<>();
        for (int note : getContent().keySet()) {
            for (List<Integer> posAndLength : getContent().get(note)) {
                List<Integer> noteAndLength = new ArrayList<>();
                noteAndLength.add(note);
                noteAndLength.add(posAndLength.get(1));
                if (transposedContent.containsKey(posAndLength.get(0))) {
                    transposedContent.get(posAndLength.get(0)).add(noteAndLength);
                } else {
                    List<List<Integer>> tmp = new ArrayList<>();
                    tmp.add(noteAndLength);
                    transposedContent.put(posAndLength.get(0), tmp);
                }
            }
        }
    }

    int getLengthInBars(){
        return 4;
    }
}
