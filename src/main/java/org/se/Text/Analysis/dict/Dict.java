package org.se.Text.Analysis.dict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import org.se.Util;
import org.se.Text.Analysis.*;
import org.se.Text.Analysis.dict.*;

public class Dict {
	WordList nounSuffixes;
	WordList nounPrefixes;
	WordList nouns;
	// TODO: Remove verbs?
	WordList verbSuffixes;
	WordList verbPrefixes;
	WordList verbs;
	WordList diphtongs;
	WordList umlautChanges;
	final String baseKey = "lemma";

	public Dict(WordList nounSuffixes, WordList nounPrefixes,
			WordList nouns, WordList verbSuffixes, WordList verbPrefixes, WordList verbs, WordList diphtongs,
			WordList umlautChanges) {
		this.nounSuffixes = nounSuffixes;
		this.nounPrefixes = nounPrefixes;
		this.nouns = nouns;
		this.verbSuffixes = verbSuffixes;
		this.verbPrefixes = verbPrefixes;
		this.verbs = verbs;
		this.diphtongs = diphtongs;
		this.umlautChanges = umlautChanges;
	}

	private static WordList[] readDictionaryFromFiles(Path affixCSV, Path nounsCSV, Path verbsCSV, Path diphtongsCSV,
			Path umlautChangesCSV)
			throws IOException {
		WordList nouns = new WordList("lemma");
		WordList nounPrefixes = new WordList("lemma");
		WordList nounSuffixes = new WordList("lemma");
		WordList verbs = new WordList("lemma");
		WordList verbPrefixes = new WordList("lemma");
		WordList verbSuffixes = new WordList("lemma");
		WordList diphtongs = new WordList("lemma");
		WordList umlautChanges = new WordList("without");

		CSVReader.readCSV(affixCSV, data -> {
			switch (data.get("type")) {
				case "nounSuffix":
					nounSuffixes.insert(data);
					break;

				case "nounPrefix":
					nounPrefixes.insert(data);
					break;

				case "verbSuffix":
					verbSuffixes.insert(data);
					break;

				case "verbPrefix":
					verbPrefixes.insert(data);
					break;
			}
		});
		CSVReader.readCSV(nounsCSV, nouns);
		CSVReader.readCSV(verbsCSV, verbs);
		CSVReader.readCSV(diphtongsCSV, diphtongs);
		CSVReader.readCSV(umlautChangesCSV, umlautChanges);

		WordList[] res = { nounSuffixes, nounPrefixes, nouns, verbSuffixes, verbPrefixes, verbs, diphtongs,
				umlautChanges };
		return res;
	}

	public Dict(Path affixCSV, Path nounsCSV, Path verbsCSV, Path diphtongsCSV, Path umlautChangesCSV)
			throws IOException {
		WordList[] res = readDictionaryFromFiles(affixCSV, nounsCSV, verbsCSV, diphtongsCSV, umlautChangesCSV);
		this.nounSuffixes = res[0];
		this.nounPrefixes = res[1];
		this.nouns = res[2];
		this.verbSuffixes = res[3];
		this.verbPrefixes = res[4];
		this.verbs = res[5];
		this.diphtongs = res[6];
		this.umlautChanges = res[7];
	}

	public Dict(Path dirPath) throws IOException {
		Path affixCSV = dirPath.resolve("affixesDict.csv");
		Path nounsCSV = dirPath.resolve("nounsDict.csv");
		Path verbsCSV = dirPath.resolve("verbsDict.csv");
		Path diphtongsCSV = dirPath.resolve("diphtongs.csv");
		Path umlautChangesCSV = dirPath.resolve("umlautChanges.csv");
		WordList[] res = readDictionaryFromFiles(affixCSV, nounsCSV, verbsCSV, diphtongsCSV, umlautChangesCSV);
		this.nounSuffixes = res[0];
		this.nounPrefixes = res[1];
		this.nouns = res[2];
		this.verbSuffixes = res[3];
		this.verbPrefixes = res[4];
		this.verbs = res[5];
		this.diphtongs = res[6];
		this.umlautChanges = res[7];
	}

