package org.se.Text.Analysis;

import java.util.*;

import org.se.Text.Analysis.dict.WordStemmer;

/**
 * @author Val Richter
 */
public class Tag {
	public TagType type;
	public String word;
	public Optional<WordStemmer> data;

	public Tag(String word, TagType type) {
		this.word = word;
		this.type = type;
		this.data = Optional.empty();
	}

	public Tag(String word, TagType type, WordStemmer data) {
		this.word = word;
		this.type = type;
		this.data = Optional.ofNullable(data);
	}

	public boolean is(TagType type) {
		return this.type == type;
	}

	public TagType getType() {
		return this.type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Optional<WordStemmer> getData() {
		return this.data;
	}

	public void setData(Optional<WordStemmer> data) {
		this.data = data;
	}

	public Tag type(TagType type) {
		setType(type);
		return this;
	}

	public Tag word(String word) {
		setWord(word);
		return this;
	}

	public Tag data(Optional<WordStemmer> data) {
		setData(data);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Tag)) {
			return false;
		}
		Tag tag = (Tag) o;
		return Objects.equals(type, tag.type) && Objects.equals(word, tag.word);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, word);
	}

	@Override
	public String toString() {
		return "{" +
				" type='" + getType() + "'" +
				", word='" + getWord() + "'" +
				"}";
	}

}
