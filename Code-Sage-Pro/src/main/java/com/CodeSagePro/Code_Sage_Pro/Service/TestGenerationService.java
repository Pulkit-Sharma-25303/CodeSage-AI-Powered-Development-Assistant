package com.CodeSagePro.Code_Sage_Pro.Service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class TestGenerationService {

    private final ChatClient chatClient;

    public TestGenerationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Generates JUnit 5 tests for a given Java file's content.
     * @param fileContent The source code of the Java class to test.
     * @param fileName The name of the file, used for context.
     * @return A string containing the generated JUnit test class.
     */
    public String generateUnitTests(String fileContent, String fileName) {
        String prompt = """
                You are an expert software developer specializing in Quality Assurance and automated testing.
                Your task is to write a complete, runnable JUnit 5 test class for the provided Java class.

                Follow these requirements precisely:
                1.  Use JUnit 5 and Mockito for mocking if necessary.
                2.  Generate meaningful test cases that cover the main logic, including edge cases and potential null inputs.
                3.  The response MUST be a single, complete Java code block for the test class, including the package declaration, all necessary imports, the class definition, and test methods.
                4.  Do not provide any explanations, comments, or any text outside of the single Java code block. The file name for the class to be tested is %s.

                Here is the Java class to write tests for:
                ```java
                %s
                ```
                """.formatted(fileName, fileContent);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}