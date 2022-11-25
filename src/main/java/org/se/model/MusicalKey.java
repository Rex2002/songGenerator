package org.se.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MusicalKey {

    public static int[] keyMin = {0,2,3,5,7,8,10};
    public static int[] keyMaj = {0,2,4,5,7,9,11};
    private final int baseNote;
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
        this.baseNote = 60 + new Random().nextInt(12);
        String[] scales = {"min", "maj"};
        this.scale = scales[new Random().nextInt(2)];
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
}
