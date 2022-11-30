package org.se.Text.Analysis;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Gender {
	@JsonProperty("male")
	Male, @JsonProperty("female")
	Female, @JsonProperty("neutral")
	Neutral,
}
