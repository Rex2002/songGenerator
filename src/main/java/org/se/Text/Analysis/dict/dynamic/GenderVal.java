package org.se.Text.Analysis.dict.dynamic;

import java.util.*;
import org.se.Text.Analysis.Gender;

public class GenderVal implements DynamicType {
	private Gender gender;

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