	public Dict addDictionary(Dict dict) {
		this.nounSuffixes.insertAll(dict.getNounSuffixes());
		this.nounPrefixes.insertAll(dict.getNounPrefixes());
		this.nouns.insertAll(dict.getNouns());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbSuffixes.insertAll(dict.getVerbSuffixes());
		this.verbs.insertAll(dict.getVerbs());
		return this;
	}

	// Make word into a term

	// If the word is a noun, a WordStemmer-object will be returned
	// Otherwise, an empty Optional will be returned
	public Optional<WordStemmer> tryNounStem(String s) {
		// TODO: Add declination
		WordStemmer w = WordStemmer.removeSuffixes(s, nounSuffixes, 2, diphtongs, baseKey);
		w.removePrefixes(nounPrefixes, 2, diphtongs);

		if (nouns.has(w.getStem()) || (w.getSuffixes().size() > 1 && Util.Any(w.getSuffixes(), data -> {
			return data.containsKey("certain") && data.getBoolean("certain");
		}))) {
			return Optional.of(w);
		} else {
			return Optional.empty();
		}
	}

	public Optional<WordStemmer> tryVerbStem(String s) {
		// TODO: Add conjugation
		WordStemmer w = WordStemmer.removeSuffixes(s, verbSuffixes, 2, diphtongs, baseKey);
		w.removePrefixes(verbPrefixes, 2, diphtongs);

		if (verbs.has(w.getStem()) || (w.getSuffixes().size() > 1 && Util.Any(w.getSuffixes(), data -> {
			return data.containsKey("certain") && data.getBoolean("certain");
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

		Optional<WordStemmer> data = tryNounStem(s);
		if (data.isPresent()) {
			return new Tag(s, TagType.Noun, data.get());
		}

		data = tryVerbStem(s);
		if (data.isPresent()) {
			return new Tag(s, TagType.Verb, data.get());
		}

		return new Tag(s, TagType.Other);
	}

	private void addWordStemmerData(Tag t, WordList suffixes, WordList prefixes) {
		// Add WordStemmer data,
		// in case it wasn't produced when tagging the word already
		// This can happen, when the Analyzer tags the word before giving it to the
		// Dictionary (for example because of capitalization of word)
		if (t.getData().isEmpty()) {
			WordStemmer w = WordStemmer.removeSuffixes(t.getWord(), suffixes, 2, diphtongs, baseKey);
			w.removePrefixes(prefixes, 2, diphtongs);
			t.setData(Optional.of(w));
		}
	}

	public NounTerm buildNounTerm(Tag t) {
		addWordStemmerData(t, nounSuffixes, nounPrefixes);

		// TODO: Add logic here
		// Specifically move the logic of determining metadata for the term here instead
		// of in the Term-class

		return new NounTerm(lemma, t.word, syllables, isPlural, grammaticalCase, gender);
	}

	public NounTerm buildVerbTerm(Tag t) {
		addWordStemmerData(t, verbSuffixes, verbPrefixes);

		// TODO: Add logic here
		// Specifically move the logic of determining metadata for the term here instead
		// of in the Term-class

		// If we extract verbs from the text too, we need a new VerbTerm class as well.
		// Maybe both NounTerm and VerbTerm class can be extending the same parent
		// "Term" class or something

		// TODO
		Integer[] syllables = { 0 };
		return new NounTerm(t.getWord(), t.getWord(), syllables, false, GrammaticalCase.Nominative,
				Gender.female);
	}

	public NounTerm buildTerm(Tag t) {
		if (t.is(TagType.Noun)) {
			return buildNounTerm(t);
		} else {
			return buildVerbTerm(t);
		}
	}

	public NounTerm createNounTerm(TermVariations variations, Gender gender, GrammaticalCase grammaticalCase,
			boolean isPlural) {
		// TODO
		Integer[] syllables = { 0 };
		return new NounTerm(variations.getLemma(), variations.getLemma(), syllables, false, GrammaticalCase.Nominative,
				Gender.female);
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
