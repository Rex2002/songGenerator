package org.se.text.analysis;

import java.util.*;

import org.se.text.analysis.dict.Dict;
import org.se.text.analysis.dict.WordWithData;

/**
 * @author Val Richter
 */
public class VerbTerm extends Term {
	String infinitive;

	public VerbTerm(String radix, String word, Integer syllableAmount, Numerus numerus, String infinitive) {
		super(radix, word, syllableAmount, numerus);
		this.infinitive = infinitive;
	}

	public VerbTerm(String word, Dict dict) {
		// TODO: Change hardcoded defaults to programmatically determined ones
		super(word);
		Optional<WordWithData> tmp = dict.getVerbs().get(word);
		if (tmp.isPresent()) {
			this.infinitive = tmp.get().get("infinitive");
		} else {
			this.infinitive = word;
		}
	}

	public VerbTerm(String word) {
		// TODO: Change hardcoded defaults to programmatically determined ones
		super(word);
		this.infinitive = word;
	}

	@Override
	public String toString() {
		return "{" + super.toStringHelper() + " infinitive='" + getInfinitive() + "'" + "}";
	}

	@Override
	public int hashData() {
		return this.hashCode();
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.radix, super.frequency, super.syllableAmount, super.word, infinitive);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof VerbTerm)) {
			return false;
		}
		VerbTerm verbTerm = (VerbTerm) o;
		return super.equals(verbTerm) && Objects.equals(infinitive, verbTerm.infinitive);
	}

	// Boilerplate:

	public String getInfinitive() {
		return this.infinitive;
	}

	public void setInfinitive(String infinitive) {
		this.infinitive = infinitive;
	}

	public VerbTerm infinitive(String infinitive) {
		setInfinitive(infinitive);
		return this;
	}
}
