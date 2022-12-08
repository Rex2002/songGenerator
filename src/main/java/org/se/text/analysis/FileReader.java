package org.se.text.analysis;

import java.io.IOException;
import com.ibm.icu.text.CharsetDetector;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//Reurn ist string!!!

public class FileReader {
	public static String main(String path) throws IOException {
		// Bekomme Pfad von UI

		// Unterschied zwischen pdf und txt Datei
		String[] cText = { ".txt", ".text", ".log", ".java" };
		String cPdf = ".pdf";

		if (path.endsWith(cPdf)) {
			return readPdf(path);
		}
		for (String ending : cText) {
			if (path.endsWith(ending)) {
				return readTxt(path);
			}
		}

		return ("Path does not meet requirements"); // Need error management because the code would still use this as
													// data input
	}

	public static String readPdf(String p) throws IOException {
		StringBuffer buff = new StringBuffer();
		try {
			PdfReader reader = new PdfReader(p); // konstrukter mit pfad p
			int numberOfPages = reader.getNumberOfPages();
			String s;
			for (int i = 1; i <= numberOfPages; i++) {
				s = PdfTextExtractor.getTextFromPage(reader, i);
				buff.append(s + "\n");
			}
			String content = buff.toString();
			return content;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String readTxt(String p) throws IOException {
		try {
			CharsetDetector detector = new CharsetDetector();
			Path path = Paths.get(p);
			byte[] bytes = Files.readAllBytes(path);
			String content = detector.getString(bytes, "");
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}