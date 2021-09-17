package net.scadsdnd.ponygala;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImageActivity extends Activity {

    private int index = 0;
    private int max_index = 0;

    private void loadImage(int index){

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);

        tvTitle.setText(getIntent().getStringArrayListExtra("imgTitle").get(index));
        tvAuthor.setText(getIntent().getStringArrayListExtra("imgAuthor").get(index));

        artRequest imgFullRQ = new artRequest();

        ProgressBar pbLoad = (ProgressBar) findViewById(R.id.pbFull);
        ImageView ivLoad = (ImageView) findViewById(R.id.ivFull);
        pbLoad.setVisibility(View.VISIBLE);
        ivLoad.setImageBitmap(
                BitmapFactory.decodeResource(
                        this.getResources(),
                        android.R.drawable.ic_popup_sync)
        );

        imgFullRQ.retryLoad = true;
        imgFullRQ.outputProgress = new ProgressBar[] {pbLoad};
        imgFullRQ.outputImgView = new ImageView[] {ivLoad};
        imgFullRQ.executeOnExecutor(
                AsyncTask.SERIAL_EXECUTOR,
                getIntent().getStringArrayListExtra("imgFull").get(index)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        index = getIntent().getIntExtra("imgIndex", 0);
        max_index = getIntent().getIntExtra("imgMaxInd", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_full);

        loadImage( index );

        ImageButton btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(index < max_index-1) {
                    index++;
                    loadImage(index);
                }
            }
        });

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index > -1) {
                    index--;
                    loadImage(index);
                }
            }
        });

        
    }
}