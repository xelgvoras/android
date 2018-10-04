package net.victium.xelg.notatry.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.victium.xelg.notatry.R;

public class DuskLayersAdapter extends RecyclerView.Adapter<DuskLayersAdapter.DuskViewHolder> {
    private Context mContext;

    public DuskLayersAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public DuskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_dusk_layer, parent, false);
        return new DuskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DuskViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class DuskViewHolder extends RecyclerView.ViewHolder {
        TextView duskLayerTextView;
        TextView duskRoundTextView;
        TextView duskTimeTextView;

        DuskViewHolder(View itemView) {
            super(itemView);
            duskLayerTextView = itemView.findViewById(R.id.tv_dusk_layer);
            duskRoundTextView = itemView.findViewById(R.id.tv_dusk_rounds);
            duskTimeTextView = itemView.findViewById(R.id.tv_dusk_time);
        }
    }
}
