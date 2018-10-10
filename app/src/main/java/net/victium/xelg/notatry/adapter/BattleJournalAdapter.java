package net.victium.xelg.notatry.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract;

public class BattleJournalAdapter extends RecyclerView.Adapter<BattleJournalAdapter.BattleJournalViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public BattleJournalAdapter(Cursor mCursor, Context mContext) {
        this.mCursor = mCursor;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BattleJournalAdapter.BattleJournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_battle_journal_message, parent, false);
        return new BattleJournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BattleJournalAdapter.BattleJournalViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position))
            return;

        String attackMessage = mCursor.getString(mCursor.getColumnIndex(NotATryContract.BattleJournalEntry.COLUMN_ATTACK_MESSAGE));
        String resultMessage = mCursor.getString(mCursor.getColumnIndex(NotATryContract.BattleJournalEntry.COLUMN_RESULT_MESSAGE));
        String systemMessage = mCursor.getString(mCursor.getColumnIndex(NotATryContract.BattleJournalEntry.COLUMN_SYSTEM_MESSAGE));

        ViewGroup.LayoutParams paramsAttackMessage = holder.attackMessageTextView.getLayoutParams();
        ViewGroup.LayoutParams paramsResultMessage = holder.resultMessageTextView.getLayoutParams();
        ViewGroup.LayoutParams paramsSystemMessage = holder.systemMessageTextView.getLayoutParams();

        if (attackMessage == null || attackMessage.length() == 0) {
            paramsAttackMessage.height = 0;
            holder.attackMessageTextView.setVisibility(View.INVISIBLE);
            holder.attackMessageTextView.setLayoutParams(paramsAttackMessage);
        } else {
            holder.attackMessageTextView.setText(attackMessage);
        }

        if (resultMessage == null || resultMessage.length() == 0) {
            paramsResultMessage.height = 0;
            holder.resultMessageTextView.setVisibility(View.INVISIBLE);
            holder.resultMessageTextView.setLayoutParams(paramsResultMessage);
        } else {
            holder.resultMessageTextView.setText(resultMessage);
        }

        if (systemMessage == null || systemMessage.length() == 0) {
            paramsSystemMessage.height = 0;
            holder.systemMessageTextView.setVisibility(View.INVISIBLE);
            holder.systemMessageTextView.setLayoutParams(paramsSystemMessage);
        } else {
            holder.systemMessageTextView.setText(systemMessage);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {

        if (mCursor != null) mCursor.close();
        mCursor = newCursor;

        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class BattleJournalViewHolder extends RecyclerView.ViewHolder {

        TextView attackMessageTextView;
        TextView resultMessageTextView;
        TextView systemMessageTextView;

        BattleJournalViewHolder(View itemView) {
            super(itemView);
            attackMessageTextView = itemView.findViewById(R.id.tv_attack_message);
            resultMessageTextView = itemView.findViewById(R.id.tv_result_message);
            systemMessageTextView = itemView.findViewById(R.id.tv_system_message);
        }
    }
}
