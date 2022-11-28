package org.se.Text.Analysis.dict.dynamic;

public interface DynamicType {
	public static DynamicType fromStr(String s) {
		return new DynamicType() {
			@Override
			public String getStr() {
				return s;
			}

			@Override
			public Object getVal() {
				return s;
			}
		};
	}

	public String getStr();

	public Object getVal();
}
