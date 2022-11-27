package org.se.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.se.logic.BeatContainer;
import org.se.logic.ChordContainer;
import org.se.logic.Config;
import org.se.logic.MidiSequence;

import java.util.*;

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
    private Genre genre;
    private Beat beat;
    private Random ran = new Random();
    private final List<MidiPlayable> midiPlayables = new ArrayList<>();

    @JsonCreator
    public Part(@JsonProperty("length") int length, @JsonProperty("req") List<InstrumentEnum> reqInsts,
                @JsonProperty("opt") List<InstrumentEnum> optInsts,
                @JsonProperty(value = "randomizationLevel", defaultValue = "0") int randomizationLevel) {
        this.length = length;
        this.reqInsts = reqInsts;
        this.optInsts = optInsts;
        this.randomizationLevel = randomizationLevel;
    }

    public void fillPart(List<List<String>> chordProgression, MusicalKey key, Map<Integer,Integer> trackMapping){
        this.chordProgression = chordProgression;
        fillPart(key, trackMapping);
    }


    public void fillPart(Part basePart, MusicalKey key, MidiSequence seq){
        // TODO select chords according to variation
        //  fill part more or less like it is done in fillAsBasePart???
    }

    private void fillPart(MusicalKey key, Map<Integer,Integer> trackMapping){
        int beatNo = ran.nextInt(BeatContainer.getDrumBeats().size());
        MidiPlayable m;
        for(int bar = 0; bar < length; bar++){
            for(InstrumentEnum instr : reqInsts){
                if(instrEnumBeginsWith(instr, "chords")) {
                    m = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())),bar, key.getBaseNote(), chordProgression.get(bar % chordProgression.size()) );
                    midiPlayables.add(m);

                }
                if(instrEnumBeginsWith(instr,"drum")){
                    m = new BeatContainer(beatNo, bar, trackMapping.get(Config.getInstrumentMapping().get(instr.toString())));
                    midiPlayables.add(m);
                }
                if(instrEnumBeginsWith(instr, "bass")){
                    m = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())),bar, key.getBaseNote(), chordProgression.get(bar % chordProgression.size()), true );
                    midiPlayables.add(m);
                }
            }
        }
    }


    public void fillRandomly(MusicalKey key, Map<Integer, Integer> trackMapping) {
        chordProgression = Config.getChordProgressions().get(ran.nextInt(Config.getChordProgressions().size()));
        fillPart(key, trackMapping);
    }


    private void selectChords(MusicalKey key, List<Chord> reqChords){

    }

    @Override
    public String toString() {
        return "Part{" +
                "length=" + length +
                ", reqInsts=" + reqInsts +
                ", optInsts=" + optInsts +
                ", randomizationLevel=" + randomizationLevel +
                ", chords=" + chordProgression +
                ", genre=" + genre +
                ", beat=" + beat +
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
    public Genre getGenre() {
        return genre;
    }
    public Beat getBeat() {
        return beat;
    }

    public void setChords(List<List<String>> chords) {
        this.chordProgression = chords;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    public void setBeat(Beat beat) {
        this.beat = beat;
    }

}
