package org.se.text.analysis.dict;

import java.util.*;

import org.se.text.analysis.DisplayableParent;
import org.se.text.analysis.Numerus;

/**
 * @author Val Richter
 */
public class TermEndings implements DisplayableParent {
	public String radix;
	public Numerus numerus;
	public Boolean toUmlaut;

	public TermEndings() {
	}

	public TermEndings(String radix, Numerus numerus, boolean toUmlaut) {
		this.radix = radix;
		this.numerus = numerus;
		this.toUmlaut = toUmlaut;
	}

	@Override
	public String toStringHelper() {
		return " radix='" + getRadix() + "'" + ", numerus='" + getNumerus() + "'" + ", toUmlaut='" + getToUmlaut() + "'";
	}

	public String getRadix() {
		return this.radix;
	}

	public void setRadix(String radix) {
		this.radix = radix;
	}

	public Numerus getNumerus() {
		return this.numerus;
	}

	public void setNumerus(Numerus numerus) {
		this.numerus = numerus;
	}

	public boolean isToUmlaut() {
		return this.toUmlaut;
	}

	public boolean getToUmlaut() {
		return this.toUmlaut;
	}

	public void setToUmlaut(boolean toUmlaut) {
		this.toUmlaut = toUmlaut;
	}

	public TermEndings radix(String radix) {
		setRadix(radix);
		return this;
	}

	public TermEndings numerus(Numerus numerus) {
		setNumerus(numerus);
		return this;
	}

	public TermEndings toUmlaut(boolean toUmlaut) {
		setToUmlaut(toUmlaut);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermEndings)) {
			return false;
		}
		TermEndings termEndings = (TermEndings) o;
		return Objects.equals(radix, termEndings.radix) && Objects.equals(numerus, termEndings.numerus) && toUmlaut == termEndings.toUmlaut;
	}

	@Override
	public int hashCode() {
		return Objects.hash(radix, numerus, toUmlaut);
	}

	@Override
	public String toString() {
		return "{" + toStringHelper() + "}";
	}

}
