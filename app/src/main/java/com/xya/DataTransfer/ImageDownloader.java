package com.xya.DataTransfer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xya.ErrorSolution.SnackbarSolution;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ubuntu on 15-1-25.
 */
public class ImageDownloader {
    private Context context;
    private URL url;
    private HttpURLConnection connection;

    public ImageDownloader(Context context) {
        this.context = context;
    }

    public Bitmap getBitmap(String url) {
        SnackbarSolution solution = new SnackbarSolution(context);
        try {
            this.url = new URL(url);
            connection = (HttpURLConnection) this.url.openConnection();
            connection.setConnectTimeout(6 * 1000);
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (IOException e) {
            solution.SnackbarSolution(8);
        }

        return null;
    }


}
