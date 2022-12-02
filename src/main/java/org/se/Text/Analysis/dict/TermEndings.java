package org.se.Text.Analysis.dict;

import java.util.*;

import org.se.Text.Analysis.DisplayableParent;
import org.se.Text.Analysis.Numerus;

/**
 * @author Val Richter
 */
public abstract class TermEndings implements DisplayableParent {
	public String radix;
	public Numerus numerus;

	public TermEndings() {
	}

	public TermEndings(String radix, Numerus numerus) {
		this.radix = radix;
		this.numerus = numerus;
	}

	@Override
	public String toStringHelper() {
		return " radix='" + getRadix() + "'" +
				", numerus='" + getNumerus() + "'";
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

	public TermEndings radix(String radix) {
		setRadix(radix);
		return this;
	}

	public TermEndings numerus(Numerus numerus) {
		setNumerus(numerus);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof TermEndings)) {
			return false;
		}
		TermEndings termEndings = (TermEndings) o;
		return Objects.equals(radix, termEndings.radix) && Objects.equals(numerus, termEndings.numerus);
	}

	@Override
	public int hashCode() {
		return Objects.hash(radix, numerus);
	}

	@Override
	public String toString() {
		return "{" +
				toStringHelper() +
				"}";
	}

}
