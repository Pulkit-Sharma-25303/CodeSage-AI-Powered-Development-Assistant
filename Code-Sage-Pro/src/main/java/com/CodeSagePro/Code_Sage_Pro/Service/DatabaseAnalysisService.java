package com.CodeSagePro.Code_Sage_Pro.Service;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class DatabaseAnalysisService {

    // Define common configuration files and regex patterns for DB properties
    private static final List<String> CONFIG_FILES = List.of("application.properties", "application.yml", "database.yml", ".env");
    private static final Map<String, Pattern> DB_PATTERNS = Map.of(
            "URL", Pattern.compile("spring\\.datasource\\.url\\s*=\\s*(.*)"),
            "Username", Pattern.compile("spring\\.datasource\\.username\\s*=\\s*(.*)"),
            "Password", Pattern.compile("spring\\.datasource\\.password\\s*=\\s*(.*)"),
            "Driver", Pattern.compile("spring\\.datasource\\.driver-class-name\\s*=\\s*(.*)")
    );

    /**
     * Scans the project directory for configuration files and extracts database connection details.
     * @param projectPath The root path of the decompressed project.
     * @return A map containing any found database connection properties.
     */
    public Map<String, String> analyzeDatabaseConnections(Path projectPath) {
        Map<String, String> foundProperties = new HashMap<>();

        try (Stream<Path> paths = Files.walk(projectPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> CONFIG_FILES.contains(path.getFileName().toString()))
                    .forEach(configFile -> {
                        try {
                            List<String> lines = Files.readAllLines(configFile);
                            for (String line : lines) {
                                for (Map.Entry<String, Pattern> entry : DB_PATTERNS.entrySet()) {
                                    Matcher matcher = entry.getValue().matcher(line);
                                    if (matcher.find()) {
                                        foundProperties.put(entry.getKey(), matcher.group(1).trim());
                                    }
                                }
                            }
                        } catch (IOException e) {
                            // Silently ignore files that cannot be read
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return foundProperties;
    }
}