
<!--  
  * @author Benjamin Frahm  
 -->  

# Music

This package deals with generating the music, adding the text at the correct positions and persisting it in a midi file.   
The generation takes part in 3 steps:

## 1. Template and text selection
In the first step a structure-template is (according to the selected genre) selected from the (in yaml-format) stored structure-templates.
The structure is then given to the TextGeneration part, where fitting texts are selected.
Thereafter some set-up work is done, like determining the number of tracks needed, calculating the length of the whole song, setting the speed, etc.
## 2. Part-filling
After the structure and the texts have been selected the actual music generation starts.
This is done by iterating over the parts (like intro, chorus, verse, etc.) of the structure and generating the tones that are to be played by the instruments specified in the part.
The different instruments are treated as follows:
- Chords: play the chord progression of the part in the right hand, play rootnote if the chord in the left hand.
- Bass: play a bassline that contains transitions between the chords of the part's progression
- Drum: play a drumbeat selected from the drumBeat-templates
- Melody: Every part has a 4 or 12 bar theme, which is then varied and applied in slightly different versions

Since some parts of the music are being reused (equal parts) or varied (theme variations), the music is not directly written as midi, but abstracted and stored as instances of the MidiPlayable class.

The sound of the abstract instruments (like chords, bass, etc.) can be specified in a settings file.
## 3. Midi-File creation
After all MidiPlayables have been created, the "translation" to actual midi begins, by iterating over the, in the structure specified, order of the song and translating the MidiPlayables of the according parts to the Midi-Sequence-Object.
After this is done, the text is added at the required positions (indicated by "vocals" instrument in the structure).
