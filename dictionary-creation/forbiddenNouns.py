verbs = list()
with open("../src/main/resources/dictionary/verbsDict.csv", "r", encoding="utf8") as file:
	for line in file.readlines():
		l = line.split(",")
		if len(l) > 1:
			verbs.append(l[1].lower())

forbidden = list()

with open("./forbidden-nouns.txt", "r", encoding="utf8") as file:
	forbidden.extend(file.read().splitlines())

with open("../src/main/resources/dictionary/nounsDict.csv", "r", encoding="utf8") as file:
	for line in file.readlines():
		l = line.split(",")
		if len(l) > 0:
			x = l[0].lower()
			if (x.endswith(("isch", "ish")) and not x.endswith(("fisch", "tisch"))) or x in verbs:
				forbidden.append(x)

forbidden = [*set(forbidden)]
forbidden.sort()

with open("./forbidden-nouns.txt", "w", encoding="utf8") as file:
	file.write("\n".join(forbidden))