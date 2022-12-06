package org.se.text.analysis.dict;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Val Richter
 */
public class WordList implements Iterable<WordWithData> {
	private List<WordWithData> store = new ArrayList<>();
	private WordWithData elementWithLongestBase = null;
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

	private int cmpBaseLengths(WordWithData x, WordWithData y) {
		return Integer.compareUnsigned(x.get().length(), y.get().length());
	}

	private void updateElementWithLongestBase() {
		WordWithData best = null;
		for (WordWithData w : store) {
			if (best == null || cmpBaseLengths(w, best) > 0) {
				best = w;
			}
		}
		elementWithLongestBase = best;
	}

	public WordWithData getElementWithLongestBase() {
		if (elementWithLongestBase != null) {
			return elementWithLongestBase;
		} else {
			updateElementWithLongestBase();
			return elementWithLongestBase;
		}
	}

	private int binSearch(String s, int start, int end) {
		int mid = (end + start) / 2;
		while (end - start > 1) {
			mid = (end + start) / 2;
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
		if (elementWithLongestBase != null && cmpBaseLengths(h, elementWithLongestBase) > 0) {
			elementWithLongestBase = h;
		}
		h.put(baseKey, h.get(baseKey).toLowerCase());
		int i = binSearch(h.get(baseKey));
		store.add(i, h);
		return true;
	}

	public boolean insert(String s) {
		WordWithData h = new WordWithData();
		h.put(baseKey, s);
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
			if (!Objects.equals(list.baseKey, this.baseKey)) {
				h.put(this.baseKey, h.get(list.baseKey));
			}
			if (!insert(h)) {
				res = false;
			}
		}
		return res;
	}

	public Optional<WordWithData> find(Predicate<? super WordWithData> f) {
		for (WordWithData w : store) {
			if (f.test(w))
				return Optional.of(w);
		}
		return Optional.empty();
	}

	/**
	 * Filters a list of elements in this WordList. This method is pure and doesn't
	 * mutate this {@link WordList} object.
	 *
	 * @param f
	 *          Function determining for each element, whether it should be in the
	 *          newly created {@link WordList}
	 * @return
	 */
	public WordList filter(Predicate<? super WordWithData> f) {
		WordList res = new WordList();
		for (int i = 0; i < store.size(); i++) {
			WordWithData w = store.get(i);
			if (f.test(w)) {
				// We can add the WordWithData object unsafely directly, because we know the
				// words were already stored in this list and we iterate over this list in an
				// ordered fashion
				res.store.add(w);
			}
		}
		return res;
	}

	/**
	 * Same as filter, but mutates this WordList object
	 *
	 * @param f
	 *          Function determining for each element, whether it should stay in
	 *          this {@link WordList}
	 */
	public void filterMut(Predicate<? super WordWithData> f) {
		elementWithLongestBase = null;
		store = store.stream().filter(f).collect(Collectors.toList());
	}

	public Optional<String> get(String s, String key) {
		s = s.toLowerCase();
		int i = binSearch(s);
		WordWithData h = store.get(i);
		if (Objects.equals(h.get(baseKey), s)) {
			return Optional.ofNullable(h.get(key));
		}
		return Optional.empty();
	}

	public Optional<WordWithData> get(String s) {
		s = s.toLowerCase();
		int i = binSearch(s);
		WordWithData h = store.get(i);
		if (h.get(baseKey).equals(s)) {
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
		int i = binSearch(s.toLowerCase());
		WordWithData h = store.get(i);
		return h != null && h.get(baseKey).equalsIgnoreCase(s);
	}
}
