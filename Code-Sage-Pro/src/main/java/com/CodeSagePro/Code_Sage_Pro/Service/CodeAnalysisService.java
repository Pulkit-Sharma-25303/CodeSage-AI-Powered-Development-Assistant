package com.CodeSagePro.Code_Sage_Pro.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CodeAnalysisService {

    private final ChatClient chatClient;

    public CodeAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Analyzes the codebase for bugs, security vulnerabilities, performance issues, and code smells.
     *
     * @param projectPath The path to the root of the decompressed project.
     * @return A string containing the AI-generated code review.
     */
    public String performCodeReview(Path projectPath) {
        String projectContent = getProjectContentAsString(projectPath);
        if (projectContent.isEmpty()) {
            return "Could not read project files for review.";
        }

        String prompt = """
                You are an expert code reviewer. Analyze the following codebase for bugs,
                security vulnerabilities, performance issues, and code smells.
                Provide a summary of your findings with specific file names and line numbers.

                Here is the codebase:
                %s
                """.formatted(projectContent);

        return chatClient.prompt().user(prompt).call().content();
    }

    /**
     * Automatically generates high-quality, Markdown-formatted documentation (e.g., a README.md).
     *
     * @param projectPath The path to the root of the decompressed project.
     * @return A string containing the AI-generated README documentation.
     */
    public String generateDocumentation(Path projectPath) {
        String projectContent = getProjectContentAsString(projectPath);
        if (projectContent.isEmpty()) {
            return "Could not read project files to generate documentation.";
        }

        String prompt = """
                You are a technical writer. Based on the following codebase, generate a high-quality,
                Markdown-formatted README.md file. The README should detailed project's purpose,
                main components, and infer potential API endpoints if any are present, if should 
                also mention all code files and its code and its directory structure and tell all requirements and dependencies utilized in it separately.

                Here is the codebase:
                %s
                """.formatted(projectContent);

        return chatClient.prompt().user(prompt).call().content();
    }

    /**
     * Reads all files in the project directory and concatenates their content into a single string.
     * This method processes files in parallel for efficiency.
     * @param projectPath The path to the project directory.
     * @return A single string containing the content of all files.
     */
    private String getProjectContentAsString(Path projectPath) {
        try (Stream<Path> paths = Files.walk(projectPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    // Use parallel stream for faster processing on multi-core systems
                    .parallel()
                    .map(path -> {
                        try {
                            String fileName = projectPath.relativize(path).toString();
                            String content = Files.readString(path);
                            return "--- File: %s ---\n%s\n".formatted(fileName, content);
                        } catch (IOException e) {
                            // Silently ignore files that cannot be read
                            return "";
                        }
                    })
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            // Log the error and return an empty string
            e.printStackTrace();
            return "";
        }
    }
}