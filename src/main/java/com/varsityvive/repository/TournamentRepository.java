package com.varsityvive.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.varsityvive.model.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Query("SELECT t FROM Tournament t WHERE t.date > :currentDate ORDER BY t.date ASC")
    List<Tournament> findUpcomingTournaments(Date currentDate);
}