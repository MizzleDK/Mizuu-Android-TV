package tv.mizuu.app;

import java.util.Date;
import java.util.List;

import tv.mizuu.app.Filepath;
import tv.mizuu.app.MovieCollection;

public class DbMovie {

    private int id;
    private int rating, runtime;

    private String title;

    private String originalTitle;

    private String plot, tagline, imdbId;
    private Date release, added;
    private List<Filepath> filepaths;
    private MovieCollection collection;
    private boolean favorite, watchlist, watched;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public int getRating() {
        return rating;

    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public boolean isWatchlist() {
        return watchlist;
    }

    public void setWatchlist(boolean watchlist) {
        this.watchlist = watchlist;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getRelease() {
        return release;
    }

    public List<Filepath> getFilepaths() {
        return filepaths;
    }

    public void setFilepaths(List<Filepath> filepaths) {
        this.filepaths = filepaths;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    public MovieCollection getCollection() {
        return collection;
    }

    public void setCollection(MovieCollection collection) {
        this.collection = collection;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}