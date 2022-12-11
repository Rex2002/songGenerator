package org.se.text.analysis.dict;

import org.se.text.analysis.NounTerm;
import org.se.text.analysis.Tag;
import org.se.text.analysis.TermVariations;
import org.se.text.analysis.VerbTerm;
import org.se.text.analysis.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class Dict {
	// TODO: Refactor this to be a singleton
	WordList nounSuffixes = new WordList();
	WordList nounPrefixes = new WordList();
	WordList nouns = new WordList();
	WordList verbSuffixes = new WordList();
	WordList verbPrefixes = new WordList();
	WordList verbs = new WordList();
	WordList diphthongs = new WordList();
	WordList umlautChanges = new WordList();
	WordList compoundParts = new WordList();
	WordList genderChangeSuffixes = new WordList();
	List<Declination> declinatedAffixes = new ArrayList<>();
	List<Conjugation> conjugatedAffixes = new ArrayList<>();
	final String baseKey;

	static final String GENDER_KEY = "gender";
	static final String TO_UMLAUT_KEY = "toUmlaut";
	static final String DEFAULT_BASE_KEY = "radix";

	public Dict(Path dirPath) throws IOException {
		this.baseKey = DEFAULT_BASE_KEY;

		Parser.readCSV(dirPath.resolve("nounsDict"), this.nouns);
		Parser.readCSV(dirPath.resolve("verbsDict"), this.verbs);
		Parser.readCSV(dirPath.resolve("diphtongs"), this.diphthongs);
		Parser.readCSV(dirPath.resolve("umlautChanges"), this.umlautChanges);
		Parser.readCSV(dirPath.resolve("compoundParts"), this.compoundParts);
		Parser.readCSV(dirPath.resolve("genderChangeSuffixes"), genderChangeSuffixes);
		Parser.parseCSV(dirPath.resolve("affixesDict"), row -> {
			switch (row.get("type")) {
				case "nounSuffix" -> nounSuffixes.insert(row);
				case "nounPrefix" -> nounPrefixes.insert(row);
				case "verbSuffix" -> verbSuffixes.insert(row);
				case "verbPrefix" -> verbPrefixes.insert(row);
			}
		});
		Parser.parseCSV(dirPath.resolve("declinatedAffixes"), declinatedAffixes, Declination.class);
		Parser.parseCSV(dirPath.resolve("conjugatedAffixes"), conjugatedAffixes, Conjugation.class);
	}

	public static Dict getDefault() throws IOException {
		return new Dict(Path.of("", "./src/main/resources/dictionary"));
	}

	// Make word into a term

	public List<WordStemmer> getPossibleNounStems(String s) {
		return getPossibleStems(s, nouns, declinatedAffixes, nounSuffixes, nounPrefixes);
	}

	public List<WordStemmer> getPossibleVerbStems(String s) {
		return getPossibleStems(s, verbs, conjugatedAffixes, verbSuffixes, nounPrefixes);
	}

	private List<WordStemmer> getPossibleStems(String s, WordList terms, List<? extends TermAffix> termAffixes, WordList suffixes,
			WordList prefixes) {
		WordStemmer[] l = WordStemmer.radicalize(s, terms, termAffixes, suffixes, prefixes, compoundParts, 2, diphthongs, umlautChanges, baseKey);
		List<WordStemmer> res = new ArrayList<>();

		for (WordStemmer w : l) {
			if (terms.has(w.getStem()) || (w.getSuffixes().size() > 1
					&& Util.any(w.getSuffixes(), data -> data.containsKey("certain") && Parser.parseBool(data.get("certain"))))) {
				res.add(w);
			}
		}
		return res;
	}

	static final int AFFIX_COUNT_BIAS = -6;
	static final int IN_DICTIONARY_BIAS = 200;
	static final int ALL_COMPOUNDS_IN_DICTIONARY = 100;
	static final int NO_COMPOUNDS = 100;
	static final int DECLINATED_SUFFIX_GENDER_BIAS = 20;
	static final int LAST_SUFFIX_GENDER_BIAS = 10;
	static final int ALL_SUFFIXES_GENDER_BIAS = 1;
	static final int ALL_SUFFIXES_DICT_GENDER_BIAS = 2;
	static final int STEM_NOUN_EXCEPTION_BIAS = -10;
	static Gender tmpGender = null;

	private static int heuristicForStemCmp(WordStemmer stem, boolean areNouns, WordList dict) {
		int count = 0;

		count += AFFIX_COUNT_BIAS * stem.affixesCount();
		if (dict.has(stem.getStem())) count += IN_DICTIONARY_BIAS;

		if (stem.getAdditionalCompounds().isEmpty()) count += NO_COMPOUNDS;
		else if (Util.all(stem.getAdditionalCompounds(), compound -> dict.has(compound.get()))) count += ALL_COMPOUNDS_IN_DICTIONARY;

		try {
			if (areNouns) {
				Optional<WordWithData> dictEntry = dict.get(stem.getStem());
				if (dictEntry.isPresent()) {
					Gender dictGender = dictEntry.get().get(GENDER_KEY, Gender.class).get();

					if (((Declination) stem.getGrammartizedSuffix()).getGender() == dictGender) count += DECLINATED_SUFFIX_GENDER_BIAS;

					if (!stem.getSuffixes().isEmpty()) {
						if (stem.getSuffixes().get(stem.getSuffixes().size() - 1).get(GENDER_KEY, Gender.class).get() == dictGender) {
							count += LAST_SUFFIX_GENDER_BIAS;
						}

						tmpGender = null;
						if (Util.all(stem.getSuffixes(), suffix -> {
							Gender currentGender = suffix.get(GENDER_KEY, Gender.class).get();
							if (tmpGender == null || currentGender == tmpGender) {
								tmpGender = currentGender;
								return true;
							} else return false;
						})) {
							count += ALL_SUFFIXES_GENDER_BIAS;
						}
						if (tmpGender == dictGender) count += ALL_SUFFIXES_DICT_GENDER_BIAS;
					}
				}
			}
		} catch (Exception e) {
			// Something went wrong when treating the stem as a noun, probably because of some NullPointer.
			// Since a noun was expected, we will decrease the count for this exception
			count += STEM_NOUN_EXCEPTION_BIAS;
		}

		return count;
	}

	/**
	 * Retrieve the best stem of a list of possible stems. The best stem is hereby
	 * defined as having been chopped into the fewest parts (suffixes/prefixes) and
	 * fitting best with those parts.
	 *
	 * @param stems
	 *            The list of possible stems to choose from.
	 * @param areNouns
	 *            Whether the stems are all nounStems. If set to true,
	 *            additional metadata like the word's gender are taken into
	 *            account
	 * @return Returns the best WordStemmer object of the input and nothing, if the
	 *         list of stems is empty.
	 */
	public Optional<WordStemmer> getBestOfStems(List<WordStemmer> stems, boolean areNouns) {
		WordList dict = areNouns ? nouns : verbs;
		Optional<WordStemmer> best = Optional.empty();
		int bestCount = 0;
		for (WordStemmer stem : stems) {
			if (best.isEmpty()) {
				best = Optional.of(stem);
				bestCount = heuristicForStemCmp(best.get(), areNouns, dict);
			} else {
				int currentCount = heuristicForStemCmp(stem, areNouns, dict);

				if (currentCount > bestCount) {
					best = Optional.of(stem);
					bestCount = currentCount;
				}
			}
		}
		return best;
	}

	public Tag tagWord(String s) {
		Optional<WordStemmer> noun = getBestOfStems(getPossibleNounStems(s), true);
		if (noun.isPresent()) return new Tag(s, TagType.NOUN, noun.get());

		Optional<WordStemmer> verb = getBestOfStems(getPossibleVerbStems(s), false);
		if (verb.isPresent()) return new Tag(s, TagType.VERB, verb.get());
		return new Tag(s, TagType.OTHER);
	}

	private void addWordStemmerData(Tag t, boolean areNouns) {
		// Add WordStemmer data,
		// in case it wasn't produced when tagging the word already
		// This can happen, when the Analyzer tags the word before giving it to the
		// Dictionary (specifically because of capitalization of word)
		if (t.getData().isEmpty()) {
			Function<String, List<WordStemmer>> f = areNouns ? this::getPossibleNounStems : this::getPossibleVerbStems;
			Optional<WordStemmer> stem = getBestOfStems(f.apply(t.getWord()), areNouns);
			t.setData(stem);
		}
	}

	public Optional<NounTerm> buildNounTerm(Tag t) {
		try {
			addWordStemmerData(t, true);

			if (t.getData().isEmpty()) return Optional.empty();

			WordStemmer data = t.getData().get();
			Declination declinatedSuffix = (Declination) data.getGrammartizedSuffix();

			StringBuilder radixBuilder = new StringBuilder();
			for (WordWithData w : data.getAdditionalCompounds()) {
				radixBuilder.append(w.get());
			}
			radixBuilder.append(data.getStem());

			Numerus numerus = declinatedSuffix.getNumerus();
			GrammaticalCase grammaticalCase = declinatedSuffix.getGrammaticalCase();
			Gender gender = declinatedSuffix.getGender();

			return Optional.of(new NounTerm(radixBuilder.toString(), t.getWord(), numerus, grammaticalCase, gender));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Optional<VerbTerm> buildVerbTerm(Tag t) {
		addWordStemmerData(t, false);

		if (t.getData().isEmpty()) return Optional.empty();

		WordStemmer data = t.getData().get();
		String radix = data.getStem();
		String infinitive = radix;
		if (verbs.has(radix)) infinitive = verbs.get(radix).get().get("infinitive");
		else if (verbs.has(t.getWord())) infinitive = verbs.get(t.getWord()).get().get("infinitive");

		VerbTerm verb = new VerbTerm(radix, t.getWord(), data.getGrammartizedSuffix().getNumerus(), infinitive);
		return Optional.of(verb);
	}

	public Optional<NounTerm> createNounTerm(TermVariations<NounTerm> variations, Gender gender, GrammaticalCase grammaticalCase, Numerus numerus) {
		String radix = variations.getRadix();

		NounTerm tmp = ((NounTerm) variations.getRandomTerm());
		if (tmp.getGender().equals(gender)) {
			return Optional.ofNullable(createNounTermHelper(radix, null, gender, grammaticalCase, numerus));
		} else if (tmp.getChangeableGender()) {
			Optional<WordWithData> genderChangeSuffix = genderChangeSuffixes.find(suffix -> {
				Optional<Gender> suffixGender = suffix.get(GENDER_KEY, Gender.class);
				return suffixGender.isPresent() && suffixGender.get().equals(gender);
			});

			if (genderChangeSuffix.isPresent()) {
				return Optional.ofNullable(createNounTermHelper(radix, genderChangeSuffix.get(), gender, grammaticalCase, numerus));
			}
		}
		return Optional.empty();
	}

	private NounTerm createNounTermHelper(final String radix, WordWithData genderChangeSuffix, Gender gender, GrammaticalCase grammaticalCase,
			Numerus numerus) {
		List<Declination> suffixes = Util.findAll(declinatedAffixes, s -> s.getGender() == gender && s.getGrammaticalCase() == grammaticalCase
				&& s.getNumerus() == numerus && !radix.endsWith(s.getRadix()) && !radix.endsWith(s.getRadix().substring(0, 1)));

		if (!suffixes.isEmpty()) {
			Declination suffix = suffixes.get(0);
			boolean toUmlaut = suffix.getToUmlaut() || (genderChangeSuffix != null && genderChangeSuffix.get(TO_UMLAUT_KEY, Boolean.class).get());

			StringBuilder strbuilder = new StringBuilder(radix);
			if (toUmlaut) strbuilder = new StringBuilder(changeUmlaut(umlautChanges, diphthongs, radix, true));
			if (genderChangeSuffix != null) strbuilder.append(genderChangeSuffix.get());
			strbuilder.append(suffix.getRadix());

			String word = strbuilder.toString();
			return new NounTerm(radix, word, numerus, grammaticalCase, gender, genderChangeSuffix != null);
		}

		return null;
	}

	public static String changeUmlaut(WordList umlautChanges, WordList diphtongs, String s, boolean addUmlaut) {
		char[] chars = s.toCharArray();
		boolean isPartOfDiphtong = false;

		String initialKey = addUmlaut ? DEFAULT_BASE_KEY : "with";
		String updatedKey = addUmlaut ? "with" : DEFAULT_BASE_KEY;

		for (int i = 0; i < chars.length; i++) {
			if (isPartOfDiphtong) isPartOfDiphtong = false;
			else {
				if (i + 1 < chars.length && diphtongs.has(chars[i] + "" + chars[i + 1])) isPartOfDiphtong = true;

				for (WordWithData umlaut : umlautChanges) {
					char[] initial = umlaut.get(initialKey).toCharArray();
					boolean comparison = true;
					for (int j = 0; comparison && j < initial.length && j + i < chars.length; j++) {
						if (initial[j] != chars[i + j]) {
							comparison = false;
							break;
						}
					}
					// If the comparison was correct, update the umlaut sequence
					// break to stop checking for other umlaut sequences
					if (comparison) {
						char[] updated = umlaut.get(updatedKey).toCharArray();
						for (int j = 0; comparison && j < updated.length && j + i < chars.length; j++) {
							chars[i + j] = updated[j];
						}
						// Increase i to avoid checking the same characters that just got updated
						// Yes, it's bad to increase the loop counter from within the loop body, but it
						// should be more efficient
						i += updated.length - 1;
						break;
					}
				}
			}
		}
		return new String(chars);
	}

	// Getters, Setters & other Boilerplate
	public WordList getNounSuffixes() {
		return this.nounSuffixes;
	}

	public WordList getNounPrefixes() {
		return this.nounPrefixes;
	}

	public WordList getNouns() {
		return this.nouns;
	}

	public WordList getVerbSuffixes() {
		return this.verbSuffixes;
	}

	public WordList getVerbPrefixes() {
		return this.verbPrefixes;
	}

	public WordList getVerbs() {
		return this.verbs;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Dict dictionary)) {
			return false;
		}
		return Objects.equals(nounSuffixes, dictionary.nounSuffixes) && Objects.equals(nounPrefixes, dictionary.nounPrefixes)
				&& Objects.equals(nouns, dictionary.nouns) && Objects.equals(verbSuffixes, dictionary.verbSuffixes)
				&& Objects.equals(verbPrefixes, dictionary.verbPrefixes) && Objects.equals(verbs, dictionary.verbs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nounSuffixes, nounPrefixes, nouns, verbSuffixes, verbPrefixes, verbs);
	}

	@Override
	public String toString() {
		return "{" + " nounSuffixes='" + getNounSuffixes() + "'" + ", nounPrefixes='" + getNounPrefixes() + "'" + ", nouns='" + getNouns() + "'"
				+ ", verbSuffixes='" + getVerbSuffixes() + "'" + ", verbPrefixes='" + getVerbPrefixes() + "'" + ", verbs='" + getVerbs() + "'" + "}";
	}

}
