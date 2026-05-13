package com.example.cookify_by_me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.adapters.RecipeAdapter;
import com.example.cookify_by_me.database.RecipeDAO;
import com.example.cookify_by_me.models.Recipe;
import com.example.cookify_by_me.utils.SessionManager;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private RecipeDAO recipeDAO;
    private SessionManager sessionManager;
    private TextView tvNoFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupNavigation();
        
        recipeDAO = new RecipeDAO(this);
        sessionManager = new SessionManager(this);

        loadFavorites();
    }

    private void initViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        tvNoFavorites = findViewById(R.id.tvNoFavorites);
    }

    private void setupNavigation() {
        // Home Navigation
        findViewById(R.id.navHomeFav).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Search Navigation
        findViewById(R.id.navSearchFav).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Profile Navigation
        findViewById(R.id.navProfileFav).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites(); // Refresh when returning from detail
    }

    private void loadFavorites() {
        recipeDAO.open();
        List<Recipe> favoriteRecipes = recipeDAO.getFavoriteRecipes(sessionManager.getUserId());
        recipeDAO.close();

        if (favoriteRecipes.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            tvNoFavorites.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);
            RecipeAdapter adapter = new RecipeAdapter(favoriteRecipes, recipe -> {
                Intent intent = new Intent(FavoritesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipeId", recipe.getId());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
            rvFavorites.setLayoutManager(new LinearLayoutManager(this));
            rvFavorites.setAdapter(adapter);
        }
    }
}
