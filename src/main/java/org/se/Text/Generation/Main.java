package org.se.Text.Generation;

import java.io.IOException;
import java.util.List;

import org.se.Text.Analysis.TermCollection;

public class Main {
    public static void main(String[] args) throws IOException {

		TermCollection x = TermExample.getExample();
		//System.out.println(x);

        Structure structure = new Structure();

        SongTextGenerator songTextGenerator = new SongTextGenerator();

        //TemplateImporter t = new TemplateImporter();
        //t.getTemplate(Structure.Genre.pop);



        List<String> songText = songTextGenerator.generateSongText(structure,x);
        //x.terms.get(args);
        //System.out.println(songText);
    }
}