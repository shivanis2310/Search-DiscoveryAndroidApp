package com.example.shivanisingh.webhw9;



public class ReviewsResults {
    private String reviewname, rating, imageurl, time, text, authorurl;

    public ReviewsResults() {
    }

    public ReviewsResults(String reviewname, String rating, String imageurl, String time, String text, String authorurl) {
        this.reviewname = reviewname;
        this.rating =rating;
        this.imageurl = imageurl;
        this.time = time;
        this.text = text;
        this.authorurl = authorurl;
    }

    public String getReviewName() {
        return reviewname;
    }

    public void setReviewName(String reviewname) {
        this.reviewname = reviewname;
    }

    public String getReviewImage() {
        return imageurl;
    }

    public void setReviewImage(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getReviewRating() {
        return rating;
    }

    public void setReviewRating(String rating) {
        this.rating = rating;
    }

    public String getReviewTime() {
        return time;
    }

    public void setReviewTime(String time) {
        this.time = time;
    }

    public String getReviewText() {
        return text;
    }

    public void setReviewText(String time) {
        this.text = text;
    }

    public String getAuthorurl() {
        return authorurl;
    }

    public void setAuthorurl(String authorurl) {
        this.authorurl = authorurl;
    }
}
