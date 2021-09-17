package net.scadsdnd.ponygala;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private caheDB dbh = new caheDB(this);
    private SQLiteDatabase db = dbh.getReadableDatabase();
    String[] rowCols = {dbh.COLS[0],dbh.COLS[1], dbh.COLS[2], dbh.COLS[3]};


    private void loadImage(int index){

        Cursor dbCursor = db.query(dbh.TAB, rowCols, dbh.COLS[0]+"="+index, null, null, null, null);


        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);

        tvTitle.setText(dbCursor.getString(2));
        tvAuthor.setText(dbCursor.getString(3));

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
                dbCursor.getString(1)
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