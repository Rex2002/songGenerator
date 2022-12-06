package org.se.text.analysis.dict;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.se.Tuple;

/**
 * @author Val Richter
 */
public class WordStemmer {
	private String stem = "";
	private TermEndings grammartizedSuffix = new TermEndings();
	private List<WordWithData> prefixes = new LinkedList<WordWithData>();
	private List<WordWithData> suffixes = new LinkedList<WordWithData>();
	final String baseKey;

	// Constructors:

	public WordStemmer() {
		this.baseKey = "radix";
	}

	public WordStemmer(String stem) {
		this.baseKey = "radix";
		this.stem = stem;
	}

	public WordStemmer(String baseKey, String stem) {
		this.baseKey = baseKey;
		this.stem = stem;
	}

	public WordStemmer(String baseKey, String stem, TermEndings grammartizedSuffix) {
		this.baseKey = baseKey;
		this.stem = stem;
		this.grammartizedSuffix = grammartizedSuffix;
	}

	public WordStemmer(String stem, List<WordWithData> prefixes, List<WordWithData> suffixes) {
		this.baseKey = "radix";
		this.stem = stem;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
	}

	public WordStemmer(String baseKey, String stem, List<WordWithData> prefixes, List<WordWithData> suffixes) {
		this.baseKey = baseKey;
		this.stem = stem;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
	}

	public WordStemmer(String stem, TermEndings grammartizedSuffix, List<WordWithData> prefixes, List<WordWithData> suffixes, String baseKey) {
		this.stem = stem;
		this.grammartizedSuffix = grammartizedSuffix;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.baseKey = baseKey;
	}

	// Actual Logic:

	public int affixesCount() {
		int count = suffixes.size() + prefixes.size();
		if (grammartizedSuffix != null && grammartizedSuffix.radix != "") count += 1;
		return count;
	}

	public static WordStemmer[] radicalize(String s, List<? extends TermEndings> grammartizedSuffixes, WordList suffixes, WordList prefixes,
			int minStemLength, WordList diphtongs, WordList umlautChanges, String baseKey) {
		List<WordStemmer> res = new LinkedList<>();
		List<WordStemmer> grammartizedRes = findGrammartizedSuffixes(s, grammartizedSuffixes, umlautChanges, minStemLength, diphtongs, baseKey);
		for (WordStemmer v : grammartizedRes) {
			for (WordStemmer w : v.findSuffixes(suffixes, minStemLength, diphtongs)) {
				res.addAll(w.findPreffixes(prefixes, minStemLength, diphtongs));
			}
		}

		res.addAll(grammartizedRes);
		return res.toArray(new WordStemmer[0]);
	}

	// TODO: Optimize grammartizedSuffixes storage
	// Currently all grammartizedSuffixes are stored in a list
	// where many objects have the same radix
	// specifically this means, that we have many duplicate calculations
	// This could be optimized by storing a list of radixes
	// mapping to a list of Declinations
	// where no duplicate radixes are stored and the result is flattened
	private static <T extends TermEndings> List<WordStemmer> findGrammartizedSuffixes(String s, List<T> grammartizedSuffixes, WordList umlautChanges,
			int minStemLength, WordList diphtongs, String baseKey) {
		List<WordStemmer> res = new ArrayList<>();

		// Check all suffixs if they apply to the stem
		for (T suffix : grammartizedSuffixes) {
			if (s.endsWith(suffix.getRadix())) {
				String scopy = s.substring(0, s.length() - suffix.getRadix().length());
				if (scopy.length() >= minStemLength) {
					// Update Umlaut sequences if necessary
					scopy = Dict.changeUmlaut(umlautChanges, diphtongs, scopy, false);
					res.add(new WordStemmer(baseKey, scopy, suffix));
				}
			}
		}

		return res;
	}

	public List<WordStemmer> findPreffixes(WordList prefixes, int minStemLength, WordList diphtongs) {
		return findAffixes(prefixes, minStemLength, diphtongs, true, true);
	}

	public List<WordStemmer> findSuffixes(WordList suffixes, int minStemLength, WordList diphtongs) {
		return findAffixes(suffixes, minStemLength, diphtongs, true, false);
	}

	private List<WordStemmer> findAffixes(WordList affixes, int minStemLength, WordList diphtongs, boolean firstCall, boolean getPrefixes) {
		List<WordStemmer> res = new LinkedList<>();
		List<WordStemmer> nextAffixes = findNextAffix(affixes, minStemLength, diphtongs, false);

		if (firstCall && !nextAffixes.isEmpty()) {
			affixes = affixes.filter(w -> w.get("compound", Boolean.class).orElse(false));
		}
		for (WordStemmer w : nextAffixes) {
			List<WordStemmer> furtherAffixes = w.findAffixes(affixes, minStemLength, diphtongs, false, getPrefixes);
			furtherAffixes.add(this); // Add the possibility of having no affix removed

			if (getPrefixes) res.addAll(furtherAffixes);
			else res.addAll(0, furtherAffixes);
		}

		res.addAll(nextAffixes);
		return res;
	}

