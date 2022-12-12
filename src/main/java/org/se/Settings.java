package org.se;

import java.util.*;
import org.se.music.model.Genre;

/**
 * @author Val Richter
 */
public class Settings {
	private final String filepath;
	private final Genre genre;
	private Integer tempo;
	private final boolean nsfw = false;

	public Settings(String filepath, Genre genre, Integer tempo) {
		this.filepath = filepath;
		this.genre = genre;
		this.tempo = tempo;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public Genre getGenre() {
		return this.genre;
	}

	public Integer getTempo() {
		return this.tempo;
	}

	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	}

	public boolean isNsfw() {
		return this.nsfw;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Settings settings)) {
			return false;
		}
		return Objects.equals(filepath, settings.filepath) && Objects.equals(genre, settings.genre) && Objects.equals(tempo, settings.tempo)
				&& nsfw == settings.nsfw;
	}

	@Override
	public int hashCode() {
		return Objects.hash(filepath, genre, tempo, nsfw);
	}

	@Override
	public String toString() {
		return "{" + " filepath='" + getFilepath() + "'" + ", genre='" + getGenre() + "'" + ", tempo='" + getTempo() + "'" + ", nsfw='" + isNsfw()
				+ "'" + "}";
	}

}
