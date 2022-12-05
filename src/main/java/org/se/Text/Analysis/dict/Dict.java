package org.se.Text.Analysis.dict;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import org.se.Util;
import org.se.Text.Analysis.*;

/**
 * @author Val Richter
 */
public class Dict {
	WordList nounSuffixes = new WordList();
	WordList nounPrefixes = new WordList();
	WordList nouns = new WordList();
	WordList verbSuffixes = new WordList();
	WordList verbPrefixes = new WordList();
	WordList verbs = new WordList();
	WordList diphtongs = new WordList();
	WordList umlautChanges = new WordList();
	List<Declination> declinatedSuffixes = new ArrayList<>();
	List<Conjugation> conjugatedSuffixes = new ArrayList<>();
	final String baseKey;

	public static String getDefaultBaseKey() {
		return "radix";
	}

	public Dict(WordList nounSuffixes, WordList nounPrefixes, WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs,
			WordList diphtongs, WordList umlautChanges, List<Declination> declinatedSuffixes, List<Conjugation> conjugatedSuffixes) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
		this.diphtongs = diphtongs;
		this.umlautChanges = umlautChanges;
		this.declinatedSuffixes = declinatedSuffixes;
		this.conjugatedSuffixes = conjugatedSuffixes;
		this.baseKey = getDefaultBaseKey();
	}

	public Dict(Path dirPath) throws IOException {
		this.baseKey = getDefaultBaseKey();

		Parser.readCSV(dirPath.resolve("nounsDict"), this.nouns);
		Parser.readCSV(dirPath.resolve("verbsDict"), this.verbs);
		Parser.readCSV(dirPath.resolve("diphtongs"), this.diphtongs);
		Parser.readCSV(dirPath.resolve("umlautChanges"), this.umlautChanges);
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
		Parser.parseCSV(dirPath.resolve("declinatedSuffixes"), declinatedSuffixes, Declination.class);
		Parser.parseCSV(dirPath.resolve("conjugatedSuffixes"), conjugatedSuffixes, Conjugation.class);
	}

	public Dict addDictionary(Dict dict) {
		this.nounSuffixes.insertAll(dict.getNounSuffixes());
		this.nounPrefixes.insertAll(dict.getNounPrefixes());
		this.nouns.insertAll(dict.getNouns());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbs.insertAll(dict.getVerbs());
		this.umlautChanges.insertAll(dict.getUmlautChanges());
		this.declinatedSuffixes.addAll(dict.getDeclinatedSuffixes());
		return this;
	}

	// Make word into a term

	public List<WordStemmer> getPossibleNounStems(String s) {
		// TODO:
		// Add check if word can be seperated into parts, all of which are in the nounsList
		// This check basically just allows for compound nouns
		// Some specific affixes should be allowed between these parts (like "s")
		// these specific affixes should have their own category in the affixesDict.csv file
		// There should also be some affixes, that can be removed from the original word for compound words
		return getPossibleStems(s, declinatedSuffixes, nouns, nounSuffixes, nounPrefixes);
	}

	public List<WordStemmer> getPossibleVerbStems(String s) {
		return getPossibleStems(s, conjugatedSuffixes, verbs, verbSuffixes, nounPrefixes);
	}

	private List<WordStemmer> getPossibleStems(String s, List<? extends TermEndings> termEndings, WordList dict, WordList suffixes,
			WordList prefixes) {
		WordStemmer[] l = WordStemmer.radicalize(s, termEndings, suffixes, prefixes, 2, diphtongs, umlautChanges, baseKey);
		List<WordStemmer> res = new ArrayList<>();

		for (WordStemmer w : l) {
			if (dict.has(w.getStem()) || (w.getSuffixes().size() > 1 && Util.Any(w.getSuffixes(), data -> {
				return data.containsKey("certain") && Parser.parseBool(data.get("certain"));
			}))) {
				res.add(w);
			}
		}
		return res;
	}

	// TODO: Test if there are better values for these variables
	final static int affixCountBias = -6;
	final static int inDictionaryBias = 20;
	final static int declinatedSuffixGenderBias = 20;
	final static int lastSuffixGenderBias = 10;
	final static int allSuffixesGenderBias = 1;
	final static int allSuffixesDictGenderBias = 2;
	final static int stemNounExceptionBias = -10;
	static Gender tmpGender = null;

	private int heuristicForStemCmp(WordStemmer stem, boolean areNouns) {
		WordList dict = areNouns ? nouns : verbs;
		int count = 0;

		count += affixCountBias * stem.affixesCount();
		if (dict.has(stem.getStem())) count += inDictionaryBias;

		try {
			if (areNouns) {
				Optional<WordWithData> dictEntry = dict.get(stem.getStem());
				if (dictEntry.isPresent()) {
					Gender dictGender = dictEntry.get().get("gender", Gender.class).get();

					if (((Declination) stem.getGrammartizedSuffix()).getGender() == dictGender) count += declinatedSuffixGenderBias;

					if (!stem.getSuffixes().isEmpty()
							&& stem.getSuffixes().get(stem.getSuffixes().size() - 1).get("gender", Gender.class).get() == dictGender) {
						count += lastSuffixGenderBias;
					}

					tmpGender = null;
					if (Util.All(stem.getSuffixes(), suffix -> {
						Gender currentGender = suffix.get("gender", Gender.class).get();
						if (tmpGender == null || currentGender == tmpGender) {
							tmpGender = currentGender;
							return true;
						} else return false;
					})) {
						count += allSuffixesGenderBias;
					}

					if (tmpGender != null && tmpGender == dictGender) count += allSuffixesDictGenderBias;
				}
			}
		} catch (Exception e) {
			// Something went wrong when treating the stem as a noun, probably because of some NullPointer.
			// Since a noun was expected, we will decrease the count for this exception
			count += stemNounExceptionBias;
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
	 *            Whether the stems are all nounStems. If set to true, additional metadata like the word's gender are taken into account
	 * @return Returns the best WordStemmer object of the input and nothing, if the
	 *         list of stems is empty.
	 */
	public Optional<WordStemmer> getBestOfStems(List<WordStemmer> stems, boolean areNouns) {
		Optional<WordStemmer> best = Optional.empty();
		for (WordStemmer stem : stems) {
			if (best.isEmpty()) {
				best = Optional.of(stem);
			} else {
				int currentCount = heuristicForStemCmp(stem, areNouns);
				int bestCount = heuristicForStemCmp(best.get(), areNouns);

				if (currentCount > bestCount) {
					best = Optional.of(stem);
				}
			}
		}
		return best;
	}

	public Tag tagWord(String s) {
		// TODO: Maybe check if word is a common stopWord (would require a list of
		// stop-words)
		// TODO: Maybe filter based on the tag of the last word (i.e. there can't be a
		// noun directly after a verb in the same sentence)

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

	private void addWordStemmerData(Tag t, WordList suffixes, WordList prefixes) {
		// Add WordStemmer data,
		// in case it wasn't produced when tagging the word already
		// This can happen, when the Analyzer tags the word before giving it to the
		// Dictionary (specifically because of capitalization of word)
		if (t.getData().isEmpty()) {
			Optional<WordStemmer> stem = getBestOfStems(getPossibleNounStems(t.word), true);
			t.setData(stem);
		}
	}

	public Optional<NounTerm> buildNounTerm(Tag t) {
		addWordStemmerData(t, nounSuffixes, nounPrefixes);

		if (t.getData().isEmpty()) {
			return Optional.empty();
		}

		// TODO: Currently this function converts the grammartizedSuffix into a
		// Declination, which is only safe as long as this function is only called for
		// nouns. There should probably be some better way to do this.
		// This is currently not an actual issue, though, and thus has a low priority

		WordStemmer data = t.getData().get();
		Declination declinatedSuffix = (Declination) data.getGrammartizedSuffix();

		String radix = data.getStem();
		Numerus numerus = declinatedSuffix.getNumerus();
		GrammaticalCase grammaticalCase = declinatedSuffix.getGrammaticalCase();
		Gender gender = declinatedSuffix.getGender();

		return Optional.of(new NounTerm(radix, t.word, 1, numerus, grammaticalCase, gender));
	}

	public Optional<VerbTerm> buildVerbTerm(Tag t) {
		addWordStemmerData(t, verbSuffixes, verbPrefixes);

		// TODO: Update this function potentially
		// TODO: Update conjugatedSuffixes.csv

		if (t.getData().isEmpty()) {
			return Optional.empty();
		}

		WordStemmer data = t.getData().get();
		String radix = data.getStem();
		String infinitive = radix;
		if (verbs.has(radix)) infinitive = verbs.get(radix).get().get("infinitive");
		else if (verbs.has(t.getWord())) infinitive = verbs.get(t.getWord()).get().get("infinitive");

		VerbTerm verb = new VerbTerm(radix, t.getWord(), 1, data.getGrammartizedSuffix().getNumerus(), infinitive);
		return Optional.of(verb);
	}

	public NounTerm createNounTerm(TermVariations<NounTerm> variations, Gender gender, GrammaticalCase grammaticalCase, Numerus numerus) {
		// TODO: Add logic for creating new variations here. Specifically use the data in declinatedSuffixes.csv and in nouns.csv
		return new NounTerm(variations.getRadix(), variations.getRadix(), 1, numerus, grammaticalCase, gender);
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

	public Dict(WordList nounSuffixes, WordList nounPrefixes, WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs,
			WordList diphtongs, WordList umlautChanges, List<Declination> declinatedSuffixes, String baseKey) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
		this.diphtongs = diphtongs;
		this.umlautChanges = umlautChanges;
		this.declinatedSuffixes = declinatedSuffixes;
		this.baseKey = baseKey;
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
		return this.declinatedSuffixes;
	}

	public void setDeclinatedSuffixes(List<Declination> declinatedSuffixes) {
		this.declinatedSuffixes = declinatedSuffixes;
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

	public Dict declinatedSuffixes(List<Declination> declinatedSuffixes) {
		setDeclinatedSuffixes(declinatedSuffixes);
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
