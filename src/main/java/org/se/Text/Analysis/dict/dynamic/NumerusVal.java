package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import org.se.Text.Analysis.Numerus;
import org.se.Text.Analysis.dict.CSVReader;

public class NumerusVal implements DynamicType {
	private Numerus numerus;

	@Override
	public static DynamicType fromStr(String s) {
		return new NumerusVal(CSVReader.parseNumerus(s));
	}

	@Override
	public String getStr() {
		return numerus.toString().toLowerCase();
	}

	@Override
	public Object getVal() {
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
