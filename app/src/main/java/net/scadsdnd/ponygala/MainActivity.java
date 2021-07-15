package net.scadsdnd.ponygala;

import android.app.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity 
{

    Integer lvl = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
                Log.i("lvl", Integer.toString(lvl));
                break;
        }

    }
}
