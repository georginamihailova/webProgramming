package mk.ukim.finki.wp.jan2023.service.impl;

import mk.ukim.finki.wp.jan2023.model.Candidate;
import mk.ukim.finki.wp.jan2023.model.Gender;
import mk.ukim.finki.wp.jan2023.model.Party;
import mk.ukim.finki.wp.jan2023.model.exceptions.InvalidCandidateIdException;
import mk.ukim.finki.wp.jan2023.model.exceptions.InvalidPartyIdException;
import mk.ukim.finki.wp.jan2023.repository.CandidateRepository;
import mk.ukim.finki.wp.jan2023.repository.PartyRepository;
import mk.ukim.finki.wp.jan2023.service.CandidateService;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;
    private final PartyRepository partyRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository, PartyRepository partyRepository) {
        this.candidateRepository = candidateRepository;
        this.partyRepository = partyRepository;
    }

    @Override
    public List<Candidate> listAllCandidates() {
        return this.candidateRepository.findAll();
    }

    @Override
    public Candidate findById(Long id) {
        return this.candidateRepository.findById(id).orElseThrow(InvalidCandidateIdException::new);
    }

    @Override
    @Transactional
    public Candidate create(String name, String bio, LocalDate dateOfBirth, Gender gender, Long party) {
        if (party == null) {
            return this.candidateRepository.save(new Candidate(name, bio, dateOfBirth, gender, null));
        } else {
            Party party1 = this.partyRepository.findById(party).orElseThrow(InvalidPartyIdException::new);
            return this.candidateRepository.save(new Candidate(name, bio, dateOfBirth, gender, party1));
        }
    }

    @Override
    @Transactional
    public Candidate update(Long id, String name, String bio, LocalDate dateOfBirth, Gender gender, Long party) {
        Candidate candidate = this.candidateRepository.findById(id).orElseThrow(InvalidCandidateIdException::new);
        if (party == null) {
            candidate.setParty(null);
        } else {
            Party party1 = this.partyRepository.findById(party).orElseThrow(InvalidPartyIdException::new);
            candidate.setParty(party1);
        }
        candidate.setBio(bio);
        candidate.setDateOfBirth(dateOfBirth);
        candidate.setGender(gender);
        candidate.setName(name);
        return this.candidateRepository.save(candidate);
    }

    @Override
    public Candidate delete(Long id) {
        Candidate candidate = this.candidateRepository.findById(id).orElseThrow(InvalidCandidateIdException::new);
        this.candidateRepository.delete(candidate);
        return candidate;
    }

    @Override
    public Candidate vote(Long id) {
        Candidate candidate = this.candidateRepository.findById(id).orElseThrow(InvalidCandidateIdException::new);
        Integer vote2 = candidate.getVotes() + 1;
        candidate.setVotes(vote2);
        return this.candidateRepository.save(candidate);
    }

    @Override
    public List<Candidate> listCandidatesYearsMoreThanAndGender(Integer yearsMoreThan, Gender gender) {
        List<Candidate> candidates = this.candidateRepository.findAll();
        List<Candidate> candidates1 = new ArrayList<>();
        Integer years;
        for (Candidate c : candidates) {
            if (yearsMoreThan == null && gender != null) {
                if (c.getGender() == gender) {
                    candidates1.add(c);
                }
            } else if (yearsMoreThan != null && gender == null) {
                Integer time = LocalDate.now().minusYears(c.getDateOfBirth().getYear()).getYear();
                if (time > yearsMoreThan) {
                    candidates1.add(c);
                }
            } else if (yearsMoreThan != null && gender != null) {
                Integer time = LocalDate.now().minusYears(c.getDateOfBirth().getYear()).getYear();
                if (time > yearsMoreThan && c.getGender() == gender) {
                    candidates1.add(c);
                }
            } else candidates1.add(c);
        }
        return candidates1;


    }
}
