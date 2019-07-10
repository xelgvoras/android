package net.victium.xelg.notatry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.victium.xelg.notatry.database.ShieldEntry;

import java.util.List;

public class ShieldArrayAdapter extends ArrayAdapter<ShieldEntry> {

    private Context mContext;
    private List shieldList;

    public ShieldArrayAdapter(@NonNull Context context, List<ShieldEntry> list) {
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

        ShieldEntry currentShield = (ShieldEntry) shieldList.get(position);

        CheckedTextView checkedTextView = listItem.findViewById(android.R.id.text1);
        checkedTextView.setText(currentShield.toString());

        return listItem;
    }
}
