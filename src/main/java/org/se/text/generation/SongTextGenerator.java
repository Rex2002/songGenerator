package org.se.text.generation;

import java.io.IOException;
import java.util.*;

import com.itextpdf.text.log.SysoCounter;
import org.se.music.logic.Config;
import org.se.music.model.*;
import org.se.text.analysis.*;
import org.se.text.analysis.model.Gender;
import org.se.text.analysis.model.GrammaticalCase;
import org.se.text.analysis.model.Numerus;

public class SongTextGenerator {

	private TemplateImporter templateImporter;

	public static void main(String[] args) throws IOException {
		SongTextGenerator g = new SongTextGenerator();
		Random ran = new Random();
		Config.loadConfig();
		List<Structure> strucs = Config.getStructures();
		Structure structure = strucs.get(ran.nextInt(Config.getStructures().size()));
		structure.setGenre(Genre.POP);

		g.generateSongText(structure, TermExample.getExample());

	}

	private MusicalKey key;
	private Map<String, Part> parts;

	private int partLength;

	// text
	private TermCollection termCollection;

	// this
	private String[] partText;

	private HashMap<Integer,String> usedWords = new HashMap<>(); // to check if a Term was used in the Song before
	private List<Integer> usedStrophes = new ArrayList<>(); // to check if a strophe was used in the Song before

	// Midi-Sequence Generator calls SongTextGenerator()
	public List<String[]> generateSongText(Structure structure, TermCollection termCollection) {

		List<String[]> songText = new ArrayList<>();
		this.termCollection = termCollection;
		key = structure.getKey();
		parts = structure.getParts();


		List<String> order = structure.getOrder();

		for (String s : order) {
			songText.add(generateStrophe(structure.getGenre(), structure.getParts().get(s).getLength()));
		}

		// TODO iwie probleme mit id

		printSongtext(songText,order);
		return songText;
	}

	private void printSongtext(List<String[]> songText,List<String> order) {
		for(int j = 0; j < songText.size();j++) {
		//for(int j = 0; j < 1;j++) {		//only for testing number 1
			System.out.println(order.get(j));

			//print part-Content
			for (int i = 0; i < songText.get(j).length; i++) {
				System.out.println("Takt" + (i + 1) + ": " + songText.get(j)[i]);
			}
		}
	}

	private String[] generateStrophe(Genre genre, int partLength) {
		if (genre == Genre.BLUES) {
			return null;
		} else if (genre == Genre.POP) {
			templateImporter = new TemplateImporter();
			List<PopTemplate> popTemplateList = templateImporter.getTemplate(genre);

			PopTemplate popTemplate = getUnusedStrophe(popTemplateList, partLength, 1000);

			// go through the strophe and store the verses
			String[] verse = new String[partLength / 2];
			for (int i = 0; i < partLength/2; i++) {
				verse[i] = getVerse(popTemplate.getStrophe()[i]);
			}

			return getPartText(verse, partLength);

		}

		return null;

	}

	private PopTemplate getUnusedStrophe(List<PopTemplate> popTemplateList, int partLength, int templateTries) {
		PopTemplate popTemplate = popTemplateList.get(0);
		for (int i = 0; i < templateTries; i++) {
			popTemplate = getRandomNotUsedValue(popTemplateList);// get random template

			if (popTemplate.getLength() == partLength) {
				return popTemplate;
			}
		}
		System.out.println("pop Template bc useless: " + popTemplate);
		return popTemplate;

		// TODO popTemplate length does not fit requirements
	}

	private String[] getPartText(String[] verse, int partNumber) {
		StringBuilder allVerses = new StringBuilder();

		int index = 0;
		while (index < verse.length && verse[index] != null) {
			allVerses.append(verse[index]).append("|");
			index++;
		}

		String[] partText = new String[partNumber];

		for (int i = 0; i < partText.length; i++) {
			int partEnd = allVerses.indexOf("|");
			partText[i] = allVerses.substring(0, partEnd);
			allVerses = new StringBuilder(allVerses.substring(partEnd + 1));
		}

		return partText;

	}

	private String getVerse(String rawString) {

		// System.out.println(rawString);
		int beginning = rawString.indexOf('$');
		int end = rawString.indexOf('$', beginning + 1);

		String requirementsVariableString = rawString.substring(beginning + 1, end);
		String[] strArr = getStringArrFromRequirementsVariableString(requirementsVariableString);

		return (rawString.substring(0, beginning) + getTerm(strArr) + rawString.substring(end + 1));
	}

	private String[] getStringArrFromRequirementsVariableString(String requirementsVariableString) {
		String[] strArr = new String[]{"","","","","","",""}; //to have empty (not null) values in String[]
		int index = 0;
		while (requirementsVariableString.length() > 0) {
			if (requirementsVariableString.charAt(0) == ',') index++;
			else {
				strArr[index] = strArr[index] + requirementsVariableString.charAt(0); // Stringbuilder
			}
			requirementsVariableString = requirementsVariableString.substring(1);
		}
		return strArr;
	}

	private PopTemplate getRandomNotUsedValue(List<PopTemplate> unusedTemplates) {
		Random ran = new Random();

		if (unusedTemplates.size() == 0) {
			unusedTemplates = templateImporter.getTemplate(Genre.POP);
		}
		PopTemplate pTemplate = unusedTemplates.get(ran.nextInt(unusedTemplates.size()));
		unusedTemplates.remove(pTemplate);
		return pTemplate;

	}

