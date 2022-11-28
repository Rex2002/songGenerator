package org.se.Text.Analysis;

import java.util.*;

/**
 * @author Val Richter
 */
class TermComp implements Comparator<NounTerm> {
	private Map<String, TermVariations> terms;
	private int generalTermBias = 1;
	private int specialVariationBias = 1;

	public TermComp(Map<String, TermVariations> terms, int generalTermBias, int specialVariationBias) {
		this.terms = terms;
		this.generalTermBias = generalTermBias;
		this.specialVariationBias = specialVariationBias;
	}

	public TermComp(Map<String, TermVariations> terms) {
		this.terms = terms;
	}

	@Override
	public int compare(NounTerm o1, NounTerm o2) {
		TermVariations l1 = terms.get(o1.getLemma());
		TermVariations l2 = terms.get(o2.getLemma());
		int x1 = o1.getFrequency() * specialVariationBias + l1.getFrequency() * generalTermBias;
		int x2 = o2.getFrequency() * specialVariationBias + l2.getFrequency() * generalTermBias;
		return Integer.compare(x1, x2);
	}
}