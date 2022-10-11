package com.example.cw1786logbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Camera extends AppCompatActivity implements ImageAnalysis.Analyzer{

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;

    private Executor executor = Executors.newSingleThreadExecutor();
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = findViewById(R.id.imageView);
        previewView = findViewById(R.id.previewView);
        Button btn = findViewById(R.id.btnTakePic);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        startCamera();

        btn.setOnClickListener(v -> {
            takPicture();
        });

        List<String> imageUris = dbHelper.getUris();

        try {
            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file:///" + imageUris.get(0))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void takPicture() {
        long timestamp = System.currentTimeMillis();
        ImageCapture.OutputFileOptions option1 = new ImageCapture.OutputFileOptions
                .Builder(new File(getApplicationContext().getFilesDir(),
                String.valueOf(timestamp))).build();
        imageCapture.takePicture(option1, executor, new ImageCapture.OnImageSavedCallback(){

            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> {
                    final Uri selectedImage = outputFileResults.getSavedUri();
                    DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

                    try {
                        dbHelper.insertUri(outputFileResults.getSavedUri().getPath());
                        imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {

            }
        });
    }

    private void startCamera(){
        final ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
        videoCapture = new VideoCapture.Builder().setVideoFrameRate(30).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(executor, this);

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture, videoCapture);
    }
    @Override
    public void analyze(@NonNull ImageProxy image){
        image.close();
    }
}