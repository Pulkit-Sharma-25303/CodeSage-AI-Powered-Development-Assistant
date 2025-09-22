package com.CodeSagePro.Code_Sage_Pro.Service;

import com.CodeSagePro.Code_Sage_Pro.Model.CodeEmbedding;
import com.CodeSagePro.Code_Sage_Pro.Repository.CodeEmbeddingRepository;
import com.pgvector.PGvector;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final CodeEmbeddingRepository repository;

    public EmbeddingService(EmbeddingModel embeddingModel, CodeEmbeddingRepository repository) {
        this.embeddingModel = embeddingModel;
        this.repository = repository;
    }

    public void generateAndStoreEmbeddings(Path projectPath) {
        repository.deleteAll();

        try (Stream<Path> paths = Files.walk(projectPath)) {
            paths.filter(Files::isRegularFile)
                    .findFirst() // Process only the first file for this debug step
                    .ifPresent(path -> {
                        try {
                            String content = Files.readString(path);
                            if (content.isBlank()) return;

                            EmbeddingRequest embeddingRequest = new EmbeddingRequest(List.of(content), null);
                            EmbeddingResponse embeddingResponse = this.embeddingModel.call(embeddingRequest);

                            // --- DEBUGGING SECTION ---
                            // Let's find the correct method to get the vector.
                            var result = embeddingResponse.getResult();
                            var embeddingObject = result.getOutput();

                            System.out.println("--- DEBUG INFO ---");
                            System.out.println("The class of the object containing the vector is: " + embeddingObject.getClass().getName());
                            System.out.println("Available methods on this object are:");
                            for (Method method : embeddingObject.getClass().getMethods()) {
                                System.out.println("- " + method.getName());
                            }
                            System.out.println("--------------------");
                            // --- END DEBUGGING SECTION ---

                            // TODO: Replace this placeholder with the correct method call from the logs above.
                            // It will likely be a method that returns a List, like 'getVector', 'asList', or 'getEmbedding'.
                            List<Double> vector = Collections.emptyList(); // Placeholder
                            // For example, if you see a method called 'getVector()', the line would be:
                            // List<Double> vector = embeddingObject.getVector();


                            // The rest of the logic remains the same
                            if (!vector.isEmpty()) {
                                float[] floatVector = new float[vector.size()];
                                for (int i = 0; i < vector.size(); i++) {
                                    floatVector[i] = vector.get(i).floatValue();
                                }
                                CodeEmbedding codeEmbedding = new CodeEmbedding();
                                codeEmbedding.setFilePath(projectPath.relativize(path).toString());
                                codeEmbedding.setContent(content);
                                codeEmbedding.setEmbedding(new PGvector(floatVector));
                                repository.save(codeEmbedding);
                                System.out.println("Successfully generated and saved embedding for one file.");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}