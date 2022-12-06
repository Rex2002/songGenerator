package org.se.text.generation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.se.music.logic.Config;
import org.se.music.model.*;
import org.se.text.analysis.*;
import org.se.text.analysis.model.Gender;
import org.se.text.analysis.model.GrammaticalCase;
import org.se.text.analysis.model.Numerus;

public class SongTextGenerator {

    public static void main(String[] args) throws IOException {
        SongTextGenerator g = new SongTextGenerator();
        Config.loadConfig();
        Config.getStructures().get(0).setGenre(Genre.POP);
        g.generateSongText(Config.getStructures().get(0), TermExample.getExample());
        System.out.println("structure: " + Config.getStructures().get(0));
    }

    private MusicalKey key;
    private Map<String, Part> parts;

    private int partLength;

    //text
    private TermCollection termCollection;

    //this
    private String [] partText;

    //Midi-Sequence Generator calls SongTextGenerator()
    public List<String> generateSongText(Structure structure, TermCollection termCollection){
        List<String> songText = new ArrayList<String>();
        this.termCollection = termCollection;
        key = structure.getKey();
        parts = structure.getParts();
        List<String> usedWordsList = new ArrayList<>();    //to check if a Term was used in the Song before
        List<Integer> usedStrophesList = new ArrayList<>();    //to check if a strophe was used in the Song before


        //songText.add(generateStrophe(Structure.Genre.pop,usedStrophesList, usedWordsList).get(0));
        System.out.println(generateStrophe(Genre.POP, usedStrophesList, usedWordsList));

        //TODO iwie probleme mit id

        String[] arr = new String[] { "n", "f", "p", "1", "1", "10" };
        //System.out.println(getTerm(arr));

        // for(int i = 0;i < generateStrophe(Structure.Genre.blues, getNounsExamples(),
        // getVerbsExamples()).size();i++){
        // songText.add(generateStrophe(Structure.Genre.blues, getNounsExamples(),
        // getVerbsExamples()).get(i) + "\n");
        //}
        System.out.println(songText);
        return songText;
    }

    private List<String> generateStrophe(Genre genre, List<Integer> usedStrophesList, List<String> usedWordsList) {
        String vers = new String();

        if (genre == Genre.BLUES) {
            //String[] verse = new String[3];
            // verse[0] = "Ich bin so traurig weil " +
            // nouns.get(getCorrectPosition(nounsIndex,0)) + " mich traurig machen";
            //verse [1] = verse[0];
            //verse [2] = verse[0];
            // verse[2] = "und deshalb muss ich " +
            // verbs.get(getCorrectPosition(verbsIndex,0));

            //List<String> bluesText = List.of();

            //return bluesText;
            return List.of();
        } else if (genre == Genre.POP) {
            TemplateImporter TemplateImporter = new TemplateImporter();
            List<PopTemplate> popTemplateList = TemplateImporter.getTemplate(genre);


            usedStrophesList.add(getRandomNotUsedValue(usedStrophesList,popTemplateList.size()));
            PopTemplate popTemplate = popTemplateList.get(getLastElement(usedStrophesList)); //get random Strophe

            String testVers = popTemplate.getStrophe()[0];
            System.out.println(testVers);
            int beginning = testVers.indexOf('$');
            int end = testVers.indexOf('$', beginning + 1);
            System.out.println(testVers.substring(beginning + 1, end));
            String requirementsVariableString = testVers.substring(beginning + 1, end);
            String[] strArr = getStringArrFromRequirementsVariableString(requirementsVariableString);

            System.out.println(
                    testVers.substring(0, beginning) + " Hier kommt das eingesetzte Wort: " + getTerm(strArr) + " " + testVers.substring(end + 1));

            return List.of();

        }

        return null;

    }

    private String getVerse(String rawString) {

        //System.out.println(rawString);
        int beginning = rawString.indexOf('$');
        int end = rawString.indexOf('$',beginning + 1);


        String requirementsVariableString = rawString.substring(beginning + 1,end);
        String [] strArr = getStringArrFromRequirementsVariableString(requirementsVariableString);


        return (rawString.substring(0,beginning) + getTerm(strArr) + rawString.substring(end + 1));
    }

    private String [] getStringArrFromRequirementsVariableString(String requirementsVariableString) {
        String [] strArr = new String[]{"","","","","","",""};
        int index = 0;
        while(requirementsVariableString.length() > 0) {
            if (requirementsVariableString.charAt(0) == ',')index++;
            else{
                strArr[index] = strArr[index] + requirementsVariableString.charAt(0);   //Stringbuilder
            }
            requirementsVariableString= requirementsVariableString.substring(1);
        }
        return strArr;
    }

    private Integer getRandomNotUsedValue(List<Integer> list, int listLength) {
        int terminateCounter = listLength;
        int possibleValue;

        do{
            possibleValue = (int)(Math.random() * (listLength));
            terminateCounter--;

            if(terminateCounter < 0)return ((int)(Math.random() * (list.size() + 1)));  //if there is no unused index left
        }while(list.contains(possibleValue));

        return possibleValue;

    }

