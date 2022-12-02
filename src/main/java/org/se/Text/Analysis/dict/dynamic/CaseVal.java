package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import org.se.Text.Analysis.GrammaticalCase;

/**
 * @author Val Richter
 */
public class CaseVal implements WordData {
	private GrammaticalCase grammaticalCase;

	@Override
	public WordData fromStr(String s) {
		s = s.toLowerCase();
		switch (s.charAt(0)) {
			case 'n':
				return new CaseVal(GrammaticalCase.Nominative);

			case 'g':
				return new CaseVal(GrammaticalCase.Genitive);

			case 'd':
				return new CaseVal(GrammaticalCase.Dative);

			case 'a':
				return new CaseVal(GrammaticalCase.Accusative);

			default:
				return new CaseVal(GrammaticalCase.Nominative);
		}
	}

	@Override
	public String getStr() {
		return grammaticalCase.toString();
	}

	@Override
	public GrammaticalCase getVal() {
		return grammaticalCase;
	}

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
