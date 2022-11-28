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
                List<String> reqChords = getImportantChords(structure.getPart(structure.getBasePartKey()).getChords());
                part.fillRandomly(structure.getKey(), trackMapping);
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

    private static int calculateLength(){
        int length = 0;
        for(String i: structure.getOrder()){
            length += structure.getParts().get(i).getLength();
        }
        return length;
    }

    private static List<String> getImportantChords(List<List<String>> basePartChords){
        //Idee: Stufen ranken nach Wichtigkeit: 0,4,3,2,1,5,6
        Map<String, Integer> chordImportanceMap = Map.of("0", 0, "4", 1, "3", 2,
                "2", 3, "1", 4, "5", 5, "6", 6);
        List<String> importantChords = new ArrayList<>();
        for (List<String> bar : basePartChords) {
            for (String chord : bar) {
                if (importantChords.isEmpty()){
                    importantChords.add(chord);
                } else if (chordImportanceMap.get(chord.substring(0,1)) < chordImportanceMap.get(importantChords.get(0))) {
                    importantChords.add(1, importantChords.get(0));
                    importantChords.add(0, chord);
                } else if (chordImportanceMap.get(chord.substring(0, 1)) < chordImportanceMap.get(importantChords.get(1))) {
                    importantChords.add(1, chord);
                }
            }
        }
        return importantChords;
    }

}
