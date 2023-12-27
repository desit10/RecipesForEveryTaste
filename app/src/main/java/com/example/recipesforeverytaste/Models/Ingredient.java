package com.example.recipesforeverytaste.Models;

import java.io.Serializable;

public class Ingredient implements Serializable {
    String ingredientName;

    public Ingredient() {}
    public Ingredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }


}

