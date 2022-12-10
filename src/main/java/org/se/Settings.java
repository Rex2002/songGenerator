package org.se;

import java.util.*;
import org.se.music.model.Genre;

/**
 * @author Val Richter
 */
public class Settings {
	public String filepath;
	public Genre genre;
	public Integer tempo;
	public boolean nsfw = false;

	public Settings() {
	}

	public Settings(String filepath, Genre genre, Integer tempo) {
		this.filepath = filepath;
		this.genre = genre;
		this.tempo = tempo;
	}

	public Settings(String filepath, Genre genre, Integer tempo, boolean nsfw) {
		this.filepath = filepath;
		this.genre = genre;
		this.tempo = tempo;
		this.nsfw = nsfw;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public Genre getGenre() {
		return this.genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
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

	public boolean getNsfw() {
		return this.nsfw;
	}

	public void setNsfw(boolean nsfw) {
		this.nsfw = nsfw;
	}

	public Settings filepath(String filepath) {
		setFilepath(filepath);
		return this;
	}

	public Settings genre(Genre genre) {
		setGenre(genre);
		return this;
	}

	public Settings tempo(Integer tempo) {
		setTempo(tempo);
		return this;
	}

	public Settings nsfw(boolean nsfw) {
		setNsfw(nsfw);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Settings)) {
			return false;
		}
		Settings settings = (Settings) o;
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
