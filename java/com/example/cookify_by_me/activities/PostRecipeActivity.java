package com.example.cookify_by_me.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.database.RecipeDAO;
import com.example.cookify_by_me.models.Recipe;
import com.example.cookify_by_me.utils.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class PostRecipeActivity extends AppCompatActivity {

    private EditText etName, etIngredients, etSteps;
    private ChipGroup categoryGroup;
    private RecipeDAO recipeDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_recipe);

        recipeDAO = new RecipeDAO(this);
        recipeDAO.open();
        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.etPostRecipeName);
        etIngredients = findViewById(R.id.etPostIngredients);
        etSteps = findViewById(R.id.etPostSteps);
        categoryGroup = findViewById(R.id.postCategoryGroup);

        findViewById(R.id.backBtnPost).setOnClickListener(v -> finish());

        findViewById(R.id.btnSubmitPost).setOnClickListener(v -> submitRecipe());
    }

    private void submitRecipe() {
        String name = etName.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String steps = etSteps.getText().toString().trim();

        int checkedChipId = categoryGroup.getCheckedChipId();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ingredients) || TextUtils.isEmpty(steps) || checkedChipId == -1) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Chip selectedChip = findViewById(checkedChipId);
        String category = selectedChip.getText().toString();

        // Create new recipe object
        Recipe newRecipe = new Recipe(
                name,
                category,
                "", // No image upload logic for now, using default
                ingredients,
                steps,
                "20 mins", // Default
                "Medium", // Default
                steps.substring(0, Math.min(steps.length(), 50)) + "...",
                "5.0 (New)",
                "300",
                "15g",
                "Freshly made!",
                "" // tags
        );

        long result = recipeDAO.addRecipe(newRecipe, sessionManager.getUserId());

        if (result != -1) {
            Toast.makeText(this, "Recipe Posted Successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to post recipe", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recipeDAO.close();
    }
}
