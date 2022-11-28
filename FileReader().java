import java.io.IOException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Reurn ist string!!!

public class FileReader{
    public String main(String path){
        
        //Bekomme Pfad von UI und speicher den in var p
        String p = path;

        //Unterschied zwischen pdf und txt Datei
        checkFormate(p);
        if(formate==pdf){
            readPdf(p);
        }
        if(formate==text){
            readTxt(p);
        }
        else{
            return("Path does not meet requirements");
        }
    }

public String checkFormate(String p){

/*prüfen ob datei existiert?? */

    String formate = 'unknown';
    String[] elements = p.split('.');
    String cText = "text,txt,java,log";
    String cPdf = "pdf";
    
    if (cText.entails elements.last().toLowerCase)
    {formate='text';}
    if (cPdf.entails elements.last().toLowerCase)
    {formate ='pdf';}
    return formate;
}

public StringBuffer readPdf(String p){
    StringBuffer buff = new StringBuffer();
    try {
        PdfReader reader = new PdfReader(p); //konstrukter mit pfad p
        int numberOfPages = reader.getNumberOfPages();
        String s;
        for (int i = 1; i <= numberOfPages; i++) {
            s = PdfTextExtractor.getTextFromPage(reader, i);
            buff.append(s + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer content = (buff.toString()); 
        return content;
    }
}

public String readTxt(String p){
    try{
        Path path = Paths.get(p);
        Stream<String> lines = Files.lines(path);

            String content = lines.collect(Collectors.joining(System.lineSeparator()));
            return (content); 
            lines.close();
      }catch(IOException e){
        e.printStackTrace();
      }
}
}

