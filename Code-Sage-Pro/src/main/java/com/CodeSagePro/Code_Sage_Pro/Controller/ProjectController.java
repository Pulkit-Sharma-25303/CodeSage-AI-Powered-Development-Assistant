package com.CodeSagePro.Code_Sage_Pro.Controller;

import com.CodeSagePro.Code_Sage_Pro.Dto.FileNode;
import com.CodeSagePro.Code_Sage_Pro.Service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// DTOs for request bodies
record TestRequest(String projectId, String filePath) {}
record RefactorRequest(String codeSnippet, String goal) {}
record LiveEditRequest(String projectId, String prompt) {}

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final FileService fileService;
    private final CodeAnalysisService analysisService;
    private final EmbeddingService embeddingService;
    private final FileStructureService fileStructureService;
    private final DatabaseAnalysisService databaseAnalysisService;
    private final TestGenerationService testGenerationService;
    private final RefactoringService refactoringService;
    private final LiveEditingService liveEditingService;

    private final Map<String, Path> activeProjects = new ConcurrentHashMap<>();

    public ProjectController(FileService fileService,
                             CodeAnalysisService analysisService,
                             EmbeddingService embeddingService,
                             FileStructureService fileStructureService,
                             DatabaseAnalysisService databaseAnalysisService,
                             TestGenerationService testGenerationService,
                             RefactoringService refactoringService,
                             LiveEditingService liveEditingService) {
        this.fileService = fileService;
        this.analysisService = analysisService;
        this.embeddingService = embeddingService;
        this.fileStructureService = fileStructureService;
        this.databaseAnalysisService = databaseAnalysisService;
        this.testGenerationService = testGenerationService;
        this.refactoringService = refactoringService;
        this.liveEditingService = liveEditingService;
    }

    /**
     * Handles the initial upload and comprehensive analysis of a project.
     * This includes code review, documentation generation, and dependency analysis.
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeProject(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload."));
        }
        try {
            Path projectPath = fileService.decompress(file);
            String projectId = projectPath.getFileName().toString();
            activeProjects.put(projectId, projectPath);

            embeddingService.generateAndStoreEmbeddings(projectPath);
            String codeReview = analysisService.performCodeReview(projectPath);
            String documentation = analysisService.generateDocumentation(projectPath);
            List<FileNode> fileTree = fileStructureService.generateFileTree(projectPath);
            Map<String, String> dbConnections = databaseAnalysisService.analyzeDatabaseConnections(projectPath);

            Map<String, Object> analysisResults = Map.of(
                    "projectId", projectId,
                    "codeReview", codeReview,
                    "documentation", documentation,
                    "fileTree", fileTree,
                    "dbConnections", dbConnections
            );
            return ResponseEntity.ok(analysisResults);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to process file: " + e.getMessage()));
        }
    }

    /**
     * Generates a step-by-step implementation guide based on a user's prompt.
     */
    @PostMapping("/live-edit")
    public ResponseEntity<?> liveEditProject(@RequestBody LiveEditRequest request) {
        Path projectPath = activeProjects.get(request.projectId());
        if (projectPath == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Project not found or session expired."));
        }
        try {
            String implementationGuide = liveEditingService.generateImplementationGuide(projectPath, request.prompt());
            return ResponseEntity.ok(Map.of("guide", implementationGuide));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error generating guide: " + e.getMessage()));
        }
    }

    @GetMapping(value = "/file-content", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getFileContent(@RequestParam String projectId, @RequestParam String filePath) {
        Path projectPath = activeProjects.get(projectId);
        if (projectPath == null) {
            return ResponseEntity.status(404).body("Project not found or session expired.");
        }
        try {
            Path targetFile = projectPath.resolve(filePath);
            if (!targetFile.normalize().startsWith(projectPath.normalize())) {
                return ResponseEntity.status(400).body("Access to the requested file is not allowed.");
            }
            if (!isTextViewable(filePath)) {
                return ResponseEntity.ok("[Content of binary or non-text file cannot be displayed]");
            }
            String content = Files.readString(targetFile);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error reading file: " + e.getMessage());
        }
    }

    @PostMapping("/generate-tests")
    public ResponseEntity<?> generateTests(@RequestBody TestRequest request) {
        Path projectPath = activeProjects.get(request.projectId());
        if (projectPath == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Project not found or session expired."));
        }
        try {
            Path targetFile = projectPath.resolve(request.filePath());
            if (!targetFile.normalize().startsWith(projectPath.normalize())) {
                return ResponseEntity.status(400).body(Map.of("error", "Access to the requested file is not allowed."));
            }
            String content = Files.readString(targetFile);
            String generatedTests = testGenerationService.generateUnitTests(content, targetFile.getFileName().toString());
            return ResponseEntity.ok(Map.of("testCode", generatedTests));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error generating tests: " + e.getMessage()));
        }
    }

    @PostMapping("/refactor-code")
    public ResponseEntity<?> refactorCode(@RequestBody RefactorRequest request) {
        if (request.codeSnippet() == null || request.codeSnippet().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code snippet cannot be empty."));
        }
        if (request.goal() == null || request.goal().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refactoring goal cannot be empty."));
        }
        try {
            String refactoredCode = refactoringService.refactorCodeSnippet(request.codeSnippet(), request.goal());
            return ResponseEntity.ok(Map.of("refactoredCode", refactoredCode));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error during refactoring: " + e.getMessage()));
        }
    }

    private boolean isTextViewable(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        String lowerCasePath = filePath.toLowerCase();
        return lowerCasePath.endsWith(".java") ||
                lowerCasePath.endsWith(".xml") ||
                lowerCasePath.endsWith(".properties") ||
                lowerCasePath.endsWith(".yml") ||
                lowerCasePath.endsWith(".yaml") ||
                lowerCasePath.endsWith(".md") ||
                lowerCasePath.endsWith(".txt") ||
                lowerCasePath.endsWith(".html") ||
                lowerCasePath.endsWith(".css") ||
                lowerCasePath.endsWith(".js") ||
                lowerCasePath.endsWith(".json") ||
                lowerCasePath.endsWith(".sql") ||
                lowerCasePath.endsWith(".py") ||
                lowerCasePath.endsWith(".gitignore");
    }
}