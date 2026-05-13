package com.example.cookify_by_me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.database.RecipeDAO;
import com.example.cookify_by_me.models.Recipe;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText ingredientInput;
    private ChipGroup ingredientChips;
    private RecyclerView suggestionsRecycler;
    private TextView recipesFoundCount;
    private RecipeDAO recipeDAO;
    private List<String> userIngredients = new ArrayList<>();
    private SuggestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupDatabase();
        setupListeners();
        
        updateSuggestions();
    }

    private void initViews() {
        ingredientInput = findViewById(R.id.ingredientInput);
        ingredientChips = findViewById(R.id.ingredientChips);
        suggestionsRecycler = findViewById(R.id.suggestionsRecycler);
        recipesFoundCount = findViewById(R.id.recipesFoundCount);

        suggestionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        
        // Header Profile Click
        findViewById(R.id.profileImage).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Bottom Nav Logic
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.navFav).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    private void setupDatabase() {
        recipeDAO = new RecipeDAO(this);
        recipeDAO.open();
    }

    private void setupListeners() {
        findViewById(R.id.addIngredientBtn).setOnClickListener(v -> {
            String text = ingredientInput.getText().toString().trim();
            if (!text.isEmpty()) {
                addIngredientChip(text);
                ingredientInput.setText("");
            }
        });

        findViewById(R.id.findRecipesBtn).setOnClickListener(v -> {
            updateSuggestions();
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            }).start();
        });
    }

    private void addIngredientChip(String text) {
        if (userIngredients.contains(text.toLowerCase())) return;

        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.primary_orange);
        chip.setTextColor(getResources().getColor(R.color.white));
        chip.setCloseIconTintResource(R.color.white);
        
        chip.setOnCloseIconClickListener(v -> {
            ingredientChips.removeView(chip);
            userIngredients.remove(text.toLowerCase());
            updateSuggestions();
        });

        ingredientChips.addView(chip);
        userIngredients.add(text.toLowerCase());
        updateSuggestions();
        
        chip.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    private void updateSuggestions() {
        List<Recipe> allRecipes = recipeDAO.getAllRecipes();
        List<RecipeMatch> matchedRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            int matches = 0;
            String ingredients = recipe.getIngredients();
            if (ingredients == null) continue;
            
            String[] recipeIngs = ingredients.toLowerCase().split(",");
            for (String userIng : userIngredients) {
                for (String rIng : recipeIngs) {
                    if (rIng.contains(userIng)) {
                        matches++;
                        break;
                    }
                }
            }
            
            if (matches > 0 || userIngredients.isEmpty()) {
                int totalRecipeIngs = recipeIngs.length;
                int missing = totalRecipeIngs - matches;
                matchedRecipes.add(new RecipeMatch(recipe, missing));
            }
        }

        matchedRecipes.sort((a, b) -> Integer.compare(a.missingCount, b.missingCount));
        recipesFoundCount.setText(matchedRecipes.size() + " RECIPES FOUND");
        
        adapter = new SuggestionAdapter(matchedRecipes);
        suggestionsRecycler.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recipeDAO.close();
    }

    private static class RecipeMatch {
        Recipe recipe;
        int missingCount;

        RecipeMatch(Recipe recipe, int missingCount) {
            this.recipe = recipe;
            this.missingCount = missingCount;
        }
    }

    private class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
        private List<RecipeMatch> items;

        SuggestionAdapter(List<RecipeMatch> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecipeMatch match = items.get(position);
            Recipe recipe = match.recipe;

            holder.title.setText(match.missingCount == 0 ? "You can make " + recipe.getName() : recipe.getName());
            holder.time.setText(recipe.getTime());
            holder.rating.setText(recipe.getRating().split(" ")[0]);
            holder.desc.setText(recipe.getDescription());
            
            if (match.missingCount == 0) {
                holder.badge.setText("Perfect Match");
                holder.badge.setBackgroundResource(R.drawable.bg_chefs_tip);
                holder.missingSection.setVisibility(View.GONE);
            } else {
                holder.badge.setText(match.missingCount + " Ingredients Missing");
                holder.badge.setBackgroundResource(R.drawable.bg_category_unselected);
                holder.badge.setTextColor(getResources().getColor(R.color.cookify_brown));
                
                holder.missingSection.setVisibility(View.VISIBLE);
                holder.missingText.setText("Add " + match.missingCount + " items to make this recipe");
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(SearchActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipeId", recipe.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, time, rating, desc, badge, missingText;
            View missingSection;

            ViewHolder(View v) {
                super(v);
                title = v.findViewById(R.id.suggestionTitle);
                time = v.findViewById(R.id.suggestionTime);
                rating = v.findViewById(R.id.suggestionRating);
                desc = v.findViewById(R.id.suggestionDesc);
                badge = v.findViewById(R.id.matchBadge);
                missingText = v.findViewById(R.id.missingText);
                missingSection = v.findViewById(R.id.missingSection);
            }
        }
    }
}
