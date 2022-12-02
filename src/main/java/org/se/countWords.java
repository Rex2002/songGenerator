public class countWords{
  public static int[] countWords(String content){
      String c = content;
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
}
