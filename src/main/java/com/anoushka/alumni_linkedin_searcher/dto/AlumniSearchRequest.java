package com.anoushka.alumni_linkedin_searcher.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumniSearchRequest {
    private String university;
    private String designation;
    private Integer passoutYear;

    public AlumniSearchRequest() {
    }

    public AlumniSearchRequest(String university, String designation, Integer passoutYear) {
        this.university = university;
        this.designation = designation;
        this.passoutYear = passoutYear;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getPassoutYear() {
        return passoutYear;
    }

    public void setPassoutYear(Integer passoutYear) {
        this.passoutYear = passoutYear;
    }
}
