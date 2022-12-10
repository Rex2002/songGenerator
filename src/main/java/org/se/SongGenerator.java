package org.se;

import java.io.IOException;
import java.util.Map;
import org.se.music.Config;
import org.se.music.logic.MidiSequence;
import org.se.music.logic.StructureGenerator;
import org.se.text.analysis.Analyzer;
import org.se.text.analysis.FileReader;
import org.se.text.analysis.TermCollection;
import org.se.text.analysis.dict.Dict;
import org.se.text.metric.MetricAnalyzer;
import javafx.concurrent.Task;

/**
 * @author Val Richter
 */
public class SongGenerator extends Task<MidiSequence> {
	private final Settings settings;
	private MidiSequence seq;

	public SongGenerator(Settings settings) {
		this.settings = settings;
	}

	@Override
	protected MidiSequence call() throws Exception {
		try {
			updateMessage("Setting everything up...");
			updateProgress(0, 100);
			Dict dict = Dict.getDefault();
			Config.loadConfig(settings.getGenre());

			if (isCancelled()) return null;

			updateMessage("Reading Input File...");
			updateProgress(20, 100);
			String content = FileReader.main(settings.getFilepath());

			if (isCancelled()) return null;

			updateMessage("Analyzing Input File...");
			updateProgress(50, 100);
			TermCollection terms = Analyzer.analyze(content, dict);
			int metrics = MetricAnalyzer.metricsGet(content, terms);

			if (isCancelled()) return null;

			updateMessage("Writing your Song...");
			updateProgress(80, 100);
			MidiSequence seq = StructureGenerator.generateStructure(settings, Map.of("tempo", metrics), terms);

			if (isCancelled()) return null;

			updateMessage("Done");
			updateProgress(100, 100);
			this.seq = seq;
			return seq;
		} catch (IOException e) {
			updateMessage("Something went wrong reading a file...");
			updateValue(null);
			return null;
		}
	}

	public MidiSequence getSeq() {
		return seq;
	}
}
