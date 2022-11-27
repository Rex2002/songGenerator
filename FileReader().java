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
    public static void main(String[] args){
        
        //Bekomme Pfad von UI und speicher den in var p
        String p = path;

        //Unterschied zwischen pdf und txt Datei
        if(/*pdf datei*/){
            pdfReader(p);
        }
        if(/*txt datei */){
            txtReader(p);
        }
        else{
            return("Path does not meet requirements");
        }
    }

public String PdfReader(path p){
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
        System.out.println(buff.toString()); //Can i use a return instead??
    }
}

public String TextReader(path p){
    try{
        Path path = Paths.get(p);
        Stream<String> lines = Files.lines(path);

            String content = lines.collect(Collectors.joining(System.lineSeparator()));
            System.out.println(content); //Can i use a return instead??
            lines.close();
      }catch(IOException e){
        e.printStackTrace();
      }
}
}
