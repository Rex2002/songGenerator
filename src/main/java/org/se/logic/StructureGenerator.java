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

        MidiSequence seq = initMidiSequence(structure);

        if (settings.get("tempo") != null){
            structure.setTempo((int) settings.get("tempo"));
        } else {
            structure.setTempo(metrics.get("tempo"));
        }

        structure.getParts().get(structure.getBasePartKey()).fillAsBasePart(structure.getKey(),seq, trackMapping);
//        for (String key: structure.getParts().keySet()) {
//            Part part = structure.getParts().get(key);
//            Variation variation;
//
//            if (part.getRandomizationLevel()==0){
//                variation = new Variation(structure.getParts().get(structure.getBasePartKey()).getChords(), part.getReqInsts(), part.getOptInsts());
//            } else if (part.getRandomizationLevel() == 1) {
//                List<Chord> reqChords = new ArrayList<>();
//                reqChords.add(structure.getParts().get(structure.getBasePartKey()).getChords().get(0));
//                reqChords.add(structure.getParts().get(structure.getBasePartKey()).getChords().get(1));
//                //TODO pick two most important chords instead of first two
//                variation = new Variation(reqChords, part.getReqInsts(), part.getOptInsts());
//            } else {
//                variation = new Variation(new ArrayList<>(), part.getReqInsts(), part.getOptInsts());
//            }
//            //hatten wir schon ne Lösung wie wir herausfinden was der basePart ist?
//            //Quick Fix: zusätzliche basePart property im Structure Template, die den key vom basePart enthält. Damit wäre isBasePart obsolet
//            part.fillPart(structure.getParts().get(structure.getBasePartKey()), structure.getKey(),variation, seq);
//        }
        seq.setEnd(20);
        seq.createFile("structureTest");
    }

    public static MidiSequence initMidiSequence(Structure s){
        int currentTrackNo = 0;
        for (String partName: s.getParts().keySet()) {
            for(InstrumentEnum instrument : s.getParts().get(partName).getReqInsts()){
                if(!trackMapping.containsKey(Config.getInstrumentMapping().get(instrument.toString()))){
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
                    if(instrument.equals(InstrumentEnum.chords) || instrument.equals(InstrumentEnum.chords2)){
                        currentTrackNo++;
                    }
                    currentTrackNo++;
                }
            }
        }
        MidiSequence seq = new MidiSequence(currentTrackNo);
        for(Integer instr : trackMapping.keySet()){
            seq.setInstrument(instr, trackMapping.get(instr));
            seq.addNote(60, 0, 24, trackMapping.get(instr));
        }
        return seq;
    }
}
