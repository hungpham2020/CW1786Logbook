package com.example.cw1786logbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    ProgressDialog mProcessDialog;
    private final int REQUIRED_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[] {"android.permission.CAMERA", "android.permission.RECORD_AUDIO"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText inputUrl = findViewById(R.id.inputUrl);
        Button btnAddUrl = findViewById(R.id.btnAddUrl);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnCamera = findViewById(R.id.btnOpenCamera);
        TextView txtPage = findViewById(R.id.txtPage);

        if(!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUIRED_CODE_PERMISSIONS);
        }
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        List<String> imageUrls = dbHelper.getUrls();

        if(imageUrls.size() <= 1){
            btnNext.setVisibility(View.INVISIBLE);
            btnPrev.setVisibility(View.INVISIBLE);
        } else{
            btnNext.setVisibility(View.VISIBLE);
            btnPrev.setVisibility(View.INVISIBLE);
        }

        AtomicInteger index = new AtomicInteger(0);

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
            txtPage.setText(String.valueOf(index.get() + 1));
            showUrl(imageUrls, index);
        });

        btnNext.setOnClickListener(v -> {
            if(index.get() < imageUrls.size() - 1){
                index.getAndIncrement();
                showUrl(imageUrls, index);
                btnPrev.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
            } if(index.get() == imageUrls.size() - 1){
                btnNext.setVisibility(View.INVISIBLE);
            }
            txtPage.setText(String.valueOf(index.get() + 1));
        });

        if(imageUrls.size() > 0){
            showUrl(imageUrls, index);
            txtPage.setText(String.valueOf(index.get() + 1));
        }

        btnAddUrl.setOnClickListener(v -> {
            dbHelper.insertUrl(inputUrl.getText().toString());
            inputUrl.setText("");
            Restart();
        });

        btnCamera.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Camera.class);
            startActivity(intent);
        });
    }

    private void showUrl(List<String> imageUrls, AtomicInteger index) {
        ImageView imageView = findViewById(R.id.imageView);

        DownLoadImageTask task = new DownLoadImageTask(mProcessDialog, this, imageView);

        task.execute(imageUrls.get(index.get()));
    }

    private boolean allPermissionsGranted(){
        for(String permission: REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        switch (requestCode){
            case REQUIRED_CODE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(MainActivity .this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
    
    public void Restart(){
        this.recreate();
    }
}