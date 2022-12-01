package org.se.Text.Analysis.dict.dynamic;

import java.util.*;

public class IntVal implements WordData {
	private Integer i;

	@Override
	public WordData fromStr(String s) {
		// TODO: Decide whether to add error handling
		// For example, we could default to some integer (like 0) on error
		return new IntVal(Integer.parseInt(s));
	}

	@Override
	public String getStr() {
		return i.toString();
	}

	@Override
	public Integer getVal() {
		return i;
	}

	public IntVal(Integer i) {
		this.i = i;
	}

	public Integer getI() {
		return this.i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public IntVal i(Integer i) {
		setI(i);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof IntVal)) {
			return false;
		}
		IntVal intVal = (IntVal) o;
		return Objects.equals(i, intVal.i);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(i);
	}

	@Override
	public String toString() {
		return "{" +
				" i='" + getI() + "'" +
				"}";
	}

}
