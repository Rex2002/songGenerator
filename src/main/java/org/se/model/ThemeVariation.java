package org.se.model;

import java.util.*;

public class ThemeVariation extends MidiPlayable {

    // some instruments swing better with smaller number of syllables (aka notes), others, like violin, can handle larger numbers without sounding crappy
    int minSyllablesPerBar = 6;
    Theme theme;
    Map<Integer, List<List<Integer>>> transposedContent;
    static Random ran = new Random();
    public ThemeVariation(Theme theme, int trackNo, int bar) {
        super(trackNo, bar);
        System.out.println("we are in bar " + bar);
        this.theme = theme;
        setContent(theme.deepCopy());
        createVariation();
    }
    public ThemeVariation(Theme theme, int trackNo, int bar, boolean variationFlag) {
        super(trackNo, bar);
        this.theme = theme;
        setContent(theme.deepCopy());
    }


    private void createVariation(){


        Map<Integer, List<List<Integer>>> themeContent = theme.transposedContent;
        int pos, posNextSmaller, posNextBigger, newNote, newLength;
        // artificially scaled the gamut one octave up

        List<Integer> posAndLength;
        for(int bar = 0; bar < getLengthInBars(); bar++){
            while(getNoteCountInBar(bar) < minSyllablesPerBar){
                pos = ran.nextInt(16)*6 + bar*96;
                posNextSmaller = getPosNextSmaller(pos);
                posNextBigger = getPosNextBigger(pos);
                if(themeContent.containsKey(posNextBigger) && themeContent.containsKey(posNextSmaller)) {
                    posAndLength = new ArrayList<>();
                    if (posNextBigger == pos) {
                        int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(), (themeContent.get(pos).get(0).get(0)));
                        newNote = gamut[(MusicalKey.findIndexOfNoteInScale(gamut, (themeContent.get(pos).get(0).get(0))) + 6) % 7];
                        newLength = themeContent.get(pos).get(0).get(1);
                        posAndLength.add(pos);
                        posAndLength.add(newLength);

                        addPosAndLengthToContent(newNote, posAndLength);
                    }
                    else if (posNextBigger == pos + 6) {
                        int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(), themeContent.get(pos + 6).get(0).get(0));
                        newNote = gamut[(MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(pos + 6).get(0).get(0)) + (7 - ran.nextInt(2))) % 7];
                        //newNote = themeContent.get(pos+6).get(0).get(0) - 3;
                        newLength = 6;
                        posAndLength.add(pos);
                        posAndLength.add(newLength);

                        addPosAndLengthToContent(newNote, posAndLength);
                    }
                    else if(posNextBigger - posNextSmaller >= 48 && themeContent.get(posNextBigger).get(0).get(0).equals(themeContent.get(posNextSmaller).get(0).get(0)) ){//&& ran.nextInt(2) == 0){
                        System.out.println("adding run@" + pos);
                        System.out.println("posNextBigger: " + posNextBigger);
                        System.out.println("posNextSmaller: " + posNextSmaller);
                        System.out.println("note at posBigger: " + themeContent.get(posNextBigger).get(0));
                        int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(),themeContent.get(posNextBigger).get(0).get(0));
                        int index = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextBigger).get(0).get(0));
                        if(index >= 4){
                            newNote = gamut[index-2];
                            newLength = 12;
                            posAndLength.add(posNextBigger - 24);
                            posAndLength.add(newLength);
                            addPosAndLengthToContent(newNote, posAndLength);
                            newNote = gamut[index-1];
                        }else{
                            newNote = gamut[index+2];
                            newLength = 12;
                            posAndLength.add(posNextBigger - 24);
                            posAndLength.add(newLength);
                            addPosAndLengthToContent(newNote, posAndLength);
                            newNote = gamut[index+1];
                        }
                        System.out.println("posAndLength before reworking: " + posAndLength);
                        posAndLength.set(0, posNextBigger-36);
                        addPosAndLengthToContent(newNote, posAndLength);
                        posAndLength.set(0, posNextBigger-12);
                        System.out.println("posAndLength after reworking:");
                        addPosAndLengthToContent(newNote, posAndLength);
                        // lauf
                    }else {
                        int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(), themeContent.get(posNextBigger).get(0).get(0));

                        int index1 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextBigger).get(0).get(0));
                        int index2 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextSmaller).get(0).get(0));
                        int newIndexInGamut = (index1 + index2) / 2;
                        newLength = posNextBigger - pos;
                        posAndLength.add(pos);
                        posAndLength.add(newLength);
                        newNote = gamut[newIndexInGamut];

                        addPosAndLengthToContent(newNote, posAndLength);
                        // make note be in the middle of them (in the scale tones)
                    }
                }
            }
        }
    }

    private void addPosAndLengthToContent(int newNote, List<Integer> posAndLength){
        List<Integer> posAndLengthCopy = new ArrayList<>(posAndLength);
        if(getContent().containsKey(newNote)){
            getContent().get(newNote).add(posAndLengthCopy);
        }
        else{
            List<List<Integer>> posAndLengthList = new ArrayList<>();
            posAndLengthList.add(posAndLengthCopy);
            getContent().put(newNote, posAndLengthList);
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
