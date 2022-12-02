package org.se.Text.Analysis.dict;

import java.util.*;

/**
 * @author Val Richter
 */
public class WordList implements Iterable<WordWithData> {
	private List<WordWithData> store = new ArrayList<WordWithData>();
	final String baseKey;

	public WordList() {
		this.baseKey = "radix";
	}

	public WordList(String baseKey) {
		this.baseKey = baseKey;
	}

	public WordList(Iterable<String> strings) {
		this.baseKey = "radix";
		for (String s : strings) {
			WordWithData h = new WordWithData();
			h.put(baseKey, s);
			this.insert(h);
		}
	}

	@Override
	public Iterator<WordWithData> iterator() {
		return store.iterator();
	}

	public boolean isEmpty() {
		return store.isEmpty();
	}

	public int size() {
		return store.size();
	}

	private int binSearch(String s, int start, int end) {
		int mid = (int) (end + start) / 2;
		while (end - start > 1) {
			mid = (int) (end + start) / 2;
			int x = store.get(mid).get(baseKey).compareTo(s);
			if (x == 0)
				return mid;
			else if (x > 0)
				end = mid;
			else
				start = mid;
		}
		return mid;
	}

	private int binSearch(String s) {
		return binSearch(s, 0, size());
	}

	public boolean insert(WordWithData h) {
		if (!h.containsKey(baseKey)) {
			return false;
		}
		int i = binSearch(h.get(baseKey));
		store.add(i, h);
		return true;
	}

	public boolean insert(String s) {
		WordWithData h = new WordWithData();
		return insert(h);
	}

	public boolean insertAll(WordWithData[] list) {
		boolean res = true;
		for (WordWithData hashMap : list) {
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
		for (WordWithData h : list.store) {
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
		WordWithData h = store.get(i);
		if (h.get(baseKey) == s) {
			return Optional.ofNullable(h.get(key));
		}
		return Optional.empty();
	}

	public Optional<WordWithData> get(String s) {
		int i = binSearch(s);
		WordWithData h = store.get(i);
		if (h.get(baseKey).equalsIgnoreCase(s)) {
			return Optional.of(h);
		}
		return Optional.empty();
	}

	public Optional<String> getDefault(String s) {
		return get(s, baseKey);
	}

	public boolean has(String s) {
		if (store.isEmpty()) {
			return false;
		}
		int i = binSearch(s);
		WordWithData h = store.get(i);
		return h != null && h.get(baseKey).compareTo(s) == 0;
	}
}
