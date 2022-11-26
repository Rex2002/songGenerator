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
    private boolean isBasePart;
    @JsonProperty
    private int randomizationLevel;
    private List<ChordContainer> chords;
    private Genre genre;
    private Beat beat;
    private Random ran = new Random();

    @JsonCreator
    public Part(@JsonProperty("length") int length, @JsonProperty("req") List<InstrumentEnum> reqInsts,
                @JsonProperty("opt") List<InstrumentEnum> optInsts,
                @JsonProperty(value = "isBasePart", defaultValue = "false") boolean isBasePart,
                @JsonProperty(value = "randomizationLevel", defaultValue = "0") int randomizationLevel) {
        this.length = length;
        this.reqInsts = reqInsts;
        this.optInsts = optInsts;
        this.isBasePart = isBasePart;
        this.randomizationLevel = randomizationLevel;
    }

    public void fillPart(Part basePart, MusicalKey key, Variation variation, MidiSequence seq){
    }


    public void fillAsBasePart(MusicalKey key, MidiSequence seq, Map<Integer, Integer> trackMapping) {
        List<List<String>> progression = Config.getChordProgressions().get(ran.nextInt(Config.getChordProgressions().size()));
        chords = new ArrayList<>();
        int beatNo = ran.nextInt(BeatContainer.getDrumBeats().size());
        MidiPlayable m;
        for(int bar = 0; bar < length; bar++){
            for(InstrumentEnum instr : reqInsts){
                if(instr == InstrumentEnum.chords) {
                    m = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(InstrumentEnum.chords.toString())),bar, key.getBaseNote(), progression.get(bar % progression.size()) );
                    seq.addMidiPlayable(m);
                }
                if(instr == InstrumentEnum.drums){
                    m = new BeatContainer(beatNo, bar, trackMapping.get(Config.getInstrumentMapping().get(InstrumentEnum.drums.toString())));
                    seq.addMidiPlayable(m);
                }
                if(instr == InstrumentEnum.bass){
                    m = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(InstrumentEnum.bass.toString())),bar, key.getBaseNote(), progression.get(bar % progression.size()), true );
                    seq.addMidiPlayable(m);
                }
            }

        }
    }

    private void selectChords(MusicalKey key, List<Chord> reqChords){

    }
    private void selectBeat(){
        //selects beat from template
    }

    @Override
    public String toString() {
        return "Part{" +
                "length=" + length +
                ", reqInsts=" + reqInsts +
                ", optInsts=" + optInsts +
                ", isBasePart=" + isBasePart +
                ", randomizationLevel=" + randomizationLevel +
                ", chords=" + chords +
                ", genre=" + genre +
                ", beat=" + beat +
                '}';
    }

    public int getLength() {
        return length;
    }
    public List<InstrumentEnum> getReqInsts() {
        return reqInsts;
    }
    public List<InstrumentEnum> getOptInsts() {
        return optInsts;
    }
    public boolean isBasePart() {
        return isBasePart;
    }
    public int getRandomizationLevel() {
        return randomizationLevel;
    }
    public List<ChordContainer> getChords() {
        return chords;
    }
    public Genre getGenre() {
        return genre;
    }
    public Beat getBeat() {
        return beat;
    }

    public void setChords(List<ChordContainer> chords) {
        this.chords = chords;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    public void setBeat(Beat beat) {
        this.beat = beat;
    }

}
