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
		radix = ""
		nomSin = ""
		genSin = ""
		datSin = ""
		accSin = ""
		nomPlu = ""
		genPlu = ""
		datPlu = ""
		accPlu = ""
		pos = []
		gender = ""
		for idx, col in enumerate(row):
			col = col.strip()
			if col != "":
				if header[idx] == "radix" and not radix:
					radix = col
				elif header[idx] == "pos" and not pos:
					pos = col.split(",")
				elif header[idx] == "nominativ singular" and not nomSin:
					nomSin = col
				elif header[idx] == "genitiv singular" and not genSin:
					genSin = col
				elif header[idx] == "dativ singular" and not datSin:
					datSin = col
				elif header[idx] == "akkusativ singular" and not accSin:
					accSin = col
				elif header[idx] == "nominativ plural" and not nomPlu:
					nomPlu = col
				elif header[idx] == "genitiv plural" and not genPlu:
					genPlu = col
				elif header[idx] == "dativ plural" and not datPlu:
					datPlu = col
				elif header[idx] == "akkusativ plural" and not accPlu:
					accPlu = col
				elif header[idx].startswith("genus") and not gender:
					gender = col

		if gender == "m":
			gender = "male"
		elif gender == "f":
			gender = "female"
		elif gender == "n":
			gender = "neutral"

		for p in pos:
			p = p.lower()
			if p.startswith("suffix") or p.startswith("gebundenes lexem"):
				pos = "nounSuffix"
				break
			elif p.startswith("substantiv") and not radix.startswith("-"):
				pos = "noun"
				break

		if type(pos) is str:
			if pos == "noun" and not radix.startswith(("\"", "'", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-")):

				toUmlaut = False
				radixLen = len(radix)
				for idx in range(len(radix)):
					for w in filter(lambda x: x != "", [nomPlu, genPlu, datPlu, accPlu]):
						if len(w) <= idx:
							radixLen = idx
						elif radix[idx] in "aou" and w[idx] in "äöü":
							toUmlaut = True
					if radixLen < len(radix):
						break

				if toUmlaut:
					toUmlaut = "true"
				else:
					toUmlaut = "false"
				radix = radix[:radixLen]

				nouns.append([radix, nomSin, nomPlu, gender, toUmlaut])
			# else:
			# 	if radix.startswith("-"):
			# 		radix = radix[1:]
			# 	elif radix.endswith("-"):
			# 		radix = radix[:-1]
			# 	if pos == "nounSuffix":
			# 		suffixes.append([radix, pos, gender])
			# 	elif pos == "nounPrefix":
			# 		prefixes.append([radix, pos, gender])


# Read Verbs
# with open("./verbs.csv", encoding="utf8") as csvFile:
# 	reader = list(csv.reader(csvFile))
# 	header = list(map(lambda x: x.lower(), reader[:1][0]))
# 	for row in reader[1:]:
# 		if len(row) == 1 and type(row[0]) is list:
# 			row = row[0]
# 		current = list()
# 		current.append(row[0]) # Radix ist currently just the copied infinitiv, which isn't desirable, but probably won't be fixed in this python file
# 		for col in row:
# 			current.append(col)
# 		verbs.append(current)

# Write new CSV Files

dir = "../src/main/resources/dictionary/"

with open(dir + "nounsDict.csv", encoding="utf8", mode="w") as csvFile:
	writer = csv.writer(csvFile, delimiter=",", lineterminator="\n")
	writer.writerow(["radix", "nominative-singular", "nominative-plural", "gender", "toUmlaut"])
	writer.writerows(nouns)

# with open(dir + "verbsDict.csv", encoding="utf8", mode="w") as csvFile:
# 	writer = csv.writer(csvFile, delimiter=",", lineterminator="\n")
# 	writer.writerow(["radix", "infinitv", "1.pers-singular-präsens", "2.pers-singular-präsens", "3.pers-singular-präsens", "1.pers-singular-präteritum", "partizip 2", "1.pers-konjunktiv 2", "imperativ-singular", "imperativ-plural", "hilfsverb"])
# 	writer.writerows(verbs)

# with open(dir + "affixDict2.csv", encoding="utf8", mode="w") as csvFile:
# 	writer = csv.writer(csvFile, delimiter=",", lineterminator="\n")
# 	writer.writerow(["radix", "type", "gender"])
# 	writer.writerows(suffixes)
# 	writer.writerows(prefixes)