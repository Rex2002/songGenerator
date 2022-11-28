package org.se;

import java.util.function.Predicate;

public class Util {
	public static <T> boolean Any(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (f.test(item)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean All(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (!f.test(item)) {
				return false;
			}
		}
		return true;
	}
}