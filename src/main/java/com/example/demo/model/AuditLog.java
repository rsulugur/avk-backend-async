package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "audit")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String userQuery;
    private List<String> suggestedProducts = new ArrayList<>();
    private Integer totalRecords = 0;
    private Double timeTaken;

    @CreatedDate
    private LocalDateTime createdDate;

    public void incrementRecords() {
        this.totalRecords ++;
    }
}
