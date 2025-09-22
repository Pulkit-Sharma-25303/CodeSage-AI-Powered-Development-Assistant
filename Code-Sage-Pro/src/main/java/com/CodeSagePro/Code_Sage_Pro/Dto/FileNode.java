package com.CodeSagePro.Code_Sage_Pro.Dto;


import java.util.List;

public class FileNode {
    private String name;
    private String type; // "file" or "directory"
    private List<FileNode> children;

    public FileNode(String name, String type, List<FileNode> children) {
        this.name = name;
        this.type = type;
        this.children = children;
    }

    // Standard Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<FileNode> getChildren() { return children; }
    public void setChildren(List<FileNode> children) { this.children = children; }
}