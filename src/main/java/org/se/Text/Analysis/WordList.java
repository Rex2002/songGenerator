package org.se.Text.Analysis;

import java.util.*;

public class WordList {
	private List<HashMap<String, String>> store = new ArrayList<HashMap<String, String>>();
	private String baseKey = "lemma";

	public WordList() {
	}

	public WordList(String baseKey) {
		this.baseKey = baseKey;
	}

	public WordList(Iterable<String> strings) {
		for (String s : strings) {
			HashMap<String, String> h = new HashMap<String, String>();
			h.put(baseKey, s);
			this.insert(h);
		}
	}

	public boolean isEmpty() {
		return store.isEmpty();
	}

	public int size() {
		return store.size();
	}

	private int binSearch(String s, int start, int end) {
		int mid = (int) end / 2 + start;
		while (end - start > 1) {
			mid = (int) (end + start) / 2;
			int x = store.get(mid).get(baseKey).compareTo(s);
			if (x == 0)
				return mid;
			else if (x < 0)
				end = mid;
			else
				start = mid;
		}
		return mid;
	}

	private int binSearch(String s) {
		return binSearch(s, 0, size());
	}

	public boolean insert(HashMap<String, String> h) {
		if (!h.containsKey(baseKey)) {
			return false;
		}
		int i = binSearch(h.get(baseKey));
		store.add(i, h);
		return true;
	}

	public boolean insert(String s) {
		HashMap<String, String> h = new HashMap<String, String>();
		return insert(h);
	}

	public boolean insertAll(HashMap<String, String>[] list) {
		boolean res = true;
		for (HashMap<String, String> hashMap : list) {
			if (!insert(hashMap)) {
				res = false;
			}
		}
		return res;
	}

	public boolean insertAll(String[] strings) {
		boolean res = true;
		for (String s : strings) {
			if (!insert(s)) {
				res = false;
			}
		}
		return res;
	}

	public boolean insertAll(WordList list) {
		boolean res = true;
		for (HashMap<String, String> h : list.store) {
			if (list.baseKey != this.baseKey) {
				h.put(this.baseKey, h.get(list.baseKey));
			}
			if (!insert(h)) {
				res = false;
			}
		}
		return res;
	}

	public Optional<String> get(String s, String key) {
		int i = binSearch(s);
		HashMap<String, String> h = store.get(i);
		if (h.get(baseKey) == s) {
			return Optional.ofNullable(h.get(key));
		}
		return Optional.empty();
	}

	public Optional<HashMap<String, String>> get(String s) {
		int i = binSearch(s);
		HashMap<String, String> h = store.get(i);
		if (h.get(baseKey) == s) {
			return Optional.of(h);
		}
		return Optional.empty();
	}

	public boolean has(String s) {
		int i = binSearch(s);
		if (store.isEmpty()) {
			return false;
		}
		HashMap<String, String> h = store.get(i);
		return h != null && h.get(baseKey) == s;
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
