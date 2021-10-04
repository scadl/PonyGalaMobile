package net.scadsdnd.ponygala;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageActivity extends AppCompatActivity {

    private int index = 0;
    private int max_index = 0;

    private caheDB dbh;
    private SQLiteDatabase db;

    private boolean isAdmin;

    private float mScale = 1f;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector gestureDetector;
    private String externalSRC;



    private void loadImage(int index){

        String[] rowCols = {dbh.COLS[0],dbh.COLS[1], dbh.COLS[2], dbh.COLS[3], dbh.COLS[4], dbh.COLS[5]};
        String[] dbParams = { String.valueOf(index) };
        Cursor dbCursor = db.query(dbh.TAB, rowCols, dbh.COLS[0]+" = ?", dbParams, null, null, null);
        dbCursor.moveToNext();

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);

        tvTitle.setText(dbCursor.getString(2));
        tvAuthor.setText(dbCursor.getString(3));
        externalSRC = dbCursor.getString(4);

        // WebView way
        WebView wvImg = (WebView) findViewById(R.id.wvFull);
        wvImg.loadUrl(dbCursor.getString(1));

        // Fit image params
        wvImg.getSettings().setLoadWithOverviewMode(true);
        wvImg.getSettings().setUseWideViewPort(true);

        // Srooll & Zoom controls
        wvImg.getSettings().setBuiltInZoomControls(true);
        wvImg.getSettings().setDisplayZoomControls(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbh = new caheDB(this);
        db = dbh.getReadableDatabase();

        index = getIntent().getIntExtra("imgIndex", 0);
        max_index = getIntent().getIntExtra("imgMaxInd", 0);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

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
                if(index > 0) {
                    index--;
                    loadImage(index);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full, menu);

        if(isAdmin){
            menu.findItem(R.id.mMoveFull).setVisible(true);
            menu.findItem(R.id.mDeleteFull).setVisible(true);
        } else {
            menu.findItem(R.id.mMoveFull).setVisible(false);
            menu.findItem(R.id.mDeleteFull).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //WebRequest artWebRq = new WebRequest();

        //artWebRq.UIContext = this;
        //artWebRq.pbIndicator = null;

        switch (item.getItemId()) {
            case R.id.mReloadFull:
                    loadImage(index);
                break;
            case R.id.mOpenOrgnal:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalSRC));
                startActivity(browserIntent);
                break;
            case R.id.mMoveFull:


                //artWebRq.execute(8, aid, catid);

                //Intent intent = new Intent();
                //intent.putExtra("artsChanged", true);
                //setResult(RESULT_OK, intent);

                Toast.makeText(ImageActivity.this, R.string.wip_func, Toast.LENGTH_SHORT).show();

                break;
            case R.id.mDeleteFull:

                //artWebRq.execute(9, aid, catid);

                //Intent intent2 = new Intent();
                //intent2.putExtra("artsChanged", true);
                //setResult(RESULT_OK, intent2);

                Toast.makeText(ImageActivity.this, R.string.wip_func, Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }





}