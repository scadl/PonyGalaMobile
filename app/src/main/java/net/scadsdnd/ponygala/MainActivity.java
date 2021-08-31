package net.scadsdnd.ponygala;

import android.app.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.scadsdnd.ponygala.WebRequest;

import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity 
{

    Integer lvl = 0;
    Boolean isAdmin = false;
    ListView catList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        StrictMode.ThreadPolicy mypolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(mypolicy);

        SharedPreferences shPrf = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        isAdmin = shPrf.getBoolean("admin_mode", false);

        WebRequest wr1 = new WebRequest();
        wr1.UIContext = this;
        wr1.OutputView = (ListView) findViewById(R.id.catListView);
        wr1.execute("act=1");

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        lvl-=1;
        switch (lvl){
            case -1:
                Toast.makeText(MainActivity.this, "Нажмите ещё раз 'Назад', чтобы выйти.", Toast.LENGTH_LONG).show();
                break;
            case -2:
                finish();
                System.exit(0);
                break;
            default:
                Log.i("SYS", "Level: " + Integer.toString(lvl));
                break;
        }

    }
}
