package net.victium.xelg.notatry.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract.*;

public class ShieldListAdapter extends RecyclerView.Adapter<ShieldListAdapter.ShieldViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public ShieldListAdapter(Cursor mCursor, Context mContext) {
        this.mCursor = mCursor;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ShieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.active_shield_item, parent, false);
        return new ShieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShieldViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position))
            return;

        String name = mCursor.getString(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_NAME));
        int cost = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_COST));
        int magDefence = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE));
        int physDefence = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE));
        int mentDefence = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE));
        int range = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_RANGE));
        long id = mCursor.getLong(mCursor.getColumnIndex(ActiveShieldsEntry._ID));

        holder.shieldNameTextView.setText(name);
        holder.shieldCostTextView.setText(String.valueOf(cost));
        holder.magicDefenceTextView.setText(String.valueOf(magDefence));
        holder.physicDefenceTextView.setText(String.valueOf(physDefence));
        holder.mentalDefenceTextView.setText(String.valueOf(mentDefence));
        holder.shieldRangeTextView.setText(String.valueOf(range));

        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {

        if (null != mCursor) mCursor.close();
        mCursor = newCursor;

        if (null != newCursor) {
            this.notifyDataSetChanged();
        }
    }

    public class ShieldViewHolder extends RecyclerView.ViewHolder {

        TextView shieldNameTextView;
        TextView shieldCostTextView;
        TextView magicDefenceTextView;
        TextView physicDefenceTextView;
        TextView mentalDefenceTextView;
        TextView shieldRangeTextView;

        public ShieldViewHolder(View itemView) {
            super(itemView);
            shieldNameTextView = itemView.findViewById(R.id.tv_shield_name);
            shieldCostTextView = itemView.findViewById(R.id.tv_shield_cost);
            magicDefenceTextView = itemView.findViewById(R.id.tv_magic_defence);
            physicDefenceTextView = itemView.findViewById(R.id.tv_physic_defence);
            mentalDefenceTextView = itemView.findViewById(R.id.tv_mental_defence);
            shieldRangeTextView = itemView.findViewById(R.id.tv_shield_range);
        }
    }
}
