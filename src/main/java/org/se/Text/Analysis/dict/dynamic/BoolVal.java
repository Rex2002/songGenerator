package org.se.Text.Analysis.dict.dynamic;

import java.util.*;

/**
 * @author Val Richter
 */
public class BoolVal implements WordData {
	private Boolean bool;

	@Override
	public WordData fromStr(String s) {
		if (s.toLowerCase().startsWith("t"))
			return new BoolVal(true);
		else
			return new BoolVal(false);
	}

	@Override
	public String getStr() {
		if (bool)
			return "true";
		else
			return "false";
	}

	@Override
	public Boolean getVal() {
		return bool;
	}

	public BoolVal() {
	}

	public BoolVal(boolean bool) {
		this.bool = bool;
	}

	public boolean isBool() {
		return this.bool;
	}

	public boolean getBool() {
		return this.bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public BoolVal bool(boolean bool) {
		setBool(bool);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof BoolVal)) {
			return false;
		}
		BoolVal boolVal = (BoolVal) o;
		return bool == boolVal.bool;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(bool);
	}

	@Override
	public String toString() {
		return "{" +
				" bool='" + isBool() + "'" +
				"}";
	}

}
