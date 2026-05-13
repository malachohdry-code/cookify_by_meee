package com.example.cookify_by_me.models;

public class Recipe {
    private int id;
    private String name;
    private String category;
    private String image;
    private String ingredients;
    private String steps;
    private String time;
    private String difficulty;
    private String description;
    private String rating;
    private String kcal;
    private String protein;
    private String chefsTip;
    private String tags;

    public Recipe(int id, String name, String category, String image, String ingredients, String steps, String time, String difficulty, String description, String rating, String kcal, String protein, String chefsTip, String tags) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
        this.time = time;
        this.difficulty = difficulty;
        this.description = description;
        this.rating = rating;
        this.kcal = kcal;
        this.protein = protein;
        this.chefsTip = chefsTip;
        this.tags = tags;
    }

    // Constructor without ID
    public Recipe(String name, String category, String image, String ingredients, String steps, String time, String difficulty, String description, String rating, String kcal, String protein, String chefsTip, String tags) {
        this.name = name;
        this.category = category;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
        this.time = time;
        this.difficulty = difficulty;
        this.description = description;
        this.rating = rating;
        this.kcal = kcal;
        this.protein = protein;
        this.chefsTip = chefsTip;
        this.tags = tags;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getKcal() { return kcal; }
    public void setKcal(String kcal) { this.kcal = kcal; }

    public String getProtein() { return protein; }
    public void setProtein(String protein) { this.protein = protein; }

    public String getChefsTip() { return chefsTip; }
    public void setChefsTip(String chefsTip) { this.chefsTip = chefsTip; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
