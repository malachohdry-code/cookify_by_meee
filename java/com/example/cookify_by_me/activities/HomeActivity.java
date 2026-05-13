package com.example.cookify_by_me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.adapters.CategoryAdapter;
import com.example.cookify_by_me.adapters.RecipeAdapter;
import com.example.cookify_by_me.database.CategoryDAO;
import com.example.cookify_by_me.database.RecipeDAO;
import com.example.cookify_by_me.models.Category;
import com.example.cookify_by_me.models.Recipe;
import com.example.cookify_by_me.utils.SessionManager;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView categoryRecycler, recipeRecycler;
    private CategoryDAO categoryDAO;
    private RecipeDAO recipeDAO;
    private View searchBar, btnGetStarted;
    private ImageView profileImage;
    private TextView viewAllCategories, viewAllRecipes;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        initViews();
        setupDatabase();
        loadCategories();
        loadPopularRecipes();
        setupNavigation();
        
        applyLayoutAnimations();
    }

    private void initViews() {
        categoryRecycler = findViewById(R.id.categoryRecycler);
        recipeRecycler = findViewById(R.id.recipeRecycler);
        searchBar = findViewById(R.id.searchBar);
        btnGetStarted = findViewById(R.id.btnGetStartedBanner);
        profileImage = findViewById(R.id.profileImage);
        viewAllCategories = findViewById(R.id.viewAllCategories);
        viewAllRecipes = findViewById(R.id.viewAllRecipes);
        
        categoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recipeRecycler.setLayoutManager(new LinearLayoutManager(this));
        recipeRecycler.setNestedScrollingEnabled(false);
    }

    private void setupDatabase() {
        categoryDAO = new CategoryDAO(this);
        recipeDAO = new RecipeDAO(this);
        categoryDAO.open();
        recipeDAO.open();
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            Intent i = new Intent(HomeActivity.this, RecipeListActivity.class);
            i.putExtra("category", category.getName());
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        categoryRecycler.setAdapter(adapter);
    }

    private void loadPopularRecipes() {
        List<Recipe> recipes = recipeDAO.getAllRecipes();
        RecipeAdapter adapter = new RecipeAdapter(recipes, new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                Intent i = new Intent(HomeActivity.this, RecipeDetailActivity.class);
                i.putExtra("recipeId", recipe.getId());
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onFavoriteToggle(Recipe recipe, boolean isFavorite) {
                recipeDAO.toggleFavorite(sessionManager.getUserId(), recipe.getId());
                loadPopularRecipes(); // Refresh to update heart icon state
            }
        });
        recipeRecycler.setAdapter(adapter);
    }

    private void setupNavigation() {
        searchBar.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(this, PostRecipeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        profileImage.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.navSearch).setOnClickListener(v -> {
             startActivity(new Intent(this, SearchActivity.class));
             overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
             finish();
        });

        findViewById(R.id.navFav).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.fabCreatePost).setOnClickListener(v -> {
            startActivity(new Intent(this, PostRecipeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        viewAllCategories.setOnClickListener(v -> {
            Intent i = new Intent(this, RecipeListActivity.class);
            i.putExtra("type", "all");
            startActivity(i);
        });

        viewAllRecipes.setOnClickListener(v -> {
            Intent i = new Intent(this, RecipeListActivity.class);
            i.putExtra("type", "all");
            startActivity(i);
        });
    }

    private void applyLayoutAnimations() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setDelay(0.15f);
        recipeRecycler.setLayoutAnimation(controller);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPopularRecipes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        categoryDAO.close();
        recipeDAO.close();
    }
}
