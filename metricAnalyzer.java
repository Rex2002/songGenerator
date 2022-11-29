public class metricAnalyzer{
  public int main(String content, String[] hyphen){
    //Find Average length for sentences and hyphen in order to determine text speed
      int averageH = averageHyphen(hyphen[]);
      int averageS = averageSentence(content);
      int bpm = 0;

    /*Define bpm for several sentence/hyphen length combinations
     *1-9 Words is short sentence
      10-18 words is average long sentence
      18+ words is long sentence
    Hyphen
      1 hyphen is short word
      2/3 hyphen is averae word
      4+ hyphen is long word

    bpm (hyphen sentence)
      60bpm long long
      80bpm long average
      100 bpm long short
      120 bpm average long
      140 bpm average average
      160 bpm average short
      180 bpm short short
     */
    if(int averageH =< 1 & int averageS=<9)
      {return bpm = 180;}
    if(int averageH =2 | int averageH=3 & int averageS=<9)
      {return bpm = 160;}
    if(int averageH =2 | int averageH=3 & int averageS=>9 && int averageS =<18)
      {return bpm = 140;}
    if(int averageH =2 | int averageH=3 & int averageS=>19)
      {return bpm = 120;}
    if(int averageH=>4 & int averageS=<9)
      {return bpm = 100;}
    if(int averageH=>4 & int averageS=>9 && int averageS =<18)
      {return bpm = 80;}
    if(int averageH=>4 & int averageS=>19)
      {return bpm = 60;}

  }

  public int averageHyphen(String[] hyphen){
    //i want to die
  }

  public int averageSentence(String content){
    String c = content;

    /*Rules for sentences if done properly ig? Only stuck to punctuation for now
     * 1. Upper Case for every new sentence
     * 2. No new sentence if last character before punctuation mark was a number
     * 3. Free space between punctiation and new word
     * 4. New sentence for .!? and new paragraph
     */
    char[] killTheSentence = ['.', '!', '?'];
    String[] sentenceParts = c.split(killTheSentence);
    int wordsTotal = 0;
    int sentenceAverage = 0;
    
    for(int i = 0; i<sententenceParts.length; i++){
      //TELL ME WHYYYYYYYYYYY

      String currentSentence = sentenceParts[i];

      /*iteration durch strings, also einzelne sätze im array
       * neues wort bei absatz, nächste zeile, oder tab
      */
      int j = 0;
      for(int j=0; j<currentSentence.lenght[j];j++){ 
        
        //this is stupid is it too late to leave
        
        int words = 0;
        int state = 0;
        if(currentSentence[j] == ' ' || currentSentence[j] == '\n' ||currentSentence[j] == '\t'){
          int state = 1;
        }
        
      //this just took way too long and i want to die more than ever
        
        if(state==1){
          int state=0;
          int words++;
        }

      }
      int wordsTotal = wordsTotal + words;
      //I feel so stupid also i want pizza
    }

  }

  int sentenceAverage = wordsTotal/sentenceParts.length;
  return sentenceAverage;
}