	private List<WordStemmer> findNextAffix(WordList affixes, int minStemLength, WordList diphtongs, boolean getPefixes) {
		int initialIndex;
		Predicate<? super Integer> condition;
		Consumer<? super Integer> afterIteration;
		Function<WordStemmer, List<WordWithData>> getWordAffixes;
		Predicate<Tuple<Integer, char[]>> additionalDiphtongCheck;
		if (getPefixes) {
			initialIndex = 0;
			condition = new Predicate<Integer>() {
				@Override
				public boolean test(Integer i) {
					return i < stem.length() - minStemLength;
				};
			};
			afterIteration = new Consumer<Integer>() {
				@Override
				public void accept(Integer i) {
					i++;
				}
			};
			getWordAffixes = new Function<WordStemmer, List<WordWithData>>() {
				@Override
				public List<WordWithData> apply(WordStemmer w) {
					return w.getPrefixes();
				}
			};
			additionalDiphtongCheck = new Predicate<Tuple<Integer, char[]>>() {
				@Override
				public boolean test(Tuple<Integer, char[]> t) {
					Integer i = t.x;
					char[] chars = t.y;
					return !diphtongs.has(chars[i - 1] + "" + chars[i]);
				}
			};
		} else {
			initialIndex = stem.length() - 1;
			condition = new Predicate<Integer>() {
				@Override
				public boolean test(Integer i) {
					return i > minStemLength;
				};
			};
			afterIteration = new Consumer<Integer>() {
				@Override
				public void accept(Integer i) {
					i--;
				}
			};
			getWordAffixes = new Function<WordStemmer, List<WordWithData>>() {
				@Override
				public List<WordWithData> apply(WordStemmer w) {
					return w.getSuffixes();
				}
			};
			additionalDiphtongCheck = new Predicate<Tuple<Integer, char[]>>() {
				@Override
				public boolean test(Tuple<Integer, char[]> t) {
					Integer i = t.x;
					char[] chars = t.y;
					return (i - 1 <= 0 || !diphtongs.has(chars[i - 2] + "" + chars[i - 1]));
				}
			};
		}

		return findNextAffix(affixes, minStemLength, diphtongs, initialIndex, condition, afterIteration, getWordAffixes, additionalDiphtongCheck);
	}

	private List<WordStemmer> findNextAffix(WordList affixes, int minStemLength, WordList diphtongs, int initialIndex,
			Predicate<? super Integer> condition, Consumer<? super Integer> afterIteration, Function<WordStemmer, List<WordWithData>> getWordAffixes,
			Predicate<Tuple<Integer, char[]>> additionalDiphtongCheck) {
		List<WordStemmer> res = new ArrayList<>();
		char[] chars = stem.toCharArray();
		String currentStringPart = new String();

		WordWithData longestAffix = affixes.getElementWithLongestBase();
		int longestAffixLength = longestAffix == null ? 0 : longestAffix.size();

		for (int i = initialIndex, counter = 0; counter < longestAffixLength && condition.test(i); counter++) {
			boolean isPartOfDiphtong = false;
			currentStringPart = chars[i] + currentStringPart;

			// Check if current character is part of a diphtong
			// To do so, first check that there even is another character after this one
			Integer icopy = Integer.valueOf(i);
			afterIteration.accept(icopy);
			if (condition.test(icopy)) {
				if (diphtongs.has(chars[icopy] + "" + chars[i])) {
					if (additionalDiphtongCheck == null || additionalDiphtongCheck.test(new Tuple<>(i, chars))) {
						isPartOfDiphtong = true;
					}
				}
			}

			if (!isPartOfDiphtong && affixes.has(currentStringPart)) {
				WordStemmer w = new WordStemmer(baseKey, stem.substring(0, i));
				getWordAffixes.apply(w).add(affixes.get(currentStringPart).get());
				res.add(w);
			}

			afterIteration.accept(i);
		}

		return res;
	}

	// Boilerplate

	public String getStem() {
		return this.stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public TermEndings getGrammartizedSuffix() {
		return this.grammartizedSuffix;
	}

	public void setGrammartizedSuffix(TermEndings grammartizedSuffix) {
		this.grammartizedSuffix = grammartizedSuffix;
	}

	public List<WordWithData> getPrefixes() {
		return this.prefixes;
	}

	public void setPrefixes(List<WordWithData> prefixes) {
		this.prefixes = prefixes;
	}

	public List<WordWithData> getSuffixes() {
		return this.suffixes;
	}

	public void setSuffixes(List<WordWithData> suffixes) {
		this.suffixes = suffixes;
	}

	public String getBaseKey() {
		return this.baseKey;
	}

	public WordStemmer stem(String stem) {
		setStem(stem);
		return this;
	}

	public WordStemmer grammartizedSuffix(TermEndings grammartizedSuffix) {
		setGrammartizedSuffix(grammartizedSuffix);
		return this;
	}

	public WordStemmer prefixes(List<WordWithData> prefixes) {
		setPrefixes(prefixes);
		return this;
	}

	public WordStemmer suffixes(List<WordWithData> suffixes) {
		setSuffixes(suffixes);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof WordStemmer)) {
			return false;
		}
		WordStemmer wordStemmer = (WordStemmer) o;
		return Objects.equals(stem, wordStemmer.stem) && Objects.equals(grammartizedSuffix, wordStemmer.grammartizedSuffix)
				&& Objects.equals(prefixes, wordStemmer.prefixes) && Objects.equals(suffixes, wordStemmer.suffixes)
				&& Objects.equals(baseKey, wordStemmer.baseKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stem, grammartizedSuffix, prefixes, suffixes, baseKey);
	}

	@Override
	public String toString() {
		return "{" + " stem='" + getStem() + "'" + ", grammartizedSuffix='" + getGrammartizedSuffix() + "'" + ", prefixes='" + getPrefixes() + "'"
				+ ", suffixes='" + getSuffixes() + "'" + ", baseKey='" + getBaseKey() + "'" + "}";
	}

}
