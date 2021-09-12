package net.scadsdnd.ponygala;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(getIntent().getStringExtra("catId"));
    }
}