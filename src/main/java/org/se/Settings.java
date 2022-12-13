package org.se;

import java.util.*;
import org.se.music.model.Genre;

/**
 * Model for the Settings, that the UI passes to the {@link SongGenerator}.
 *
 * @author Val Richter
 */
public class Settings {
	private final String filepath;
	private final Genre genre;
	private Integer tempo;

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

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Settings settings)) {
			return false;
		}
		return Objects.equals(filepath, settings.filepath) && Objects.equals(genre, settings.genre) && Objects.equals(tempo, settings.tempo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filepath, genre, tempo);
	}

	@Override
	public String toString() {
		return "{" + " filepath='" + getFilepath() + "'" + ", genre='" + getGenre() + "'" + ", tempo='" + getTempo() + "'" + "}";
	}

}
