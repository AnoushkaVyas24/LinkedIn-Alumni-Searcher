package com.anoushka.alumni_linkedin_searcher.repository;

import com.anoushka.alumni_linkedin_searcher.model.AlumniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumniProfileRepository extends JpaRepository<AlumniProfile, Long> {
}