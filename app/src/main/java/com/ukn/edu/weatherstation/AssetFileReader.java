package com.ukn.edu.weatherstation;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class AssetFileReader {
    public String readAssetFile(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        StringBuilder stringBuilder = new StringBuilder();

        try{
            InputStream inputStream = assetManager.open(fileName);
            byte[] buffer = new byte[1024];
            int byteRead;

            while ((byteRead = inputStream.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, byteRead));
            }

            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }
}
