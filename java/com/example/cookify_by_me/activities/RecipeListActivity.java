package com.example.cookify_by_me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class RecipeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeDAO recipeDAO;
    private String category, type;
    private TextView toolbarTitle, resultCountText;
    private ImageButton backBtn;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        sessionManager = new SessionManager(this);
        initViews();
        setupActions();
        setupNavigation();
        
        category = getIntent().getStringExtra("category");
        type = getIntent().getStringExtra("type");

        if (category != null) {
            toolbarTitle.setText(category);
        } else if ("my_posts".equals(type)) {
            toolbarTitle.setText("My Published Recipes");
        }

        recipeDAO = new RecipeDAO(this);
        recipeDAO.open();

        loadData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        toolbarTitle = findViewById(R.id.toolbar_title);
        resultCountText = findViewById(R.id.resultCountText);
        backBtn = findViewById(R.id.backBtnList);
    }

    private void setupActions() {
        backBtn.setOnClickListener(v -> finish());
        
        findViewById(R.id.topProfileIconList).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void setupNavigation() {
        findViewById(R.id.navHomeL).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.navSearchL).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        });

        findViewById(R.id.navFavL).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
        });

        findViewById(R.id.navProfileL).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    private void loadData() {
        List<Recipe> recipes;
        if ("my_posts".equals(type)) {
            recipes = recipeDAO.getUserPosts(sessionManager.getUserId());
        } else if (category != null) {
            recipes = recipeDAO.getRecipesByCategory(category);
        } else {
            recipes = recipeDAO.getAllRecipes();
        }

        String label = " Recipes Found";
        if ("my_posts".equals(type)) label = " Published Posts";
        
        String countText = recipes.size() + label;
        resultCountText.setText(countText);

        RecipeAdapter adapter = new RecipeAdapter(recipes, recipe -> {
            Intent intent = new Intent(RecipeListActivity.this, RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            startActivity(intent);
            
            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type != null) loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recipeDAO != null) recipeDAO.close();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
