package org.se.Text.Analysis.dict;

import java.util.*;
import org.se.Text.Analysis.*;

public class WordWithData extends HashMap<String, String> {
	private String baseKey = "radix";

	public WordWithData() {
	}

	public WordWithData(String baseKey) {
		this.baseKey = baseKey;
	}

	public Gender getGender(String key) {
		return Parser.parseGender(get(key));
	}

	public Gender getGender() {
		return Parser.parseGender(baseKey);
	}

	public GrammaticalCase getGrammaticalCase(String key) {
		return Parser.parseGrammaticalCase(get(key));
	}

	public GrammaticalCase getGrammaticalCase() {
		return Parser.parseGrammaticalCase(baseKey);
	}

	public Numerus getNumerus(String key) {
		return Parser.parseNumerus(get(key));
	}

	public Numerus getNumerus() {
		return Parser.parseNumerus(baseKey);
	}

	public boolean getBool(String key) {
		return Parser.parseBool(get(key));
	}

	public boolean getBool() {
		return Parser.parseBool(baseKey);
	}

	public String[] getList(String key) {
		return Parser.parseList(get(key));
	}

	public String[] getList() {
		return Parser.parseList(baseKey);
	}

	public Optional<Integer> getInt(String key) {
		return Parser.parseInt(get(key));
	}

	public Optional<Integer> getInt() {
		return Parser.parseInt(baseKey);
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
