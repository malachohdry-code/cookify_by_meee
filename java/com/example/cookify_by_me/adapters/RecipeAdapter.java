package com.example.cookify_by_me.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.database.RecipeDAO;
import com.example.cookify_by_me.models.Recipe;
import com.example.cookify_by_me.utils.SessionManager;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private OnRecipeClickListener listener;
    private RecipeDAO recipeDAO;
    private SessionManager sessionManager;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
        default void onFavoriteToggle(Recipe recipe, boolean isFavorite) {}
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        recipeDAO = new RecipeDAO(parent.getContext());
        recipeDAO.open();
        sessionManager = new SessionManager(parent.getContext());
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        boolean isFav = recipeDAO.isFavorite(sessionManager.getUserId(), recipe.getId());
        holder.bind(recipe, isFav, listener);
        
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView title, time, rating, tag1, tag2;
        ImageView image, favoriteIcon;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recipeTitle);
            time = itemView.findViewById(R.id.recipeTime);
            rating = itemView.findViewById(R.id.recipeRating);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            image = itemView.findViewById(R.id.recipeImage);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }

        public void bind(final Recipe recipe, boolean isFavorite, final OnRecipeClickListener listener) {
            title.setText(recipe.getName());
            time.setText(recipe.getTime());
            rating.setText(recipe.getRating());
            
            tag1.setText(recipe.getCategory());
            tag2.setText(recipe.getDifficulty());
            
            // Use Glide for loading images (supports both local resources and URLs)
            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                if (recipe.getImage().startsWith("http")) {
                    Glide.with(itemView.getContext())
                            .load(recipe.getImage())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(image);
                } else {
                    int resId = itemView.getContext().getResources().getIdentifier(
                            recipe.getImage(), "drawable", itemView.getContext().getPackageName());
                    Glide.with(itemView.getContext())
                            .load(resId != 0 ? resId : R.drawable.ic_launcher_background)
                            .into(image);
                }
            } else {
                image.setImageResource(R.drawable.ic_launcher_background);
            }
            
            updateFavoriteIcon(isFavorite);

            itemView.setOnClickListener(v -> {
                v.animate().scaleX(0.98f).scaleY(0.98f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    listener.onRecipeClick(recipe);
                }).start();
            });

            favoriteIcon.setOnClickListener(v -> {
                v.animate().scaleX(1.3f).scaleY(1.3f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                }).start();
                listener.onFavoriteToggle(recipe, !isFavorite);
            });
        }

        private void updateFavoriteIcon(boolean isFavorite) {
            if (isFavorite) {
                favoriteIcon.setImageResource(android.R.drawable.btn_star_big_on);
                favoriteIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.primary_orange));
            } else {
                favoriteIcon.setImageResource(android.R.drawable.btn_star_big_off);
                favoriteIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.gray_medium));
            }
        }
    }
}
