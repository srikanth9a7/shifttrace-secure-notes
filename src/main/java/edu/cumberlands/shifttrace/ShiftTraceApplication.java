package edu.cumberlands.shifttrace;

import edu.cumberlands.shifttrace.data.MaintenanceNoteRepository;
import edu.cumberlands.shifttrace.model.MaintenanceNote;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ShiftTraceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShiftTraceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedSampleData(MaintenanceNoteRepository notes) {
        return args -> {
            if (notes.count() == 0) {
                notes.saveAll(List.of(
                    new MaintenanceNote("LAB-RACK-01", "Power-on verification complete", "LOW"),
                    new MaintenanceNote("LAB-NET-02", "Network uplink retest scheduled", "MEDIUM"),
                    new MaintenanceNote("LAB-NODE-03", "Firmware baseline checked", "LOW")
                ));
            }
        };
    }
}
