package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import java.util.stream.Stream;

import org.se.Text.Analysis.dict.CSVReader;

public class ListVal implements DynamicType {
	private List<DynamicType> store;

	public static DynamicType fromStr(String s) {
		ListVal l = new ListVal();
		for (String x : CSVReader.parseList(s)) {
			l.store.add();
		}
	}

	public String getStr() {

	}

	public Object getVal() {

	}

	public ListVal() {
		store = new ArrayList<>();
	}

	public ListVal(List<DynamicType> store) {
		this.store = store;
	}

	public List<DynamicType> getStore() {
		return this.store;
	}

	public void setStore(List<DynamicType> store) {
		this.store = store;
	}

	public ListVal store(List<DynamicType> store) {
		setStore(store);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ListVal)) {
			return false;
		}
		ListVal listVal = (ListVal) o;
		return Objects.equals(store, listVal.store);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(store);
	}

	@Override
	public String toString() {
		return "{" +
				" store='" + getStore() + "'" +
				"}";
	}

}
