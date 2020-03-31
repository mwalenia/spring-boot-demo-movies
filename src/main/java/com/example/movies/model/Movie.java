package com.example.movies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Movie {

  @Id @GeneratedValue private long id;

  @Column(unique = true, nullable = false)
  private String title;

  private String director;
  private int rating = 0;
}
