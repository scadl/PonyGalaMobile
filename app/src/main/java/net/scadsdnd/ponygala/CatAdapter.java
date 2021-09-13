package net.scadsdnd.ponygala;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class CatAdapter extends ArrayAdapter<String> {

    public Map<String, String[]> catData;
    public TextView statusUI;

    public CatAdapter(Context cont, String[] in_array, Map<String, String[]> allSrvData){
        super(cont, R.layout.cat_entry, in_array);
        this.catData = allSrvData;
        this.statusUI = null;
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

        helpersAG utilAg = new helpersAG();

        if (Integer.valueOf(this.catData.get("counters")[pos])>5) {
            for(int i=0; i<5; i++){
                imgThumbs[i].setImageBitmap(
                        utilAg.loadImage(this.catData.get("img_"+i)[pos])
                );
            }
        }
        return view;
    }


}
