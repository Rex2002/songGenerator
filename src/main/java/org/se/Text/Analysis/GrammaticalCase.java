package org.se.Text.Analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GrammaticalCase {
	@JsonProperty("nominative")
	Nominative, @JsonProperty("genitive")
	Genitive, @JsonProperty("dative")
	Dative, @JsonProperty("accusative")
	Accusative,
}
