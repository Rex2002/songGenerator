import csv

with open("nouns.csv") as csvFile:
	reader = list(csv.reader(csvFile))
	header = list(map(lambda x: x.lower(), reader[:1]))
	for row in reader[1:]:
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
		print(lemma, word, pos)