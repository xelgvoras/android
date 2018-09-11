package net.victium.xelg.notatry.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import net.victium.xelg.notatry.data.Shield;

import java.util.ArrayList;
import java.util.List;

public class ShieldArrayAdapter extends ArrayAdapter<Shield> {

    private Context mContext;
    private List<Shield> shieldList = new ArrayList<>();

    public ShieldArrayAdapter(@NonNull Context context, ArrayList<Shield> list) {
        super(context, 0, list);
        mContext = context;
        shieldList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if (null == listItem) {
            listItem = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        Shield currentShield = shieldList.get(position);

        CheckedTextView checkedTextView = listItem.findViewById(android.R.id.text1);
        checkedTextView.setText(currentShield.toString());

        return listItem;
    }
}