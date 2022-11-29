package org.se.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MusicalKey {

    public static int[] keyMin = {0,2,3,5,7,8,10};
    public static int[] keyMaj = {0,2,4,5,7,9,11};
    static String[] scales = {"min", "maj"};
    private final int baseNote;
    private String base;
    private final String scale;

    public static int[] getKeyNotes(int baseNote, String scale){
        int[] k = new int[6];
        for (int i = 0; i < keyMaj.length; i++) {
            k[i] = baseNote + (scale.equals("min") ? keyMin[i] : keyMaj[i]);
        }
        return k;
    }

    @Deprecated
    public MusicalKey(int baseNote, String scale){
        this.baseNote = baseNote;
        this.scale = scale;
    }

    public MusicalKey(){
        this.scale = scales[new Random().nextInt(2)];
        if(scale.equals("min")){
            base = (String) musicalKeyMinor.keySet().toArray()[new Random().nextInt(musicalKeyMinor.keySet().size())];
        }
        else{
            base = (String) musicalKeyMajor.keySet().toArray()[new Random().nextInt(musicalKeyMajor.keySet().size())];
        }
        this.baseNote = translateNoteStringToValue(base);
    }

    public int getBaseNote() {
        return baseNote;
    }

    public String getBase() {
        return base;
    }

    public String getScale() {
        return scale;
    }

    public static int translateNoteStringToValue(String s){
        return musicalStringToNoteValue.get(s.charAt(0)) + (s.length() == 1 ? 0 :
                (s.charAt(1) == 'b' ? -1 : 1));
    }

    public static final Map<String, Byte> musicalKeyMajor= new HashMap<>();
    static {
        musicalKeyMajor.put("Cb", (byte) -7);
        musicalKeyMajor.put("Gb", (byte) -6);
        musicalKeyMajor.put("Db", (byte) -5);
        musicalKeyMajor.put("Ab", (byte) -4);
        musicalKeyMajor.put("Eb", (byte) -3);
        musicalKeyMajor.put("Bb", (byte) -2);
        musicalKeyMajor.put("F", (byte) -1);
        musicalKeyMajor.put("C", (byte) 0);
        musicalKeyMajor.put("G", (byte) 1);
        musicalKeyMajor.put("D", (byte) 2);
        musicalKeyMajor.put("A", (byte) 3);
        musicalKeyMajor.put("E", (byte) 4);
        musicalKeyMajor.put("B", (byte) 5);
        musicalKeyMajor.put("Fs", (byte) 6);
        musicalKeyMajor.put("Cs", (byte) 7);

    }
    public static final Map<String, Byte> musicalKeyMinor = new HashMap<>();
    static {
        musicalKeyMinor.put("Ab", (byte) -7);
        musicalKeyMinor.put("Eb", (byte) -6);
        musicalKeyMinor.put("Bb", (byte) -5);
        musicalKeyMinor.put("F", (byte) -4);
        musicalKeyMinor.put("C", (byte) -3);
        musicalKeyMinor.put("G", (byte) -2);
        musicalKeyMinor.put("D", (byte) -1);
        musicalKeyMinor.put("A", (byte) 0);
        musicalKeyMinor.put("E", (byte) 1);
        musicalKeyMinor.put("B", (byte) 2);
        musicalKeyMinor.put("Fs", (byte) 3);
        musicalKeyMinor.put("Cs", (byte) 4);
        musicalKeyMinor.put("Gs", (byte) 5);
        musicalKeyMinor.put("Ds", (byte) 6);
        musicalKeyMinor.put("As", (byte) 7);
    }

    public static final Map<Character, Integer> musicalStringToNoteValue = new HashMap<>();
    static {
        musicalStringToNoteValue.put('C', 60);
        musicalStringToNoteValue.put('D', 62);
        musicalStringToNoteValue.put('E', 64);
        musicalStringToNoteValue.put('F', 65);
        musicalStringToNoteValue.put('G', 67);
        musicalStringToNoteValue.put('A', 69);
        musicalStringToNoteValue.put('B', 71);


    }

    @Override
    public String toString() {
        return "MusicalKey{" +
                ", \nbaseNote=" + baseNote +
                ", \nbase='" + base + '\'' +
                ", \nscale='" + scale + '\'' +
                '}';
    }
}
