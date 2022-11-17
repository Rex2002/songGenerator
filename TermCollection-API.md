# TermCollection

Nach der vollständigen Analyse des Texts wird ein Objekt der Klasse `TermCollection` weitergegeben. Prinzipiell ist die TermCollection einfach eine Liste an Termen. Allerdings soll sie auch einige Methoden bieten, um die Nutzung zu vereinfachen. Falls andere Methoden sinnvoll wären, sag mir einfach Bescheid und dann können wir die stattdessen einbauen.

### Zur Erklärung der Struktur:

-   Ein `Term` ist eine Liste an Wörtern, die hintereinander im Text vorkommen
-   Eine `TermVariation` ist eine Liste an `Term`s, die Variationen von einander sind (mehr dazu unten)
-   Die `TermCollection` ist eine Liste an `TermVariation`s

### Idee hinter der Struktur:

Wir wollen Terme finden, die wir in den Songtext einsetzen können. Terme sind v.a. Nomen und Listen an Nomen, die häufig im Text vorkommen (Bsp.: "binäre Baum-Struktur").

Oftmals kommen die selben Terme in unterschiedlichen Variationen im Text vor - z.B. "Binärbaum", "Binärbäume" und "Binärbäumen" sind alles Variation des selben Terms. Die Klasse `TermVariations` speichert alle Variationen eines Terms zusammen.

Der Plan ist, dass wir bei den einzelnen Termen erkennen, welchem Geschlecht sie angehörigen, in welchem grammatischen Fall sie sind und ob sie im Plural sind. Entsprechend bietet die Klasse `TermVariations` die Möglichkeit zu schauen, ob eine Variation dieser drei Kategorien vorhanden ist.

Warum brauchen wir das? Wenn wir z.B. für die Lyrics ein Template mit `Alle meine <Entchen>` haben (wobei `<Entchen>` ersetzt werden soll), dann müssen wir einen Term einsetzen, der ebenfalls im Plural Nominativ ist (also `Alle meine Binärbäume` statt `Alle meine Binärbaum` oder `Alle meine Binärbäumen`).

Die Klasse `TermCollection` speichert nun eine Liste an `TermVariations`. Diese Liste ist sortiert nach der Häufigkeit der Terme (Variationen eines Terms werden bei der Häufigkeit natürlich zusammen gezählt).

Ich habe mir außerdem gedacht, dass es wahrscheinlich sehr sinnvoll wäre, wenn du einfach nach Termen suchen könntest, die bestimmten Kriterien unterliegen (z.B. nur Nominativ Plural sind). Bei diesen Queries würde man dann immer eine (möglicherweise leere) Liste an `TermVariations` zurück bekommen.

Nach dieser kurzen Erklärung, hier jetzt die genaue Spezifikation:

## Spezifikation

### TermCollection

