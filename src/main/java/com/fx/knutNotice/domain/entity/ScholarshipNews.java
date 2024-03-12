package com.fx.knutNotice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
public class ScholarshipNews {

    @Id
    private Long nttId;
    private Long boardNumber;
    private String title;
    private String dept;
    private LocalDate date;
    private Long boardViews;
    private String file;

}