package com.locallm.QxoticAI;

import com.qxotic.jinfer.spring.ai.JinferSpeechModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class TextToSpeech implements CommandLineRunner {

    Logger LOG = LoggerFactory.getLogger(TextToSpeech.class);

    @Value("${qxotic.local:false}")
    private boolean local;

    @Value("${qxotic.models-dir:models}")
    private Path modelsDir;

    @Value("${qxotic.tts.model}")
    private String ttsModel;

    @Value("${qxotic.tts.file}")
    private String ttsFile;

    @Value("${qxotic.tts.voice}")
    private String ttsVoice;

    @Value("${qxotic.tts.voice-file}")
    private String ttsVoiceFile;

    @Value("classpath:JAVA_Qxotic.txt")
    private Resource textFileToSpeech;

    @Override
    public void run(String... args) throws Exception {
        var builder = JinferSpeechModel.builder();
        if (local) {
            Path gguf = modelsDir.resolve(ttsFile).toAbsolutePath().normalize();
            Path voice = modelsDir.resolve(ttsVoiceFile).toAbsolutePath().normalize();
            LOG.info("Loading TTS model from local files {} and {}", gguf, voice);
            builder.modelPath(gguf).companionPath("voice", voice);
        } else {
            builder.model(ttsModel).companion("voice", ttsVoice);
        }
        try (var tts = builder.build()) {
            String content = StreamUtils.copyToString(textFileToSpeech.getInputStream(), StandardCharsets.UTF_8);
            Path out = Path.of("textToSpeech.wav");
            Files.deleteIfExists(out);
            LOG.info("Generating audio");
            Files.write(out, tts.call(content));
            LOG.info("Generated audio at: {}", out.toAbsolutePath().normalize());
        }
    }
}
