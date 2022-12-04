package org.se;

import java.io.IOException;
import org.se.Text.Analysis.TermCollection;

public class Main {
	public static void main(String[] args) throws IOException {
		TermCollection x = TermExample.getExample();
		System.out.println(x);
	}
}