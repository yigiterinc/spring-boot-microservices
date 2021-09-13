package com.moviecatalogservice.resources;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.Rating;
import com.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.Path;
import javax.xml.catalog.Catalog;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private final RestTemplate restTemplate;

    public MovieCatalogResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Makes a call to MovieInfoService to get movieId, name and description,
     * Makes a call to RatingsService to get ratings
     * Accumulates both data to create a MovieCatalog
     * @param userId
     * @return CatalogItem that contains name, description and rating
     */
    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod = "getFallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable String userId) {

        // Get all the movies that this user has rated
        String ratingsUrl = "http://ratings-data-service/ratings/" + userId;
        List<Rating> ratings = Objects.requireNonNull(restTemplate.getForObject(ratingsUrl, UserRating.class))
                                        .getRatings();

        // Call the movie-info-service to fetch movie metadata, using the id we get from rating service
        List<CatalogItem> catalog = ratings.stream().map(rating -> {
            String movieDetailsUrl = "http://movie-info-service/movies/" + rating.getMovieId(); // TODO, use service discovery instead
            Movie movie = this.restTemplate.getForObject(movieDetailsUrl, Movie.class);
            return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
        }).collect(Collectors.toList());

        // For each movie ID, call movie info service and get details
        return catalog;
    }

    // Handle the failure on one of the microsystems, return data from cache or just return some default data
    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
        return Arrays.asList(new CatalogItem("No movie", "",0));
    }
}
