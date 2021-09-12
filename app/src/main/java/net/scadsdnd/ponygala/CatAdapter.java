package net.scadsdnd.ponygala;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CatAdapter extends ArrayAdapter<String> {

    public String[] catNums;

    public CatAdapter(Context cont, String[] objs, String[] nums){
        super(cont, R.layout.cat_entry, objs);
        this.catNums = nums;
    }

    public View getView(int pos, View contVw, ViewGroup vG){

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.cat_entry, vG, false);

        TextView textView = (TextView) view.findViewById(R.id.CatName);
        textView.setText(getItem(pos));

        TextView textView1 = (TextView) view.findViewById(R.id.CatNum);
        textView1.setText(this.catNums[pos]);

        return view;
    }
}
