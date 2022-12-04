package org.se.logic;

import org.se.model.*;
import java.util.*;


/**
 * @author Malte Richert
 * @author Benjamin Frahm
 */
public class StructureGenerator {
    private static final Random ran = new Random();
    private static Structure structure;
    private static final Map<Integer, Integer> trackMapping = new HashMap<>();

    public static void generateStructure(Map<String, Object> settings, Map<String, Integer> metrics){
        structure = Config.getStructures().get(ran.nextInt(Config.getStructures().size()));
        structure.setGenre((Genre) settings.get("genre"));
        structure.setKey(new MusicalKey());
        if (settings.get("tempo") != null){
            structure.setTempo((int) settings.get("tempo"));
        } else {
            structure.setTempo(metrics.get("tempo"));
        }

        MidiSequence seq = initMidiSequence(structure);

        structure.getPart(structure.getBasePartKey()).fillRandomly(structure.getKey(), trackMapping);

        for (String key: structure.getParts().keySet()) {
            if(key.equals(structure.getBasePartKey())){
                continue;
            }
            Part part = structure.getPart(key);

            if (part.getRandomizationLevel()==0){
                List<List<String>> progression = structure.getPart(structure.getBasePartKey()).getChords();
                part.fillPart(progression, structure.getKey(), trackMapping);
            } else if (part.getRandomizationLevel() == 1) {
                List<String> reqChords = getImportantChords(structure.getPart(structure.getBasePartKey()).getChords());
                List<List<List<String>>> progression = ChordContainer.getMatchingProgressions(reqChords);
                part.fillPart(progression.get(ran.nextInt(progression.size())), structure.getKey(), trackMapping);
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
            seq.addText(barOffset*4, 0, partName);
            barOffset += structure.getPart(partName).getLength();
        }
        seq.createFile("structureTest");
        System.out.println(structure);
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
        List<Integer> drumInstrs = getDrumInstrNo();
        for(Integer instr : trackMapping.keySet()){
            if(drumInstrs.contains(instr)){
                seq.setInstrument(instr, trackMapping.get(instr), true);
            }
            else{
                seq.setInstrument(instr, trackMapping.get(instr));
            }
            seq.setKey(structure.getKey().getBase(),trackMapping.get(instr));
            seq.addText(2, trackMapping.get(instr), instr.toString());
            //seq.addNote(60, 0, 24, trackMapping.get(instr));
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
                if (importantChords.contains(chord)){
                    continue;
                }
                if (importantChords.isEmpty()){
                    importantChords.add(chord);
                }
                else if (chordImportanceMap.get(chord.substring(0,1)) < chordImportanceMap.get(importantChords.get(0).substring(0,1))) {
                    importantChords.add(0, chord);
                    importantChords.remove(importantChords.size()-1);
                }
                else if (importantChords.size() < 2 || chordImportanceMap.get(chord.substring(0, 1)) < chordImportanceMap.get(importantChords.get(1).substring(0,1))) {
                    if(importantChords.size() > 1 ){importantChords.remove(1);}
                    importantChords.add(1, chord);

                }
            }
        }
        return importantChords;
    }

    public static List<Integer> getDrumInstrNo(){
        List<Integer> d = new ArrayList<>();
        for(InstrumentEnum instr : InstrumentEnum.values()){
            if(instr.toString().startsWith("drum")){
                d.add(Config.getInstrumentMapping().get(instr.toString()));
            }
        }
        return d;
    }

}
