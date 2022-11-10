package org.se.Text.Analysis.Archive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Nouns {
	ArrayList<HashMap<String, String>> nouns;

	public Nouns(ArrayList<HashMap<String, String>> nouns) {
		this.nouns = nouns;
	}

	public static Nouns fromCSV(Path filepath) throws IOException {
		ArrayList<HashMap<String, String>> nouns = new ArrayList<HashMap<String, String>>();
		String s = Files.readString(filepath);
		String[] rows = s.split("\r?\n");
		if (rows.length > 0) {
			ArrayList<String> header = new ArrayList<String>();
			for (String col : rows[0].split(",")) {
				header.add(col);
			}
			for (int i = 1; i < rows.length; i++) {
				String[] row = rows[i].split(",");
				HashMap<String, String> map = new HashMap<String, String>();
				for (int j = 0; j < header.size(); j++) {
					String key = header.get(j);
					String val = row[j];
					if (!val.isEmpty()) map.put(key, val);
				}
				if (!map.isEmpty()) nouns.add(map);
			}
		}
		return new Nouns(nouns);
	}
}
