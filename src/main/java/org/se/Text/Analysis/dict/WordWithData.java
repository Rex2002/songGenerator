package org.se.text.analysis.dict;

import java.util.*;

/**
 * @author Val Richter
 */
public class WordWithData extends HashMap<String, String> {
	private String baseKey = "radix";

	public WordWithData() {
	}

	public WordWithData(String baseKey, HashMap<? extends String, ? extends String> map) {
		super(map);
		this.baseKey = baseKey;
	}

	public WordWithData(String baseKey) {
		this.baseKey = baseKey;
	}

	public String get() {
		return this.get(baseKey);
	}

	// Since parseInt accepts a third argument, this method must exist, even though
	// all other <getType> methods were replaced with the generic get(Class<T> cls)
	// There might be a better workaround, but I find this more elegant than having
	// a method for each type cluttered up in this class
	public Integer getInt(String key, Integer def) {
		return Parser.parseInt(get(key), def);
	}

	public <T> Optional<T> get(Class<T> cls) {
		return Parser.parse(get(), cls);
	}

	public <T> Optional<T> get(String key, Class<T> cls) {
		return Parser.parse(get(key), cls);
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
		if (o == this) return true;
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
		return "{" + " baseKey='" + getBaseKey() + "'" + "}";
	}

}
