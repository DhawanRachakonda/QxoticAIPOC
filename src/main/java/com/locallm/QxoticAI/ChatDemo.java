package com.locallm.QxoticAI;

import com.qxotic.jinfer.spring.ai.JinferChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class ChatDemo implements CommandLineRunner {

    Logger LOG = LoggerFactory.getLogger(ChatDemo.class);

    @Value("${qxotic.local:false}")
    private boolean local;

    @Value("${qxotic.models-dir:models}")
    private Path modelsDir;

    @Value("${qxotic.chat.model}")
    private String chatModel;

    @Value("${qxotic.chat.file}")
    private String chatFile;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Using Local LLM");
        var builder = JinferChatModel.builder();
        if (local) {
            Path gguf = modelsDir.resolve(chatFile).toAbsolutePath().normalize();
            LOG.info("Loading chat model from local file {}", gguf);
            builder.modelPath(gguf);
        } else {
            builder.model(chatModel);
        }
        try (var model = builder.build()) {
            String question = """
                    Which programming language is good for building web application that serves for critical industry?
                    JAVA, Rust, Go, Python, NodeJS
                    My must include things:
                    Performance.
                    minimal latency.
                    less CPU usage.
                    Should serve million of user requests.
                    Proven history.
                    """;
            LOG.info("Firing Question {}", question);
            String response = model.call(question);
            LOG.info("Response -> {}", response);
        }
    }
}
