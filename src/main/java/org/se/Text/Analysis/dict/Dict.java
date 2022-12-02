package org.se.Text.Analysis.dict;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import org.se.Tuple;
import org.se.Util;
import org.se.Text.Analysis.*;

public class Dict {
	WordList nounSuffixes;
	WordList nounPrefixes;
	WordList nouns;
	WordList verbSuffixes;
	WordList verbPrefixes;
	WordList verbs;
	WordList diphtongs;
	WordList umlautChanges;
	List<Declination> caseEndings;
	List<Declination> conjugationEndings;
	final String baseKey;

	public static String getDefaultBaseKey() {
		return "radix";
	}

	public Dict(WordList nounSuffixes, WordList nounPrefixes,
			WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs, WordList diphtongs,
			WordList umlautChanges, List<Declination> caseEndings, List<Declination> conjugationEndings) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
		this.diphtongs = diphtongs;
		this.umlautChanges = umlautChanges;
		this.caseEndings = caseEndings;
		this.conjugationEndings = conjugationEndings;
		this.baseKey = getDefaultBaseKey();
	}

	private static Tuple<WordList[], List<Declination>> readDictionaryFromFiles(Path affixCSV, Path nounsCSV,
			Path verbsCSV, Path diphtongsCSV, Path umlautChangesCSV, Path caseEndingsCSV, Path conjugationEndingsCSV)
			throws IOException {
		String baseKey = getDefaultBaseKey();
		WordList nouns = new WordList(baseKey);
		WordList nounPrefixes = new WordList(baseKey);
		WordList nounSuffixes = new WordList(baseKey);
		WordList verbs = new WordList("Infinitiv");
		WordList verbPrefixes = new WordList(baseKey);
		WordList verbSuffixes = new WordList(baseKey);
		WordList diphtongs = new WordList(baseKey);
		WordList umlautChanges = new WordList(baseKey);
		List<Declination> caseEndings = new ArrayList<>();
		List<Declination> conjugationEndings = new ArrayList<>();
		// caseEndings isn't a WordList, because we need a list of duplicate
		// ending-radixes

		Parser.readCSV(affixCSV, row -> {
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
		Parser.readCSV(nounsCSV, nouns);
		Parser.readCSV(verbsCSV, verbs);
		Parser.readCSV(diphtongsCSV, diphtongs);
		Parser.readCSV(umlautChangesCSV, umlautChanges);
		Parser.readCSV(caseEndingsCSV, row -> {
			Declination dec = new Declination();
			dec.setGender(row.getGender("gender"));
			dec.setGrammaticalCase(row.getGrammaticalCase("grammaticalCase"));
			dec.setNumerus(row.getNumerus("numerus"));
			dec.setRadix(row.get("radix"));
			dec.setToUmlaut(row.getBool("toUmlaut"));
			caseEndings.add(dec);
		});
		Parser.readCSV(conjugationEndingsCSV, row -> {

		});

		WordList[] wordLists = { nounSuffixes, nounPrefixes, nouns, verbSuffixes, verbPrefixes, verbs, diphtongs,
				umlautChanges };
		return new Tuple<>(wordLists, caseEndings);
	}

	public Dict(Path affixCSV, Path nounsCSV, Path verbsCSV, Path diphtongsCSV, Path umlautChangesCSV,
			Path caseEndingsCSV, Path conjugationEndingsCSV)
			throws IOException {
		Tuple<WordList[], List<Declination>> res = readDictionaryFromFiles(affixCSV, nounsCSV, verbsCSV, diphtongsCSV,
				umlautChangesCSV, caseEndingsCSV, conjugationEndingsCSV);
		this.nounSuffixes = res.x[0];
		this.nounPrefixes = res.x[1];
		this.nouns = res.x[2];
		this.verbSuffixes = res.x[3];
		this.verbPrefixes = res.x[4];
		this.verbs = res.x[5];
		this.diphtongs = res.x[6];
		this.umlautChanges = res.x[7];
		this.caseEndings = res.y;
		this.baseKey = getDefaultBaseKey();
	}

	public Dict(Path dirPath) throws IOException {
		Path affixCSV = dirPath.resolve("affixesDict");
		Path nounsCSV = dirPath.resolve("nounsDict");
		Path verbsCSV = dirPath.resolve("verbsDict");
		Path diphtongsCSV = dirPath.resolve("diphtongs");
		Path umlautChangesCSV = dirPath.resolve("umlautChanges");
		Path caseEndingsCSV = dirPath.resolve("declinationEndings");
		Path conjugationEndingsCSV = dirPath.resolve("conjugationEndings");
		Tuple<WordList[], List<Declination>> res = readDictionaryFromFiles(affixCSV, nounsCSV, verbsCSV, diphtongsCSV,
				umlautChangesCSV, caseEndingsCSV, conjugationEndingsCSV);
		this.nounSuffixes = res.x[0];
		this.nounPrefixes = res.x[1];
		this.nouns = res.x[2];
		this.verbSuffixes = res.x[3];
		this.verbPrefixes = res.x[4];
		this.verbs = res.x[5];
		this.diphtongs = res.x[6];
		this.umlautChanges = res.x[7];
		this.caseEndings = res.y;
		this.baseKey = getDefaultBaseKey();
	}

	public Dict addDictionary(Dict dict) {
		this.nounSuffixes.insertAll(dict.getNounSuffixes());
		this.nounPrefixes.insertAll(dict.getNounPrefixes());
		this.nouns.insertAll(dict.getNouns());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbs.insertAll(dict.getVerbs());
		this.umlautChanges.insertAll(dict.getUmlautChanges());
		this.caseEndings.addAll(dict.getCaseEndings());
		return this;
	}

	// Make word into a term

	public List<WordStemmer> tryNounStem(String s) {
		List<WordStemmer> res = new ArrayList<>();
		WordStemmer[] l = WordStemmer.radicalize(s, caseEndings, nounSuffixes, nounPrefixes, 2, diphtongs,
				umlautChanges,
				baseKey);

		// TODO: Fix this
		for (WordStemmer w : l) {
			if (nouns.has(w.getStem()) || (w.getSuffixes().size() > 1 && Util.Any(w.getSuffixes(), data -> {
				return data.containsKey("certain") && data.getBool("certain");
			}))) {
				res.add(w);
			}
		}

		return res;
	}

	public Optional<WordStemmer> tryVerbStem(String s) {
		// TODO: Add conjugation
		WordStemmer w = WordStemmer.removeSuffixes(s, verbSuffixes, 2, diphtongs, baseKey);
		w.removePrefixes(verbPrefixes, 2, diphtongs);

		if (verbs.has(w.getStem()) || (w.getSuffixes().size() > 1 && Util.Any(w.getSuffixes(), data -> {
			return data.containsKey("certain") && data.getBool("certain");
		}))) {
			return Optional.of(w);
		} else {
			return Optional.empty();
		}
	}

	public Tag tagWord(String s) {
		// TODO: Maybe check is word is a common stopWord (would require a list of
		// stop-words)
		// TODO: Maybe filter based on the tag of the last word (i.e. there can't be a
		// noun directly after a verb in the same sentence)

		List<WordStemmer> nouns = tryNounStem(s);
		if (!nouns.isEmpty()) {
			return new Tag(s, TagType.Noun, nouns.get(0));
		}

		Optional<WordStemmer> verb = tryVerbStem(s);
		if (verb.isPresent()) {
			return new Tag(s, TagType.Verb, verb.get());
		}

		return new Tag(s, TagType.Other);
	}

	private void addWordStemmerData(Tag t, WordList suffixes, WordList prefixes) {
		// Add WordStemmer data,
		// in case it wasn't produced when tagging the word already
		// This can happen, when the Analyzer tags the word before giving it to the
		// Dictionary (for example because of capitalization of word)
		if (t.getData().isEmpty()) {
			WordStemmer[] res = WordStemmer.radicalize(t.word, caseEndings, suffixes, prefixes, 2, diphtongs,
					umlautChanges,
					baseKey);

			for (WordStemmer w : res) {
				if (nouns.has(w.getStem()) || (w.getSuffixes().size() > 1 && Util.Any(w.getSuffixes(),
						data -> data.containsKey("certain") && data.getBool("certain")))) {
					t.setData(Optional.of(w));
					break;
				}
			}
		}
	}

	public Optional<NounTerm> buildNounTerm(Tag t) {
		addWordStemmerData(t, nounSuffixes, nounPrefixes);

		if (t.getData().isEmpty()) {
			return Optional.empty();
		}

		WordStemmer data = t.getData().get();
		Declination caseEnding = data.getCaseEnding();

		// System.out.println(t);
		// System.out.println(data);

		String radix = data.getStem();
		Integer syllableAmount = 1;
		Numerus numerus = caseEnding.getNumerus();
		GrammaticalCase grammaticalCase = caseEnding.getGrammaticalCase();
		Gender gender = caseEnding.getGender();

		return Optional.of(new NounTerm(radix, t.word, syllableAmount, numerus, grammaticalCase, gender));
	}

	public Optional<VerbTerm> buildVerbTerm(Tag t) {
		addWordStemmerData(t, verbSuffixes, verbPrefixes);

		// TODO: Add logic here
		// Specifically move the logic of determining metadata for the term here instead
		// of in the Term-class

		// If we extract verbs from the text too, we need a new VerbTerm class as well.
		// Maybe both NounTerm and VerbTerm class can be extending the same parent
		// "Term" class or something

		// TODO
		return Optional.of(new VerbTerm(t.getWord()));
	}

	// TODO: Probably unnecessary and can be removed by now
	public Optional<? extends Term> buildTerm(Tag t) {
		if (t.is(TagType.Noun)) {
			return buildNounTerm(t);
		} else {
			return buildVerbTerm(t);
		}
	}

	public NounTerm createNounTerm(TermVariations<NounTerm> variations, Gender gender, GrammaticalCase grammaticalCase,
			Numerus numerus) {
		// TODO
		return new NounTerm(variations.getRadix(), variations.getRadix(), 1, numerus,
				grammaticalCase, gender);
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

	public Dict(WordList nounSuffixes, WordList nounPrefixes, WordList nouns, WordList verbSuffixes,
			WordList verbPrefixes, WordList verbs, WordList diphtongs, WordList umlautChanges,
			List<Declination> caseEndings,
			String baseKey) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
		this.diphtongs = diphtongs;
		this.umlautChanges = umlautChanges;
		this.caseEndings = caseEndings;
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

	public List<Declination> getCaseEndings() {
		return this.caseEndings;
	}

	public void setCaseEndings(List<Declination> caseEndings) {
		this.caseEndings = caseEndings;
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

	public Dict caseEndings(List<Declination> caseEndings) {
		setCaseEndings(caseEndings);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Dict)) {
			return false;
		}
		Dict dictionary = (Dict) o;
		return Objects.equals(nounSuffixes, dictionary.nounSuffixes)
				&& Objects.equals(nounPrefixes, dictionary.nounPrefixes) && Objects.equals(nouns, dictionary.nouns)
				&& Objects.equals(verbSuffixes, dictionary.verbSuffixes)
				&& Objects.equals(verbPrefixes, dictionary.verbPrefixes) && Objects.equals(verbs, dictionary.verbs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nounSuffixes, nounPrefixes, nouns, verbSuffixes, verbPrefixes, verbs);
	}

	@Override
	public String toString() {
		return "{" +
				" nounSuffixes='" + getNounSuffixes() + "'" +
				", nounPrefixes='" + getNounPrefixes() + "'" +
				", nouns='" + getNouns() + "'" +
				", verbSuffixes='" + getVerbSuffixes() + "'" +
				", verbPrefixes='" + getVerbPrefixes() + "'" +
				", verbs='" + getVerbs() + "'" +
				"}";
	}

}
