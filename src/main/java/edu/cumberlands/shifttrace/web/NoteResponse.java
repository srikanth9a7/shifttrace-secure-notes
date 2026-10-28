package edu.cumberlands.shifttrace.web;

import edu.cumberlands.shifttrace.model.MaintenanceNote;

public record NoteResponse(Long id, String assetTag, String summary, String priority) {
    public static NoteResponse from(MaintenanceNote note) {
        return new NoteResponse(note.getId(), note.getAssetTag(),
                                note.getSummary(), note.getPriority());
    }
}
