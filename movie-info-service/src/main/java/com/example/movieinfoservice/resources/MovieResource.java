package com.example.movieinfoservice.resources;

import com.example.movieinfoservice.models.Movie;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(String movieId) {
        return new Movie(movieId, "Test name");
    }
}
