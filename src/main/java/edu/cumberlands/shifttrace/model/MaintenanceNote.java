package edu.cumberlands.shifttrace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "maintenance_notes")
public class MaintenanceNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String assetTag;

    @Column(nullable = false, length = 160)
    private String summary;

    @Column(nullable = false, length = 16)
    private String priority;

    protected MaintenanceNote() { }

    public MaintenanceNote(String assetTag, String summary, String priority) {
        this.assetTag = assetTag;
        this.summary = summary;
        this.priority = priority;
    }

    public Long getId() { return id; }
    public String getAssetTag() { return assetTag; }
    public String getSummary() { return summary; }
    public String getPriority() { return priority; }
}
