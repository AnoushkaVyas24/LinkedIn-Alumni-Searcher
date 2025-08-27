package com.anoushka.alumni_linkedin_searcher.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "alumni_profiles")
@Data   // Lombok: generates getters, setters, toString, equals, hashCode
@Builder
public class AlumniProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // SERIAL (PK)

    @Column(nullable = false)
    private String name;

    @Column(name = "role")
    private String currentRole;

    private String university;

    private String location;

    @Column(name = "linkedin_headline")
    private String linkedinHeadline;

    @Column(name = "passout_year")
    private Integer passoutYear;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLinkedinHeadline() {
        return linkedinHeadline;
    }

    public void setLinkedinHeadline(String linkedinHeadline) {
        this.linkedinHeadline = linkedinHeadline;
    }

    public Integer getPassoutYear() {
        return passoutYear;
    }

    public void setPassoutYear(Integer passoutYear) {
        this.passoutYear = passoutYear;
    }

    public AlumniProfile(Long id, String name, String currentRole, String university, String location, String linkedinHeadline, Integer passoutYear) {
        this.id = id;
        this.name = name;
        this.currentRole = currentRole;
        this.university = university;
        this.location = location;
        this.linkedinHeadline = linkedinHeadline;
        this.passoutYear = passoutYear;
    }

    public AlumniProfile() {
    }

    // --- Private constructor for builder ---
    private AlumniProfile(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.currentRole = builder.currentRole;
        this.university = builder.university;
        this.location = builder.location;
        this.linkedinHeadline = builder.linkedinHeadline;
        this.passoutYear = builder.passoutYear;
    }

    // --- Static method to access builder ---
    public static Builder builder() {
        return new Builder();
    }

    // --- Builder class ---
    public static class Builder {
        private Long id;
        private String name;
        private String currentRole;
        private String university;
        private String location;
        private String linkedinHeadline;
        private Integer passoutYear;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder currentRole(String currentRole) {
            this.currentRole = currentRole;
            return this;
        }

        public Builder university(String university) {
            this.university = university;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder linkedinHeadline(String linkedinHeadline) {
            this.linkedinHeadline = linkedinHeadline;
            return this;
        }

        public Builder passoutYear(Integer passoutYear) {
            this.passoutYear = passoutYear;
            return this;
        }

        public AlumniProfile build() {
            return new AlumniProfile(this);
        }
    }
}