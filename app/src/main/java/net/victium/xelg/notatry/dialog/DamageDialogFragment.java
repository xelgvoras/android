package net.victium.xelg.notatry.dialog;

import android.app.Activity;
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
import android.util.Log;
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

    private String[] mSelectGroupShieldArg;
    private String[] mSelectPersonalShieldArg;

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
                .setMessage("Укажите тип и силу воздействия, в случае необходимости - выберите заклинение из списка")
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

        switch (mTypeDamage) {
            case 1:
                damageResult = testCheckAttack(new String[]{getString(R.string.shields_cloack_of_darkness)});
                break;
            case 2:
                damageResult = testCheckAttack(new String[]{getString(R.string.shields_crystal_shield),
                        getString(R.string.shields_concave_shield)});
                break;
            case 3:
                damageResult = checkMentalAttack(mInputDamage);
                break;
                default:
                    damageResult = testCheckAttack(new String[]{getString(R.string.shields_cloack_of_darkness)});
                    break;
        }

        return damageResult;
    }

    private String testCheckAttack(String[] specialShields) {
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

            if (specialShields.length > 0) {

                for (int i = 0; i < specialShields.length; i++) {
                    if (shieldName.equals(specialShields[i])) {
                        return "Заклинание промахнулось";
                    } else if (shieldName.equals(specialShields[i])) {
                        return "Хрустальный щит блокирует любые физические атаки";
                    }
                }
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
                            return builder.append("Воздействие заблокировано").toString();
                        case BURST:
                            delShields("_id=" + shieldId, null);
                            return builder.append("Персональный щит лопнул, воздействие не прошло").toString();
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
                        return builder.append("Воздействие заблокировано").toString();
                    case BURST:
                        delShields(selection, mSelectPersonalShieldArg);
                        return builder.append("Персональные щиты лопнули, воздействие не прошло").toString();
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
                    return builder.append("Естественная защита выдержала воздействие").toString();
                case BURST:
                    return builder.append("Естественная защита лопнула, воздействие ослаблено").toString();
                case DROP:
                    return builder.append("Естественная защита пробита, полный эффект воздействия").toString();
            }
        }

        return builder.append("Воздействие прошло, полный эффект").toString();
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

    private String checkMagicAttack(int damage) {
        StringBuilder builder = new StringBuilder();

        String selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=? AND " +
                "(" + NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?)";
        String[] selectionArgs = new String[] {"групповой", "маг", "особый", "унив"};

        Cursor cursor = getShields(null, selection, selectionArgs);
        int columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        int columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        int columnShieldDefence = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(columnShieldId);
            String shieldName = cursor.getString(columnShieldName);
            int shieldDefence = cursor.getInt(columnShieldDefence);

            if (getString(R.string.shields_cloack_of_darkness).equals(shieldName)) {
                return "Заклинание промахнулось";
            }

            if (damage <= shieldDefence) {
                return "Заклинание заблокировано";
            } else if (damage <= (2 * shieldDefence)) {
                delShields("_id=" + id, null);
                return "Внешний щит лопнул, заклинание не прошло";
            } else {
                delShields("_id=" + id, null);
                builder.append("Внешний щит пробит\n");
            }
        }

        selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=? AND " +
                "(" + NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?)";
        selectionArgs = new String[] {"персональный", "маг", "унив"};

        cursor = getShields(null, selection, selectionArgs);
        columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        columnShieldDefence = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE);

        if (cursor.getCount() > 0) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                String shieldName = cursor.getString(columnShieldName);

                if (getString(R.string.shields_mag_shield).equals(shieldName)) {
                    int shieldDefence = cursor.getInt(columnShieldDefence);
                    int id = cursor.getInt(columnShieldId);

                    if (damage <= shieldDefence) {
                        shieldDefence = shieldDefence - damage;

                        if (shieldDefence > 0) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, shieldDefence);
                            contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, shieldDefence);
                            contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, shieldDefence);

                            mDb.update(
                                    NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                                    contentValues,
                                    "_id=" + id,
                                    null
                            );

                            return builder.append("Заклинание заблокировано, щит мага ослаб").toString();
                        } else {
                            delShields("_id=" + id, null);
                            return builder.append("Заклинание заблокировано, щит мага иссяк").toString();
                        }
                    } else if (damage <= (2 * shieldDefence)) {
                        delShields("_id=" + id, null);
                        return builder.append("Щит мага лопнул, заклинание не прошло").toString();
                    } else {
                        delShields("_id=" + id, null);
                        damage = damage - shieldDefence;
                        builder.append("Щит мага пробит\n");
                    }
                }
            }

            String[] columns = new String[]{
                    "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE + ") as " +
                            NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM
            } ;

            cursor = getShields(columns, selection, selectionArgs);
            cursor.moveToFirst();
            columnShieldDefence = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE_SUM);

            int shieldDefence = cursor.getInt(columnShieldDefence);

            if (damage <= shieldDefence) {
                return builder.append("Заклинание заблокировано").toString();
            } else if (damage <= (2 * shieldDefence)) {
                delShields(selection, selectionArgs);
                return builder.append("Персональные щиты лопнули, заклинание не прошло").toString();
            } else {
                delShields(selection, selectionArgs);
                damage = damage - shieldDefence;
                builder.append("Персональные щиты лопнули\n");
            }
        }

        cursor.close();
        cursor = mDb.query(
                NotATryContract.CharacterStatusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        int columnNaturalDefence = cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE);
        int naturalDefence = cursor.getInt(columnNaturalDefence);

        if (naturalDefence > 0) {
            if (damage <= naturalDefence) {
                return builder.append("Естественная защита выдержала, заклинание заблокировано").toString();
            } else if (damage <= (2 * naturalDefence)) {
                return builder.append("Естественная защита не пробита, эффект заклинания ослаблен").toString();
            } else {
                return builder.append("Естественная защита не выдержала, полный эффект заклинания").toString();
            }
        }

        return builder.append("Заклинание прошло, полный эффект").toString();
    }

    private String checkPhysicAttack(int damage) {
        StringBuilder builder = new StringBuilder();
        String concaveEffect = "";

        String selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=? AND " +
                "(" + NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?)";
        String[] selectionArgs = new String[] {"групповой", "физ", "особый", "унив"};

        Cursor cursor = getShields(null, selection, selectionArgs);
        int columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        int columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        int columnShieldDefence = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(columnShieldId);
            String shieldName = cursor.getString(columnShieldName);
            int shieldDefence = cursor.getInt(columnShieldDefence);

            if (getString(R.string.shields_crystal_shield).equals(shieldName)) {
                return "Хрустальный щит блокирует любые физические атаки";
            }

            if (damage <= shieldDefence) {
                return "Физическая атака заблокирована";
            } else if (damage <= (2 * shieldDefence)) {
                delShields("_id=" + id, null);
                return "Внешний щит лопнул, атака заблокирована";
            } else {
                delShields("_id=" + id, null);
                builder.append("Внешний щит пробит\n");
            }
        }

        selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=? AND " +
                "(" + NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?)";
        selectionArgs = new String[] {"персональный", "физ", "унив"};

        cursor = getShields(null, selection, selectionArgs);
        columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        columnShieldDefence = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE);

        if (cursor.getCount() > 0) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                String shieldName = cursor.getString(columnShieldName);

                if (getString(R.string.shields_mag_shield).equals(shieldName)) {
                    int shieldDefence = cursor.getInt(columnShieldDefence);
                    int id = cursor.getInt(columnShieldId);

                    if (damage <= shieldDefence) {
                        shieldDefence = shieldDefence - damage;

                        if (shieldDefence > 0) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, shieldDefence);
                            contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, shieldDefence);
                            contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, shieldDefence);

                            mDb.update(
                                    NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                                    contentValues,
                                    "_id=" + id,
                                    null
                            );

                            return builder.append("Атака заблокирована, щит мага ослаб").toString();
                        } else {
                            delShields("_id=" + id, null);
                            return builder.append("Атака заблокирована, щит мага иссяк").toString();
                        }
                    } else if (damage <= (2 * shieldDefence)) {
                        delShields("_id=" + id, null);
                        return builder.append("Щит мага лопнул, атака не прошла").toString();
                    } else {
                        delShields("_id=" + id, null);
                        damage = damage - shieldDefence;
                        builder.append("Щит мага пробит\n");
                    }
                }
            }

            cursor.moveToFirst();
            String[] columns = new String[]{
                    "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE + ") as " +
                            NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM
            } ;

            cursor = getShields(columns, selection, selectionArgs);
            cursor.moveToFirst();
            columnShieldDefence = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE_SUM);
            int shieldDefence = cursor.getInt(columnShieldDefence);

            String selectConcaveShield = NotATryContract.ActiveShieldsEntry.COLUMN_NAME + " like '" +
                    getString(R.string.shields_concave_shield) + "'";
            Cursor concaveCursor = getShields(null, selectConcaveShield, null);

            if (concaveCursor.getCount() > 0) {
                concaveEffect = ", противник в полете";
            }

            if (damage <= shieldDefence) {
                return builder.append("Атака заблокирована").append(concaveEffect).toString();
            } else if (damage <= (2 * shieldDefence)) {
                delShields(selection, selectionArgs);
                return builder.append("Персональные щиты лопнули, атака не прошла").append(concaveEffect).toString();
            } else {
                delShields(selection, selectionArgs);
                damage = damage - shieldDefence;
                builder.append("Персональные щиты лопнули\n");
            }
        }

        cursor.close();
        cursor = mDb.query(
                NotATryContract.CharacterStatusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        int columnNaturalDefence = cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE);
        int naturalDefence = cursor.getInt(columnNaturalDefence);

        if (naturalDefence > 0) {
            if (damage <= naturalDefence) {
                return builder.append("Естественная защита выдержала, атака заблокирована").append(concaveEffect).toString();
            } else if (damage <= (2 * naturalDefence)) {
                return builder.append("Естественная защита не пробита, атака ослаблена").append(concaveEffect).toString();
            } else {
                return builder.append("Естественная защита не выдержала, полный эффект атаки").append(concaveEffect).toString();
            }
        }

        return builder.append("Атака прошла, полный эффект").append(concaveEffect).toString();
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

    /*private String checkBattle(int damage, int type) {
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
                    delShields(shieldId);
                    damage = damage - shield;
                } else {
                    resultBuilder.append("пробит\n");
                    delShields(shieldId);
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
    }*/

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
