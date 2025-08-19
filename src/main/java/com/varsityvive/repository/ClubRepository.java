package com.varsityvive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varsityvive.model.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
}