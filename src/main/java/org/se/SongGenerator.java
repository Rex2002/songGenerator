package org.se;

import org.se.music.Config;
import org.se.music.logic.*;
import org.se.text.analysis.*;
import org.se.text.analysis.dict.Dict;
import org.se.text.analysis.model.Sentence;
import org.se.text.metric.MetricAnalyzer;
import org.se.text.metric.Metrics;

import java.util.List;

/**
 * This class generates the song.
 * It runs in a seperate thread from the UI and only talks with the main tread via Progress- and Message-Updates.
 *
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
	protected MidiSequence call() {
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
			e.printStackTrace();
			updateMessage("Something went wrong. Restart the application and make sure your Input File has Machine-Readable Text.");
			updateValue(null);
			return null;
		}
	}

	public MidiSequence getSeq() {
		return seq;
	}
}
