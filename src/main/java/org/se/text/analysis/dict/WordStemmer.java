package org.se.text.analysis.dict;

import java.util.*;
import java.util.function.*;
import org.se.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class WordStemmer {
	private String stem = "";
	private List<WordWithData> additionalCompounds = new LinkedList<>();
	private TermAffix grammartizedSuffix = new TermAffix();
	private TermAffix grammartizedPrefix = new TermAffix();
	private List<WordWithData> prefixes = new LinkedList<>();
	private List<WordWithData> suffixes = new LinkedList<>();
	final String baseKey;

	static final String DEFAULT_BASE_KEY = "radix";

	// Constructors:

	public WordStemmer(String baseKey, String stem) {
		this.baseKey = baseKey;
		this.stem = stem;
	}

	public WordStemmer(String baseKey, String stem, TermAffix grammartizedSuffix) {
		this.baseKey = baseKey;
		this.stem = stem;
		this.grammartizedSuffix = grammartizedSuffix;
	}

	public WordStemmer(String baseKey, String stem, TermAffix grammartizedSuffix, TermAffix grammartizedPrefix) {
		this.baseKey = baseKey;
		this.stem = stem;
		this.grammartizedSuffix = grammartizedSuffix;
		this.grammartizedPrefix = grammartizedPrefix;
	}

	public WordStemmer(String stem, WordWithData additionlCompound, TermAffix grammartizedPrefix, TermAffix grammartizedSuffix,
			List<WordWithData> prefixes, List<WordWithData> suffixes, String baseKey) {
		this.stem = stem;
		this.additionalCompounds.add(additionlCompound);
		this.grammartizedPrefix = grammartizedPrefix;
		this.grammartizedSuffix = grammartizedSuffix;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.baseKey = baseKey;
	}

	// Actual Logic:

	public int affixesCount() {
		int count = suffixes.size() + prefixes.size();
		if (grammartizedSuffix != null && !"".equals(grammartizedSuffix.getRadix())) count += 1;
		return count;
	}

	public static WordStemmer[] radicalize(String s, WordList terms, List<? extends TermAffix> grammartizedAffixes, WordList suffixes,
			WordList prefixes, WordList compoundParts, int minStemLength, WordList diphtongs, WordList umlautChanges, String baseKey) {
		WordList addableCompoundParts = compoundParts
				.filter(x -> x.containsKey("type") && x.get("type", CompoundPart.class).get() == CompoundPart.ADDITION);
		WordList subtractabeCompoundParts = compoundParts
				.filter(x -> x.containsKey("type") && x.get("type", CompoundPart.class).get() == CompoundPart.SUBTRACTION);

		List<WordStemmer> grammartizedRes = findGrammartizedAffixes(s, grammartizedAffixes, umlautChanges, minStemLength, diphtongs, baseKey);
		List<WordStemmer> res = new LinkedList<>(grammartizedRes);
		for (WordStemmer u : grammartizedRes) {
			for (WordStemmer v : u.findSuffixes(suffixes, minStemLength, diphtongs)) {
				for (WordStemmer w : v.findPreffixes(prefixes, minStemLength, diphtongs)) {
					res.addAll(w.findCompounds(terms, minStemLength, addableCompoundParts, subtractabeCompoundParts, diphtongs));
				}
			}
		}

		return res.toArray(new WordStemmer[0]);
	}

	private static <T extends TermAffix> List<WordStemmer> findGrammartizedAffixes(String s, List<T> grammartizedAffixes, WordList umlautChanges,
			int minStemLength, WordList diphtongs, String baseKey) {
		List<WordStemmer> res = new ArrayList<>();
		for (T suffix : grammartizedAffixes) {
			if (suffix.getType() == AffixType.SUFFIX && s.endsWith(suffix.getRadix())) {
				List<T> prefixes = grammartizedAffixes.stream()
						.filter(affix -> affix.getType() == AffixType.PREFIX && affix.grammaticallyEquals(suffix)).toList();
				final String scopy = s.substring(0, s.length() - suffix.getRadix().length());

				if (scopy.length() >= minStemLength) {
					for (T prefix : prefixes) {
						if (scopy.length() - prefix.getRadix().length() >= minStemLength && scopy.startsWith(prefix.getRadix())) {
							WordStemmer w = new WordStemmer(baseKey, scopy.substring(prefix.getRadix().length()), prefix, suffix);
							if (suffix.getToUmlaut() || prefix.getToUmlaut()) {
								w.setStem(Dict.changeUmlaut(umlautChanges, diphtongs, scopy, false));
							}
							res.add(w);
						}
					}
					// Without added prefix
					WordStemmer w = new WordStemmer(baseKey, scopy, suffix);
					if (suffix.getToUmlaut()) {
						w.setStem(Dict.changeUmlaut(umlautChanges, diphtongs, scopy, false));
					}
					res.add(w);
				}
			}
		}

		return res;
	}

	public List<WordStemmer> findCompounds(WordList terms, int minStemLength, WordList addableCompoundParts, WordList subtractableCompoundParts,
			WordList diphthongs) {
		List<WordStemmer> nextCompounds = findNextCompound(terms, minStemLength, addableCompoundParts, subtractableCompoundParts, diphthongs);

		List<WordStemmer> res = new LinkedList<>(nextCompounds);

		for (WordStemmer w : nextCompounds) {
			List<WordStemmer> furtherCompounds = w.findCompounds(terms, minStemLength, addableCompoundParts, subtractableCompoundParts, diphthongs);
			res.addAll(furtherCompounds);
		}

		return res;
	}

	public List<WordStemmer> findNextCompound(WordList terms, int minStemLength, WordList addableCompoundParts, WordList subtractabeCompoundParts,
			WordList diphthongs) {
		List<WordStemmer> res = new LinkedList<>();
		StringBuilder strbuilder = new StringBuilder(stem.substring(0, minStemLength));
		char[] chars = stem.toCharArray();
		boolean lastWasDiphtong = minStemLength > 2 && diphthongs.has(chars[minStemLength - 2] + "" + chars[minStemLength - 1]);

		for (int i = minStemLength; i < chars.length - minStemLength; i++) {
			strbuilder.append(chars[i]);

			// Check that the character isn't the start of a diphtong
			if (lastWasDiphtong || i + 1 >= chars.length || !diphthongs.has(chars[i] + "" + chars[i + 1])) {
				for (WordWithData addable : addableCompoundParts) {
					for (WordWithData subtractable : subtractabeCompoundParts) {
						String str = strbuilder.toString();
						if (str.endsWith(addable.get())) {
							str = str.substring(0, str.length() - addable.get().length());
						}
						str += subtractable.get();
						if (terms.has(str)) {
							res.add(new WordStemmer(stem.substring(i + 1), terms.get(str).get(), grammartizedPrefix, grammartizedSuffix, prefixes,
									suffixes, baseKey));
						}
					}
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
		List<WordStemmer> nextAffixes = findNextAffix(affixes, minStemLength, diphtongs, getPrefixes);

		if (firstCall) res.add(this);
		res.addAll(nextAffixes);

		if (firstCall && !nextAffixes.isEmpty()) {
			affixes = affixes.filter(w -> w.get("compound", Boolean.class).orElse(false));
		}
		for (WordStemmer w : nextAffixes) {
			List<WordStemmer> furtherAffixes = w.findAffixes(affixes, minStemLength, diphtongs, false, getPrefixes);

			if (getPrefixes) res.addAll(furtherAffixes);
			else res.addAll(0, furtherAffixes);
		}

		return res;
	}

	private List<WordStemmer> findNextAffix(WordList affixes, int minStemLength, WordList diphthongs, boolean getPrefixes) {
		int initialIndex;
		Predicate<? super Integer> condition;
		Consumer<? super Integer> afterIteration;
		Function<WordStemmer, List<WordWithData>> getWordAffixes;
		Predicate<Tuple<Integer, char[]>> additionalDiphthongCheck;
		if (getPrefixes) {
			initialIndex = 0;
			condition = i -> i < stem.length() - minStemLength;
			afterIteration = i -> i++;
			getWordAffixes = WordStemmer::getPrefixes;
			additionalDiphthongCheck = t -> {
				char[] chars = t.y;
				Integer i = t.x;
				return i <= 0 || !diphthongs.has(chars[i - 1] + "" + chars[i]);
			};
		} else {
			initialIndex = stem.length() - 1;
			condition = i -> i > minStemLength;
			afterIteration = i -> i--;
			getWordAffixes = WordStemmer::getSuffixes;
			additionalDiphthongCheck = t -> {
				char[] chars = t.y;
				Integer i = t.x;
				return i - 1 <= 0 || !diphthongs.has(chars[i - 2] + "" + chars[i - 1]);
			};
		}

		return findNextAffix(affixes, diphthongs, initialIndex, condition, afterIteration, getWordAffixes, additionalDiphthongCheck);
	}

	private List<WordStemmer> findNextAffix(WordList affixes, WordList diphtongs, int initialIndex, Predicate<? super Integer> condition,
			Consumer<? super Integer> afterIteration, Function<WordStemmer, List<WordWithData>> getWordAffixes,
			Predicate<Tuple<Integer, char[]>> additionalDiphtongCheck) {
		List<WordStemmer> res = new ArrayList<>();
		char[] chars = stem.toCharArray();
		StringBuilder currentStringPart = new StringBuilder();

		WordWithData longestAffix = affixes.getElementWithLongestBase();
		int longestAffixLength = longestAffix == null ? 0 : longestAffix.size();

		for (int counter = 0; counter < longestAffixLength && condition.test(initialIndex); counter++) {
			boolean isPartOfDiphtong = false;
			currentStringPart.insert(0, chars[initialIndex]);

			// Check whether current character is part of a diphthong
			// To do so, first check that there even is another character after this one
			Integer iCopy = initialIndex;
			afterIteration.accept(iCopy);
			if (condition.test(iCopy) && diphtongs.has(chars[iCopy] + "" + chars[initialIndex])
					&& (additionalDiphtongCheck == null || additionalDiphtongCheck.test(new Tuple<>(initialIndex, chars)))) {
				isPartOfDiphtong = true;
			}

			String str = currentStringPart.toString();
			if (!isPartOfDiphtong && affixes.has(str)) {
				WordStemmer w = new WordStemmer(baseKey, stem.substring(0, initialIndex));
				getWordAffixes.apply(w).add(affixes.get(str).get());
				res.add(w);
			}

			afterIteration.accept(initialIndex);
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

	public TermAffix getGrammartizedSuffix() {
		return this.grammartizedSuffix;
	}

	public TermAffix getGrammartizedPrefix() {
		return this.grammartizedPrefix;
	}

	public List<WordWithData> getPrefixes() {
		return this.prefixes;
	}

	public List<WordWithData> getSuffixes() {
		return this.suffixes;
	}

	public String getBaseKey() {
		return this.baseKey;
	}

	public List<WordWithData> getAdditionalCompounds() {
		return this.additionalCompounds;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof WordStemmer wordStemmer)) {
			return false;
		}
		return Objects.equals(stem, wordStemmer.stem) && Objects.equals(grammartizedSuffix, wordStemmer.grammartizedSuffix)
				&& Objects.equals(grammartizedPrefix, wordStemmer.grammartizedPrefix) && Objects.equals(prefixes, wordStemmer.prefixes)
				&& Objects.equals(suffixes, wordStemmer.suffixes) && Objects.equals(baseKey, wordStemmer.baseKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stem, additionalCompounds, grammartizedSuffix, grammartizedPrefix, prefixes, suffixes, baseKey);
	}

	@Override
	public String toString() {
		return "{" + " stem='" + getStem() + "'" + ", additionalCompounds='" + getAdditionalCompounds() + "'" + ", grammartizedSuffix='"
				+ getGrammartizedSuffix() + "'" + ", grammartizedPrefix='" + getGrammartizedPrefix() + "'" + ", prefixes='" + getPrefixes() + "'"
				+ ", suffixes='" + getSuffixes() + "'" + ", baseKey='" + getBaseKey() + "'" + "}";
	}
}
