package com.moviecatalogservice.services;

import com.moviecatalogservice.models.Rating;
import com.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;

@Service
public class UserRatingService {

    private final RestTemplate restTemplate;

    public UserRatingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getFallbackUserRatings")
    public UserRating getUserRating(@PathVariable("userId") String userId) {
        String ratingsUrl = "http://ratings-data-service/ratings/" + userId;
        return Objects.requireNonNull(restTemplate.getForObject(ratingsUrl, UserRating.class));
    }

    public UserRating getFallbackUserRatings(@PathVariable("userId") String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setRatings(Collections.singletonList(
                new Rating("0", 0)
        ));

        return userRating;
    }
}
