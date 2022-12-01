public static int metricsGet(String content, String terms){
    //Find Average length for sentences and hyphen in order to determine text speed
      int averageH = averageHyphen(terms);
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
    if(averageH <= 1 & averageS<=9)
      {bpm = 180;}
    if(averageH ==2 | averageH==3 & averageS<=9)
      {bpm = 160;}
    if(averageH ==2 | averageH==3 & averageS>=9 & averageS <=18)
      {bpm = 140;}
    if(averageH ==2 | averageH==3 & averageS>=19)
      {bpm = 120;}
    if(averageH>=4 & averageS<=9)
      {bpm = 100;}
    if(averageH>=4 & averageS>=9 & averageS <=18)
      {bpm = 80;}
    if(averageH>=4 & averageS>=19)
      {bpm = 60;}
return bpm;
  }

  public static int averageHyphen(String terms){
    String term = terms;
    int hyphenTotal = CountSyllabes(term);
    int[] termAndSentences = countWords(term);
    int termAmount = termAndSentences[0];
    int averageH = hyphenTotal/termAmount;
    return averageH;
  }
  

  public static int averageSentence(String content){
    String c = content;
    int[] termAndSentences = countWords(c);
    int wordsTotal = termAndSentences[0];
    int cTcSTotal = termAndSentences[1];
  int sentenceAverage = wordsTotal/cTcSTotal;
  return sentenceAverage;
}
  
  
  public static int[] countWords(String content){
      String c = content;

    /*Rules for sentences if done properly ig? Only stuck to punctuation for now
     * 1. Upper Case for every new sentence
     * 2. No new sentence if last character before punctuation mark was a number
     * 3. Free space between punctiation and new word
     * 4. New sentence for .!? and new paragraph
     */
    String[] sentenceParts = c.split(".!?");
    int ctCsTotal = sentenceParts.length;
    int wordsTotal = 0;
    int words = 0;
    int sentenceAverage = 0;
    
    for(int i = 0; i<sentenceParts.length; i++){

      String currentSentence = sentenceParts[i];
        words = 0;
      for(int j=0; j<currentSentence.length();j++){ 

        int state = 0;
        if(currentSentence.charAt(j) == ' ' | currentSentence.charAt(j) == '\n' |currentSentence.charAt(j) == '\t'){
          state = 1;
        }
        if(state==1){
          words = words+1;
        }
      }
      wordsTotal = wordsTotal + words;

    }

      int[] wordArray = {wordsTotal, ctCsTotal};
      
      return wordArray;
  }
