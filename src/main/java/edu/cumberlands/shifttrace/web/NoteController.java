package edu.cumberlands.shifttrace.web;

import edu.cumberlands.shifttrace.data.MaintenanceNoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoteController {
    private final MaintenanceNoteRepository repository;

    public NoteController(MaintenanceNoteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/notes")
    public List<NoteResponse> findNotes(
            @RequestParam(required = false) String assetTag) {
        if (assetTag != null && assetTag.length() > 64) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        var found = (assetTag == null || assetTag.isBlank())
                ? repository.findAll()
                : repository.findByExactAssetTag(assetTag);
        return found.stream().map(NoteResponse::from).toList();
    }


    // Optional alias: both documented search URL forms are supported.
    // The existing /api/notes?assetTag=... form remains the canonical endpoint.
    @GetMapping("/notes/search")
    public List<NoteResponse> searchNotes(@RequestParam String assetTag) {
        return findNotes(assetTag);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/summary")
    public Map<String, Long> adminSummary() {
        return Map.of("storedNoteCount", repository.count());
    }
}
