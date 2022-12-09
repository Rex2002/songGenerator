package org.se.text.analysis.dict;

import java.util.*;

import org.se.text.analysis.model.AffixType;
import org.se.text.analysis.model.DisplayableParent;
import org.se.text.analysis.model.Numerus;

/**
 * @author Val Richter
 */
public class TermAffixes implements DisplayableParent {
	public String radix;
	public Numerus numerus;
	public AffixType type;
	public Boolean toUmlaut;

	public TermAffixes() {
	}

	public TermAffixes(String radix, Numerus numerus, AffixType type, boolean toUmlaut) {
		this.radix = radix;
		this.numerus = numerus;
		this.type = type;
		this.toUmlaut = toUmlaut;
	}

	public boolean grammarticallyEquals(TermAffixes other) {
		return numerus == other.numerus;
	}

	@Override
	public String toStringHelper() {
		return " radix='" + getRadix() + "'" + ", numerus='" + getNumerus() + "'" + ", type='" + getType() + "'" + ", toUmlaut='" + getToUmlaut()
				+ "'";
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

	public AffixType getType() {
		return type;
	}

	public void setType(AffixType type) {
		this.type = type;
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

	public TermAffixes radix(String radix) {
		setRadix(radix);
		return this;
	}

	public TermAffixes numerus(Numerus numerus) {
		setNumerus(numerus);
		return this;
	}

	public TermAffixes type(AffixType type) {
		setType(type);
		return this;
	}

	public TermAffixes toUmlaut(boolean toUmlaut) {
		setToUmlaut(toUmlaut);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermAffixes)) {
			return false;
		}
		TermAffixes termEndings = (TermAffixes) o;
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
