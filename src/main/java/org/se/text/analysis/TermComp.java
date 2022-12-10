package org.se.text.analysis;

import java.util.*;

/**
 * @author Val Richter
 */
class TermComp<T extends Term> implements Comparator<T> {
	private final Map<String, TermVariations<T>> terms;
	private int generalTermBias = 1;
	private int specialVariationBias = 1;

	public TermComp(Map<String, TermVariations<T>> terms) {
		this.terms = terms;
	}

	@Override
	public int compare(T t1, T t2) {
		TermVariations<T> v1 = terms.get(t1.getRadix());
		TermVariations<T> v2 = terms.get(t2.getRadix());
		int x1 = t1.getFrequency() * specialVariationBias + v1.getFrequency() * generalTermBias;
		int x2 = t2.getFrequency() * specialVariationBias + v2.getFrequency() * generalTermBias;
		return Integer.compare(x1, x2);
	}
}