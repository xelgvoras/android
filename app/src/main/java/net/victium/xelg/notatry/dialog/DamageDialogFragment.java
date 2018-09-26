package net.victium.xelg.notatry.dialog;

import android.app.Dialog;
import android.content.ContentValues;
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

    private final String TARGET_GROUP = "групповой";
    private final String TARGET_PERSONAL = "персональный";
    private final String TYPE_UNIVERSAL = "унив";
    private final String TYPE_SPECIAL = "особый";
    private final String CONCAVE_SHIELD = "Вогнутый щит";

    private String[] mSelectGroupShieldArg;
    private String[] mSelectPersonalShieldArg;
    private String[] mCheckConcaveShield;

    private int mTypeDamage;
    private int mInputDamage;
    private String mTypeDamageArg;
    private String mColumnDefenceKey;
    private String mColumnDefenceSumKey;
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
        mTypeDamageArg = "маг";
        mColumnDefenceKey = NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE;
        mColumnDefenceSumKey = NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM;

        builder.setTitle("Входящее воздействие")
                .setMessage("Укажите тип и силу воздействия, в случае необходимости - выберите заклинание из списка")
                .setView(view)
                .setPositiveButton("проверить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String input = mDamagePower.getText().toString();

                        if (input.length() == 0) {
                            Toast.makeText(getActivity(), "Не указана сила воздействия", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mInputDamage = Integer.parseInt(input);
                        mResultSummary = checkBattle();
                        mListener.onDialogPositiveClick(DamageDialogFragment.this);
                    }
                });

        return builder.create();
    }

    private String checkBattle() {
        String damageResult;

        mSelectGroupShieldArg = new String[]{TARGET_GROUP, mTypeDamageArg, TYPE_UNIVERSAL, TYPE_SPECIAL};
        mSelectPersonalShieldArg = new String[]{TARGET_PERSONAL, mTypeDamageArg, TYPE_UNIVERSAL, TYPE_SPECIAL};
        mCheckConcaveShield = new String[]{TARGET_PERSONAL, mTypeDamageArg, CONCAVE_SHIELD};

        switch (mTypeDamage) {
            case 1:
                damageResult = testCheckAttack();
                break;
            case 2:
                damageResult = testCheckAttack();
                break;
            case 3:
                damageResult = checkMentalAttack(mInputDamage);
                break;
                default:
                    damageResult = testCheckAttack();
                    break;
        }

        return damageResult;
    }

    private String testCheckAttack() {
        StringBuilder builder = new StringBuilder();
        String selection = createSelection();

        Cursor cursor = getShields(null, selection, mSelectGroupShieldArg);
        int columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        int columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        int columnShieldDefence = cursor.getColumnIndex(mColumnDefenceKey);

        if (cursor.moveToFirst()) {
            int shieldId = cursor.getInt(columnShieldId);
            String shieldName = cursor.getString(columnShieldName);
            int shieldDefence = cursor.getInt(columnShieldDefence);

            switch (shieldName){
                case "Плащ тьмы":
                    return "Заклинание промахнулось";
                case "Хрустальный щит":
                    return "Хрустальный щит блокирует любые физические атаки";
            }

            switch (checkDamage(mInputDamage, shieldDefence)) {
                case BLOCK:
                    return builder.append("Воздействие заблокировано").toString();
                case BURST:
                    delShields("_id=" + shieldId, null);
                    return builder.append("Внешний щит лопнул, воздействие не прошло").toString();
                case DROP:
                    delShields("_id=" + shieldId, null);
                    mInputDamage = mInputDamage - shieldDefence;
                    builder.append("Внещний щит пробит\n");
                    break;
            }
        }
        cursor.close();

        cursor = getShields(null, selection, mSelectPersonalShieldArg);
        columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        columnShieldDefence = cursor.getColumnIndex(mColumnDefenceKey);

        String specialEffect = "";
        if (checkConcaveShield(mCheckConcaveShield)) {
            specialEffect = "\nПротивник в полете";
        }

        if (cursor.moveToFirst()) {
            if (cursor.getCount() == 1) {
                int shieldId = cursor.getInt(columnShieldId);
                String shieldName = cursor.getString(columnShieldName);
                int shieldDefence = cursor.getInt(columnShieldDefence);

                if (shieldName.equals(getString(R.string.shields_mag_shield))) {
                    switch (checkDamage(mInputDamage, shieldDefence)) {
                        case BLOCK:
                            shieldDefence = shieldDefence - mInputDamage;
                            if (shieldDefence > 0) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, shieldDefence);
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, shieldDefence);
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, shieldDefence);

                                mDb.update(
                                        NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                                        contentValues,
                                        "_id=" + shieldId,
                                        null
                                );

                                return builder.append("Воздействие заблокировано, щит мага ослаб").toString();
                            } else {
                                delShields("_id=" + shieldId, null);
                                return builder.append("Воздействие заблокировано, щит мага иссяк").toString();
                            }
                        case BURST:
                            delShields("_id=" + shieldId, null);
                            return builder.append("Щит мага лопнул, воздействие не прошло").toString();
                        case DROP:
                            delShields("_id=" + shieldId, null);
                            mInputDamage = mInputDamage - shieldDefence;
                            builder.append("Щит мага пробит\n");
                            break;
                    }
                } else {
                    switch (checkDamage(mInputDamage, shieldDefence)) {
                        case BLOCK:
                            return builder.append("Воздействие заблокировано").append(specialEffect).toString();
                        case BURST:
                            delShields("_id=" + shieldId, null);
                            return builder.append("Персональный щит лопнул, воздействие не прошло").append(specialEffect).toString();
                        case DROP:
                            delShields("_id=" + shieldId, null);
                            mInputDamage = mInputDamage - shieldDefence;
                            builder.append("Персональный щит пробит\n");
                            break;
                    }
                }
            } else {
                String[] columns = new String[]{"SUM(" + mColumnDefenceKey + ") as " + mColumnDefenceSumKey};
                cursor.close();
                cursor = getShields(columns, selection, mSelectPersonalShieldArg);
                cursor.moveToFirst();
                int columnShieldSum = cursor.getColumnIndex(mColumnDefenceSumKey);
                int shieldDefence = cursor.getInt(columnShieldSum);

                switch (checkDamage(mInputDamage, shieldDefence)) {
                    case BLOCK:
                        return builder.append("Воздействие заблокировано").append(specialEffect).toString();
                    case BURST:
                        delShields(selection, mSelectPersonalShieldArg);
                        return builder.append("Персональные щиты лопнули, воздействие не прошло").append(specialEffect).toString();
                    case DROP:
                        delShields(selection, mSelectPersonalShieldArg);
                        mInputDamage = mInputDamage - shieldDefence;
                        builder.append("Персональные щиты пробиты\n");
                        break;
                }
            }
        }
        cursor.close();

        cursor = mDb.query(NotATryContract.CharacterStatusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();

        int columnNaturalDefence = cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE);
        int naturalDefence = cursor.getInt(columnNaturalDefence);

        if (naturalDefence > 0) {
            switch (checkDamage(mInputDamage, naturalDefence)) {
                case BLOCK:
                    return builder.append("Естественная защита выдержала воздействие").append(specialEffect).toString();
                case BURST:
                    return builder.append("Естественная защита лопнула, воздействие ослаблено").append(specialEffect).toString();
                case DROP:
                    return builder.append("Естественная защита пробита, полный эффект воздействия").append(specialEffect).toString();
            }
        }

        return builder.append("Воздействие прошло, полный эффект").append(specialEffect).toString();
    }

    private String createSelection() {
        return NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=? AND " +
                "(" + NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?)";
    }

    private spv checkDamage(int damage, int defence) {
        if (damage <= defence) {
            return spv.BLOCK;
        } else if (damage <= (2 * defence)) {
            return spv.BURST;
        } else {
            return spv.DROP;
        }
    }

    enum spv {
        BLOCK,
        BURST,
        DROP
    }

    private String checkMentalAttack(int damage) {
        StringBuilder builder = new StringBuilder();

        String[] columns = new String[]{
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + ") as " +
                        NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM
        };

        Cursor cursor = getShields(columns, null, null);
        int columnMentalDefenceId =
                cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM);

        if (cursor.moveToFirst()) {
            int mentalDefence = cursor.getInt(columnMentalDefenceId);

            if (damage >= mentalDefence) {
                String selection =
                        NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?";
                String[] selectionArgs = new String[]{"мент"};
                int deletedShieldsCount = delShields(selection, selectionArgs);

                if (deletedShieldsCount > 0) {
                    builder.append("Ментальные щиты развеяны, ");
                } else {
                    builder.append("Ментальное ");
                }
            } else {
                return "Ментальная атака заблокирована";
            }
        }

        return builder.append("заклинание прошло").toString();
    }

    private Cursor getShields(String[] columns, String selection, String[] selectionArgs) {

        return mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private int delShields(String selection, String[] selectionArgs) {

        return  mDb.delete(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
    }

    private boolean checkConcaveShield(String[] selectionArgs) {

        Cursor cursor = mDb.query(
                NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                null,
                NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=? AND " +
                        NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? AND " +
                        NotATryContract.ActiveShieldsEntry.COLUMN_NAME + "=?",
                selectionArgs,
                null,
                null,
                null
        );

        int count = cursor.getCount();
        cursor.close();

        return count == 1;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rb_magic_damage:
                mTypeDamage = 1;
                mTypeDamageArg = "маг";
                mColumnDefenceKey = NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE;
                mColumnDefenceSumKey = NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM;
                break;
            case R.id.rb_physic_damage:
                mTypeDamage = 2;
                mTypeDamageArg = "физ";
                mColumnDefenceKey = NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE;
                mColumnDefenceSumKey = NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM;
                break;
            case R.id.rb_mental_damage:
                mTypeDamage = 3;
                mTypeDamageArg = "мент";
                mColumnDefenceKey = NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE;
                mColumnDefenceSumKey = NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM;
                break;
        }
    }
}
