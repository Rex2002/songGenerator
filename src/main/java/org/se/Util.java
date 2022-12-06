package org.se;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Val Richter
 */
public class Util {
	public static <T> boolean any(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (f.test(item)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean all(Iterable<T> items, Predicate<? super T> f) {
		for (T item : items) {
			if (!f.test(item)) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean contains(Iterable<T> items, T item) {
		return any(items, x -> x == item);
	}

	public static <T> Optional<T> find(Iterable<T> items, Predicate<? super T> f) {
		for (T t : items) {
			if (f.test(t)) return Optional.of(t);
		}
		return Optional.empty();
	}
}
