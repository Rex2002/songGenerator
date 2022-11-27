package org.se.logic;

import org.se.model.*;

import java.util.*;

public class StructureGenerator {
    private static final Random ran = new Random();
    private static Structure structure;
    private static Map<String, Object> settings;
    private static Map<String, Integer> metrics;
    private static Map<Integer, Integer> trackMapping = new HashMap<>();



    public static void main(String[] args) {
        //these are test values that will eventually be passed by UI and TextAnalyzer
        settings = Map.of("genre", Genre.POP, "nsfw", false, "tempo", 120);
        metrics = Map.of("tempo", 70);

        Config.loadConfig();

        structure = Config.getStructures().get(ran.nextInt(Config.getStructures().size()));
        structure.setGenre((Genre) settings.get("genre"));
        structure.setKey(new MusicalKey());
        if (settings.get("tempo") != null){
            structure.setTempo((int) settings.get("tempo"));
        } else {
            structure.setTempo(metrics.get("tempo"));
        }

        MidiSequence seq = initMidiSequence(structure);

        structure.getParts().get(structure.getBasePartKey()).fillRandomly(structure.getKey(), trackMapping);
        for (String key: structure.getParts().keySet()) {
            Part part = structure.getPart(key);

            if (part.getRandomizationLevel()==0){
                List<List<String>> progression = structure.getPart(structure.getBasePartKey()).getChords();
                part.fillPart(progression, structure.getKey(), trackMapping);
            } else if (part.getRandomizationLevel() == 1) {
                List<List<String>> reqChords = new ArrayList<>();
                reqChords.add(structure.getPart(structure.getBasePartKey()).getChords().get(0));
                reqChords.add(structure.getPart(structure.getBasePartKey()).getChords().get(1));
                part.fillRandomly(structure.getKey(), trackMapping);
                //TODO pick two most important chords instead of first two
                // TODO pick chord progressions matching reqChords instead of picking any randomly
            } else {
                part.fillRandomly(structure.getKey(), trackMapping);
            }
        }


        seq.setEnd(calculateLength());
        int barOffset = 0;
        for(String partName : structure.getOrder()){
            for(MidiPlayable m : structure.getPart(partName).getMidiPlayables()){
                m.setBar(m.getBar() + barOffset);
                seq.addMidiPlayable(m);
                m.setBar(m.getBar() - barOffset);
            }
            System.out.println("partName: " + partName);
            seq.addText(barOffset*4, 1, partName);
            barOffset += structure.getPart(partName).getLength();
        }
        seq.createFile("structureTest");
    }

    public static MidiSequence initMidiSequence(Structure s){
        int currentTrackNo = 0;
        int drumTrackNo = 0;
        for (String partName: s.getParts().keySet()) {
            for(InstrumentEnum instrument : s.getParts().get(partName).getReqInsts()){
                if(!trackMapping.containsKey(Config.getInstrumentMapping().get(instrument.toString()))){
                    if(instrument == InstrumentEnum.drums || instrument == InstrumentEnum.drums2){
                        drumTrackNo++;
                        continue;
                    }
                    trackMapping.put(Config.getInstrumentMapping().get(instrument.toString()), currentTrackNo);
                    if(instrument.equals(InstrumentEnum.chords) || instrument.equals(InstrumentEnum.chords2)){
                        currentTrackNo++;
                    }
                    currentTrackNo++;
                }
            }
            for(InstrumentEnum instrument : s.getParts().get(partName).getOptInsts()){
                if(!trackMapping.containsKey(Config.getInstrumentMapping().get(instrument.toString()))){
                    trackMapping.put(Config.getInstrumentMapping().get(instrument.toString()), currentTrackNo);
                    if(instrument == InstrumentEnum.drums || instrument == InstrumentEnum.drums2){
                        drumTrackNo++;
                        continue;
                    }
                    if(instrument.equals(InstrumentEnum.chords) || instrument.equals(InstrumentEnum.chords2)){
                        currentTrackNo++;
                    }
                    currentTrackNo++;
                }
            }
        }
        if(drumTrackNo != 0){
            trackMapping.put(Config.getInstrumentMapping().get(InstrumentEnum.drums.toString()), currentTrackNo);
            currentTrackNo++;
        }
        MidiSequence seq = new MidiSequence(currentTrackNo);
        for(Integer instr : trackMapping.keySet()){
            seq.setInstrument(instr, trackMapping.get(instr));
            seq.addNote(60, 0, 24, trackMapping.get(instr));
        }
        seq.setBPM(structure.getTempo());
        return seq;
    }

    public static int calculateLength(){
        int length = 0;
        for(String i: structure.getOrder()){
            length += structure.getParts().get(i).getLength();
        }
        return length;
    }
}
