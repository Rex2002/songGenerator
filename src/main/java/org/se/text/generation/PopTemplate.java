package org.se.text.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class PopTemplate {
	@JsonProperty
	private String[] strophe;
	@JsonProperty
	private int length;

	public PopTemplate(@JsonProperty("strophe") String[] strophe,@JsonProperty("length") int length) {
		this.strophe = strophe;
		this.length = length;
	}

	public String[] getStrophe() {
		return strophe;
	}
	public int getLength() {return length; 	}
}
