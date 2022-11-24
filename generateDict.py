import csv


l = list()

with open("nouns.csv", encoding="utf8") as csvFile:
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
			if p.startswith("suffix"):
				pos = "suffix"
				break
			elif p.startswith("substantiv"):
				pos = "noun"
				break

		if type(pos) is str:
			l.append([lemma, word, pos])

with open("dictionary.csv", encoding="utf8", mode="w") as csvFile:
	writer = csv.writer(csvFile, delimiter=",", lineterminator="\n")
	writer.writerow(["lemma", "word", "type"])
	writer.writerows(l)