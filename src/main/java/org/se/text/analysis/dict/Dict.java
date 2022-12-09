package org.se.text.analysis.dict;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import org.se.Util;
import org.se.text.analysis.*;
import org.se.text.analysis.model.*;

/**
 * @author Val Richter
 */
public class Dict {
	// TODO: Refactor to have all all attributes and methods static
	// Reason for this is that it makes no sense for our application to create several instances of the Dict class
	// and we thus allow the same data to be loaded twice
	WordList nounSuffixes = new WordList();
	WordList nounPrefixes = new WordList();
	WordList nouns = new WordList();
	WordList verbSuffixes = new WordList();
	WordList verbPrefixes = new WordList();
	WordList verbs = new WordList();
	WordList diphtongs = new WordList();
	WordList umlautChanges = new WordList();
	WordList compoundParts = new WordList();
	WordList genderChangeSuffixes = new WordList();
	List<Declination> declinatedAffixes = new ArrayList<>();
	List<Conjugation> conjugatedAffixes = new ArrayList<>();
	final String baseKey;

	static final String GENDER_KEY = "gender";
	static final String GRAMMATICAL_CASE_KEY = "grammaticalCase";
	static final String NUMERUS_KEY = "numerus";
	static final String TO_UMLAUT_KEY = "toUmlaut";
	static final String DEFAULT_BASE_KEY = "radix";

