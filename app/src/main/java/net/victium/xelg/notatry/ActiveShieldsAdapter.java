package net.victium.xelg.notatry;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.victium.xelg.notatry.data.NotATryContract;

public class ActiveShieldsAdapter extends RecyclerView.Adapter<ActiveShieldsAdapter.ActiveShieldViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public ActiveShieldsAdapter(Context context) {
        this.mContext = context;
    }


    @NonNull
    @Override
    public ActiveShieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.active_shield_item, parent, false);

        return new ActiveShieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveShieldViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        int shieldNameIndex = mCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_SHIELD_NAME);
        int costIndex = mCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST);
        int magDefenceIndex = mCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE);
        int physDefenceIndex = mCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE);
        int mentalDefenceIndex = mCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE);
        int rangeIndex = mCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_RANGE);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String shieldName = mCursor.getString(shieldNameIndex);
        int cost = mCursor.getInt(costIndex);
        int magDefence = mCursor.getInt(magDefenceIndex);
        int physDefence = mCursor.getInt(physDefenceIndex);
        int mentalDefence = mCursor.getInt(mentalDefenceIndex);
        int range = mCursor.getInt(rangeIndex);

        holder.itemView.setTag(id);
        holder.nameView.setText(shieldName);
        holder.costView.setText(String.valueOf(cost));
        holder.magDefView.setText(String.valueOf(magDefence));
        holder.physDefView.setText(String.valueOf(physDefence));
        holder.mentalView.setText(String.valueOf(mentalDefence));
        holder.rangeView.setText(String.valueOf(range));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (null != c) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public class ActiveShieldViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView costView;
        TextView magDefView;
        TextView physDefView;
        TextView mentalView;
        TextView rangeView;

        public ActiveShieldViewHolder(View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.tv_active_shield_name);
            costView = itemView.findViewById(R.id.tv_active_shield_cost);
            magDefView = itemView.findViewById(R.id.tv_active_shield_mag_defence);
            physDefView = itemView.findViewById(R.id.tv_active_shield_phys_defence);
            mentalView = itemView.findViewById(R.id.tv_active_shield_mental_defence);
            rangeView = itemView.findViewById(R.id.tv_active_shield_range);
        }
    }
}
