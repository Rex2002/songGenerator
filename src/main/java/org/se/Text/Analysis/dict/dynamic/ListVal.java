package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Val Richter
 */
public class ListVal implements WordData {
	private List<WordData> store;

	@Override
	public WordData fromStr(String s) {
		return new ListVal(Stream.of(s.split("-")).map(str -> new StrVal(str)).collect(Collectors.toList()));
	}

	public String getStr() {
		// int lastIndex = store.size() - 1;
		// String s = "[";
		// for (int i = 0; i < lastIndex; i++) {
		// s += store.get(i).getStr() + ", ";
		// }
		// s += store.get(lastIndex).getStr() + "]";
		// return s;
		return store.toString();
	}

	public List<WordData> getVal() {
		return store;
	}

	public ListVal() {
		store = new ArrayList<>();
	}

	public ListVal(List<WordData> store) {
		this.store = store;
	}

	public List<WordData> getStore() {
		return this.store;
	}

	public void setStore(List<WordData> store) {
		this.store = store;
	}

	public ListVal store(List<WordData> store) {
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
