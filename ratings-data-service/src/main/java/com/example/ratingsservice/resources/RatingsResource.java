package com.example.ratingsservice.resources;

import com.example.ratingsservice.models.Rating;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingsResource {

    @RequestMapping("/{userId}")
    public List<Rating> getRatingsOfUser(@PathVariable String userId) {
        List<Rating> ratings = Arrays.asList(
                new Rating("1234", 4),
                 new Rating("5678", 3)
        );

        return ratings;
    }
}
