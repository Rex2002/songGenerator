# Text-Analysis

Table of Contents:

- [Text-Analysis](#text-analysis)
	- [Explanations](#explanations)
		- [Term](#term)
		- [Tag](#tag)
	- [Overview](#overview)
		- [Preprocessing](#preprocessing)
		- [Tagging](#tagging)
		- [Term-building](#term-building)
		- [Determining Term-Frequency](#determining-term-frequency)
		- [Specifying the TermCollection](#specifying-the-termcollection)

This sub-component of the Text-component takes care of analyzing the input-text. From the outside, this package is a black box, taking a text as input and outputting a collection of most common terms in the input.

Internally, we achieve this aim via the following process:

1. Preprocessing of the input
2. Tagging words in the text
3. Building Terms from tags
4. Determining Term-Frequency
5. Collecting all Terms in the aptly named "TermCollection"

In the following, we will both give a more detailed description of each of these parts and an [explanation](#explanations) of the vocabulary and names used here.

## Explanations

### Term

TBD

### Tag

TBD

## Overview

### Preprocessing

Nearly always when analyzing some data, it needs to be normalized in some way or other in a preprocessing step. So let's take a look at th data, we receive as input: Text. To be more precise, the text is assumed to be grammatically correct, german language text. Due to accepting both PDF- and Text-Files we can't take the formatting of the text into account (at least not uniformly). All we care about then is to analyze valid german sentences.

A common preprocessing step in NLP is to convert the whole text to lower case. Since we will try to exploit german capitalization rules in our analysis later, we can't do that. We could also remove stopwords (such as "and", "the" etc.), instead however, we will simply ignore such words when building terms later on. The benefit of this approach is that we can ignore unnecesssary words very generally based on grammatical rules when building terms, instead of needing to use a big dictionary, that would most likely miss many words regardless.

So what preprocessing do we do then?

1. We remove punctuation

We will ignore the specific punctuation used (as we don't need that information for collecting common terms) and split the text into a list of sentences (each of which without punctuation).

There are some risks associated with ignoring punctuation, of course, yet all of them are acceptable in our case.

First, acronyms (which might be counted as terms) can be destroyed this way. Checking whether the dot is only one in a series of dots seperating only single-letter characters should probably circumvent this problem already. But even if the problem persists, it would only remove one term from our collection and would have no other side-effects on the workings of the program.

Second, line-breaks between paragraphs are ignored. Paragraphs are a way of structuring text, which we would simply ignore. This is, however, acceptable, since we are only interested in collecting terms and not in analyzing their relationships in context. The semantics of the text's structure can therefore be ignored without issue.

2. We remove `-` and line-breaks.

Specifically in PDF-Files, we find words being seperated over line-breaks. Because we want to focus on a single sentence at once only, we want to remove line-breaks in sentences and restore the seperated words. This should be easy enough by checking if a `-` is followed by whitespace and line-break(s). However, words are also seperated for other reasons, especially for listings (e.g. "left- and right-associativity"). Checking for such word-seperations and restoring the full words again (e.g. "left-associativity and right-associativity") would be nice, but is not planned currently.

As a last word on preprocessing, I want to mention normalizing unicode to ascii. Especially PDF-Files will use many symbols, that, at least in the text-extraction, get read as non-ascii characters, even though they could just as easily be represented by ascii characters (e.g. `’“` are different characters than `'"`).

### Tagging

Tagging in our context means to map each word in the text to a tag (often also called token), which hold certain metadata about the word. For now, the only information stored in the tag will be the type of the specified word (e.g. "noun", "adjective" etc.). This tag is crucial for building terms in the next step. Specifically, since we initially only plan to build terms out of nouns, we only need to tag if a word is a "noun" or "other".

To figure out the correct tag for any one word, we use several techniques based in grammatical rules and dictionary-lookups. The simplest of these is to simply look the word up in a preconfigured dictionary, which stores a list of common german words with their specified tag. In many cases, however, this lookup will fail, even for very large dictionaries, as almost all german words change based on their case, gender, tempus, etc.

Thus, instead of only using a dictionary, we also build in some simple and common grammatical rules of the german language, to recognize a word's type.

The simplest of these rules tells us that capitalized words (at least if they're not at the start of the sentence) are usually nouns. Another rule tries to categorize words based on their pre- and suffixes. For example, in german, words ending in "-ung" or "-heit" are always nouns. More sophisticated rules would be nice, but are currently not planned.

### Term-building

Having tagged the entire text, we can now go through and build terms. We will initially define Terms as a sequence of at least one consecutive noun. If we find in our testing data, that we can tag adjectives and adverbs well too, then we would use the following linguistic filter for finding terms: `(adjective|adverb)*Noun+`.

Let's say, as an example, that we would have three consecutive nouns `A B C`. We will then define any interval in-between `A` and `C` as a "term candidate" - in this case that would be the candidates `A`, `B`, `C`, `A B`, `B C`, `A B C`. All other words in the text, that aren't part of a term-candidate can be ignored.

The simplest would be to either accept only the broadest interval for candidate terms (in our example only `A B C`) or to accept all intervals - which would have to be considered when determining Term-Frequency as well. However, we can do better as well, by checking whether certain intervals in a candidate appear more frequently in other candidates as well. This approach could even be extended to seperating compund words into their parts. There are more clever techniques, that could be used here as well, however, all of them increase runtime significantly by requiring further iterations through all possible term candidates. Before testing the efficacy of the simplest term-choosing algorithm then, we will not use more advanced algorithms for building terms yet.

### Determining Term-Frequency

We wish to know which terms appear most commonly in the text, to favor them when writing the lyrics. However, we cannot simply go through our list of terms and count how often any of them appears. The reason for that, is that words in natural languages are changed slightly based on many factors, making a simple comparison incorrect. Take as an example the words "Vorbereitung" and "Vorbereitungen". Both should clearly count for the same term, a simple string comparison would tell us otherwise, though.

To avoid this problem, we need to lemmatize (or stem) our terms, meaning to reduce all words to some common word-stem, to count diferent variations of a term as the same term, when determining its frequency. To stem the terms, we can use relatively simple rules of detecting certain suffixes or prefixes, which are usually added for grammatical purposes. In our example, the suffix "-en" is added for plural and the suffix "-ung" is added to make the word a noun. The common lemma might then be "Vorbereit". This example shows also, that a word can have several suffixes added.

### Specifying the TermCollection

Lastly, we want to add all terms with certain metadata into a "TermCollection". This object is used to offer an easy-to-use API for the next parts of the application to use the collected terms and data about them. In this section, we will now specify the structure and information stored in the TermCollection and in which steps we best retrieve said information.

Firstly, as in the last step, we want to have different variations of a single term collected under the same term. We thus store a list of variations rather than a list of terms themselves actually. This collection should also be ordered by the terms' frequency, as determined in the last step.

Further, we want to know the amount of syllables for each variation of a term. Specifically, the syllables should be stored as a list of indices, so we know where each syllable starts and ends. This information is rather independent from all other steps and should thus be done when amount of words to iterate over is as small as possible. Thus we will collect said information at the end, after collecting variations of terms and counting their variations.

Additionally, we also want to know the grammatical differences between variations of a term, to know when to use which, since lyrics should be mostly grammatically correct. To find what case a certain noun is in, we can again check for its suffixes. This can be done directly when comparing terms with possible variations. We aim to be more correct than complete here, meaning that we favor checking such cases than trying to declinate a given word into a new case. This preference protects us from building completely wrong words in the language.
