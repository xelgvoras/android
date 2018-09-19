package net.victium.xelg.notatry.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.NotATryDbHelper;

public class DamageDialogFragment extends DialogFragment implements View.OnClickListener {

    private int mTypeDamage;
    private EditText mDamagePower;
    private SQLiteDatabase mDb;

    public String mResultSummary;

    public interface DamageDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);
    }

    DamageDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (DamageDialogListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        NotATryDbHelper notATryDbHelper = new NotATryDbHelper(getActivity());
        mDb = notATryDbHelper.getWritableDatabase();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.damage_dialog, null);

        mDamagePower = view.findViewById(R.id.et_damage_power);
        RadioButton mMagicDamageRadioButton = view.findViewById(R.id.rb_magic_damage);
        mMagicDamageRadioButton.setOnClickListener(this);
        RadioButton mPhysicDamageRadioButton = view.findViewById(R.id.rb_physic_damage);
        mPhysicDamageRadioButton.setOnClickListener(this);
        RadioButton mMentalDamageRadioButton = view.findViewById(R.id.rb_mental_damage);
        mMentalDamageRadioButton.setOnClickListener(this);

        ((RadioButton) view.findViewById(R.id.rb_magic_damage)).setChecked(true);
        mTypeDamage = 1;

        builder.setTitle("Входящее воздействие")
                .setMessage("Укажите тип и силу воздействия, в случае необходимости - выберете заклинение из списка")
                .setView(view)
                .setPositiveButton("проверить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String input = mDamagePower.getText().toString();

                        if (input.length() == 0) {
                            Toast.makeText(getActivity(), "Не указана сила воздействия", Toast.LENGTH_LONG).show();
                            return;
                        }

                        int damage = Integer.parseInt(input);
                        mResultSummary = checkBattle(damage, mTypeDamage);
                        mListener.onDialogPositiveClick(DamageDialogFragment.this);
                    }
                });

        return builder.create();
    }

    private String checkBattle(int damage, int type) {
        String[] columns;
        String selection;
        String[] selectionArgs;
        String orderBy;
        String typeDamage;
        StringBuilder resultBuilder = new StringBuilder();

        Cursor cursor = getShields(null,null, null, null);
        int columnType = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_TYPE);
        int columnDef;
        int columnDefenceSum;

        switch (type) {
            case 1:
                typeDamage = "маг";
                columnDef = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE);
                break;
            case 2:
                typeDamage = "физ";
                columnDef = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE);
                break;
            case 3:
                typeDamage = "мент";
                columnDef = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE);
                break;
                default:
                    typeDamage = "маг";
                    columnDef = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE);
                    break;
        }

        // Берем из таблицы групповой щит
        selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=?";
        selectionArgs = new String[]{"групповой"};
        cursor.close();
        cursor = getShields(null, selection, selectionArgs, null);

        // Проверка наличия группового щита
        if (cursor.moveToFirst()) {

            if (cursor.getString(columnType).equals(typeDamage) || cursor.getString(columnType).equals("унив")) {

                int shieldId = cursor.getInt(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID));
                resultBuilder.append(cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME)))
                        .append(": ");

                int shield = Integer.parseInt(cursor.getString(columnDef));

                if (damage <= shield) {
                    resultBuilder.append("блок");
                    return resultBuilder.toString();
                } else if (damage <= (2 * shield)) {
                    resultBuilder.append("лопунл\n");
                    delShield(shieldId);
                    damage = damage - shield;
                } else {
                    resultBuilder.append("пробит\n");
                    delShield(shieldId);
                    damage = damage - shield;
                }
            }
        }

        // Берем из таблицы персональные щиты
        columns = new String[] {
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM,
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + ") as " + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM
        };
        selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=? AND " +
                "(" + NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?)";
        selectionArgs = new String[] {"персональный", typeDamage, "унив"};
        cursor.close();
        cursor = getShields(columns, selection, selectionArgs, null);

        switch (type) {
            case 1:
                columnDefenceSum = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM);
                break;
            case 2:
                columnDefenceSum = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM);
                break;
            case 3:
                columnDefenceSum = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM);
                break;
                default:
                    columnDefenceSum = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM);
        }

        // Проверяем, есть ли хоть какая-то защита?
        if (cursor.moveToFirst()) {

            resultBuilder.append("Персональные щиты: ");

            int shield = cursor.getInt(columnDefenceSum);

            if (damage <= shield) {
                resultBuilder.append("блок\n").append("Вы выжили, заклинание на вас не подействовало");
                return resultBuilder.toString();
            } else if (damage <= (2 * shield)) {
                resultBuilder.append("лопнули\n").append("Щиты лопнули, но вы не пострадали");
                cursor.close();
                mDb.delete(
                        NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                return resultBuilder.toString();
            } else {
                resultBuilder.append("пробиты\n").append("Очень жаль, щиты лопнули и вы огребли по полной. Вы в ауте.");
                cursor.close();
                mDb.delete(
                        NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                return resultBuilder.toString();
            }
        }

        return resultBuilder.toString();
    }

    private Cursor getShields(String[] columns, String selection, String[] selectionArgs, String orderBy) {

        return mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );
    }

    private void delShield(int id) {

        mDb.delete(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                "_id=?",
                new String[]{String.valueOf(id)}
        );
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rb_magic_damage:
                mTypeDamage = 1;
                break;
            case R.id.rb_physic_damage:
                mTypeDamage = 2;
                break;
            case R.id.rb_mental_damage:
                mTypeDamage = 3;
                break;
        }
    }
}
