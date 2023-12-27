package com.example.recipesforeverytaste.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesforeverytaste.Models.Ingredient;
import com.example.recipesforeverytaste.R;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    Context context;
    ArrayList<Ingredient> ingredientList;

    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }


    //Какой дизайн
    @NonNull
    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ingredientItem = LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false);

        return new IngredientAdapter.IngredientViewHolder(ingredientItem);
    }

    //Взаимодействие с объектом и его элементами
    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.IngredientViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Ingredient ingredient = ingredientList.get(position);
        holder.ingredientName.setText(ingredient.getIngredientName());

        holder.btnDeleteIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    //Элементы с объекта с которыми работаем
    public static final class IngredientViewHolder extends RecyclerView.ViewHolder{

        TextView ingredientName;
        ImageButton btnDeleteIngredient;
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            ingredientName = itemView.findViewById(R.id.ingredientName);
            btnDeleteIngredient = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
