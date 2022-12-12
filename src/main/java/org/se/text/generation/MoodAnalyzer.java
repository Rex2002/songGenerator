package org.se.text.generation;

/**
 * @author Jakob Kautz
 */

/*
 * Gets list of split sentences from Preprocessor function
 * reads Mood templates and compares these words to the current sentence
 * if sentence contains a word of the string count moodCounter one up
 * returns mood with highest count
 */

public class MoodAnalyzer {
/*
 * I need input as String array of sentences
 * preprocess String from input data
 * ask Val for help cuz im lost
 */

    public String getMood(String[] sentences){
/*
 * Global variables
 * Counter to figure out how many words of each mood r in the text
 * Strings that contain mood Words
 */
         int happyCounter = 0;
         int hulkCounter = 0;
         int thirstyyyCounter = 0;
         int sadCounter = 0;
         String negation = FileReader.main(new File("./src/main/resources/text/WordNegations.txt"));
         String happy = FileReader.main(new File("./src/main/resources/text/HappyMood.txt"));
         String sad = FileReader.main(new File("./src/main/resources/text/SadMood.txt"));
         String angry = FileReader.main(new File("./src/main/resources/text/HulkMood.txt"));
         String horny = FileReader.main(new File("./src/main/resources/text/ThirstyyyMood.txt"));


/*
 * Traversing through String[] (input sentences from original text input after preopocessor)
 */
        for(String thisOne : s){
            /*counting happy words, if negation its not happy anymore */
            for(int j = 0; j<thisOne.length();j++){ //Iteration sucks but its 1:30am and my brain stopped working
                if(containsWord(s, happy)){
                    if(containsWord(s, negation)){
                       sadCounter++;
                    }
                    else{
                        happyCounter++;
                    }
                 }
                 if(containsWord(s, sad)){
                    if(containsWord(s, negation)){
                       happyCounter++;
                    }
                    else{
                        sadCounter++;
                    }
                 }
                 if(containsWord(s, angry)){
                    hulkCounter++;
                 }
                 if(containsWord(s, horny)){
                    thirstyyyCounter++;
                 }
            }
        }

        String mood = andUrGoddamnFuckingResultIs(happyCounter, sadCounter, hulkCounter, thirstyyyCounter);
        return mood;
    }
/*
 * Function to traverse through StringArray and check if it contains word from anoher String
 * Like the fucker u have for Lists but not for Stringarrays cuz Java sucks:/
 */

    public boolean containsWord(String[] arr, String s) {
        for (String t : arr) {
            if (t.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

/*
 * Function for final mood
 * Compares counts
 * sets max count as mood
 * This is probably way easier i just dont know how and cant check cuz my wifi is being a fuckin snowflake again
 */

    public String andUrGoddamnFuckingResultIs(int ha, int s, int a, int ho){
        String mood = ""; //das muss so viel schöner gehen rip
        if(ha>s && ha>a && ha>ho){
            mood = "happy";
        }
        else if(s>ha && s>a && s>ho){
            mood = "sad";
        }
        else if(a>s && a>ha && a>ho){
            mood = "angry";
        }
        else if(ho>s && ho>a && ho>ha){
            mood = "horny";
        }
        return mood;
    }

}

