public class WordCounter{
  public static int[] countWords(String content){
      String c = content;
      List<String> sentenceParts = splitSentence(c);
      int ctCsTotal = sentenceParts.size();
      int wordsTotal = 0;
      int words = 0;
      int sentenceAverage = 0;
    
    for(int i = 0; i<sentenceParts.size(); i++){

      String currentSentence = sentenceParts.get(i);
        words = 0;
      for(int j=0; j<currentSentence.size();j++){ 

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
  
  public static List<String> splitSentence(String content){
	List<String> sentences = new ArrayList<String>;
	int sentenceStart = 0;
	for(int i = 0; i<content.length();i++){
		if(content[i]=='.'|| content[i]=='!'|| content[i]=='?'){
			sentences.add(content.substring(sentenceStart,i));
			sentenceStart=i+1;
		}
	}
	return sentences;
}
  
}
