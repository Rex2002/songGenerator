package org.se.text.analysis.dict;

import java.util.*;

/**
 * @author Val Richter
 */
public class WordWithData extends HashMap<String, String> {
	private final String baseKey = "radix";

	public WordWithData() {
	}

	public String get() {
		return this.get(baseKey);
	}

	@Override
	public String toString() {
		StringBuilder strbuilder = new StringBuilder();
		for (Entry<String, String> entry : entrySet()) {
			strbuilder.append(", ");
			strbuilder.append(entry.getKey());
			strbuilder.append("='");
			strbuilder.append(entry.getValue());
			strbuilder.append("'");
		}
		return "{" + " baseKey='" + getBaseKey() + "'" + strbuilder + "}";
	}

	public <T> Optional<T> get(String key, Class<T> cls) {
		return Parser.parse(get(key), cls);
	}

	public String getBaseKey() {
		return this.baseKey;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		return o instanceof WordWithData;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(baseKey);
	}
}
