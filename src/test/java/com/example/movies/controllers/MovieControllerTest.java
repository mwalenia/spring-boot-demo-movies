package com.example.movies.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.movies.model.Movie;
import com.example.movies.repositories.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

  @Autowired private MockMvc mockMvc;
  private static final String URL_ROOT = "/api/movies/";
  @Autowired private MovieRepository repository;
  @Autowired ObjectMapper mapper;

  @BeforeEach
  void cleanDatabase() {
    repository.deleteAll();
  }

  @Test
  void findAll_returnsOK() throws Exception {
    mockMvc.perform(get(URL_ROOT)).andExpect(status().isOk());
  }

  @Test
  void findMovieByTitle_ReturnsMovie() throws Exception {
    Movie expected = saveMovie();
    mockMvc
        .perform(get(URL_ROOT + "title/" + expected.getTitle()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(expected.getTitle()))
        .andExpect(jsonPath("$.director").value(expected.getDirector()))
        .andExpect(jsonPath("$.rating").value(expected.getRating()));
  }

  @Test
  void findMovieByTitle_ThrowsError() throws Exception {
    mockMvc
        .perform(get(URL_ROOT + "title/" + "nonsense"))
        .andExpect(status().isNotFound())
        .andExpect(status().reason("Movie not found"));
  }

  @Test
  void findMovieById_ReturnsMovie() throws Exception {
    Movie expected = saveMovie();
    mockMvc
        .perform(get(URL_ROOT + expected.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(expected.getTitle()))
        .andExpect(jsonPath("$.director").value(expected.getDirector()))
        .andExpect(jsonPath("$.rating").value(expected.getRating()));
  }

  @Test
  void findMovieById_ThrowsException() throws Exception {
    mockMvc
        .perform(get(URL_ROOT + "5"))
        .andExpect(status().isNotFound())
        .andExpect(status().reason("Movie not found"));
  }

  @Test
  void create_returnsMovie() throws Exception {
    Movie toCreate = createAMovie();
    mockMvc
        .perform(
            post(URL_ROOT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toCreate)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(toCreate.getTitle()))
        .andExpect(jsonPath("$.director").value(toCreate.getDirector()))
        .andExpect(jsonPath("$.rating").value(toCreate.getRating()));
  }

  @Test
  void delete_removesRecord() throws Exception {
    Movie movie = saveMovie();
    mockMvc.perform(delete(URL_ROOT + movie.getId())).andExpect(status().isOk());
    mockMvc.perform(get(URL_ROOT + movie.getId())).andExpect(status().isNotFound());
  }

  @Test
  void delete_throwsError() throws Exception {
    mockMvc.perform(delete(URL_ROOT + "5")).andExpect(status().isNotFound());
  }

  @Test
  void update_changesData() throws Exception {
    Movie movie = saveMovie();
    movie.setRating(1);
    mockMvc
        .perform(
            put(URL_ROOT + movie.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(movie)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value(movie.getTitle()))
        .andExpect(jsonPath("$.director").value(movie.getDirector()))
        .andExpect(jsonPath("$.rating").value(movie.getRating()));
  }

  @Test
  void update_throwsNotFound() throws Exception {
    Movie movie = saveMovie();
    movie.setId(25);
    mockMvc
        .perform(
            put(URL_ROOT + movie.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(movie)))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_throwsBadRequest() throws Exception {
    Movie movie = saveMovie();
    long movieId = movie.getId();
    movie.setId(9999);
    mockMvc
        .perform(
            put(URL_ROOT + movieId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(movie)))
        .andExpect(status().isBadRequest());
  }

  private Movie saveMovie() {
    return repository.save(createAMovie());
  }

  private Movie createAMovie() {
    Movie movie = new Movie();
    movie.setRating(5);
    movie.setTitle("Armageddon");
    movie.setDirector("Michael Bay");
    return movie;
  }
}
