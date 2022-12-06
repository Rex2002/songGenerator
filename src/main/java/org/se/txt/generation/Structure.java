package org.se.txt.generation;

public class Structure {

	private Part[] parts;
	private Key key;
	private Genre genre;

	enum Key {
		idkwhat
	}

	enum Genre {
		pop, rock, electro, blues
	}

	public Part[] getParts() {
		return parts;
	}

	public Key getKey() {
		return key;
	}

	public Genre getGenre() {
		return genre;
	}

}
