package mk.ukim.finki.wp.kol2023.g2.service.impl;

import mk.ukim.finki.wp.kol2023.g2.model.Director;
import mk.ukim.finki.wp.kol2023.g2.model.Genre;
import mk.ukim.finki.wp.kol2023.g2.model.Movie;
import mk.ukim.finki.wp.kol2023.g2.model.exceptions.InvalidDirectorIdException;
import mk.ukim.finki.wp.kol2023.g2.model.exceptions.InvalidMovieIdException;
import mk.ukim.finki.wp.kol2023.g2.repository.DirectorRepository;
import mk.ukim.finki.wp.kol2023.g2.repository.MovieRepository;
import mk.ukim.finki.wp.kol2023.g2.service.MovieService;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;

    public MovieServiceImpl(MovieRepository movieRepository, DirectorRepository directorRepository) {
        this.movieRepository = movieRepository;
        this.directorRepository = directorRepository;
    }

    @Override
    public List<Movie> listAllMovies() {
        return this.movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        return this.movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
    }

    @Override
    @Transactional
    public Movie create(String name, String description, Double rating, Genre genre, Long director) {
        Director director1 = null;
        if (director != null){
            director1 = this.directorRepository.findById(director).orElseThrow(InvalidDirectorIdException::new);
        }
        return this.movieRepository.save(new Movie(name,description,rating,genre,director1));
    }

    @Override
    @Transactional
    public Movie update(Long id, String name, String description, Double rating, Genre genre, Long director) {
        Director director1 = null;
        Movie movie = findById(id);
        if (director != null){
            director1 = this.directorRepository.findById(director).orElseThrow(InvalidDirectorIdException::new);
        }
        movie.setName(name);
        movie.setDescription(description);
        movie.setRating(rating);
        movie.setGenre(genre);
        movie.setDirector(director1);
        return this.movieRepository.save(movie);

    }

    @Override
    public Movie delete(Long id) {
        Movie movie = findById(id);
        this.movieRepository.delete(movie);
        return movie;
    }

    @Override
    @Transactional
    public Movie vote(Long id) {
        Movie movie = findById(id);
        movie.setVotes(movie.getVotes() + 1);
        return this.movieRepository.save(movie);
    }

    @Override
    public List<Movie> listMoviesWithRatingLessThenAndGenre(Double rating, Genre genre) {
        if (rating != null && genre == null){
            return this.movieRepository.findAllByRatingBefore(rating);
        } else if (rating == null && genre != null) {
            return this.movieRepository.findAllByGenreLike(genre);
        } else if (rating != null && genre != null) {
            return this.movieRepository.findAllByRatingBeforeAndGenreLike(rating,genre);
        }else return this.movieRepository.findAll();

    }
}
