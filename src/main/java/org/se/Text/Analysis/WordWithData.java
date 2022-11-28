package org.se.Text.Analysis;

import java.util.*;

public class WordWithData extends HashMap<String, String> {
	private String baseKey = "lemma";

	public WordWithData() {
	}

	public WordWithData(String baseKey) {
		this.baseKey = baseKey;
	}

	public static Optional<Integer> parseInt(String s) {
		try {
			return Optional.of(Integer.parseInt(s.trim()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static boolean parseBool(String s) {
		if (s.toLowerCase().startsWith("t")) {
			return true;
		}
		return false;
	}

	public static String[] parseList(String s) {
		return s.split("-");
	}

	public boolean getBoolean(String key) {
		return parseBool(get(key));
	}

	public boolean getBoolean() {
		return getBoolean(baseKey);
	}

	public String[] getList(String key) {
		return parseList(get(key));
	}

	public String[] getList() {
		return getList(baseKey);
	}

	public Optional<Integer> getInt(String key) {
		return parseInt(get(key));
	}

	public Optional<Integer> getInt() {
		return getInt(baseKey);
	}

	public String get() {
		return this.get(baseKey);
	}

	public String getBaseKey() {
		return this.baseKey;
	}

	public void setBaseKey(String baseKey) {
		this.baseKey = baseKey;
	}

	public WordWithData baseKey(String baseKey) {
		setBaseKey(baseKey);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof WordWithData)) {
			return false;
		}
		WordWithData wordWithData = (WordWithData) o;
		return Objects.equals(baseKey, wordWithData.baseKey);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(baseKey);
	}

	@Override
	public String toString() {
		return "{" +
				" baseKey='" + getBaseKey() + "'" +
				"}";
	}

}
