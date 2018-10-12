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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.victium.xelg.notatry.R;
import net.victium.xelg.notatry.data.Character;
import net.victium.xelg.notatry.data.NotATryContract;
import net.victium.xelg.notatry.utilities.SpellsUtil;
import net.victium.xelg.notatry.enums.SPV;

import java.util.ArrayList;

public class DamageDialogFragment extends DialogFragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TYPE_UNIVERSAL = "унив";

    private Activity mActivity;
    private TextView mAttackTypeTextView;
    private Spinner mAttackListSpinner;
    private ArrayAdapter<CharSequence> mAdapterSpinner;

    private int mTypeDamage;
    private int mInputDamage;
    private int mReactionCount;
    private String mTypeDamageArg;
    private String mColumnDefenceKey;
    private String mBattleForm;
    private String mCharacterType;
    private String mCharacterSide;
    private EditText mDamagePower;
    private Uri mShieldUri;
    private boolean mUseReaction;
    private boolean mSuccessDodge;

    public String mResultSummary;
    public boolean mShouldBeTransformed;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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
        Character character = new Character(mActivity);
        mCharacterType = character.getCharacterType();
        mCharacterSide = character.getCharacterSideToString();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_damage, null);

        mAttackTypeTextView = view.findViewById(R.id.tv_attack_type);
        mAttackListSpinner = view.findViewById(R.id.sp_attack_list);

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
        mAttackTypeTextView.setText("Заклинание");
        mAdapterSpinner = ArrayAdapter.createFromResource(mActivity,
                R.array.spells_array, android.R.layout.simple_spinner_item);
        mAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAttackListSpinner.setAdapter(mAdapterSpinner);
        mAttackListSpinner.setOnItemSelectedListener(this);

        mReactionCount = getReactionCount();
        mUseReaction = false;

        builder.setTitle("Входящее воздействие")
                .setMessage("Укажите тип и силу воздействия, в случае необходимости - выберите заклинание из списка")
                .setView(view)
                .setPositiveButton("проверить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*String input = mDamagePower.getText().toString();

                        if (input.length() == 0) {
                            Toast.makeText(getActivity(), "Не указана сила воздействия", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mInputDamage = Integer.parseInt(input);
                        String stringInputDamage = String.valueOf(mInputDamage);
                        String selectedAttack = mAttackListSpinner.getSelectedItem().toString();
                        mResultSummary = checkBattle(selectedAttack);
                        insertMessageToBattleJournal(selectedAttack, stringInputDamage);*/
                        testCheckBattle();
                        mListener.onDialogPositiveClick(DamageDialogFragment.this);
                    }
                })
                .setNegativeButton("реакция(" + mReactionCount + ")", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mReactionCount == 0) {
                            Toast.makeText(mActivity, "У вас закончились реакции, выполнена стандартная проверка воздействия", Toast.LENGTH_LONG).show();
                        } else {
                            mUseReaction = true;
                        }

                        testCheckBattle();
                        mListener.onDialogPositiveClick(DamageDialogFragment.this);
                    }
                });

        return builder.create();
    }

    private void testCheckBattle() {
        String input = mDamagePower.getText().toString();

        if (input.length() == 0) {
            Toast.makeText(getActivity(), "Не указана сила воздействия", Toast.LENGTH_LONG).show();
            return;
        }

        mInputDamage = Integer.parseInt(input);
        String stringInputDamage = String.valueOf(mInputDamage);
        String selectedAttack = mAttackListSpinner.getSelectedItem().toString();

        switch (mTypeDamage) {
            case 1:
                mResultSummary = checkMagicAttack(selectedAttack);
                break;
            case 2:
                mResultSummary = checkPhysicAttack(selectedAttack);
                break;
            case 3:
                mResultSummary = checkMentalAttack(mInputDamage);
                break;
            default:
                break;
        }

        insertMessageToBattleJournal(selectedAttack, stringInputDamage);
    }

    private String checkBattle(String selectedAttack) {
        String damageResult;

        switch (mTypeDamage) {
            case 1:
                damageResult = checkMagicAttack(selectedAttack);
                break;
            case 2:
                damageResult = checkPhysicAttack(selectedAttack);
                break;
            case 3:
                damageResult = checkMentalAttack(mInputDamage);
                break;
            default:
                return "Ошибка";
        }

        return damageResult;
    }

    @NonNull
    private String checkMagicAttack(String spellName) {
        StringBuilder builder = new StringBuilder();
        SPV damageResult = SPV.DROP;
        SpellsUtil.Spell spell = SpellsUtil.getSpell(spellName, mBattleForm, mCharacterType);
        Cursor shieldsCursor = getShields();

        final String spellSpearOfSuffering = getString(R.string.spells_spear_of_suffering);
        final String spellSpearOfLight = getString(R.string.spells_spear_of_light);
        final String spellHazeTransylvania = getString(R.string.spells_haze_transylvania);
        final String spellSparklingWall = getString(R.string.spells_sparkling_wall);
        final String spellVacuumBlow = mActivity.getString(R.string.spells_vacuum_blow);
        final String spellFetters = mActivity.getString(R.string.spells_fetters);
        final String spellExpropriation = mActivity.getString(R.string.spells_expropriation);

        final String shieldRainbowSphere = getString(R.string.shields_rainbow_sphere);
        final String shieldSphereOfNegation = getString(R.string.shields_sphere_of_negation);
        final String shieldMagShield = getString(R.string.shields_mag_shield);
        final String shieldHighestMagShield = getString(R.string.shields_highest_mag_shield);
        final String shieldForceBarrier = mActivity.getString(R.string.shields_force_barrier);

        ArrayList<String> shieldListIgnoredHazeTransylvania = new ArrayList<>();
        shieldListIgnoredHazeTransylvania.add(shieldSphereOfNegation);
        shieldListIgnoredHazeTransylvania.add(shieldMagShield);
        shieldListIgnoredHazeTransylvania.add(shieldHighestMagShield);
        shieldListIgnoredHazeTransylvania.add(shieldForceBarrier);

        if (mUseReaction) {
            builder.append(dropGroupShield()).append("\n").append(checkReaction(spell)).append("\n\n");
            updateReactionCount();
        }

        if (mSuccessDodge) {
            return builder.toString();
        }

        if (shieldsCursor.moveToFirst()) {

            while (true) {
                String shieldId = shieldsCursor.getString(shieldsCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID));
                String shieldName = shieldsCursor.getString(shieldsCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME));
                String shieldType = shieldsCursor.getString(shieldsCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_TYPE));
                int shieldDefence = shieldsCursor.getInt(shieldsCursor.getColumnIndex(mColumnDefenceKey));
                int shieldCost = shieldsCursor.getInt(shieldsCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST));
                int damage = mInputDamage;
                boolean ignoreShield = false;

                mShieldUri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(shieldId).build();

                // COMPLETED(bug) Вакуумный удар, игнорирует любые щиты, кроме универсальных
                // СПВ для заклинания: ничего - пробит - пробит
                // COMPLETED(bug) Путы захви блокируются любой магической или универсальной защитой, не снижают силу щитов

                // COMPLETED(bug) Плащ тьмы должен укрывать также от физических атак
                if (shieldName.equals(mActivity.getString(R.string.shields_cloack_of_darkness)) && spell.getTarget().equals("напр")) {
                    return "Заклинание промахнулось";
                }

                if (spellName.equals(spellFetters) || spellName.equals(spellExpropriation)) {
                    return "Заклинание заблокировано";
                }

                if (spellName.equals(spellSparklingWall) && mCharacterSide.equals("тьма")) {
                    mInputDamage = 2 * mInputDamage;
                }

                if (spell.getType().equals("боевое")) {
                    damage = 2 * mInputDamage;
                }

                if (spellName.equals(spellSpearOfSuffering) || spellName.equals(spellSpearOfLight)) {
                    if (shieldsCursor.getCount() == 1) {
                        damage = 4 * mInputDamage;
                    } else {
                        return "Нет эффекта";
                    }
                }

                // COMPLETED(bug) Марево Трансильвании игнорирует силовой барьер

                if (spellName.equals(spellVacuumBlow) && !shieldType.equals("унив")) {
                    ignoreShield = true;
                }

                damageResult = checkDamage(damage, shieldDefence);

                // COMPLETED(bug) Тайга больше не лопает радужную сферу

                if (spellName.equals(spellVacuumBlow)) {
                    if (!shieldType.equals("унив")) {
                        ignoreShield = true;
                    } else if (damageResult == SPV.BURST) {
                        damageResult = SPV.DROP;
                    }
                }

                if (spellName.equals(spellHazeTransylvania)) {
                    if (shieldListIgnoredHazeTransylvania.contains(shieldName)) {
                        ignoreShield = true;
                    } else if (damageResult == SPV.BLOCK && shieldName.equals(shieldRainbowSphere)) {
                        damageResult = SPV.BURST;
                    }
                }

                if (ignoreShield) {
                    damageResult = SPV.DROP;
                } else {
                    builder.append(checkShield(damageResult, shieldName, shieldCost, shieldDefence, damage));
                }

                if (!shieldsCursor.moveToNext() || !damageResult.equals(SPV.DROP)) {
                    break;
                }
            }
        }
        shieldsCursor.close();

        if (damageResult.equals(SPV.DROP)) {
            if (mBattleForm.equals("боевая форма")) {
                builder.append(checkNaturalDefence(spell, null));
            } else if (spell.getEffect().containsKey(damageResult)) {
                builder.append(spell.getEffect().get(damageResult));

            }
        }

        return builder.toString();
    }

    @NonNull
    private String checkPhysicAttack(String attackType) {
        StringBuilder builder = new StringBuilder();
        SPV damageResult = SPV.DROP;
        Cursor shieldsCursor = getShields();

        if (mUseReaction) {
            Toast.makeText(mActivity, "Для физических воздействий реакции не обрабатываются", Toast.LENGTH_LONG).show();
        }

        if (shieldsCursor.moveToFirst()) {

            while (true) {
                String shieldId = shieldsCursor.getString(shieldsCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry._ID));
                String shieldName = shieldsCursor.getString(shieldsCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_NAME));
                int shieldDefence = shieldsCursor.getInt(shieldsCursor.getColumnIndex(mColumnDefenceKey));
                int shieldCost = shieldsCursor.getInt(shieldsCursor.getColumnIndex(NotATryContract.ActiveShieldsEntry.COLUMN_COST));
                int damage = mInputDamage;

                mShieldUri = NotATryContract.ActiveShieldsEntry.CONTENT_URI.buildUpon().appendPath(shieldId).build();

                if (shieldName.equals(mActivity.getString(R.string.shields_crystal_shield))) {
                    return "Хрустальный щит блокирует любые физические атаки";
                }

                if (shieldName.equals(mActivity.getString(R.string.shields_cloack_of_darkness))) {
                    return "Невозможно атаковать через плащ тьмы";
                }

                if (shieldName.equals(mActivity.getString(R.string.shields_concave_shield))) {
                    builder.append("Противник отправлен в полет\n");
                }
                damageResult = checkDamage(damage, shieldDefence);

                builder.append(checkShield(damageResult, shieldName, shieldCost, shieldDefence, damage));

                if (!shieldsCursor.moveToNext() || !damageResult.equals(SPV.DROP)) {
                    break;
                }
            }
        }
        shieldsCursor.close();

        if (damageResult.equals(SPV.DROP)) {
            if (mBattleForm.equals("боевая форма")) {
                builder.append(checkNaturalDefence(null, attackType));
            } else {
                switch (attackType) {
                    case "Бью":
                        builder.append("Отлетаете и падаете, сломана конечность или ребра, сотрясение мозга, теряете сознание, оглушены, среднее ранение");
                        break;
                    case "Раню":
                        builder.append("Серьезная рваная рана, теряете сознание, оглушены, тяжелое ранение");
                }
            }
        }

        return builder.toString();
    }

    @NonNull
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

    @NonNull
    private String checkShield(SPV damageResult, String shieldName, int shieldCost, int shieldDefence, int damage) {
        StringBuilder builder = new StringBuilder();

        String message = "Заклинание заблокировано";

        if (mTypeDamageArg.equals("физ")) {
            message = "Атака заблокирована";
        }

        switch (damageResult) {
            case BLOCK:
                if (shieldName.equals(getString(R.string.shields_mag_shield)) || shieldName.equals(getString(R.string.shields_force_barrier))) {
                    shieldCost = shieldCost - damage;

                    if (shieldCost == 0) {
                        mActivity.getContentResolver().delete(mShieldUri, null, null);
                        // COMPLETED(bug) изменить текст записи с "иссяк" на "лопнул"
                        builder.append("Щит \"").append(shieldName).append("\" лопнул\n");
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_COST, shieldCost);
                        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE, shieldCost);
                        contentValues.put(NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE, shieldCost);
                        mActivity.getContentResolver().update(mShieldUri, contentValues, null, null);
                        builder.append(shieldName).append(" ослаб").append("\n");
                    }
                }
                builder.append(message);
                break;
            case BURST:
                mActivity.getContentResolver().delete(mShieldUri, null, null);
                builder.append("Щит \"").append(shieldName).append("\" лопнул\n").append(message);
                break;
            case DROP:
                mInputDamage = mInputDamage - shieldDefence;
                mActivity.getContentResolver().delete(mShieldUri, null, null);
                builder.append("Щит \"").append(shieldName).append("\" пробит");
                break;
        }

        return builder.append("\n").toString();
    }

    @NonNull
    private String checkNaturalDefence(SpellsUtil.Spell spell, String attackType) {
        StringBuilder builder = new StringBuilder();
        ArrayMap<SPV, String> attackEffect = new ArrayMap<>();
        final String mGrayWolf = getString(R.string.spells_gray_wolf);

        String message = "Естественная защита выдержала";

        if (attackType != null) {
            switch (attackType) {
                case "Бью":
                    attackEffect.put(SPV.BLOCK, "Ничего не произошло");
                    attackEffect.put(SPV.BURST, "Легкие ранения");
                    attackEffect.put(SPV.DROP, "Средние ранения, сбит с ног");
                    break;
                case "Раню":
                    attackEffect.put(SPV.BLOCK, "Легкие ранения");
                    attackEffect.put(SPV.BURST, "Средние ранения");
                    attackEffect.put(SPV.DROP, "Тяжелые ранения");
            }
        }

        Cursor naturalDefenceCursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        naturalDefenceCursor.moveToFirst();
        int naturalDefence = naturalDefenceCursor.getInt(naturalDefenceCursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE));

        SPV damageResult = checkDamage(mInputDamage, naturalDefence);

        if (spell != null) {
            if (spell.getEffect().containsKey(damageResult)) {
                builder.append(spell.getEffect().get(damageResult));
                if (spell.getName().equals(mGrayWolf)) {
                    mShouldBeTransformed = true;
                }
            } else {
                builder.append(message);
            }

            if ("огонь".equals(spell.getElement())) {
                naturalDefence = naturalDefence - (mInputDamage / 2);
                if (naturalDefence < 0) {
                    naturalDefence = 0;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_NATURAL_DEFENCE, naturalDefence);
                mActivity.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI, contentValues, null, null);
            }
        } else {
            builder.append(attackEffect.get(damageResult));
        }

        return builder.append("\n").toString();
    }

    private String checkReaction(SpellsUtil.Spell spell) {

        if (!spell.isCanDodge()) {
            mSuccessDodge = false;
            return "От заклинания невозможно увернуться";
        }

        if (mBattleForm.equals("боевая форма")) {
            mSuccessDodge = true;
            return "Вы успешно увернулись от атаки";
        } else if (spell.getTarget().equals("область") && spell.getSpeed().equals("медленное")) {
            mSuccessDodge = true;
            return "Вы успешно увернилсь от атаки";
        }

        return "Вам не удалось увернуться от атаки";
    }

    private Cursor getShields() {
        String selection = NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=? OR " +
                NotATryContract.ActiveShieldsEntry.COLUMN_TYPE + "=?";
        String[] selectionArgs = new String[]{mTypeDamageArg, TYPE_UNIVERSAL};
        String sortOrder = NotATryContract.ActiveShieldsEntry.COLUMN_RANGE + " DESC";
        return mActivity.getContentResolver().query(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder);
    }

    private String dropGroupShield() {
        String selection = NotATryContract.ActiveShieldsEntry.COLUMN_TARGET + "=?";
        String[] selectionArgs = new String[]{"групповой"};
        int deletedShields = mActivity.getContentResolver().delete(NotATryContract.ActiveShieldsEntry.CONTENT_URI,
                selection, selectionArgs);
        if (deletedShields > 0) {
            return "Использована реакция, групповые щиты распались";
        }

        return "Использована реакция";
    }

    private int getReactionCount() {
        Cursor cursor = mActivity.getContentResolver().query(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                null, null, null, null);
        cursor.moveToFirst();
        int returnReactionCount = cursor.getInt(cursor.getColumnIndex(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER));
        cursor.close();

        return returnReactionCount;
    }

    private void updateReactionCount() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.CharacterStatusEntry.COLUMN_REACTIONS_NUMBER, --mReactionCount);
        mActivity.getContentResolver().update(NotATryContract.CharacterStatusEntry.CONTENT_URI,
                contentValues, null, null);
    }

    private void insertMessageToBattleJournal(String selectedAttack, String inputDamage) {

        String attackMessage = "Атака:\n" + mTypeDamageArg + " " + selectedAttack + " " + inputDamage;
        String resultMessage = "Результат:\n" + mResultSummary;

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotATryContract.BattleJournalEntry.COLUMN_ATTACK_MESSAGE, attackMessage);
        contentValues.put(NotATryContract.BattleJournalEntry.COLUMN_RESULT_MESSAGE, resultMessage);

        mActivity.getContentResolver().insert(NotATryContract.BattleJournalEntry.CONTENT_URI, contentValues);
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rb_magic_damage:
                mTypeDamage = 1;
                mTypeDamageArg = "маг";
                mColumnDefenceKey = NotATryContract.ActiveShieldsEntry.COLUMN_MAGIC_DEFENCE;
                mAttackTypeTextView.setText("Заклинание");
                mAdapterSpinner = ArrayAdapter.createFromResource(mActivity,
                        R.array.spells_array, android.R.layout.simple_spinner_item);
                mAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mAttackListSpinner.setAdapter(mAdapterSpinner);
                break;
            case R.id.rb_physic_damage:
                mTypeDamage = 2;
                mTypeDamageArg = "физ";
                mColumnDefenceKey = NotATryContract.ActiveShieldsEntry.COLUMN_PHYSIC_DEFENCE;
                mAttackTypeTextView.setText("Атака");
                mAdapterSpinner = ArrayAdapter.createFromResource(mActivity,
                        R.array.attacks_array, android.R.layout.simple_spinner_item);
                mAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mAttackListSpinner.setAdapter(mAdapterSpinner);
                break;
            case R.id.rb_mental_damage:
                mTypeDamage = 3;
                mTypeDamageArg = "мент";
                mColumnDefenceKey = NotATryContract.ActiveShieldsEntry.COLUMN_MENTAL_DEFENCE;
                mAttackTypeTextView.setText("Ментальное заклинание (в разработке)");
                mAdapterSpinner = ArrayAdapter.createFromResource(mActivity,
                        R.array.attacks_array, android.R.layout.simple_spinner_item);
                mAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mAttackListSpinner.setAdapter(mAdapterSpinner);
                break;
        }
    }
}
