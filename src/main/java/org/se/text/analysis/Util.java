package org.se.text.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Val Richter
 */
public class Util {
	public static <T> boolean any(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (f.test(item)) return true;
		}
		return false;
	}

	public static <T> boolean all(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (!f.test(item)) return false;
		}
		return true;
	}

	public static <T> List<T> findAll(Iterable<T> items, Predicate<? super T> f) {
		List<T> res = new ArrayList<>();
		for (T t : items) {
			if (f.test(t)) res.add(t);
		}
		return res;
	}
}