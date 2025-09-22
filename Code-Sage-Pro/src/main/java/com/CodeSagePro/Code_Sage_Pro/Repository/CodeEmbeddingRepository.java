package com.CodeSagePro.Code_Sage_Pro.Repository;

import com.CodeSagePro.Code_Sage_Pro.Model.CodeEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CodeEmbeddingRepository extends JpaRepository<CodeEmbedding, Long> {

    /**
     * Finds the top 'k' most similar code embeddings to a given vector using Cosine Distance.
     * The '<=>' operator calculates the cosine distance (0=exact match, 1=opposite, 2=orthogonal).
     *
     * @param embedding The vector to compare against.
     * @param limit The number of nearest neighbors to return.
     * @return A list of the most similar CodeEmbedding entities.
     */
    @Query(value = "SELECT * FROM code_embedding ORDER BY embedding <=> ?1 LIMIT ?2", nativeQuery = true)
    List<CodeEmbedding> findMostSimilar(String embedding, int limit);
}