    private String getTerm(String [] requirements){
        List<NounTerm> termList;
        if(isNoun(requirements)){
            termList = getNounsTermListFromRequirements(requirements);
        }else{
            termList = getNounsTermListFromRequirements(requirements);//hier fürs Beispiel nomen Statt Verben TODO!!
            //List<String> termList = getVerbsTermListFromRequirements(requirements);
        }

        //System.out.println(termList);

        //index gives the position of the noun in termList
        int termListSize = termList.size();
        int position = 0; //default 0
        if(termListSize != 0) {
            position = getCorrectPosition(termList.size(), getIdFromRequirements(requirements));
        }else{
            return "DaPasstWohlNichts";
        }
        return termList.get(position).word;
    }

    //boa ne Hashmap statt dem String wäre schlauer!!!!!!!!!!!!!!!
    private List<NounTerm> getNounsTermListFromRequirements(String [] requirements) {
        //requirements looks like [n,f,p,n,1,1,10]
        // [Noun,Gender,Plural,Case,id,syllMin,syllMax]
        GrammaticalCase grammaticalCase;
        Gender gender;
        Numerus numerus;
        int syllMin, syllMax;



        //detect Gender
        switch (requirements[1]) {
            case "f":
                gender = Gender.FEMALE;
                break;
            case "m":
                gender = Gender.MALE;
                break;
            default:
                gender = Gender.NEUTRAL; // in case of a false Input, neutral is selected
        }

        //detect if is Plural
        switch (requirements[2]) {
            case "p":
                numerus = Numerus.PLURAL;
                break;
            default:
                numerus = Numerus.SINGULAR; // in case of a false Input singular is selected
        }

        // detect Grammatical case//TODO
        switch (requirements[3]) {
            case "a":
                grammaticalCase = GrammaticalCase.ACCUSATIVE;
                break;
            case "d":
                grammaticalCase = GrammaticalCase.DATIVE;
                break;
            case "g":
                grammaticalCase = GrammaticalCase.GENITIVE;
                break;
            default:
                grammaticalCase = GrammaticalCase.NOMINATIVE;
        }

        //detect min Syll
        try {
            syllMin = Integer.parseInt(requirements[5]);
        } catch (NumberFormatException ex) {
            syllMin = 0;
        }

        //detect max Syll
        try {
            syllMax = Integer.parseInt(requirements[6]);
        } catch (NumberFormatException ex) {
            syllMax = 15;
        }


        return termCollection.query(grammaticalCase, gender, numerus, syllMin, syllMax);
    }

    private List<String> getVerbsTermListFromRequirements(String [] requirements) {
        return getVerbsExamples(); //TODO
    }

    private int getIdFromRequirements(String[] requirements) {
        try {
            return Integer.parseInt(requirements[4]);
        } catch (NumberFormatException ex) {
            return 4; //should be a random Number but would require a Math library

        }
    }

    public static Integer getLastElement(List<Integer> list){
        if((list != null) && (list.isEmpty() == false))
        {
            int lastIdx = list.size() - 1;
            Integer lastElement = list.get(lastIdx);
            return lastElement;
        }
        else
            return null;
    }

    private int getCorrectPosition(int Index, int position){
        if(Index - position <= 0) return getCorrectPosition(Index, position-Index);
        return position;
    }

    private boolean isNoun(String[] requirements) {
        return (requirements[0] == "n");
    }

    //-----------------Testing---------------\\
    private List<NounTerm> getOneTermTesting(){
        return termCollection.query(GrammaticalCase.NOMINATIVE, Gender.FEMALE, Numerus.SINGULAR, 1, 100);

    }

    private List<String> getVerbsExamples(){
        List<String> verbExamples = new ArrayList<>();
        verbExamples.add("spielen");
        verbExamples.add("hüpfen");
        verbExamples.add("addieren");
        return verbExamples;
    }

    private List<String> getNounsExamples(){
        return Arrays.asList(getOneTermTesting().get(0).word);
    }

    private String generateChorus(List<String> nouns, List<String> verbs){
        return "alla";
    }

    private String AlleMeineEntchen(List<Term> terms){
        String vari = terms.get(0).word;

        String AlleMeineEntchen = "Alle meine " + vari + "\n" + "Schwimmen auf dem See\n" + "Schwimmen auf dem See\n" + "Köpfchen in das Wasser\n"
                + "Schwänzchen in die Höh\n" + "\n" + "Alle meine Täubchen\n" + "Gurren auf dem Dach\n" + "Gurren auf dem Dach\n"
                + "Fliegt eins in die Lüfte\n" + "Fliegen alle nach\n" + "\n" + "\n" + "Alle meine Hühner\n" + "Scharren in dem Stroh\n"
                + "Scharren in dem Stroh\n" + "Finden sie ein Körnchen\n" + "Sind sie alle froh\n" + "\n" + "Alle meine Gänschen\n"
                + "Watscheln durch den Grund\n" + "Watscheln durch den Grund\n" + "Suchen in dem Tümpel\n" + "Werden kugelrund\n" + "\n"
                + "Alle meine " + vari + "\n" + "Schwimmen auf dem See\n" + "Schwimmen auf dem See\n" + "Köpfchen in das Wasser\n"
                + "Schwänzchen in die Höh\n" + "Köpfchen in das Wasser\n" + "Schwänzchen in die Höh";

        return ("Alle meine " + vari + ": \n\n"+ AlleMeineEntchen);

    }
}