```java
public class TermCollection {
	// Die sortierte Liste an TermVariations
	public ArrayList<TermVariations> terms;

	// Erhalte eine Liste an TermVariations, die Terme enthält, die den eingegebenen Kriterien unterliegen. Alle Kriterien können mit `null` ignoriert werden
	// Es lässt sich auch einzeln nach den jeweiligen Kriterien suchen, mit den unten aufgelisteten Methoden.
	public ArrayList<TermVariations> query(@Nullable GrammaticalCase grammaticalCase, @Nullable Gender gender, @Nullable Boolean isPlural, @Nullable Integer syllableMin, @Nullable Integer syllableMax);

	// Erhalte eine Liste an TermVariations, die Terme enthält, deren Silbenanzahl zwischen `minSyllableAmount` und `maxSyllableAmount` liegt.
	public ArrayList<TermVariations> queryBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount);

	// Erhalte eine Liste an TermVariations, die Terme enthält, deren Silbenanzahl genau `syllableAmount` entspricht.
	public ArrayList<TermVariations> queryBySyllableAmount(Integer syllableAmount);

	// Erhalte eine Liste an TermVariations, die Terme enthält, deren grammatischer Fall gleich dem Input ist.
	public ArrayList<TermVariations> queryByGrammaticalCase(GrammaticalCase grammaticalCase);

	// Erhalte eine Liste an TermVariations, die Terme enthält, die im Plural sind, falls der Input `True` ist, und andernfalls im Singular sind.
	public ArrayList<TermVariations> queryByPlural(Boolean onlyPluralTerms);

	// Erhalte eine Liste an TermVariations, die Terme enthält, deren grammatisches Geschlecht gleich dem Input ist.
	public ArrayList<TermVariations> queryByGender(Gender gender);

	// Erhalte eine Liste an TermVariationen, die verglichen mit dem Rest sehr häufig im Text vorkam
	public ArrayList<TermVariations> mostCommonTerms();

	// Erhalte einen zufälligen Term aus der Liste an TermVariations
	public Term getRandomTerm();


	// Die Funktionen hier sind die selben wie oben, mit der Ausnahme, dass sie statisch sind und eine Liste an TermVariations als weiteren Input nehmen. Bei den queries werden dann nur TermVariations aus diesem Input betrachtet, statt der ganzen Liste an TermVariations.
	// Das erlaubt, dass man nicht nur nach der Silbenlänge querien kann, sondern z.B. nach Silbenlänge, Fall und Geschlecht zusammen.

	public static ArrayList<TermVariations> queryBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount, ArrayList<TermVariations> terms);

	public static ArrayList<TermVariations> queryBySyllableAmount(Integer syllableAmount, ArrayList<TermVariations> terms);

	public static ArrayList<TermVariations> queryByGrammaticalCase(Integer grammaticalCase, ArrayList<TermVariations> terms);

	public static ArrayList<TermVariations> queryByPlural(Boolean onlyPluralTerms, ArrayList<TermVariations> terms);

	public static ArrayList<TermVariations> queryByGender(Gender gender, ArrayList<TermVariations> terms);


	public static ArrayList<TermVariations> mostCommonTerms(ArrayList<TermVariations> terms);

	public static Term getRandomTerm(ArrayList<TermVariations> terms);
}
```

### TermVariations

```java
public class TermVariations {
	// Die Liste an Termen, die Variationen von einander sind
	public ArrayList<Term> variations;

	// Die Häufigkeit dieser Terme im Text
	public Integer frequency;

	// Erhalte eine (möglicherweise leere) Liste an Termen, die den eingegeben Kriterien unterliegen. Funktioniert ähnlich wie die "query" Methode bei der `TermCollection`-Klasse
	public ArrayList<Term> getVariations(@Nullable GrammaticalCase grammaticalCase, @Nullable Gender gender, @Nullable Boolean isPlural, @Nullable Integer syllableMin, @Nullable Integer syllableMax);

	// Erhalte einen Term der spezifizierten Variation oder, falls ein solcher Term nicht im Text vorkam, versuche in zu erstellen.
	// z.B. wenn im Text nur "Binärbäume" vorkam, können wir trotzdem (versuchen) "Binärbaum" automatisch zu erstellen, wenn Nominativ Singular verlangt wird
	// Dieses automatische Erstellen von Variationen wird wahrscheinlich eher schlecht als recht funktionieren lol
	public Term createVariation(GrammaticalCase grammaticalCase, Boolean isPlural);
}
```

### Term

```java
public class Term {
	// Eine Liste an Indizes, die sagt, wo im Term die Silben anfange bzw. aufhören
	public Integer[] syllables;
	// Eine Liste an Wörtern, aus denen der Term besteht
	public String[] words;
	// Bestimmt ob der Term im Plural ist
	public Boolean isPlural;
	// Bestimmt den grammatischen Fall des Terms
	public GrammaticalCase grammaticalCase;
	// Bestimmt das grammatische Geschlecht des Terms
	public Gender gender;

	// Konkatiniert die Wörter des Terms mit Leerzeichen zusammen
	public String toString();
}
```

### GrammaticalCase

```java
public enum GrammaticalCase {
	Nominative,
	Genitive,
	Dative,
	Accusative,
}
```

### Gender

```java
public enum Gender {
	male,
	female,
	neutral,
}
```
