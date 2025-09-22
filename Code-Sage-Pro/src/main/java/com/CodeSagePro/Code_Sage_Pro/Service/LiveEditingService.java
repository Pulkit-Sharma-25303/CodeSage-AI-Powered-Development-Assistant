package com.CodeSagePro.Code_Sage_Pro.Service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LiveEditingService {

    private final ChatClient chatClient;

    public LiveEditingService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Generates a step-by-step guide for a developer to implement a feature.
     * @param projectPath The path to the root of the decompressed project.
     * @param userPrompt The user's goal for the implementation.
     * @return A Markdown-formatted string containing the step-by-step guide.
     * @throws IOException if a file I/O error occurs.
     */
    public String generateImplementationGuide(Path projectPath, String userPrompt) throws IOException {
        String projectContent = getProjectContentAsString(projectPath);

        // *** THE NEW PROMPT ***
        String prompt = """
                You are an expert software architect and senior developer.
                Your task is to provide a clear, step-by-step guide for a junior developer to implement a new feature based on their request, using the provided codebase as context.

                USER REQUEST: "%s"

                Follow these requirements precisely:
                1.  Analyze the provided codebase to understand its structure and conventions.
                2.  Break down the task into a series of simple, actionable steps.
                3.  For each step, specify which file to create or modify.
                4.  Provide the exact code snippets that need to be added or changed.
                5.  The entire response MUST be formatted in Markdown. Use headings, lists, and code blocks.

                EXISTING CODEBASE:
                %s
                """.formatted(userPrompt, projectContent);

        // The service now simply returns the AI's direct response. No parsing is needed.
        return chatClient.prompt().user(prompt).call().content();
    }

    /**
     * Reads all files in the project directory and concatenates their content into a single string.
     */
    private String getProjectContentAsString(Path projectPath) throws IOException {
        try (Stream<Path> paths = Files.walk(projectPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .parallel()
                    .map(path -> {
                        try {
                            String fileName = projectPath.relativize(path).toString();
                            String content = Files.readString(path);
                            return "--- File: %s ---\n%s\n".formatted(fileName, content);
                        } catch (IOException e) {
                            return "";
                        }
                    })
                    .collect(Collectors.joining("\n"));
        }
    }
}