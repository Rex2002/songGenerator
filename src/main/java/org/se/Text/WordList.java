package org.se.Text;

import java.util.*;

public class WordList {
	private List<String> store = new ArrayList<String>();

	public boolean isEmpty() {
		return store.isEmpty();
	}

	public int size() {
		return store.size();
	}

	private int binSearch(String s, int start, int end) {
		int mid = (int) end / 2 + start;
		while (end - start > 1) {
			mid = (int) end / 2 + start;
			int x = store.get(mid).compareTo(s);
			if (x == 0) return mid;
			else if (x < 0) end = mid;
			else start = mid;
		}
		return mid;
	}

	private int binSearch(String s) {
		return binSearch(s, 0, size());
	}

	public void insert(String s) {
		int i = binSearch(s);
		store.add(i, s);
	}

	public void insertAll(String[] strings) {
		for (String s : strings) insert(s);
	}

	public boolean has(String s) {
		int i = binSearch(s);
		return store.get(i) == s;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof WordList)) {
			return false;
		}
		WordList wordList = (WordList) o;
		return Objects.equals(store, wordList.store);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(store);
	}
}
