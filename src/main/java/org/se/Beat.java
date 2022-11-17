package org.se;

import java.util.ArrayList;
import java.util.HashMap;

public class Beat {
    HashMap<String, ArrayList<ArrayList<Integer>>> mainPattern;
    HashMap<String, ArrayList<ArrayList<Integer>>> bigFill;
    HashMap<String, ArrayList<ArrayList<Integer>>> smallFill;

    public Beat(HashMap<String, ArrayList<ArrayList<Integer>>> mainPattern,
                HashMap<String, ArrayList<ArrayList<Integer>>> bigFill,
                HashMap<String, ArrayList<ArrayList<Integer>>> smallFill) {
        this.mainPattern = mainPattern;
        this.bigFill = bigFill;
        this.smallFill = smallFill;
    }
}
