package org.se.Text.Analysis;

public class Lemma {
	String str;

	public Lemma(String str) {
		this.str = str;
	}

	@Override
	public int hashCode() {
		return str.hashCode();
	}
}
