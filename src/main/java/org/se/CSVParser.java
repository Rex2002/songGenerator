package org.se;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVParser {
	public static ArrayList<HashMap<String, String>>parseFile(Path filepath) throws IOException {
		return CSVParser.parse(Files.readString(filepath));
	}

	public static ArrayList<HashMap<String, String>>parseFile(Path filepath, String colDelimiter) throws IOException {
		return CSVParser.parse(Files.readString(filepath), colDelimiter);
	}

	public static ArrayList<HashMap<String, String>>parseFile(Path filepath, String colDelimiter, String rowDelimiter) throws IOException {
		return CSVParser.parse(Files.readString(filepath), colDelimiter, rowDelimiter);
	}

	public static ArrayList<HashMap<String, String>>parse(String csv, String colDelimiter, String rowDelimiter) {
		ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
		Boolean doubleQuoted = false;
		Boolean singleQuoted = false;

		return res;
	}

	public static ArrayList<HashMap<String, String>>parse(String csv, String colDelimiter) {
		return CSVParser.parse(csv, colDelimiter, "\r?\n");
	}

	public static ArrayList<HashMap<String, String>>parse(String csv) {
		return CSVParser.parse(csv, ",", "\r?\n");
	}
}
