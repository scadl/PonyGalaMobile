package net.scadsdnd.ponygala;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImageActivity extends Activity {

    private int index = 0;
    private int max_index = 0;

    private caheDB dbh;
    private SQLiteDatabase db;

    private void loadImage(int index){

        String[] rowCols = {dbh.COLS[0],dbh.COLS[1], dbh.COLS[2], dbh.COLS[3]};
        String[] dbParams = { String.valueOf(index) };
        Cursor dbCursor = db.query(dbh.TAB, rowCols, dbh.COLS[0]+" = ?", dbParams, null, null, null);
        dbCursor.moveToNext();


        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);

        tvTitle.setText(dbCursor.getString(2));
        tvAuthor.setText(dbCursor.getString(3));

        artRequest imgFullRQ = new artRequest();

        final ImageView ivLoad = (ImageView) findViewById(R.id.ivFull);
        ProgressBar pbLoad = (ProgressBar) findViewById(R.id.pbFull);

        pbLoad.setVisibility(View.VISIBLE);
        ivLoad.setImageBitmap(
                BitmapFactory.decodeResource(
                        this.getResources(),
                        android.R.drawable.ic_popup_sync)
        );

        imgFullRQ.retryLoad = true;
        imgFullRQ.outputProgress = new ProgressBar[] {pbLoad};
        imgFullRQ.outputImgView = new ImageView[] {ivLoad};
        imgFullRQ.scaleType = ImageView.ScaleType.FIT_CENTER;
        imgFullRQ.executeOnExecutor(
                AsyncTask.SERIAL_EXECUTOR,
                dbCursor.getString(1)
        );

        //??? https://stackoverflow.com/questions/16557076/how-to-smoothly-move-a-image-view-with-users-finger-on-android-emulator
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbh = new caheDB(this);
        db = dbh.getReadableDatabase();

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

        ImageButton btnReload = (ImageButton) findViewById(R.id.btnReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(index);
            }
        });

        ImageButton btnFit = (ImageButton) findViewById(R.id.btnFit);
        btnFit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView ivLoad = (ImageView) findViewById(R.id.ivFull);
                if(ivLoad.getScaleType() == ImageView.ScaleType.FIT_CENTER) {
                    ivLoad.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    ivLoad.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

            }
        });
        
    }
}