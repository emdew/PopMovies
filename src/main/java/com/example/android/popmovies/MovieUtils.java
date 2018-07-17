package com.example.android.popmovies;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MovieUtils {

    private static final String LOG_TAG = MovieUtils.class.getName();

    public final static String YOUTUBE_URL = "https://www.youtube.com/watch";
    private final static String PARAM_V = "v";
    private static String BASE_URL = "http://api.themoviedb.org/3/movie";
    private final static String PARAM_API_KEY = "api_key";
    private static final String API_KEY = "36bfad9d0ba02dad9b3c2c167b27d286";

    private MovieUtils(){
    }

    // TMDb Url for trailers, movie ID is the param
    public static URL buildTrailerURLFromMdb(String mMovieID) {

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(mMovieID)
                .appendPath("videos")
                .appendQueryParameter(PARAM_API_KEY, API_KEY).build();

        URL Url = null;
        try {
            Url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Url;
    }

    // TMDb Url for reviews, movie ID is the param
    public static URL buildReviewURLFromMdb(String mMovieID) {

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(mMovieID)
                .appendPath("reviews")
                .appendQueryParameter(PARAM_API_KEY, API_KEY).build();

        URL Url = null;
        try {
            Url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Url;
    }

    // Returns new URL object from the given string URL, called in fetchMovieData
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // Query the API and return a list of {@link Movies} objects
    public static List<Movies> fetchMovieData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movies}
        List<Movies> movies = parseMoviesFromJson(jsonResponse);

        return movies;
    }

    // Query the trailer API and return a list of {@link Trailers} objects
    public static List<Trailers> fetchTrailerKeys(URL trailerUrl) {

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(trailerUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movies}
        List<Trailers> trailerKeys = parseTrailersFromJson(jsonResponse);

        return trailerKeys;
    }

    // Query the review API and return a list of {@link Reviews} objects
    public static List<Reviews> fetchReviews(URL reviewUrl) {

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(reviewUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movies}
        List<Reviews> reviews = parseReviewsFromJson(jsonResponse);

        return reviews;
    }

    private static List<Movies> parseMoviesFromJson(String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding movies to
        List<Movies> movies = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray moviesArray = baseJsonResponse.optJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {

                // Build up a list of 'Movies' objects with the corresponding data.
                JSONObject currentMovie = moviesArray.optJSONObject(i);

                int movieId = currentMovie.optInt("id");
                String posterPath = currentMovie.optString("poster_path");
                String backdropPath = currentMovie.optString("backdrop_path");
                String originalTitle = currentMovie.optString("original_title");
                String rating = currentMovie.optString("vote_average");
                String releaseDate = currentMovie.optString("release_date");
                String synopsis =currentMovie.optString("overview");

                Movies movie = new Movies(movieId, posterPath, backdropPath, originalTitle, rating, releaseDate, synopsis);
                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing the json results", e);
        }

        // Return the list of movies
        return movies;
    }

    private static List<Trailers> parseTrailersFromJson(String jsonResponse){

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding trailers to
        List<Trailers> trailers = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray results = baseJsonResponse.optJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                // Build up a list of 'Trailers' objects with the corresponding data.
                JSONObject currentTrailer = results.optJSONObject(i);

                String trailerKey = currentTrailer.optString("key");

                Trailers trailer = new Trailers(trailerKey);
                trailers.add(trailer);
            }

        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing the json results", e);
        }
        return trailers;
    }

    private static List<Reviews> parseReviewsFromJson(String jsonResponse){

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding trailers to
        List<Reviews> reviews = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray results = baseJsonResponse.optJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                // Build up a list of 'Trailers' objects with the corresponding data.
                JSONObject currentReview = results.optJSONObject(i);

                String author = currentReview.optString("author");
                String content = currentReview.optString("content");

                Reviews review = new Reviews(author, content);
                reviews.add(review);
            }

        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing the json results", e);
        }
        return reviews;
    }
}
