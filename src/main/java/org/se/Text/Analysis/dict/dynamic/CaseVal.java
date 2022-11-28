package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import org.se.Text.Analysis.GrammaticalCase;

public class CaseVal implements DynamicType {
	private GrammaticalCase grammaticalCase;

	public CaseVal(GrammaticalCase grammaticalCase) {
		this.grammaticalCase = grammaticalCase;
	}

	public GrammaticalCase getGender() {
		return this.grammaticalCase;
	}

	public void setGender(GrammaticalCase grammaticalCase) {
		this.grammaticalCase = grammaticalCase;
	}

	public CaseVal grammaticalCase(GrammaticalCase grammaticalCase) {
		setGender(grammaticalCase);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof CaseVal)) {
			return false;
		}
		CaseVal caseVal = (CaseVal) o;
		return Objects.equals(grammaticalCase, caseVal.grammaticalCase);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(grammaticalCase);
	}

	@Override
	public String toString() {
		return "{" +
				" grammaticalCase='" + getGender() + "'" +
				"}";
	}

}
