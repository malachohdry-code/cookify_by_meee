package com.example.cookify_by_me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.adapters.RecipeAdapter;
import com.example.cookify_by_me.database.RecipeDAO;
import com.example.cookify_by_me.database.UserDAO;
import com.example.cookify_by_me.models.Recipe;
import com.example.cookify_by_me.models.User;
import com.example.cookify_by_me.utils.SessionManager;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvUsername, tvEmail, tvRecipeCount;
    private RecyclerView rvFavorites, rvRecentlyViewed;
    private View sectionFavorites;
    private ImageView arrowFav;
    private boolean isFavExpanded = false;

    private RecipeDAO recipeDAO;
    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        recipeDAO = new RecipeDAO(this);
        userDAO = new UserDAO(this);
        
        recipeDAO.open();
        userDAO.open();

        initViews();
        setupNavigation();
        loadUserData();
        loadFavorites();
        loadRecentlyViewed();
    }

    private void initViews() {
        tvFullName = findViewById(R.id.profileFullName);
        tvUsername = findViewById(R.id.profileUsername);
        tvEmail = findViewById(R.id.profileEmail);
        tvRecipeCount = findViewById(R.id.statRecipeCount);
        
        rvFavorites = findViewById(R.id.rvFavoritesProfile);
        rvRecentlyViewed = findViewById(R.id.rvRecentlyViewed);
        sectionFavorites = findViewById(R.id.sectionFavorites);
        arrowFav = findViewById(R.id.arrowFav);

        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvRecentlyViewed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        setupNavListItem(R.id.btnRecentlyViewed, "Recently Viewed Recipes", android.R.drawable.ic_menu_recent_history);
        setupNavListItem(R.id.btnMyPosts, "My Posts", android.R.drawable.ic_menu_agenda);
        setupNavListItem(R.id.btnSettings, "Settings", android.R.drawable.ic_menu_preferences);
        setupNavListItem(R.id.btnLogout, "Logout", android.R.drawable.ic_lock_power_off);

        sectionFavorites.setOnClickListener(v -> {
            isFavExpanded = !isFavExpanded;
            rvFavorites.setVisibility(isFavExpanded ? View.VISIBLE : View.GONE);
            arrowFav.setRotation(isFavExpanded ? 180 : 0);
        });

        findViewById(R.id.fabPostRecipe).setOnClickListener(v -> {
            startActivity(new Intent(this, PostRecipeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void setupNavListItem(int includeId, String title, int iconRes) {
        View view = findViewById(includeId);
        if (view == null) return;
        TextView tv = view.findViewById(R.id.navTitle);
        ImageView iv = view.findViewById(R.id.navIcon);
        if (tv != null) tv.setText(title);
        if (iv != null) iv.setImageResource(iconRes);
        
        if (includeId == R.id.btnLogout) {
            if (tv != null) tv.setTextColor(getResources().getColor(R.color.error_red));
            if (iv != null) iv.setColorFilter(getResources().getColor(R.color.error_red));
        }
    }

    private void setupNavigation() {
        findViewById(R.id.navHomeP).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.navSearchP).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        });
        findViewById(R.id.navFavP).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
            finish();
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        
        findViewById(R.id.btnMyPosts).setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeListActivity.class);
            intent.putExtra("type", "my_posts");
            startActivity(intent);
        });

        findViewById(R.id.btnRecentlyViewed).setOnClickListener(v -> {
            Toast.makeText(this, "Showing Recently Viewed", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        User user = userDAO.getUserById(sessionManager.getUserId());
        if (user != null) {
            tvFullName.setText(user.getName());
            tvUsername.setText("@" + user.getEmail().split("@")[0]);
            tvEmail.setText(user.getEmail());
            
            List<Recipe> userPosts = recipeDAO.getUserPosts(user.getId());
            tvRecipeCount.setText(String.valueOf(userPosts.size()));
        }
    }

    private void loadFavorites() {
        List<Recipe> favs = recipeDAO.getFavoriteRecipes(sessionManager.getUserId());
        RecipeAdapter adapter = new RecipeAdapter(favs, recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            startActivity(intent);
        });
        rvFavorites.setAdapter(adapter);
    }

    private void loadRecentlyViewed() {
        List<Recipe> recent = recipeDAO.getRecentlyViewed(sessionManager.getUserId());
        RecipeAdapter adapter = new RecipeAdapter(recent, recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            startActivity(intent);
        });
        rvRecentlyViewed.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            loadUserData();
            loadFavorites();
            loadRecentlyViewed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recipeDAO != null) recipeDAO.close();
        if (userDAO != null) userDAO.close();
    }
}
