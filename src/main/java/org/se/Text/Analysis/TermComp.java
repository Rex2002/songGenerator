package org.se.Text.Analysis;

import java.util.*;

/**
 * @author Val Richter
 */
class TermComp<T extends Term> implements Comparator<T> {
	private Map<String, TermVariations<T>> terms;
	private int generalTermBias = 1;
	private int specialVariationBias = 1;

	public TermComp(Map<String, TermVariations<T>> terms, int generalTermBias, int specialVariationBias) {
		this.terms = terms;
		this.generalTermBias = generalTermBias;
		this.specialVariationBias = specialVariationBias;
	}

	public TermComp(Map<String, TermVariations<T>> terms) {
		this.terms = terms;
	}

	@Override
	public int compare(T o1, T o2) {
		TermVariations<T> l1 = terms.get(o1.getRadix());
		TermVariations<T> l2 = terms.get(o2.getRadix());
		int x1 = o1.getFrequency() * specialVariationBias + l1.getFrequency() * generalTermBias;
		int x2 = o2.getFrequency() * specialVariationBias + l2.getFrequency() * generalTermBias;
		return Integer.compare(x1, x2);
	}
}