package com.example.recipesforeverytaste.Models;



import java.util.ArrayList;

public class Recipe {

    String author, nameRecipe, nationalityRecipe, сlassificationRecipe, descriptionRecipe, video, idRecipe, idVideo;
    int likes, favorites, shares;
    ArrayList<String> images, ingredients;

    public Recipe() {}

    //Для постов с рецептами
    public Recipe(String idRecipe, String author, String nameRecipe, String nationalityRecipe, String сlassificationRecipe,
                  String descriptionRecipe, ArrayList<String> images, ArrayList<String> ingredients, int likes, int favorites, int shares) {
        this.idRecipe = idRecipe;
        this.author = author;
        this.nameRecipe = nameRecipe;
        this.nationalityRecipe = nationalityRecipe;
        this.сlassificationRecipe = сlassificationRecipe;
        this.descriptionRecipe = descriptionRecipe;
        this.images = images;
        this.ingredients = ingredients;
        this.likes = likes;
        this.favorites = favorites;
        this.shares = shares;

    }

    //Для видео с рецептами
    public Recipe(String idVideo, String video, String author, String nameRecipe, String nationalityRecipe,
                  String сlassificationRecipe, int likes, int favorites, int shares) {
        this.idVideo = idVideo;
        this.video = video;
        this.author = author;
        this.nameRecipe = nameRecipe;
        this.nationalityRecipe = nationalityRecipe;
        this.сlassificationRecipe = сlassificationRecipe;
        this.likes = likes;
        this.favorites = favorites;
        this.shares = shares;
    }

    public String getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(String idRecipe) {
        this.idRecipe = idRecipe;
    }

    public String getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(String idVideo) {
        this.idVideo = idVideo;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNameRecipe() {
        return nameRecipe;
    }

    public void setNameRecipe(String nameRecipe) {
        this.nameRecipe = nameRecipe;
    }

    public String getNationalityRecipe() {
        return nationalityRecipe;
    }

    public void setNationalityRecipe(String nationalityRecipe) {
        this.nationalityRecipe = nationalityRecipe;
    }

    public String getСlassificationRecipe() {
        return сlassificationRecipe;
    }

    public void setСlassificationRecipe(String сlassificationRecipe) {
        this.сlassificationRecipe = сlassificationRecipe;
    }

    public String getDescriptionRecipe() {
        return descriptionRecipe;
    }

    public void setDescriptionRecipe(String descriptionRecipe) {
        this.descriptionRecipe = descriptionRecipe;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }
}
