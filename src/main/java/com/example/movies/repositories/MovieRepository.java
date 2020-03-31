package com.example.movies.repositories;

import com.example.movies.model.Movie;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {
  Optional<Movie> findByTitle(String title);
}
