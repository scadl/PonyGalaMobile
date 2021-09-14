package net.scadsdnd.ponygala;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImageFull extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_full);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvTitle.setText(getIntent().getStringExtra("imgTitle"));
        tvAuthor.setText(getIntent().getStringExtra("imgAuthor"));
        
        artRequest imgFullRQ = new artRequest();
        imgFullRQ.outputProgress = (ProgressBar) findViewById(R.id.pbFull);
        imgFullRQ.outputImgView = (ImageView) findViewById(R.id.ivFull);
        imgFullRQ.execute(getIntent().getStringExtra("imgFull"));
        
    }
}