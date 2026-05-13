package com.example.cookify_by_me.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookify_by_me.R;
import com.example.cookify_by_me.models.Recipe;
import java.util.List;

public class HorizontalRecipeAdapter extends RecyclerView.Adapter<HorizontalRecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public HorizontalRecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.title.setText(recipe.getName());
        holder.desc.setText(recipe.getDescription());
        holder.rating.setText(recipe.getRating().split(" ")[0]);
        
        // Dynamic image loading
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    recipe.getImage(), "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.image.setImageResource(resId);
            } else {
                holder.image.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                listener.onRecipeClick(recipe);
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, rating;
        ImageView image;

        ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.recipeTitleH);
            desc = v.findViewById(R.id.recipeDescH);
            rating = v.findViewById(R.id.recipeRatingH);
            image = v.findViewById(R.id.recipeImageH);
        }
    }
}
