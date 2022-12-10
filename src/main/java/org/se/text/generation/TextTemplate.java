package org.se.text.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
/**
 * @author Olivier Stenzel
 */
public class TextTemplate {
	@JsonProperty
	private final String[] strophe;
	@JsonProperty
	private final int length;

	public TextTemplate(@JsonProperty("strophe") String[] strophe, @JsonProperty("length") int length) {
		this.strophe = strophe;
		this.length = length;
	}

	public String[] getStrophe() {
		return strophe;
	}
	public int getLength() {return length; 	}

	@Override
	public String toString() {
		return "Template{" +
				"strophe=" + Arrays.toString(strophe) +
				", length=" + length +
				'}';
	}
}
