<!--
  * @author Val Richter
 -->

# Text

This package deals with reading the text in the input file, extracting it's most common terms and using those and a general structure of the song, to write the lyrics for the song.

## Reading Text from the File

We allow input from plain text (.txt) and PDF (.pdf) files. The first part of this "Text"-component is thus to read the input file and extract the text from it.

## Extracting Terms from the Text

To generate a song based on an input file, we need to analyze said input. This analysis will be done using rule- as well as dictionary-based Natural Language Processing (NLP) techniques. The aim of these algorithms is to extract terms from the text, which can then be used to create the song-text from.

## Generating the Lyrics

To generate the lyrics for the song, we need both the terms extracted from the input-text, as well as a general structure of the song itself. Said structure will be the basis for creating the music to the song as well as the lyrics. To generate the lyrics we use templates, which we aim to make as modular as possible.
