package com.tp.main.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "key_storage")
@Data
public class KeyRange {

	@Id
    private Integer id; // Usually just use '1' as the ID for the global counter

    @Column(name = "next_start_value", nullable = false)
    private Long nextStartValue;

    // Default constructor for JPA
    public KeyRange() {}

    public KeyRange(Integer id, Long nextStartValue) {
        this.id = id;
        this.nextStartValue = nextStartValue;
    }
}
