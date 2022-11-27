import csv

# TODO:
# All filepaths are currently relative to the terminal's current working directory
# instead they should be changed to be relative to this file's directory

suffixes = list()
prefixes = list()
nouns = list()
verbs = list()


# Read nouns
with open("./nouns.csv", encoding="utf8") as csvFile:
	reader = list(csv.reader(csvFile))
	header = list(map(lambda x: x.lower(), reader[:1][0]))
	for row in reader[1:]:
		if len(row) == 1:
			row = row[0]
		lemma = ""
		word = ""
		pos = []
		for idx, col in enumerate(row):
			if header[idx] == "lemma":
				lemma = col
			elif header[idx] == "pos":
				pos = col.split(",")
			elif header[idx] == "nominativ singular":
				word = col

		for p in pos:
			p = p.lower()
			if p.startswith("suffix") or p.startswith("gebundenes lexem"):
				pos = "nounSuffix"
				break
			elif p.startswith("substantiv") and not lemma.startswith("-"):
				pos = "noun"
				break

		if type(pos) is str:
			if pos == "noun":
				nouns.append([lemma, word])
			else:
				if lemma.startswith("-"):
					lemma = lemma[1:]
				elif lemma.endswith("-"):
					lemma = lemma[:-1]
				if pos == "nounSuffix":
					suffixes.append([lemma, pos])
				elif pos == "nounPrefix":
					prefixes.append([lemma, pos])


# Read Verbs
with open("./verbs.csv", encoding="utf8") as csvFile:
	reader = list(csv.reader(csvFile))
	header = list(map(lambda x: x.lower(), reader[:1][0]))
	for row in reader[1:]:
		if len(row) == 1 and type(row[0]) is list:
			row = row[0]
		current = list()
		current.append(row[0]) # Lemma ist currently just the copied infinitiv, which isn't desirable, but probably won't be fixed in this python file
		for col in row:
			current.append(col)
		verbs.append(current)

# Write new CSV Files

dir = "../src/main/java/org/se/Text/Analysis/Dictionary/"

with open(dir + "nounsDict.csv", encoding="utf8", mode="w") as csvFile:
	writer = csv.writer(csvFile, delimiter=",", lineterminator="\n")
	writer.writerow(["lemma", "nominativ-singular"])
	writer.writerows(nouns)

with open(dir + "verbsDict.csv", encoding="utf8", mode="w") as csvFile:
	writer = csv.writer(csvFile, delimiter=",", lineterminator="\n")
	writer.writerow(["lemma", "infinitv", "1.pers-singular-präsens", "2.pers-singular-präsens", "3.pers-singular-präsens", "1.pers-singular-präteritum", "partizip 2", "1.pers-konjunktiv 2", "imperativ-singular", "imperativ-plural", "hilfsverb"])
	writer.writerows(verbs)

with open(dir + "affixDict.csv", encoding="utf8", mode="w") as csvFile:
	writer = csv.writer(csvFile, delimiter=",", lineterminator="\n")
	writer.writerow(["lemma", "type"])
	writer.writerows(suffixes)
	writer.writerows(prefixes)