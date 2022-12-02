package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.se.logic.*;

import java.util.*;


/**
 * @author Malte Richert
 * @author Benjamin Frahm
 */

public class Part {
    @JsonProperty
    private int length;
    @JsonProperty
    private List<InstrumentEnum> reqInsts;
    @JsonProperty
    private List<InstrumentEnum> optInsts;
    @JsonProperty
    private int randomizationLevel;
    private List<List<String>> chordProgression;
    private final Random ran = new Random();
    private final List<MidiPlayable> midiPlayables = new ArrayList<>();

    @JsonCreator
    public Part(@JsonProperty("length") int length, @JsonProperty("req") List<InstrumentEnum> reqInsts,
                @JsonProperty(value = "opt", defaultValue = "[]") List<InstrumentEnum> optInsts,
                @JsonProperty(value = "randomizationLevel", defaultValue = "0") int randomizationLevel) {
        this.length = length;
        this.reqInsts = reqInsts;
        this.optInsts = optInsts;
        this.randomizationLevel = randomizationLevel;
    }

    /**
     * method for filling a part based on a given chord progression
     * @param chordProgression - the chord progression that is meant to be used
     * @param key - the key of the part
     * @param trackMapping - the Instrument-track-mapping of the sequence
     */
    public void fillPart(List<List<String>> chordProgression, MusicalKey key, Map<Integer,Integer> trackMapping){
        this.chordProgression = chordProgression;
        fillPart(key, trackMapping);
    }

    private void fillPart(MusicalKey key, Map<Integer,Integer> trackMapping){
        int beatNo = ran.nextInt(BeatContainer.getDrumBeats().size());
        Theme theme = new Theme(0,0, key, chordProgression);
        MidiPlayable m;
        for(int bar = 0; bar < length; bar++){
            for(InstrumentEnum instr : reqInsts){
                if(instrEnumBeginsWith(instr, "chords")) {
                    m = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())),bar,
                            key, chordProgression.get(bar % chordProgression.size()) );
                    midiPlayables.add(m);
                    m = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString()))+1,bar,
                            key, chordProgression.get(bar % chordProgression.size()), true );
                    midiPlayables.add(m);
                }
                else if(instrEnumBeginsWith(instr,"drum")){
                    // adds fills at the following positions with chances:
                    //   every second bar:
                    //      small fill: 50%
                    //   every fourth bar:
                    //      big fill: 50%
                    //      small fill: 50%
                    //  last bar of the part:
                    //      big fill: 100%
                    int fill;
                    if (bar == length - 1){
                        fill = 1;
                    }
                    else if(bar % 4 == 3){
                        fill = ran.nextInt(2);
                    }
                    else if(bar % 2 == 1){
                        fill = ran.nextInt(2) - 1;
                    }
                    else{
                        fill = -1;
                    }
                    m = new BeatContainer(beatNo, bar, fill, trackMapping.get(Config.getInstrumentMapping().get(instr.toString())));
                    midiPlayables.add(m);
                }
                else if(instrEnumBeginsWith(instr, "bass")){
                    m = new BassContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())),bar,
                            key, chordProgression.get(bar % chordProgression.size()),
                            chordProgression.get((bar+1) % chordProgression.size()));
                    midiPlayables.add(m);
                }
                else if(bar % 4 == 0){
                    m = new ThemeVariation(theme, trackMapping.get(Config.getInstrumentMapping().get(instr.toString())), bar);
                    //m = new Melody(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())), bar, key, chordProgression.get(bar % chordProgression.size()));
                    midiPlayables.add(m);
                }
            }
        }
    }

    /**
     * Method to fill a new part with a random chord progression
     * @param key - the key of the part
     * @param trackMapping - the Instrument-track-mapping of the sequence
     */
    public void fillRandomly(MusicalKey key, Map<Integer, Integer> trackMapping) {
        chordProgression = Config.getChordProgressions().get(ran.nextInt(Config.getChordProgressions().size()));
        fillPart(key, trackMapping);
    }

    @Override
    public String toString() {
        return "Part{" +
                "length=" + length +
                ", reqInsts=" + reqInsts +
                ", optInsts=" + optInsts +
                ", randomizationLevel=" + randomizationLevel +
                ", chords=" + chordProgression +
                '}';
    }
    private boolean instrEnumBeginsWith(InstrumentEnum instr, String startPhrase){
        return instr.toString().startsWith(startPhrase);
    }
    public int getLength() {
        return length;
    }

    public List<MidiPlayable> getMidiPlayables() { return midiPlayables; }

    public List<InstrumentEnum> getReqInsts() {
        return reqInsts;
    }
    public List<InstrumentEnum> getOptInsts() {
        return optInsts;
    }
    public int getRandomizationLevel() {
        return randomizationLevel;
    }
    public List<List<String>> getChords() {
        return chordProgression;
    }

}
