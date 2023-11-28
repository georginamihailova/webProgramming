package mk.ukim.finki.wp.jan2023.service.impl;

import mk.ukim.finki.wp.jan2023.model.Party;
import mk.ukim.finki.wp.jan2023.model.exceptions.InvalidPartyIdException;
import mk.ukim.finki.wp.jan2023.repository.PartyRepository;
import mk.ukim.finki.wp.jan2023.service.PartyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartyServiceImpl implements PartyService {
    private final PartyRepository partyRepository;

    public PartyServiceImpl(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    @Override
    public Party findById(Long id) {
        return this.partyRepository.findById(id).orElseThrow(InvalidPartyIdException::new);
    }

    @Override
    public List<Party> listAll() {
        return this.partyRepository.findAll();
    }

    @Override
    public Party create(String name) {
        Party party = new Party(name);
        return this.partyRepository.save(party);
    }
}
