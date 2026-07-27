package com.example.swinecore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "genetics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Genetics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Full name, e.g. "Yorkshire" */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /** Short code used in pig serial, e.g. "Y" */
    @Column(nullable = false, unique = true, length = 6)
    private String code;

    private String description;
    @Builder.Default
    private boolean active = true;
}
