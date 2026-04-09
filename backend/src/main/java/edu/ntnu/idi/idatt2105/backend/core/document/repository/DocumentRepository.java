package edu.ntnu.idi.idatt2105.backend.core.document.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.ntnu.idi.idatt2105.backend.core.document.entity.Document;
import edu.ntnu.idi.idatt2105.backend.core.document.enums.DocumentArea;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @EntityGraph(attributePaths = {"uploadedBy", "tags"})
    @Query("""
            select distinct d
            from Document d
            left join d.tags tag
            where d.tenant.id = :tenantId
              and (:area is null or d.area = :area)
              and (
                :query is null
                or lower(d.title) like :query
                or lower(coalesce(d.description, '')) like :query
                or lower(d.originalFilename) like :query
                or lower(coalesce(tag, '')) like :query
              )
              and (:tagsEmpty = true or lower(coalesce(tag, '')) in :tags)
            order by d.updatedAt desc
            """)
    List<Document> searchDocuments(
            @Param("tenantId") Long tenantId,
            @Param("area") DocumentArea area,
            @Param("query") String query,
            @Param("tagsEmpty") boolean tagsEmpty,
            @Param("tags") List<String> tags);

    @EntityGraph(attributePaths = {"uploadedBy", "tags"})
    Optional<Document> findByIdAndTenantId(Long id, Long tenantId);
}
