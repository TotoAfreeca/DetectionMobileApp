package com.example.detectionapp.Recycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detectionapp.Api.ApiHandler;
import com.example.detectionapp.FullScreenActivity;
import com.example.detectionapp.R;
import com.example.detectionapp.db.Detection;
import com.example.detectionapp.db.DetectionViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetectionAdapter extends RecyclerView.Adapter<DetectionAdapter.DetectionViewHolder>{


    Context context;
    List<Detection> detections;
    private DetectionViewModel detectionViewModel;
    ApiHandler apiHandler;
    public DetectionAdapter(Context ct, List<Detection> list)
    {
        context=ct;
        detections = list;
    }

    @NonNull
    @Override
    public DetectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.detection_list_element, parent, false);

        apiHandler = new ApiHandler(context);
        detectionViewModel = ViewModelProviders.of((FragmentActivity) context).get(DetectionViewModel.class);
        return new DetectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetectionViewHolder holder, final int position) {

        holder.classname.setText("Class: " + detections.get(position).classname);
        holder.probability.setText("Probability: " + String.valueOf(detections.get(position).probability));

//        Bitmap myBitmap = BitmapFactory.decodeFile(photos.get(position).filepath);
//        holder.image.setImageBitmap(myBitmap);

        Picasso.get()
                .load(detections.get(position).filepath)
                .fit()
                .centerInside()
                .into(holder.image);

        holder.image.setOnClickListener(v->{
            Intent intent = new Intent(context,
                    FullScreenActivity.class);
            intent.putExtra("filepath", detections.get(position).filepath);
            intent.putExtra("filename", detections.get(position).classname);
            context.startActivity(intent);
        });



        holder.deleteButton.setOnClickListener(v ->{
            detectionViewModel.delete(detections.get(position));
        });


        //holder.image.setImageResource(R.drawable.star_icon);
    }



    @Override
    public int getItemCount() {
        if (detections != null)
            return detections.size();
        else
            return 0;
    }

    public void setDetections(List<Detection> detections){
        this.detections = detections;
        notifyDataSetChanged();
    }

    public class DetectionViewHolder extends RecyclerView.ViewHolder{

        TextView classname;
        TextView probability;
        ImageView image;
        ConstraintLayout mainLayout;
        Button deleteButton;
        Button detectButton;


        public DetectionViewHolder(@NonNull View itemView) {
            super(itemView);
            classname = itemView.findViewById(R.id.class_name);
            probability = itemView.findViewById(R.id.probability);
            image = itemView.findViewById(R.id.photo_image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            deleteButton = itemView.findViewById(R.id.button_delete_photo);


        }
    }
}