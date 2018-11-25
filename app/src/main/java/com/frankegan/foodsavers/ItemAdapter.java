package com.frankegan.foodsavers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.frankegan.foodsavers.model.Post;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Post> {
    // View lookup cache
    private static class ViewHolder {
        TextView outlet_name;
        TextView description;
        TextView start_time;
        TextView end_time;
        TextView distance;
        TextView rating;
    }

    public ItemAdapter(Context context, ArrayList<Post> posts) {
        super(context, R.layout.listitem, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Post post = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem, parent, false);
            viewHolder.outlet_name = (TextView) convertView.findViewById(R.id.outlet_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.start_time = (TextView) convertView.findViewById(R.id.start_time);
            viewHolder.end_time = (TextView) convertView.findViewById(R.id.end_time);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.rating);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        // some dummy data
        viewHolder.outlet_name.setText(post.getProducer());
        viewHolder.description.setText(post.getDescription());
        viewHolder.start_time.setText("6pm");
        viewHolder.end_time.setText("9pm");
        viewHolder.distance.setText(String.valueOf(post.getLocation().getLatitude()));
        viewHolder.rating.setText("4.0");
        // Return the completed view to render on screen
        return convertView;
    }
}