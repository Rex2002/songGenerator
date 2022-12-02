package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import org.se.Text.Analysis.Gender;

/**
 * @author Val Richter
 */
public class GenderVal implements WordData {
	private Gender gender;

	@Override
	public WordData fromStr(String s) {
		s = s.toLowerCase();
		if (s.startsWith("m")) {
			return new GenderVal(Gender.Male);
		} else if (s.startsWith("f")) {
			return new GenderVal(Gender.Female);
		} else {
			return new GenderVal(Gender.Neutral);
		}
	}

	@Override
	public String getStr() {
		return gender.toString().toLowerCase();
	}

	@Override
	public Gender getVal() {
		return gender;
	}

	public GenderVal(Gender gender) {
		this.gender = gender;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public GenderVal gender(Gender gender) {
		setGender(gender);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof GenderVal)) {
			return false;
		}
		GenderVal genderVal = (GenderVal) o;
		return Objects.equals(gender, genderVal.gender);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(gender);
	}

	@Override
	public String toString() {
		return "{" +
				" gender='" + getGender() + "'" +
				"}";
	}

}
