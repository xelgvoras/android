package net.victium.xelg.notatry.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract.*;
import net.victium.xelg.notatry.database.ShieldEntry;

import java.util.List;

public class ShieldListAdapter extends RecyclerView.Adapter<ShieldListAdapter.ShieldViewHolder> {

    private final ItemClickListener mClickHandler;

    private Context mContext;
    private List<ShieldEntry> mShieldEntries;

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public ShieldListAdapter(Context mContext, ItemClickListener listener) {
        this.mContext = mContext;
        mClickHandler = listener;
    }

    @NonNull
    @Override
    public ShieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_active_shield, parent, false);

        return new ShieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShieldViewHolder holder, int position) {

        /*if (!mCursor.moveToPosition(position))
            return;

        String name = mCursor.getString(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_NAME));
        int cost = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_COST));
        int magDefence = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE));
        int physDefence = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE));
        int mentDefence = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE));
        int range = mCursor.getInt(mCursor.getColumnIndex(ActiveShieldsEntry.COLUMN_RANGE));
        long id = mCursor.getLong(mCursor.getColumnIndex(ActiveShieldsEntry._ID));*/

        ShieldEntry shieldEntry = mShieldEntries.get(position);
        String name = shieldEntry.getName();
        int power = shieldEntry.getPower();
        int magicDefence = shieldEntry.getMagicDefence();
        int physicDefence = shieldEntry.getPhysicDefence();
        int mentalDefence = shieldEntry.getMentalDefence();
        int range = shieldEntry.getRange();

        holder.shieldNameTextView.setText(name);
        holder.shieldCostTextView.setText(String.valueOf(power));
        holder.magicDefenceTextView.setText(String.valueOf(magicDefence));
        holder.physicDefenceTextView.setText(String.valueOf(physicDefence));
        holder.mentalDefenceTextView.setText(String.valueOf(mentalDefence));
        holder.shieldRangeTextView.setText(String.valueOf(range));
    }

    @Override
    public int getItemCount() {
        if (mShieldEntries == null) {
            return 0;
        }
        return mShieldEntries.size();
    }

    public List<ShieldEntry> getShields() {
        return mShieldEntries;
    }

    public void setShields(List<ShieldEntry> shieldEntries) {
        mShieldEntries = shieldEntries;
        notifyDataSetChanged();
    }

    /*public void swapCursor(Cursor newCursor) {

        if (null != mCursor) mCursor.close();
        mCursor = newCursor;

        if (null != newCursor) {
            this.notifyDataSetChanged();
        }
    }*/

    class ShieldViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView shieldNameTextView;
        TextView shieldCostTextView;
        TextView magicDefenceTextView;
        TextView physicDefenceTextView;
        TextView mentalDefenceTextView;
        TextView shieldRangeTextView;

        ShieldViewHolder(View itemView) {
            super(itemView);
            shieldNameTextView = itemView.findViewById(R.id.tv_shield_name);
            shieldCostTextView = itemView.findViewById(R.id.tv_shield_cost);
            magicDefenceTextView = itemView.findViewById(R.id.tv_magic_defence);
            physicDefenceTextView = itemView.findViewById(R.id.tv_physic_defence);
            mentalDefenceTextView = itemView.findViewById(R.id.tv_mental_defence);
            shieldRangeTextView = itemView.findViewById(R.id.tv_shield_range);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mShieldEntries.get(getAdapterPosition()).getId();
            mClickHandler.onItemClickListener(elementId);
        }
    }
}
