package org.se.text.analysis.dict;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.se.Tuple;
import org.se.Util;
import org.se.text.analysis.model.AffixType;
import org.se.text.analysis.model.CompoundPart;

/**
 * @author Val Richter
 */
public class WordStemmer {
	private String stem = "";
	private List<WordWithData> additionalCompounds = new LinkedList<>();
	private TermAffixes grammartizedAffix = new TermAffixes();
	private List<WordWithData> prefixes = new LinkedList<>();
	private List<WordWithData> suffixes = new LinkedList<>();
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

	public WordStemmer(String baseKey, String stem, TermAffixes grammartizedAffix) {
		this.baseKey = baseKey;
		this.stem = stem;
		this.grammartizedAffix = grammartizedAffix;
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

	public WordStemmer(String stem, TermAffixes grammartizedAffix, List<WordWithData> prefixes, List<WordWithData> suffixes, String baseKey) {
		this.stem = stem;
		this.grammartizedAffix = grammartizedAffix;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.baseKey = baseKey;
	}

	public WordStemmer(String stem, WordWithData additionlCompound, TermAffixes grammartizedAffix, List<WordWithData> prefixes,
			List<WordWithData> suffixes, String baseKey) {
		this.stem = stem;
		this.additionalCompounds.add(additionlCompound);
		this.grammartizedAffix = grammartizedAffix;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.baseKey = baseKey;
	}

	public WordStemmer(String stem, List<WordWithData> additionalCompounds, TermAffixes grammartizedAffix, List<WordWithData> prefixes,
			List<WordWithData> suffixes, String baseKey) {
		this.stem = stem;
		this.additionalCompounds = additionalCompounds;
		this.grammartizedAffix = grammartizedAffix;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.baseKey = baseKey;
	}

	// Actual Logic:

	public int affixesCount() {
		int count = suffixes.size() + prefixes.size();
		if (grammartizedAffix != null && grammartizedAffix.radix != "") count += 1;
		return count;
	}

	public static WordStemmer[] radicalize(String s, WordList terms, List<? extends TermAffixes> grammartizedAffixes, WordList suffixes,
			WordList prefixes, WordList compoundParts, int minStemLength, WordList diphtongs, WordList umlautChanges, String baseKey) {
		List<WordStemmer> res = new LinkedList<>();
		WordList addableCompoundParts = compoundParts
				.filter(x -> x.containsKey("type") && x.get("type", CompoundPart.class).get() == CompoundPart.ADDITION);
		WordList subtractabeCompoundParts = compoundParts
				.filter(x -> x.containsKey("type") && x.get("type", CompoundPart.class).get() == CompoundPart.SUBTRACTION);

		List<WordStemmer> grammartizedRes = findGrammartizedAffixes(s, grammartizedAffixes, umlautChanges, minStemLength, diphtongs, baseKey);
		res.addAll(grammartizedRes);
		for (WordStemmer u : grammartizedRes) {
			for (WordStemmer v : u.findSuffixes(suffixes, minStemLength, diphtongs)) {
				for (WordStemmer w : v.findPreffixes(prefixes, minStemLength, diphtongs)) {
					res.addAll(w.findCompounds(terms, minStemLength, addableCompoundParts, subtractabeCompoundParts, diphtongs, true));
				}
			}
		}

		return res.toArray(new WordStemmer[0]);
	}

	// TODO: Optimize grammartizedAffixes storage
	// Currently all grammartizedAffixes are stored in a list
	// where many objects have the same radix
	// specifically this means, that we have many duplicate calculations
	// This could be optimized by storing a list of radixes
	// mapping to a list of Declinations
	// where no duplicate radixes are stored and the result is flattened
	private static <T extends TermAffixes> List<WordStemmer> findGrammartizedAffixes(String s, List<T> grammartizedAffixes, WordList umlautChanges,
			int minStemLength, WordList diphtongs, String baseKey) {
		List<WordStemmer> res = new ArrayList<>();
		for (T suffix : grammartizedAffixes) {
			if (suffix.getType() == AffixType.SUFFIX && s.endsWith(suffix.getRadix())) {
				List<T> prefixes = grammartizedAffixes.stream()
						.filter(affix -> affix.getType() == AffixType.PREFIX && affix.grammarticallyEquals(suffix)).toList();
				final String scopy = s.substring(0, s.length() - suffix.getRadix().length());

				if (scopy.length() >= minStemLength && Util.all(prefixes, p -> scopy.startsWith(p.getRadix()))) {
					WordStemmer w = new WordStemmer(baseKey, scopy, suffix);
					if (suffix.getToUmlaut() || Util.any(prefixes, p -> p.getToUmlaut())) {
						w.setStem(Dict.changeUmlaut(umlautChanges, diphtongs, scopy, false));
					}
					res.add(w);
				}
			}
		}

		return res;
	}

	public List<WordStemmer> findCompounds(WordList terms, int minStemLength, WordList addableCompoundParts, WordList subtractableCompoundParts,
			WordList diphtongs, boolean firstCall) {
		List<WordStemmer> res = new LinkedList<>();
		List<WordStemmer> nextCompounds = findNextCompound(terms, minStemLength, addableCompoundParts, subtractableCompoundParts, diphtongs);

		// if (firstCall) res.add(this);
		res.addAll(nextCompounds);

		for (WordStemmer w : nextCompounds) {
			List<WordStemmer> furtherCompounds = w.findCompounds(terms, minStemLength, addableCompoundParts, subtractableCompoundParts, diphtongs,
					false);
			res.addAll(furtherCompounds);
		}

		return res;
	}

	public List<WordStemmer> findNextCompound(WordList terms, int minStemLength, WordList addableCompoundParts, WordList subtractabeCompoundParts,
			WordList diphtongs) {
		List<WordStemmer> res = new LinkedList<>();
		StringBuilder strbuilder = new StringBuilder(stem.substring(0, minStemLength));
		char[] chars = stem.toCharArray();
		boolean lastWasDiphtong = minStemLength > 2 && diphtongs.has(chars[minStemLength - 2] + "" + chars[minStemLength - 1]);

		for (int i = minStemLength; i < chars.length - minStemLength; i++) {
			strbuilder.append(chars[i]);

			// Check that the character isn't the start of a diphtong
			if (lastWasDiphtong || i + 1 >= chars.length || !diphtongs.has(chars[i] + "" + chars[i + 1])) {
				for (WordWithData addable : addableCompoundParts) {
					for (WordWithData subtractable : subtractabeCompoundParts) {
						String str = strbuilder.toString();
						if (str.endsWith(addable.get())) {
							str = str.substring(0, str.length() - addable.get().length());
						}
						str += subtractable.get();
						if (terms.has(str)) {
							res.add(new WordStemmer(stem.substring(i + 1), terms.get(str).get(), grammartizedAffix, prefixes, suffixes, baseKey));
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
		List<WordStemmer> nextAffixes = findNextAffix(affixes, minStemLength, diphtongs, false);

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

	private List<WordStemmer> findNextAffix(WordList affixes, int minStemLength, WordList diphtongs, boolean getPefixes) {
		int initialIndex;
		Predicate<? super Integer> condition;
		Consumer<? super Integer> afterIteration;
		Function<WordStemmer, List<WordWithData>> getWordAffixes;
		Predicate<Tuple<Integer, char[]>> additionalDiphtongCheck;
		if (getPefixes) {
			initialIndex = 0;
			condition = i -> i < stem.length() - minStemLength;
			afterIteration = i -> i++;
			getWordAffixes = WordStemmer::getPrefixes;
			additionalDiphtongCheck = t -> {
				char[] chars = t.y;
				Integer i = t.x;
				return i <= 0 || !diphtongs.has(chars[i - 1] + "" + chars[i]);
			};
		} else {
			initialIndex = stem.length() - 1;
			condition = i -> i > minStemLength;
			afterIteration = i -> i--;
			getWordAffixes = WordStemmer::getSuffixes;
			additionalDiphtongCheck = t -> {
				char[] chars = t.y;
				Integer i = t.x;
				return i - 1 <= 0 || !diphtongs.has(chars[i - 2] + "" + chars[i - 1]);
			};
		}

		return findNextAffix(affixes, minStemLength, diphtongs, initialIndex, condition, afterIteration, getWordAffixes, additionalDiphtongCheck);
	}

	private List<WordStemmer> findNextAffix(WordList affixes, int minStemLength, WordList diphtongs, int initialIndex,
			Predicate<? super Integer> condition, Consumer<? super Integer> afterIteration, Function<WordStemmer, List<WordWithData>> getWordAffixes,
			Predicate<Tuple<Integer, char[]>> additionalDiphtongCheck) {
		List<WordStemmer> res = new ArrayList<>();
		char[] chars = stem.toCharArray();
		StringBuilder currentStringPart = new StringBuilder();

		WordWithData longestAffix = affixes.getElementWithLongestBase();
		int longestAffixLength = longestAffix == null ? 0 : longestAffix.size();

		for (int i = initialIndex, counter = 0; counter < longestAffixLength && condition.test(i); counter++) {
			boolean isPartOfDiphtong = false;
			currentStringPart.insert(0, chars[i]);

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

			String str = currentStringPart.toString();
			if (!isPartOfDiphtong && affixes.has(str)) {
				WordStemmer w = new WordStemmer(baseKey, stem.substring(0, i));
				getWordAffixes.apply(w).add(affixes.get(str).get());
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

	public TermAffixes getGrammartizedAffix() {
		return this.grammartizedAffix;
	}

	public void setGrammartizedAffix(TermAffixes grammartizedAffix) {
		this.grammartizedAffix = grammartizedAffix;
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

	public WordStemmer grammartizedAffix(TermAffixes grammartizedAffix) {
		setGrammartizedAffix(grammartizedAffix);
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

	public List<WordWithData> getAdditionalCompounds() {
		return this.additionalCompounds;
	}

	public void setAdditionalCompounds(List<WordWithData> additionalCompounds) {
		this.additionalCompounds = additionalCompounds;
	}

	public WordStemmer additionalCompounds(List<WordWithData> additionalCompounds) {
		setAdditionalCompounds(additionalCompounds);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof WordStemmer)) {
			return false;
		}
		WordStemmer wordStemmer = (WordStemmer) o;
		return Objects.equals(stem, wordStemmer.stem) && Objects.equals(grammartizedAffix, wordStemmer.grammartizedAffix)
				&& Objects.equals(prefixes, wordStemmer.prefixes) && Objects.equals(suffixes, wordStemmer.suffixes)
				&& Objects.equals(baseKey, wordStemmer.baseKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stem, additionalCompounds, grammartizedAffix, prefixes, suffixes, baseKey);
	}

	@Override
	public String toString() {
		return "{" + " stem='" + getStem() + "'" + ", additionalCompounds='" + getAdditionalCompounds() + "'" + ", grammartizedAffix='"
				+ getGrammartizedAffix() + "'" + ", prefixes='" + getPrefixes() + "'" + ", suffixes='" + getSuffixes() + "'" + ", baseKey='"
				+ getBaseKey() + "'" + "}";
	}
}
