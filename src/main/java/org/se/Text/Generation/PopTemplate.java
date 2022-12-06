package org.se.text.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class PopTemplate {
	@JsonProperty
	private String[] strophe;

	public PopTemplate(@JsonProperty("strophe") String[] strophe) {
		this.strophe = strophe;
	}

	public String[] getStrophe() {
		return strophe;
	}
}
