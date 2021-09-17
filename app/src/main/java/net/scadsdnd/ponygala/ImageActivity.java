package net.scadsdnd.ponygala;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImageActivity extends Activity {

    private void loadImage(){
        artRequest imgFullRQ = new artRequest();
        imgFullRQ.outputProgress = new ProgressBar[] {(ProgressBar) findViewById(R.id.pbFull)};
        imgFullRQ.outputImgView = new ImageView[] {(ImageView) findViewById(R.id.ivFull)};
        imgFullRQ.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getIntent().getStringExtra("imgFull"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_full);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvTitle.setText(getIntent().getStringExtra("imgTitle"));
        tvAuthor.setText(getIntent().getStringExtra("imgAuthor"));
        
        loadImage();
        
    }
}