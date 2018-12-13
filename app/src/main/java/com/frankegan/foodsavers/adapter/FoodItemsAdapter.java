package com.frankegan.foodsavers.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frankegan.foodsavers.R;
import com.frankegan.foodsavers.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class FoodItemsAdapter extends FireStoreAdapter<FoodItemsAdapter.ViewHolder> {

    public interface OnFoodSelectedListener {
        void onFoodSelected(DocumentSnapshot postModel);
    }

    private OnFoodSelectedListener mListener;

    protected FoodItemsAdapter(Query query, OnFoodSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.food_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.food_address)
        TextView foodAddressView;
        @BindView(R.id.food_producerName)
        TextView foodProducerNameView;
        @BindView(R.id.food_items)
        TextView foodItemsView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(
                final DocumentSnapshot snapshot,
                final OnFoodSelectedListener listener
        ) {
            Post postModel = snapshot.toObject(Post.class);

            foodAddressView.setText(postModel.getAddress());
            List<String> foodList = (postModel).getTags();
            String foodString = " ";
            if (foodList != null) {
                foodString = TextUtils.join(", ", foodList);
            }
            foodProducerNameView.setText(foodString);

            foodItemsView.setText(postModel.getDescription());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onFoodSelected(snapshot);
                    }
                }
            });
        }

    }
}
