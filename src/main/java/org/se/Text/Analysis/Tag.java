package org.se.Text.Analysis;

public class Tag {
	public TagType type;
	public String word;

	public Tag(String word, TagType type) {
		this.word = word;
		this.type = type;
	}

	public boolean is(TagType type) {
		return this.type == type;
	}
}