	public Dict(WordList nounSuffixes, WordList nounPrefixes, WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs,
			WordList diphtongs, WordList umlautChanges, WordList compoundParts, WordList genderChangeSuffixes, List<Declination> declinatedAffixes,
			List<Conjugation> conjugatedAffixes) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
		this.diphtongs = diphtongs;
		this.umlautChanges = umlautChanges;
		this.compoundParts = compoundParts;
		this.declinatedAffixes = declinatedAffixes;
		this.conjugatedAffixes = conjugatedAffixes;
		this.genderChangeSuffixes = genderChangeSuffixes;
		this.baseKey = DEFAULT_BASE_KEY;
	}

	public Dict(Path dirPath) throws IOException {
		this.baseKey = DEFAULT_BASE_KEY;

		Parser.readCSV(dirPath.resolve("nounsDict"), this.nouns);
		Parser.readCSV(dirPath.resolve("verbsDict"), this.verbs);
		Parser.readCSV(dirPath.resolve("diphtongs"), this.diphtongs);
		Parser.readCSV(dirPath.resolve("umlautChanges"), this.umlautChanges);
		Parser.readCSV(dirPath.resolve("compoundParts"), this.compoundParts);
		Parser.readCSV(dirPath.resolve("genderChangeSuffixes"), genderChangeSuffixes);
		Parser.parseCSV(dirPath.resolve("affixesDict"), row -> {
			switch (row.get("type")) {
				case "nounSuffix":
					nounSuffixes.insert(row);
					break;

				case "nounPrefix":
					nounPrefixes.insert(row);
					break;

				case "verbSuffix":
					verbSuffixes.insert(row);
					break;

				case "verbPrefix":
					verbPrefixes.insert(row);
					break;
			}
		});
		Parser.parseCSV(dirPath.resolve("declinatedAffixes"), declinatedAffixes, Declination.class);
		Parser.parseCSV(dirPath.resolve("conjugatedAffixes"), conjugatedAffixes, Conjugation.class);
	}

	public Dict addDictionary(Dict dict) {
		this.nounSuffixes.insertAll(dict.getNounSuffixes());
		this.nounPrefixes.insertAll(dict.getNounPrefixes());
		this.nouns.insertAll(dict.getNouns());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbs.insertAll(dict.getVerbs());
		this.umlautChanges.insertAll(dict.getUmlautChanges());
		this.compoundParts.insertAll(dict.getCompoundParts());
		this.genderChangeSuffixes.insertAll(dict.getGenderChangeSuffixes());
		this.declinatedAffixes.addAll(dict.getDeclinatedSuffixes());
		this.conjugatedAffixes.addAll(dict.getConjugatedSuffixes());
		return this;
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
		WordStemmer[] l = WordStemmer.radicalize(s, terms, termAffixes, suffixes, prefixes, compoundParts, 2, diphtongs, umlautChanges, baseKey);
		List<WordStemmer> res = new ArrayList<>();

		for (WordStemmer w : l) {
			if (terms.has(w.getStem()) || (w.getSuffixes().size() > 1 && Util.any(w.getSuffixes(), data -> {
				return data.containsKey("certain") && Parser.parseBool(data.get("certain"));
			}))) {
				res.add(w);
			}
		}
		return res;
	}

	// TODO: Test if there are better values for these variables
	// TODO: Add bias for compoundParts
	static final int AFFIX_COUNT_BIAS = -6;
	static final int IN_DICTIONARY_BIAS = 200;
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
						if (tmpGender != null && tmpGender == dictGender) count += ALL_SUFFIXES_DICT_GENDER_BIAS;
					}

				}
			}
		} catch (Exception e) {
			// Something went wrong when treating the stem as a noun, probably because of
			// some NullPointer.
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
		if (noun.isPresent()) {
			return new Tag(s, TagType.Noun, noun.get());
		}

		Optional<WordStemmer> verb = getBestOfStems(getPossibleVerbStems(s), false);
		if (verb.isPresent()) {
			return new Tag(s, TagType.Verb, verb.get());
		}

		return new Tag(s, TagType.Other);
	}

	private void addWordStemmerData(Tag t, boolean areNouns) {
		// Add WordStemmer data,
		// in case it wasn't produced when tagging the word already
		// This can happen, when the Analyzer tags the word before giving it to the
		// Dictionary (specifically because of capitalization of word)
		Function<String, List<WordStemmer>> f = areNouns ? this::getPossibleNounStems : this::getPossibleVerbStems;
		if (t.getData().isEmpty()) {
			Optional<WordStemmer> stem = getBestOfStems(f.apply(t.word), areNouns);
			t.setData(stem);
		}
	}

	public Optional<NounTerm> buildNounTerm(Tag t) {
		try {
			addWordStemmerData(t, true);

			if (t.getData().isEmpty()) {
				return Optional.empty();
			}

			// TODO: Currently this function converts the grammartizedAffix into a
			// Declination, which is only safe as long as this function is only called for
			// nouns. There should probably be some better way to do this.
			// This is currently not an actual issue, though, and thus has a low priority

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

			return Optional.of(new NounTerm(radixBuilder.toString(), t.word, numerus, grammaticalCase, gender));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Optional<VerbTerm> buildVerbTerm(Tag t) {
		addWordStemmerData(t, false);

		// TODO: Update this function potentially
		// TODO: Update conjugatedAffixes.csv

		if (t.getData().isEmpty()) {
			return Optional.empty();
		}

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
		} else if (tmp.changeableGender) {
			Optional<WordWithData> genderChangeSuffix = genderChangeSuffixes.find(suffix -> {
				Optional<Gender> suffixGender = suffix.get(GENDER_KEY, Gender.class);
				if (suffixGender.isPresent()) return suffixGender.get().equals(gender);
				else return false;
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
			if (toUmlaut) strbuilder = new StringBuilder(changeUmlaut(umlautChanges, diphtongs, radix, true));
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
						}
					}
					// If the comparison was correct, update the umlaut sequence
					// break to stop checking for other umlaut sequences
					if (comparison) {
						char[] updated = umlaut.get(updatedKey).toCharArray();
						for (int j = 0; comparison && j < updated.length && j + i < chars.length; j++) {
							chars[i + j] = updated[j];
						}
						// Increae i to avoid checking the same characters, that just got updated
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

	public void setNounSuffixes(WordList nounSuffixes) {
		this.nounSuffixes = nounSuffixes;
	}

	public WordList getNounPrefixes() {
		return this.nounPrefixes;
	}

	public void setNounPrefixes(WordList nounPrefixes) {
		this.nounPrefixes = nounPrefixes;
	}

	public WordList getNouns() {
		return this.nouns;
	}

	public void setNouns(WordList nouns) {
		this.nouns = nouns;
	}

	public WordList getVerbSuffixes() {
		return this.verbSuffixes;
	}

	public void setVerbSuffixes(WordList verbSuffixes) {
		this.verbSuffixes = verbSuffixes;
	}

	public WordList getVerbPrefixes() {
		return this.verbPrefixes;
	}

	public void setVerbPrefixes(WordList verbPrefixes) {
		this.verbPrefixes = verbPrefixes;
	}

	public WordList getVerbs() {
		return this.verbs;
	}

	public void setVerbs(WordList verbs) {
		this.verbs = verbs;
	}

	public Dict nounSuffixes(WordList nounSuffixes) {
		setNounSuffixes(nounSuffixes);
		return this;
	}

	public Dict nounPrefixes(WordList nounPrefixes) {
		setNounPrefixes(nounPrefixes);
		return this;
	}

	public Dict nouns(WordList nouns) {
		setNouns(nouns);
		return this;
	}

	public Dict verbSuffixes(WordList verbSuffixes) {
		setVerbSuffixes(verbSuffixes);
		return this;
	}

	public Dict verbPrefixes(WordList verbPrefixes) {
		setVerbPrefixes(verbPrefixes);
		return this;
	}

	public Dict verbs(WordList verbs) {
		setVerbs(verbs);
		return this;
	}

	public WordList getDiphtongs() {
		return this.diphtongs;
	}

	public void setDiphtongs(WordList diphtongs) {
		this.diphtongs = diphtongs;
	}

	public WordList getUmlautChanges() {
		return this.umlautChanges;
	}

	public void setUmlautChanges(WordList umlautChanges) {
		this.umlautChanges = umlautChanges;
	}

	public List<Declination> getDeclinatedSuffixes() {
		return this.declinatedAffixes;
	}

	public void setDeclinatedSuffixes(List<Declination> declinatedAffixes) {
		this.declinatedAffixes = declinatedAffixes;
	}

	public String getBaseKey() {
		return this.baseKey;
	}

	public Dict diphtongs(WordList diphtongs) {
		setDiphtongs(diphtongs);
		return this;
	}

	public Dict umlautChanges(WordList umlautChanges) {
		setUmlautChanges(umlautChanges);
		return this;
	}

	public Dict declinatedAffixes(List<Declination> declinatedAffixes) {
		setDeclinatedSuffixes(declinatedAffixes);
		return this;
	}

	public List<Conjugation> getConjugatedSuffixes() {
		return this.conjugatedAffixes;
	}

	public void setConjugatedSuffixes(List<Conjugation> conjugatedAffixes) {
		this.conjugatedAffixes = conjugatedAffixes;
	}

	public Dict conjugatedAffixes(List<Conjugation> conjugatedAffixes) {
		setConjugatedSuffixes(conjugatedAffixes);
		return this;
	}

	public Dict(WordList nounSuffixes, WordList nounPrefixes, WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs,
			WordList diphtongs, WordList umlautChanges, WordList compoundParts, List<Declination> declinatedAffixes,
			List<Conjugation> conjugatedAffixes, String baseKey) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
		this.diphtongs = diphtongs;
		this.umlautChanges = umlautChanges;
		this.compoundParts = compoundParts;
		this.declinatedAffixes = declinatedAffixes;
		this.conjugatedAffixes = conjugatedAffixes;
		this.baseKey = baseKey;
	}

	public WordList getCompoundParts() {
		return this.compoundParts;
	}

	public void setCompoundParts(WordList compoundParts) {
		this.compoundParts = compoundParts;
	}

	public Dict compoundParts(WordList compoundParts) {
		setCompoundParts(compoundParts);
		return this;
	}

	public WordList getGenderChangeSuffixes() {
		return this.genderChangeSuffixes;
	}

	public void setGenderChangeSuffixes(WordList genderChangeSuffixes) {
		this.genderChangeSuffixes = genderChangeSuffixes;
	}

	public Dict genderChangeSuffixes(WordList genderChangeSuffixes) {
		setGenderChangeSuffixes(genderChangeSuffixes);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Dict)) {
			return false;
		}
		Dict dictionary = (Dict) o;
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
