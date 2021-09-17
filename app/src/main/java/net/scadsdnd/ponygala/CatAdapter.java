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

        artRequest utilAg = new artRequest();
        utilAg.outputImgView = imgThumbs;
        utilAg.outputProgress = pbThumbs;
        utilAg.retryLoad = false;
        utilAg.executeOnExecutor(
                AsyncTask.SERIAL_EXECUTOR,
                catData.get("img_0")[pos],
                catData.get("img_1")[pos],
                catData.get("img_2")[pos],
                catData.get("img_3")[pos],
                catData.get("img_4")[pos]
        );

        //notifyDataSetChanged();
        return view;
    }

}
