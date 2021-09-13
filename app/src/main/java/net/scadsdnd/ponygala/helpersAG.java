package net.scadsdnd.ponygala;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class helpersAG {
    public Bitmap loadImage(String in_url){
        Bitmap bmImage = null;
        try {
            URL myURL = new URL(in_url);
            URLConnection myConn = myURL.openConnection();
            myConn.connect();
            InputStream inStream = myConn.getInputStream();
            BufferedInputStream buffInStream = new BufferedInputStream(inStream);
            bmImage = BitmapFactory.decodeStream(buffInStream);
            buffInStream.close();
            inStream.close();
        } catch (IOException e){
            Log.e("IMG_ERR", e.getLocalizedMessage());
        }
        return bmImage;

    }
}
