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
        String formate = '';
        //Unterschied zwischen pdf und txt Datei
        checkFormate(p);
        if(formate==pdf){
           return  readPdf(p);
        }
        if(formate==text){
            return readTxt(p);
        }
        else{
            return("Path does not meet requirements"); //Need error management because the code would still use this as data input
        }
    }

public String checkFormate(String p){


    String formate = 'unknown';
    String[] elements = p.split('.');
    String[] cText = new String[] {"text", "txt", "java", "log"};
    String cPdf = "pdf";
    
    if (cText.contains(elements.last().toLowerCase))
    {formate='text';}
    if (cPdf.contains(elements.last().toLowerCase))
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
            lines.close();
            return (content); 
      }catch(IOException e){
        e.printStackTrace();
      }
}
}

