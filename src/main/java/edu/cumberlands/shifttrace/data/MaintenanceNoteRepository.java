package edu.cumberlands.shifttrace.data;

import edu.cumberlands.shifttrace.model.MaintenanceNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaintenanceNoteRepository extends JpaRepository<MaintenanceNote, Long> {
    // The tag is bound as a parameter; it cannot alter the JPQL query syntax.
    @Query("select n from MaintenanceNote n " +
           "where lower(n.assetTag) = lower(:tag) order by n.id")
    List<MaintenanceNote> findByExactAssetTag(@Param("tag") String tag);
}
