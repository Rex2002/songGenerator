package org.se.Text.Analysis.dict.dynamic;

import java.util.Objects;

public class StrVal implements WordData {
	private String str;

	public WordData fromStr(String s) {
		return new StrVal(s);
	}

	public String getStr() {
		return str;
	}

	public String getVal() {
		return str;
	}

	public StrVal(String str) {
		this.str = str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public StrVal str(String str) {
		setStr(str);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof StrVal)) {
			return false;
		}
		StrVal strVal = (StrVal) o;
		return Objects.equals(str, strVal.str);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(str);
	}

	@Override
	public String toString() {
		return "{" +
				" str='" + getStr() + "'" +
				"}";
	}
}
