package com.CodeSagePro.Code_Sage_Pro.Model;

import com.pgvector.PGvector;
import jakarta.persistence.*;

@Entity
public class CodeEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "vector(4096)")
    private PGvector embedding;

    // --- GETTERS AND SETTERS ---
    // Make sure all of these are present

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) { // <-- FIX: Added this method
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PGvector getEmbedding() {
        return embedding;
    }

    public void setEmbedding(PGvector embedding) { // <-- FIX: Added this method
        this.embedding = embedding;
    }
}