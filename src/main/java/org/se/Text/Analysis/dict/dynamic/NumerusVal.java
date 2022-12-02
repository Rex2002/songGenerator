package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import org.se.Text.Analysis.Numerus;

/**
 * @author Val Richter
 */
public class NumerusVal implements WordData {
	private Numerus numerus;

	@Override
	public NumerusVal fromStr(String s) {
		switch (s.toLowerCase().charAt(0)) {
			// t for true
			case 't':
				return new NumerusVal(Numerus.Plural);

			// p for plural
			case 'p':
				return new NumerusVal(Numerus.Plural);

			default:
				return new NumerusVal(Numerus.Singular);
		}
	}

	@Override
	public String getStr() {
		return numerus.toString().toLowerCase();
	}

	@Override
	public Numerus getVal() {
		return numerus;
	}

	public NumerusVal(Numerus numerus) {
		this.numerus = numerus;
	}

	public Numerus getGender() {
		return this.numerus;
	}

	public void setGender(Numerus numerus) {
		this.numerus = numerus;
	}

	public NumerusVal numerus(Numerus numerus) {
		setGender(numerus);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof NumerusVal)) {
			return false;
		}
		NumerusVal numerusVal = (NumerusVal) o;
		return Objects.equals(numerus, numerusVal.numerus);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(numerus);
	}

	@Override
	public String toString() {
		return "{" +
				" numerus='" + getGender() + "'" +
				"}";
	}
}
