package org.se;

import java.util.*;
import org.se.music.model.Genre;

/**
 * @author Val Richter
 */
public class Settings {
	public Genre genre;
	public Integer tempo;
	public boolean nsfw;

	public Settings() {
	}

	public Settings(Genre genre, Integer tempo, boolean nsfw) {
		this.genre = genre;
		this.tempo = tempo;
		this.nsfw = nsfw;
	}

	public Genre getGenre() {
		return this.genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
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

	public Integer getTempo() {
		return this.tempo;
	}

	public void setTempo(Integer tempo) {
		this.tempo = tempo;
	}

	public Settings genre(Genre genre) {
		setGenre(genre);
		return this;
	}

	public Settings nsfw(boolean nsfw) {
		setNsfw(nsfw);
		return this;
	}

	public Settings tempo(Integer tempo) {
		setTempo(tempo);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Settings)) {
			return false;
		}
		Settings settings = (Settings) o;
		return Objects.equals(genre, settings.genre) && nsfw == settings.nsfw && tempo == settings.tempo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(genre, nsfw, tempo);
	}

	@Override
	public String toString() {
		return "{" + " genre='" + getGenre() + "'" + ", nsfw='" + isNsfw() + "'" + ", tempo='" + getTempo() + "'" + "}";
	}

}
