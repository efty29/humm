package com.varsityvive.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varsityvive.model.Tournament;
import com.varsityvive.model.User;
import com.varsityvive.repository.TournamentRepository;
import com.varsityvive.repository.UserRepository;
import com.varsityvive.service.TournamentService;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;
    
    @Autowired
    private TournamentRepository tournamentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User organizer = userRepository.findByEmail(email).orElse(null);
        
        if (organizer == null) {
            return ResponseEntity.status(401).build();
        }
        
        tournament.setOrganizer(organizer);
        Tournament saved = tournamentService.createTournament(tournament);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    @GetMapping("/upcoming")
    public List<Tournament> getUpcomingTournaments() {
        return tournamentService.getUpcomingTournaments();
    }

    @PostMapping("/{tournamentId}/register")
    public ResponseEntity<Tournament> registerForTournament(@PathVariable Long tournamentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User participant = userRepository.findByEmail(email).orElse(null);
        
        if (participant == null) {
            return ResponseEntity.status(401).build();
        }
        
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament == null) {
            return ResponseEntity.notFound().build();
        }
        
        tournament.getParticipants().add(participant);
        Tournament updated = tournamentRepository.save(tournament);
        return ResponseEntity.ok(updated);
    }
}