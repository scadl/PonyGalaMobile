package net.scadsdnd.ponygala;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        WebRequest artWebRq = new WebRequest();
        WebProcessor artsWebProc = new WebProcessor();

        artWebRq.UIContext = this;
        artWebRq.regCb(artsWebProc);
        artWebRq.StatusUI = (TextView) findViewById(R.id.tvStatus);

        artsWebProc.UIContext = this;
        artsWebProc.OutputView = findViewById(R.id.gvArts);

        TextView tv = (TextView) findViewById(R.id.tvStatus);
        tv.setText(getText(R.string.load_start));

        artWebRq.execute(
                2,
                Integer.parseInt(
                        getIntent().getStringExtra("catId")
                )
        );

    }
}