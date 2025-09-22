package com.CodeSagePro.Code_Sage_Pro.Service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class RefactoringService {

    private final ChatClient chatClient;

    public RefactoringService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Asks the AI to refactor a given code snippet based on a specific goal.
     * @param codeSnippet The piece of code to refactor.
     * @param goal The refactoring objective (e.g., "improve readability", "improve performance").
     * @return A string containing the refactored code snippet.
     */
    public String refactorCodeSnippet(String codeSnippet, String goal) {
        String prompt = """
                You are an expert software developer specializing in writing clean, efficient, and maintainable code.
                Your task is to refactor the following code snippet.

                The user's goal is to: "%s"

                Follow these requirements precisely:
                1.  Analyze the provided code snippet.
                2.  Rewrite the code to meet the user's goal.
                3.  The response MUST be ONLY the refactored code block.
                4.  Do not provide any explanations, comments, or any text outside of the code block.

                Here is the code snippet to refactor:
                ```
                %s
                ```
                """.formatted(goal, codeSnippet);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}