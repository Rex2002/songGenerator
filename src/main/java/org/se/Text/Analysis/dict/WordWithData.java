package org.se.Text.Analysis.dict;

import java.util.*;

public class WordWithData extends HashMap<String, String> {
	private String baseKey = "radix";

	public WordWithData() {
	}

	public WordWithData(String baseKey) {
		this.baseKey = baseKey;
	}

	public boolean getBoolean(String key) {
		return Parser.parseBool(get(key));
	}

	public boolean getBoolean() {
		return getBoolean(baseKey);
	}

	public String[] getList(String key) {
		return Parser.parseList(get(key));
	}

	public String[] getList() {
		return getList(baseKey);
	}

	public Optional<Integer> getInt(String key) {
		return Parser.parseInt(get(key));
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
