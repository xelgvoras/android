package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.data.SpellsUtil;
import net.victium.xelg.notatry.enums.SPV;

import java.lang.reflect.InvocationTargetException;

public class DamageDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TARGET_GROUP = "групповой";
    private static final String TARGET_PERSONAL = "персональный";
    private static final String TYPE_UNIVERSAL = "унив";
    private static final String TYPE_SPECIAL = "особый";
    private static final String CONCAVE_SHIELD = "Вогнутый щит";

    private Activity mActivity;

    private String[] mSelectGroupShieldArg;
    private String[] mSelectPersonalShieldArg;

    private int mTypeDamage;
    private int mInputDamage;
    private String mTypeDamageArg;
    private String mColumnDefenceKey;
    private String mColumnDefenceSumKey;
    private String mBattleForm;
    private EditText mDamagePower;

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

        mActivity = getActivity();
        mBattleForm = getArguments().getString("battleForm");

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

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

    private String checkMagicAttack(String spellName, int inputDamage) {
        StringBuilder builder = new StringBuilder();
        SpellsUtil.Spell spell;
        ArrayMap<SPV, String> effectArrayMap = new ArrayMap<>();
        SPV damageResult = SPV.DROP;

        switch (spellName) {
            case "Тройное лезвие":
                effectArrayMap.put(SPV.DROP, "сильное кровотечение, ранение средней тяжести");
                spell = new SpellsUtil.Spell(spellName, "напр", "боевое", effectArrayMap);
                break;
                default:
                    Toast.makeText(mActivity, "Unknown spell: " + spellName, Toast.LENGTH_LONG).show();
                    return "Ошибка";
        }

        String selection = NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?";
        String[] selectionArgs = new String[]{mTypeDamageArg, TYPE_UNIVERSAL};
        String sortOrder = NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC";
        Cursor cursor = mActivity.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder);

        if (cursor.moveToFirst()) {

            while (true) {
                String shieldId = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID));
                String shieldName = cursor.getString(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME));
                int shieldDefence = cursor.getInt(cursor.getColumnIndex(mColumnDefenceKey));
                int shieldCost = cursor.getInt(cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST));
                Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(shieldId).build();
                int damage = inputDamage;

                if (shieldName.equals(mActivity.getString(R.string.shields_cloack_of_darkness)) && spell.getRange().equals("напр")) {
                    return "Заклинание промахнулось";
                }

                if (spell.getType().equals("боевое")) {
                    damage = 2 * inputDamage;
                }

                damageResult = checkDamage(damage, shieldDefence);

                switch (damageResult) {
                    case BLOCK:
                        builder.append("Заклинание заблокировано");
                        if (shieldName.equals(getString(R.string.shields_mag_shield)) || shieldName.equals(getString(R.string.shields_force_barrier))) {
                            shieldCost = shieldCost - inputDamage;

                            if (shieldCost == 0) {
                                mActivity.getContentResolver().delete(uri, null, null);
                                return builder.append("\n").append(shieldName).append(" иссяк").toString();
                            } else {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, shieldCost);
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, shieldCost);
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, shieldCost);
                                mActivity.getContentResolver().update(uri, contentValues, null, null);
                                return builder.append("\n").append(shieldName).append(" ослаб").toString();
                            }
                        }
                        return "Заклинание заблокировано";
                    case BURST:
                        mActivity.getContentResolver().delete(uri, null, null);
                        return "Щит \"" + shieldName + "\" лоппнул, заклинание заблокировано";
                    case DROP:
                        inputDamage = inputDamage - shieldDefence;
                        mActivity.getContentResolver().delete(uri, null, null);
                        builder.append("Щит \"").append(shieldName).append("\" пробит\n");
                        break;
                }

                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        cursor.close();

        if (mBattleForm.equals("боевая форма")) {
            cursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                    null, null, null, null);
            cursor.moveToFirst();
            int naturalDefence = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE));

            damageResult = checkDamage(inputDamage, naturalDefence);

            switch (damageResult) {
                case BLOCK:
                    if (spell.getEffect().containsKey(SPV.BLOCK)) {
                        builder.append(spell.getEffect().get(SPV.BLOCK));
                    } else {
                        builder.append("заклинание заблокировано");
                    }

                    if ("огонь".equals(spell.getElement())) {
                        naturalDefence = naturalDefence - (inputDamage / 2);
                        if (naturalDefence < 0) {
                            naturalDefence = 0;
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, naturalDefence);
                        mActivity.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues, null, null);
                    }
                    break;
                case BURST:
                    if (spell.getEffect().containsKey(SPV.BURST)) {
                        builder.append(spell.getEffect().get(SPV.BURST));
                    } else {
                        builder.append("заклинание заблокировано");
                    }

                    if (spell.getElement().equals("огонь")) {
                        naturalDefence = naturalDefence - (inputDamage / 2);
                        if (naturalDefence < 0) {
                            naturalDefence = 0;
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, naturalDefence);
                        mActivity.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues, null, null);
                    }
                    break;
                case DROP:
                    if (spell.getEffect().containsKey(SPV.DROP)) {
                        builder.append(spell.getEffect().get(SPV.DROP));
                    } else {
                        builder.append("заклинание заблокировано");
                    }

                    if (spell.getElement().equals("огонь")) {
                        naturalDefence = naturalDefence - (inputDamage / 2);
                        if (naturalDefence < 0) {
                            naturalDefence = 0;
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, naturalDefence);
                        mActivity.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues, null, null);
                    }
                    break;
            }
        }

        switch (damageResult) {
            case BLOCK:
                if (spell.getEffect().containsKey(SPV.BLOCK)) {
                    builder.append(spell.getEffect().get(SPV.BLOCK));
                }
                break;
            case BURST:
                if (spell.getEffect().containsKey(SPV.BURST)) {
                    builder.append(spell.getEffect().get(SPV.BURST));
                }
                break;
            case DROP:
                if (spell.getEffect().containsKey(SPV.DROP)) {
                    builder.append(spell.getEffect().get(SPV.DROP));
                }
        }

        return builder.toString();
    }

    private String checkBattle() {
        String damageResult;

        mSelectGroupShieldArg = new String[]{TARGET_GROUP, mTypeDamageArg, TYPE_UNIVERSAL, TYPE_SPECIAL};
        mSelectPersonalShieldArg = new String[]{TARGET_PERSONAL, mTypeDamageArg, TYPE_UNIVERSAL, TYPE_SPECIAL};

        switch (mTypeDamage) {
            case 1:
//                damageResult = testCheckAttack();
                damageResult = checkMagicAttack("Тройное лезвие", mInputDamage);
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

        Cursor cursor = mActivity.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null, selection, mSelectGroupShieldArg, null);
        int columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        int columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        int columnShieldDefence = cursor.getColumnIndex(mColumnDefenceKey);

        if (cursor.moveToFirst()) {
            String shieldId = cursor.getString(columnShieldId);
            String shieldName = cursor.getString(columnShieldName);
            int shieldDefence = cursor.getInt(columnShieldDefence);

            Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(shieldId).build();

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
                    mActivity.getContentResolver().delete(uri, null, null);
                    return builder.append("Внешний щит лопнул, воздействие не прошло").toString();
                case DROP:
                    mActivity.getContentResolver().delete(uri, null, null);
                    mInputDamage = mInputDamage - shieldDefence;
                    builder.append("Внещний щит пробит\n");
                    break;
            }
        }
        cursor.close();

        cursor = mActivity.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null, selection, mSelectPersonalShieldArg, null);
        columnShieldId = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID);
        columnShieldName = cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME);
        columnShieldDefence = cursor.getColumnIndex(mColumnDefenceKey);

        String specialEffect = "";
        if (checkConcaveShield()) {
            specialEffect = "\nПротивник в полете";
        }

        if (cursor.moveToFirst()) {
            if (cursor.getCount() == 1) {
                String shieldId = cursor.getString(columnShieldId);
                String shieldName = cursor.getString(columnShieldName);
                int shieldDefence = cursor.getInt(columnShieldDefence);
                Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(shieldId).build();

                if (shieldName.equals(getString(R.string.shields_mag_shield))) {
                    switch (checkDamage(mInputDamage, shieldDefence)) {
                        case BLOCK:
                            shieldDefence = shieldDefence - mInputDamage;
                            if (shieldDefence > 0) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, shieldDefence);
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, shieldDefence);
                                contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, shieldDefence);

                                mActivity.getContentResolver().update(uri, contentValues, null, null);

                                return builder.append("Воздействие заблокировано, щит мага ослаб").toString();
                            } else {
                                mActivity.getContentResolver().delete(uri, null, null);
                                return builder.append("Воздействие заблокировано, щит мага иссяк").toString();
                            }
                        case BURST:
                            mActivity.getContentResolver().delete(uri, null, null);
                            return builder.append("Щит мага лопнул, воздействие не прошло").toString();
                        case DROP:
                            mActivity.getContentResolver().delete(uri, null, null);
                            mInputDamage = mInputDamage - shieldDefence;
                            builder.append("Щит мага пробит\n");
                            break;
                    }
                } else {
                    switch (checkDamage(mInputDamage, shieldDefence)) {
                        case BLOCK:
                            return builder.append("Воздействие заблокировано").append(specialEffect).toString();
                        case BURST:
                            mActivity.getContentResolver().delete(uri, null, null);
                            return builder.append("Персональный щит лопнул, воздействие не прошло").append(specialEffect).toString();
                        case DROP:
                            mActivity.getContentResolver().delete(uri, null, null);
                            mInputDamage = mInputDamage - shieldDefence;
                            builder.append("Персональный щит пробит\n");
                            break;
                    }
                }
            } else {
                String[] projection = new String[]{"SUM(" + mColumnDefenceKey + ") as " + mColumnDefenceSumKey};
                cursor.close();
                cursor = mActivity.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                        projection, selection, mSelectPersonalShieldArg, null);
                cursor.moveToFirst();
                int columnShieldSum = cursor.getColumnIndex(mColumnDefenceSumKey);
                int shieldDefence = cursor.getInt(columnShieldSum);

                switch (checkDamage(mInputDamage, shieldDefence)) {
                    case BLOCK:
                        return builder.append("Воздействие заблокировано").append(specialEffect).toString();
                    case BURST:
                        mActivity.getContentResolver().delete(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                                selection, mSelectPersonalShieldArg);
                        return builder.append("Персональные щиты лопнули, воздействие не прошло").append(specialEffect).toString();
                    case DROP:
                        mActivity.getContentResolver().delete(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                                selection, mSelectPersonalShieldArg);
                        mInputDamage = mInputDamage - shieldDefence;
                        builder.append("Персональные щиты пробиты\n");
                        break;
                }
            }
        }
        cursor.close();

        cursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
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

    private SPV checkDamage(int damage, int defence) {
        if (damage <= defence) {
            return SPV.BLOCK;
        } else if (damage <= (2 * defence)) {
            return SPV.BURST;
        } else {
            return SPV.DROP;
        }
    }

    private String checkMentalAttack(int damage) {
        StringBuilder builder = new StringBuilder();

        String[] projection = new String[]{
                "SUM(" + NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE + ") as " +
                        NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM
        };

        Cursor cursor = mActivity.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                projection, null, null, null);
        int columnMentalDefenceId =
                cursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE_SUM);

        if (cursor.moveToFirst()) {
            int mentalDefence = cursor.getInt(columnMentalDefenceId);

            if (damage >= mentalDefence) {
                String selection =
                        NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?";
                String[] selectionArgs = new String[]{"мент"};
                int deletedShieldsCount = mActivity.getContentResolver().delete(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                        selection, selectionArgs);

                if (deletedShieldsCount > 0) {
                    builder.append("Ментальные щиты развеяны, ");
                } else {
                    builder.append("Ментальное ");
                }
            } else {
                return "Ментальная атака заблокирована";
            }
        }

        cursor.close();

        return builder.append("заклинание прошло").toString();
    }

    private boolean checkConcaveShield() {

        if (!mTypeDamageArg.equals("физ")) {
            return false;
        }

        Uri uri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(CONCAVE_SHIELD).build();

        Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
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
