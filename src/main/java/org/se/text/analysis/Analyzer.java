package org.se.text.analysis;

import java.util.*;

import org.se.PartialProgressTask;
import org.se.text.analysis.dict.*;
import org.se.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class Analyzer extends PartialProgressTask<TermCollection> {
	private final List<Sentence> sentences;
	private final Dict dict;

	public Analyzer(List<Sentence> sentences, Dict dict) {
		super(2);
		this.sentences = sentences;
		this.dict = dict;
	}

	@Override
	protected TermCollection call() throws Exception {
		updateMessage("Tagging Words in Text...");
		List<Tag> tags = tag(sentences);
		procedureDone();

		updateMessage("Building Terms from Text...");
		TermCollection res = buildTerms(tags);
		procedureDone();
		return res;
	}

	private int capitalizedCount(String str) {
		int count = 0;
		for (char c : str.toCharArray()) {
			if (Character.isUpperCase(c)) {
				count++;
			}
		}
		return count;
	}

	private boolean isCapitalized(String word) {
		return capitalizedCount(word) == 1 && Character.isUpperCase(word.charAt(0));
	}

	private List<Tag> tag(List<Sentence> sentences) {
		List<Tag> tags = new ArrayList<>();

		int sentencesAmount = sentences.size();
		for (int i = 0; i < sentencesAmount; i++) {
			Sentence sentence = sentences.get(i);

			double wordsAmount = sentence.size();
			for (int j = 0; j < wordsAmount; j++) {
				String word = sentence.get(j);
				Tag tag;

				// If it's not the first word in the sentence and is capitalized, it's a noun
				// this check can only be trusted on, if not every word is capitalized
				if (j != 0 && isCapitalized(word)) {
					tag = new Tag(word, TagType.NOUN);
				} // Otherwise, let the dictionary tag the word
				else {
					tag = dict.tagWord(word);
				}

				tags.add(tag);
				updateProgress((i + (j / wordsAmount)) / sentencesAmount);
			}
		}
		return tags;
	}

	private TermCollection buildTerms(List<Tag> tags) {
		Map<String, TermVariations<NounTerm>> nounVariations = new HashMap<>();
		Map<String, TermVariations<VerbTerm>> verbVariations = new HashMap<>();

		int tagsAmount = tags.size();
		for (int i = 0; i < tagsAmount; i++) {
			Tag t = tags.get(i);
			if (!t.is(TagType.OTHER)) {

				if (t.is(TagType.NOUN)) {
					Optional<NounTerm> term = dict.buildNounTerm(t);
					if (term.isPresent()) {
						if (nounVariations.containsKey(term.get().getRadix())) {
							nounVariations.get(term.get().getRadix()).add(term.get());
						} else {
							nounVariations.put(term.get().getRadix(), new TermVariations<>(term.get()));
						}
					}
				} else {
					Optional<VerbTerm> term = dict.buildVerbTerm(t);
					if (term.isPresent()) {
						if (verbVariations.containsKey(term.get().getRadix())) {
							verbVariations.get(term.get().getRadix()).add(term.get());
						} else {
							verbVariations.put(term.get().getRadix(), new TermVariations<>(term.get()));
						}
					}
				}
			}
			updateProgress((i / tagsAmount));
		}
		return new TermCollection(dict, nounVariations, verbVariations);
	}

}
