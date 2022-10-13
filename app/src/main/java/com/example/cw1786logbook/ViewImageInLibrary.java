package com.example.cw1786logbook;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewImageInLibrary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_in_library);

        Button btnNext = findViewById(R.id.btnLibNext);
        Button btnPrev = findViewById(R.id.btnLibPrev);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        List<String> imageUris = dbHelper.getUris();

        AtomicInteger index = new AtomicInteger(0);

        if(imageUris.size() <= 1){
            btnNext.setVisibility(View.INVISIBLE);
            btnPrev.setVisibility(View.INVISIBLE);
        } else{
            btnNext.setVisibility(View.VISIBLE);
            btnPrev.setVisibility(View.INVISIBLE);
        }

        btnPrev.setOnClickListener(v -> {
            if(index.get() > 0){
                index.getAndDecrement();
                btnPrev.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
            }
            if(index.get() == 0){
                btnPrev.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.VISIBLE);
            }
            showUrl(imageUris, index);
        });

        btnNext.setOnClickListener(v -> {
            if(index.get() < imageUris.size() - 1){
                index.getAndIncrement();
                showUrl(imageUris, index);
                btnPrev.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
            } if(index.get() == imageUris.size() - 1){
                btnNext.setVisibility(View.INVISIBLE);
            }
        });
        showUrl(imageUris, index);
    }

    private void showUrl(List<String> imageUris, AtomicInteger index) {
        ImageView imageView = findViewById(R.id.libImageView);
        if(imageUris.size() > 0){
            try {
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file:///" + imageUris.get(index.get()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}