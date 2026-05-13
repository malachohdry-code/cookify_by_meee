package com.example.cookify_by_me.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.database.RecipeDAO;
import com.example.cookify_by_me.models.Recipe;
import com.example.cookify_by_me.utils.SessionManager;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView title, time, difficulty, rating, description, ingredientCount, chefsTip, kcal, protein;
    private ImageView image;
    private LinearLayout ingredientsContainer, stepsContainer;
    private ImageButton backBtn, shareBtn, favBtn;
    private View startCookingBtn;
    private RecipeDAO recipeDAO;
    private Recipe recipe;
    private boolean isFavorite = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        sessionManager = new SessionManager(this);
        initViews();
        setupActions();
        loadData();
    }

    private void initViews() {
        title = findViewById(R.id.recipeTitleDetail);
        time = findViewById(R.id.recipeTimeDetail);
        difficulty = findViewById(R.id.recipeDifficultyDetail);
        rating = findViewById(R.id.recipeRatingDetail);
        description = findViewById(R.id.recipeDescription);
        ingredientCount = findViewById(R.id.ingredientCount);
        chefsTip = findViewById(R.id.chefsTipText);
        kcal = findViewById(R.id.kcalText);
        protein = findViewById(R.id.proteinText);
        image = findViewById(R.id.recipeImageDetail);
        
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        stepsContainer = findViewById(R.id.stepsContainer);
        
        backBtn = findViewById(R.id.backBtn);
        shareBtn = findViewById(R.id.shareBtn);
        favBtn = findViewById(R.id.favBtnDetail);
        startCookingBtn = findViewById(R.id.startCookingBtn);
    }

    private void setupActions() {
        backBtn.setOnClickListener(v -> finish());
        
        shareBtn.setOnClickListener(v -> {
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                Toast.makeText(this, "Sharing Recipe...", Toast.LENGTH_SHORT).show();
            }).start();
        });

        favBtn.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteUI();
            
            v.animate().scaleX(1.3f).scaleY(1.3f).setDuration(150).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            }).start();
            
            String msg = isFavorite ? "Saved to Favorites" : "Removed from Favorites";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        startCookingBtn.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                Toast.makeText(this, "Let's start cooking!", Toast.LENGTH_SHORT).show();
            }).start();
        });
    }

    private void updateFavoriteUI() {
        if (isFavorite) {
            favBtn.setImageResource(android.R.drawable.btn_star_big_on);
            favBtn.setColorFilter(getResources().getColor(R.color.primary_orange));
        } else {
            favBtn.setImageResource(android.R.drawable.btn_star_big_off);
            favBtn.setColorFilter(getResources().getColor(R.color.cookify_brown));
        }
    }

    private void loadData() {
        int recipeId = getIntent().getIntExtra("recipeId", -1);
        recipeDAO = new RecipeDAO(this);
        recipeDAO.open();
        recipe = recipeDAO.getRecipeById(recipeId);

        if (recipe != null) {
            title.setText(recipe.getName());
            time.setText(recipe.getTime());
            difficulty.setText(recipe.getDifficulty());
            description.setText(recipe.getDescription());
            rating.setText(recipe.getRating());
            chefsTip.setText(recipe.getChefsTip());
            kcal.setText(recipe.getKcal());
            protein.setText(recipe.getProtein());
            
            // Dynamic image loading
            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                int resId = getResources().getIdentifier(recipe.getImage(), "drawable", getPackageName());
                if (resId != 0) {
                    image.setImageResource(resId);
                } else {
                    image.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                image.setImageResource(R.drawable.ic_launcher_background);
            }
            
            populateIngredients(recipe.getIngredients());
            populateSteps(recipe.getSteps());
            
            // Log to Recently Viewed
            if (sessionManager.isLoggedIn()) {
                recipeDAO.addToRecentlyViewed(sessionManager.getUserId(), recipeId);
            }
            
            // Entrance animations
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            title.startAnimation(fadeIn);
            description.startAnimation(fadeIn);
            startCookingBtn.startAnimation(fadeIn);
        }
    }

    private void populateIngredients(String ingredientsRaw) {
        ingredientsContainer.removeAllViews();
        if (ingredientsRaw == null || ingredientsRaw.isEmpty()) return;
        
        String[] items = ingredientsRaw.split(",");
        ingredientCount.setText(items.length + " items");
        
        for (String item : items) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_ingredient, ingredientsContainer, false);
            TextView nameText = view.findViewById(R.id.ingredientName);
            TextView amountText = view.findViewById(R.id.ingredientAmount);
            
            if (item.contains(":")) {
                String[] parts = item.split(":");
                nameText.setText(parts[0].trim());
                amountText.setText(parts[1].trim());
            } else {
                nameText.setText(item.trim());
                amountText.setText("");
            }
            
            ingredientsContainer.addView(view);
        }
    }

    private void populateSteps(String stepsRaw) {
        stepsContainer.removeAllViews();
        if (stepsRaw == null || stepsRaw.isEmpty()) return;
        
        String[] steps = stepsRaw.split("\\.\\."); 
        int count = 1;
        for (String stepText : steps) {
            if (stepText.trim().isEmpty()) continue;
            
            View view = LayoutInflater.from(this).inflate(R.layout.item_step, stepsContainer, false);
            TextView num = view.findViewById(R.id.stepNumber);
            TextView stepTitle = view.findViewById(R.id.stepTitle);
            TextView desc = view.findViewById(R.id.stepDescription);
            
            num.setText(String.valueOf(count++));
            
            if (stepText.contains("|")) {
                String[] parts = stepText.split("\\|");
                stepTitle.setText(parts[0].trim());
                desc.setText(parts[1].trim());
            } else {
                stepTitle.setText("Step " + num.getText());
                desc.setText(stepText.trim());
            }
            
            stepsContainer.addView(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recipeDAO != null) recipeDAO.close();
    }
    
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
    }
}
