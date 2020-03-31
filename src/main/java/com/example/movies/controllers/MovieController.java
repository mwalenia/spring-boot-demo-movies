package com.example.movies.controllers;

import com.example.movies.exceptions.MovieIdMismatchException;
import com.example.movies.exceptions.MovieNotFoundException;
import com.example.movies.model.Movie;
import com.example.movies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
  final MovieRepository repository;

  public MovieController(MovieRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public Iterable<Movie> findAll() {
    return repository.findAll();
  }

  @GetMapping(path = "/title/{title}")
  public Movie findMovieByTitle(@PathVariable String title) {
    return repository.findByTitle(title).orElseThrow(MovieNotFoundException::new);
  }

  @GetMapping(path = "/{id}")
  public Movie findMovieById(@PathVariable Long id) {
    return repository.findById(id).orElseThrow(MovieNotFoundException::new);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Movie create(@RequestBody Movie movie) {
    return repository.save(movie);
  }

  @DeleteMapping(path = "/{id}")
  public void delete(@PathVariable Long id) {
    Movie movie = repository.findById(id).orElseThrow(MovieNotFoundException::new);
    repository.delete(movie);
  }

  @PutMapping("/{id}")
  public Movie update(@PathVariable Long id, @RequestBody Movie movie) {
    if (id != movie.getId()) {
      throw new MovieIdMismatchException();
    }
    repository.findById(id).orElseThrow(MovieNotFoundException::new);
    return repository.save(movie);
  }
}
