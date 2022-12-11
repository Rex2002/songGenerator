package org.se;

import org.se.music.Config;
import org.se.music.logic.MidiSequence;
import org.se.music.logic.StructureGenerator;
import org.se.text.analysis.Analyzer;
import org.se.text.analysis.FileReader;
import org.se.text.analysis.TermCollection;
import org.se.text.analysis.dict.Dict;
import org.se.text.metric.MetricAnalyzer;

import java.io.IOException;

/**
 * @author Val Richter
 */
public class SongGenerator extends PartialProgressTask<MidiSequence> {
	private final Settings settings;
	private MidiSequence seq;

	public SongGenerator(Settings settings) {
		super(4);
		this.settings = settings;
	}

	@Override
	protected MidiSequence call() throws Exception {
		try {
			updateProgress(0);
			updateMessage("Setting everything up...");
			Dict dict = Dict.getDefault();
			Config.loadConfig(settings.getGenre());
			procedureDone();

			updateMessage("Reading Input File...");
			String content = FileReader.main(settings.getFilepath());
			procedureDone();

			updateMessage("Analyzing Input File...");
			Analyzer analyzer = new Analyzer(content, dict);
			analyzer.progressProperty().addListener((observable, oldVal, newVal) -> updateProgress(newVal.doubleValue()));
			analyzer.messageProperty().addListener((observable, oldVal, newVal) -> updateMessage(newVal));
			analyzer.run();
			TermCollection terms = analyzer.get();
			updateMessage("Retrieving Metrics from the Input File..");
			int metrics = MetricAnalyzer.metricsGet(content, terms);
			procedureDone();
			if (settings.getTempo() == -1) settings.setTempo(metrics);

			if (isCancelled()) return null;

			updateMessage("Writing your Song...");
			seq = StructureGenerator.generateStructure(settings, terms);
			procedureDone();

			updateMessage("Done");
			updateProgress(1);
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
