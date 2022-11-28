# The data we need:

A list of nouns with their...

-   stem (computable?),
-   nominativ singular,
-   nominativ plural,
-   (default) gender and
-   a flag showing, whether the word can change its gender through endings (e.g. Lehrer -> Lehrerin).

Why singular & plural? There are some words, like "Eltern", which are only used in plural.

Our initial dataset might not even need the stem of the noun, as we should be able to compute that with the same algorithms, that we use for nouns extracted from the text.

We also need a list of possible endings that are appended because of grammatical cases. Specifically, we need to map each ending its

-   case,
-   numerus,
-   gender and
-   a flag showing, whether the ending can induce letters to be changed with their umlaut-versions

(See declination.csv for an attempt at storing said data).

# The logic we need:

1. We need to remove suffixes that were appended due to the grammatical case. Since there can often be several possible endings, we need to remove all of them.

2. We need to remove general noun suffixes (see affixes list).

3. We need to check if the word is in our list of nouns (see nouns list).

4. If the word's not in our nouns-list, we need to consider substrings of the word. Specifically the substring has to end at the ending and increase in size towards the word's beginning. We should do this until we find a substring that is in our list of nouns.

We do this because of how common compound nouns are in german.
There is some risk here of incorrectly classifying a word as a noun, because it ends with a substring, which is a noun (e.g. "ei"). To get around this we could make sure that the other substrings of the word are also nouns (recursively). This might be very computationally costly though and it might result in losing many terms, because our list of nouns can't be comprehensive. (See through testing).

5. The last noun of the possible compound-noun word that we got, determines the word's gender. The endings removed in step 1. determine case and numerus. If one of the removed suffixes induces a change of gender (specifically the suffix "in") then the term's gender will be determined by the suffix instead. Since case and numerus determined from the ending can change depending on the word's gender, the gender has to be determined first.

# The interfaces we need:

-   ``
-   `Tuple<GrammaticalCase, Numerus> determineCaseNumerus(Gender gender, String ending)` (returns the term's case and numerus)
