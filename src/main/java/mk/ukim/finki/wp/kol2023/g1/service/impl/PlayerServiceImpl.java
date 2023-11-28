package mk.ukim.finki.wp.kol2023.g1.service.impl;

import mk.ukim.finki.wp.kol2023.g1.model.Player;
import mk.ukim.finki.wp.kol2023.g1.model.PlayerPosition;
import mk.ukim.finki.wp.kol2023.g1.model.Team;
import mk.ukim.finki.wp.kol2023.g1.model.exceptions.InvalidPlayerIdException;
import mk.ukim.finki.wp.kol2023.g1.model.exceptions.InvalidTeamIdException;
import mk.ukim.finki.wp.kol2023.g1.repository.PlayerRepository;
import mk.ukim.finki.wp.kol2023.g1.repository.TeamRepository;
import mk.ukim.finki.wp.kol2023.g1.service.PlayerService;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Player> listAllPlayers() {
        return this.playerRepository.findAll();
    }

    @Override
    public Player findById(Long id) {
        return this.playerRepository.findById(id).orElseThrow(InvalidPlayerIdException::new);
    }

    @Override
    @Transactional
    public Player create(String name, String bio, Double pointsPerGame, PlayerPosition position, Long team) {
        Team team1 = null;
        if (team != null) {
            team1 = this.teamRepository.findById(team).orElseThrow(InvalidTeamIdException::new);

        }
        return this.playerRepository.save(new Player(name, bio, pointsPerGame, position, team1));
    }

    @Override
    @Transactional
    public Player update(Long id, String name, String bio, Double pointsPerGame, PlayerPosition position, Long team) {
        Player player = this.playerRepository.findById(id).orElseThrow(InvalidPlayerIdException::new);
        player.setName(name);
        player.setBio(bio);
        player.setPointsPerGame(pointsPerGame);
        player.setPosition(position);
        if (team != null) {
            Team team1 = this.teamRepository.findById(team).orElseThrow(InvalidTeamIdException::new);
            player.setTeam(team1);
        }
        return this.playerRepository.save(player);

    }

    @Override
    public Player delete(Long id) {
        Player player = this.playerRepository.findById(id).orElseThrow(InvalidPlayerIdException::new);
        this.playerRepository.delete(player);
        return player;
    }

    @Override
    @Transactional
    public Player vote(Long id) {
        Player player = this.playerRepository.findById(id).orElseThrow(InvalidPlayerIdException::new);
        Integer votes = player.getVotes() + 1;
        player.setVotes(votes);
        return this.playerRepository.save(player);
    }

    @Override
    public List<Player> listPlayersWithPointsLessThanAndPosition(Double pointsPerGame, PlayerPosition position) {
        if (pointsPerGame != null && position == null){
            return this.playerRepository.findAllByPointsPerGameLessThan(pointsPerGame);
        } else if (pointsPerGame == null && position != null) {
            return this.playerRepository.findAllByPositionLike(position);
        } else if (pointsPerGame != null && position != null) {
            return this.playerRepository.findAllByPointsPerGameLessThanAndPositionLike(pointsPerGame,position);
        }else return this.playerRepository.findAll();
    }
}