	private String getTerm(String[] requirements) {
		int id = getIdFromRequirements(requirements);
		//if id was already used
		if(usedWords.containsKey(id))return usedWords.get(id);

		List<NounTerm> termList;
		String[] test = new String[]{"n"};
		if (isNoun(requirements)) {
			termList = getNounsTermListFromRequirements(requirements);
		} else {
			termList = getNounsTermListFromRequirements(requirements);// hier fürs Beispiel nomen Statt Verben TODO!!
			// List<String> termList = getVerbsTermListFromRequirements(requirements);
		}

		// System.out.println(termList);

		// index gives the position of the noun in termList
		int termListSize = termList.size();
		int position; // default 0
		if (termListSize != 0) {
			position = getCorrectPosition(termList, id);
			usedWords.put(id,termList.get(position).getWord());
			return termList.get(position).getWord();
		} else {
			return "DaPasstWohlNichts";
		}
	}

	private List<NounTerm> getNounsTermListFromRequirements(String[] requirements) {
		// requirements looks like [n,f,p,n,1,1,10]
		// [Noun,Gender,Plural,Case,id,syllMin,syllMax]
		GrammaticalCase grammaticalCase;
		Gender gender;
		Numerus numerus;
		int syllMin, syllMax;

		// detect Gender
		gender = switch (requirements[1]) {
			case "f" -> Gender.FEMALE;
			case "m" -> Gender.MALE;
			default -> Gender.NEUTRAL; // in case of a false Input, neutral is selected
		};

		// detect if is Plural
		if ("p".equals(requirements[2])) {
			numerus = Numerus.PLURAL;
		} else {
			numerus = Numerus.SINGULAR; // in case of a false Input singular is selected
		}

		// detect Grammatical case//TODO
		grammaticalCase = switch (requirements[3]) {
			case "a" -> GrammaticalCase.ACCUSATIVE;
			case "d" -> GrammaticalCase.DATIVE;
			case "g" -> GrammaticalCase.GENITIVE;
			default -> GrammaticalCase.NOMINATIVE;
		};

		// detect min Syll
		try {
			syllMin = Integer.parseInt(requirements[5]);
		} catch (NumberFormatException ex) {
			syllMin = 0;
		}

		// detect max Syll
		try {
			syllMax = Integer.parseInt(requirements[6]);
		} catch (NumberFormatException ex) {
			syllMax = 15;
		}

		return termCollection.query(grammaticalCase, gender, numerus, syllMin, syllMax);
	}

	private List<String> getVerbsTermListFromRequirements(String[] requirements) {
		return getVerbsExamples(); // TODO
	}

	private int getIdFromRequirements(String[] requirements) {
		try {
			return Integer.parseInt(requirements[4]);
		} catch (NumberFormatException ex) {
			return 4; // should be a random Number but would require a Math library

		}
	}

	public static Integer getLastElement(List<Integer> list) {
		if ((null != list) && (!list.isEmpty())) {
			int lastIdx = list.size() - 1;
			return list.get(lastIdx);
		} else return null;
	}

	private int getCorrectPosition(List<NounTerm> termList, int id) {
		Random ran = new Random();
		int termListSize = termList.size();

		for(int i = 0; i < termListSize;i++){
			//word was not used yet
			if (!usedWords.containsValue(termList.get(i))) {
				return i;
			}
		}
		return ran.nextInt(termListSize);
	}

	private boolean isNoun(String[] requirements) {
		return (requirements[0].equals("n"));
	}

	// -----------------Testing---------------\\
	private List<NounTerm> getOneTermTesting() {
		return termCollection.query(GrammaticalCase.NOMINATIVE, Gender.FEMALE, Numerus.SINGULAR, 1, 100);

	}

	private List<String> getVerbsExamples() {
		List<String> verbExamples = new ArrayList<>();
		verbExamples.add("spielen");
		verbExamples.add("hüpfen");
		verbExamples.add("addieren");
		return verbExamples;
	}

	private List<String> getNounsExamples() {
		return List.of(getOneTermTesting().get(0).getWord());
	}


	private String AlleMeineEntchen(List<Term> terms) {
		String vari = terms.get(0).getWord();

		String AlleMeineEntchen = "Alle meine " + vari + "\n" + "Schwimmen auf dem See\n" + "Schwimmen auf dem See\n" + "Köpfchen in das Wasser\n"
				+ "Schwänzchen in die Höh\n" + "\n" + "Alle meine Täubchen\n" + "Gurren auf dem Dach\n" + "Gurren auf dem Dach\n"
				+ "Fliegt eins in die Lüfte\n" + "Fliegen alle nach\n" + "\n" + "\n" + "Alle meine Hühner\n" + "Scharren in dem Stroh\n"
				+ "Scharren in dem Stroh\n" + "Finden sie ein Körnchen\n" + "Sind sie alle froh\n" + "\n" + "Alle meine Gänschen\n"
				+ "Watscheln durch den Grund\n" + "Watscheln durch den Grund\n" + "Suchen in dem Tümpel\n" + "Werden kugelrund\n" + "\n"
				+ "Alle meine " + vari + "\n" + "Schwimmen auf dem See\n" + "Schwimmen auf dem See\n" + "Köpfchen in das Wasser\n"
				+ "Schwänzchen in die Höh\n" + "Köpfchen in das Wasser\n" + "Schwänzchen in die Höh";

		return ("Alle meine " + vari + ": \n\n" + AlleMeineEntchen);

	}
}
