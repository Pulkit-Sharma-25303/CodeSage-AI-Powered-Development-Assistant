package com.CodeSagePro.Code_Sage_Pro.Service;

import com.CodeSagePro.Code_Sage_Pro.Dto.FileNode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FileStructureService {

    /**
     * Recursively scans a directory and builds a tree structure of files and subdirectories.
     * @param directoryPath The path to the directory to scan.
     * @return A list of FileNode objects representing the contents of the directory.
     */
    public List<FileNode> generateFileTree(Path directoryPath) throws IOException {
        List<FileNode> nodes = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    // If it's a directory, recursively call this method for its children
                    List<FileNode> children = generateFileTree(path);
                    nodes.add(new FileNode(path.getFileName().toString(), "directory", children));
                } else {
                    // If it's a file, it has no children
                    nodes.add(new FileNode(path.getFileName().toString(), "file", null));
                }
            }
        }
        // Sort nodes so directories appear before files
        nodes.sort(Comparator.comparing(FileNode::getType).reversed().thenComparing(FileNode::getName));
        return nodes;
    }
}