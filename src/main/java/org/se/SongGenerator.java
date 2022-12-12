package org.se;

import org.se.music.Config;
import org.se.music.logic.MidiSequence;
import org.se.music.logic.StructureGenerator;
import org.se.text.Preprocessor;
import org.se.text.analysis.Analyzer;
import org.se.text.analysis.FileReader;
import org.se.text.analysis.TermCollection;
import org.se.text.analysis.dict.Dict;
import org.se.text.analysis.model.Sentence;
import org.se.text.metric.MetricAnalyzer;
import org.se.text.metric.Metrics;

import java.io.IOException;
import java.util.List;

/**
 * @author Val Richter
 */
public class SongGenerator extends PartialProgressTask<MidiSequence> {
	private final Settings settings;
	private MidiSequence seq;

	public SongGenerator(Settings settings) {
		super(6);
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

			updateMessage("Preprocessing Input File");
			Preprocessor preprocessor = new Preprocessor(content);
			preprocessor.progressProperty().addListener((observable, oldVal, newVal) -> updateProgress(newVal.doubleValue()));
			preprocessor.messageProperty().addListener((observable, oldVal, newVal) -> updateMessage(newVal));
			preprocessor.run();
			List<Sentence> sentences = preprocessor.get();
			procedureDone();

			Analyzer analyzer = new Analyzer(sentences, dict);
			analyzer.progressProperty().addListener((observable, oldVal, newVal) -> updateProgress(newVal.doubleValue()));
			analyzer.messageProperty().addListener((observable, oldVal, newVal) -> updateMessage(newVal));
			analyzer.run();
			TermCollection terms = analyzer.get();
			procedureDone();

			updateMessage("Retrieving Metrics from the Input File..");
			Metrics metrics = MetricAnalyzer.getMetrics(content, sentences, terms);
			if (settings.getTempo() == -1) settings.setTempo(metrics.getTempo());
			procedureDone();

			updateMessage("Writing your Song...");
			seq = StructureGenerator.generateStructure(settings, terms, metrics.getMood());
			procedureDone();

			updateMessage("Done");
			updateProgress(1);
			return seq;
		} catch (Exception e) {
			updateMessage("Something went wrong...");
			updateValue(null);
			return null;
		}
	}

	public MidiSequence getSeq() {
		return seq;
	}
}
