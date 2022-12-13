package org.se.text.analysis;

import java.util.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
class TermComp<T extends Term<T>> implements Comparator<T> {
	private int generalTermBias = 1;
	private int specialVariationBias = 1;

	@Override
	public int compare(T t1, T t2) {
		TermVariations<T> v1 = t1.getVariations();
		TermVariations<T> v2 = t2.getVariations();
		int x1 = t1.getFrequency() * specialVariationBias + (v1 != null ? v1.getFrequency() * generalTermBias : 0);
		int x2 = t2.getFrequency() * specialVariationBias + (v2 != null ? v2.getFrequency() * generalTermBias : 0);
		return Integer.compare(x2, x1);
	}
}