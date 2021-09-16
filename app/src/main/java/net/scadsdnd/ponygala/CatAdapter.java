package net.scadsdnd.ponygala;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;

public class CatAdapter extends ArrayAdapter<String> {

    public Map<String, String[]> catData;

    public CatAdapter(Context cont, String[] in_array){
        super(cont, R.layout.cat_entry, in_array);
    }

    public View getView(int pos, View contVw, ViewGroup vG){

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.cat_entry, vG, false);

        TextView tvCatTitle = (TextView) view.findViewById(R.id.CatName);
        tvCatTitle.setText(getItem(pos));

        TextView tvCatCount = (TextView) view.findViewById(R.id.CatNum);
        tvCatCount.setText(this.catData.get("counters")[pos]);


        ImageView[] imgThumbs = {
                (ImageView) view.findViewById(R.id.imgCat1),
                (ImageView) view.findViewById(R.id.imgCat2),
                (ImageView) view.findViewById(R.id.imgCat3),
                (ImageView) view.findViewById(R.id.imgCat4),
                (ImageView) view.findViewById(R.id.imgCat5)
        };

        ProgressBar[] pbThumbs = {
                (ProgressBar) view.findViewById(R.id.pbCat1),
                (ProgressBar) view.findViewById(R.id.pbCat2),
                (ProgressBar) view.findViewById(R.id.pbCat3),
                (ProgressBar) view.findViewById(R.id.pbCat4),
                (ProgressBar) view.findViewById(R.id.pbCat5)
        };


            String[] thumbArray = new String[5];
            for (int i = 0; i < 5; i++) {                /// <<<<<<<<<<<<<<< remove cycle.
                if (catData.get("img_" + i)[pos] != null) {

                    artRequest utilAg = new artRequest();
                    utilAg.outputImgView = imgThumbs[i];
                    utilAg.outputProgress = pbThumbs[i];
                    utilAg.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, catData.get("img_" + i)[pos], "cat");

                    thumbArray[i] = catData.get("img_"+i)[pos];
                }
            }
            view.setTag(thumbArray);

        //notifyDataSetChanged();
        return view;
    }

}
