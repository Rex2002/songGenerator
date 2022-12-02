package org.se.Text.Analysis.dict;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import org.se.Util;
import org.se.Text.Analysis.*;

public class Dict {
	WordList nounSuffixes = new WordList();
	WordList nounPrefixes = new WordList();
	WordList nouns = new WordList();
	WordList verbSuffixes = new WordList();
	WordList verbPrefixes = new WordList();
	WordList verbs = new WordList();
	WordList diphtongs = new WordList();
	WordList umlautChanges = new WordList();
	List<Declination> caseEndings = new ArrayList<>();
	List<Conjugation> conjugationEndings = new ArrayList<>();
	final String baseKey;

	public static String getDefaultBaseKey() {
		return "radix";
	}

	public Dict(WordList nounSuffixes, WordList nounPrefixes,
			WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs, WordList diphtongs,
			WordList umlautChanges, List<Declination> caseEndings, List<Conjugation> conjugationEndings) {
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
		Parser.parseCSV(dirPath.resolve("declinationEndings"), caseEndings, Declination.class);
		Parser.parseCSV(dirPath.resolve("conjugationEndings"), conjugationEndings, Conjugation.class);
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
				return data.containsKey("certain") && Parser.parseBool(data.get("certain"));
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
			return data.containsKey("certain") && Parser.parseBool(data.get("certain"));
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
						data -> data.containsKey("certain") && Parser.parseBool(data.get("certain"))))) {
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
