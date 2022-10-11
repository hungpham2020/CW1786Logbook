package com.example.cw1786logbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownLoadImageTask extends AsyncTask {

    ProgressDialog mProgressDialog;
    Context context;

    public DownLoadImageTask(ProgressDialog mProgressDialog, Context context, ImageView image) {
        this.mProgressDialog = mProgressDialog;
        this.context = context;
        this.image = image;
    }

    ImageView image;

    @Override
    protected  Object doInBackground(Object[] objects){
        String ImageURL = (String) objects[0];
        Bitmap bitmap = null;
        try {
            InputStream input = new java.net.URL(ImageURL).openStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Download Image tutor");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        image.setImageBitmap((Bitmap) o);
        mProgressDialog.dismiss();
    }
}
