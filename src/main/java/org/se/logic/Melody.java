package org.se.logic;

import org.se.model.MidiPlayable;
import org.se.model.MusicalKey;
import org.se.model.PitchedPlayable;

import java.util.*;

public class Melody extends PitchedPlayable {

    private MidiPlayable[] theme;

    public Melody(int trackNo, int bar, MusicalKey key, List<String> chord) {
        super(trackNo, bar, key, chord);
        createTheme();
        setContent();
        transposeContent();
        //System.exit(0);
    }

    public void createTheme(){
        Map<Integer,List<List<Integer>>> content = new HashMap<>();
        Random ran = new Random();
        for(int i = 0; i < 8; i++){
            if(ran.nextInt(8) == 0 || (i%2 == 0 && ran.nextInt(4) != 0 )){
                Integer c = inflatedChords[i/2].getChord().get(ran.nextInt(inflatedChords[i/2].getChord().size()));
                List<Integer> l2 = new ArrayList<>();
                l2.add(i*12);
                l2.add(12+12*ran.nextInt(3));
                if(content.containsKey(c)){
                    content.get(c).add(l2);
                } else{
                    List<List<Integer>> l = new ArrayList<>();
                    l.add(l2);
                    content.put(c, l);
                }
            }
        }
        super.setContent(content);
    }
    public void setContent() {

        //super.setContent(content);
    }

    private void transposeContent(){
        if(getContent().isEmpty()){
            throw new RuntimeException("transpose content called with empty content");
        }Map<Integer,List<List<Integer>>> content = new HashMap<>();
        Random ran = new Random();
        for(int i = 0; i < 8; i++){
            if(ran.nextInt(8) == 0 || (i%2 == 0 && ran.nextInt(4) != 0 )){
                Integer c = inflatedChords[i/2].getChord().get(ran.nextInt(inflatedChords[i/2].getChord().size()));
                List<Integer> l2 = new ArrayList<>();
                l2.add(i*12);
                l2.add(12+12*ran.nextInt(3));
                if(content.containsKey(c)){
                    content.get(c).add(l2);
                } else{
                    List<List<Integer>> l = new ArrayList<>();
                    l.add(l2);
                    content.put(c, l);
                }
            }
        }
        HashMap<Integer, List<List<Integer>>> transposedContent = new HashMap<>();
        for(int note : getContent().keySet()){
            for(List<Integer> posAndLength : getContent().get(note)){
                List<Integer> noteAndLength = new ArrayList<>();
                noteAndLength.add(note);
                noteAndLength.add(posAndLength.get(1));
                if(transposedContent.containsKey(posAndLength.get(0))){
                    transposedContent.get(posAndLength.get(0)).add(noteAndLength);
                }
                else{
                    List<List<Integer>> tmp = new ArrayList<>();
                    tmp.add(noteAndLength);
                    transposedContent.put(posAndLength.get(0), tmp);
                }
            }
        }
        System.out.println("initial content: " + getContent());
        System.out.println("transposed content:" + transposedContent);
    }
}
