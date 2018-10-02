package net.victium.xelg.notatry.data;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import net.victium.xelg.notatry.R;

import java.util.ArrayList;
import java.util.Map;

public class SpellsUtil {

    private SpellsUtil(){}

    public static class Spell {

        private String mName;
        private String mRange;
        private String mType;
        private String mElement;
        private Map mEffect;
        private Map mVopEffect;
        private int mPowerModule;

        public Spell(String mName, String mRange, String mType, Map mEffect) {
            this.mName = mName;
            this.mRange = mRange;
            this.mType = mType;
            this.mEffect = mEffect;
            this.mVopEffect = mEffect;
            this.mPowerModule = 1;
        }

        public Spell(String mName, String mRange, String mType, String mElement, Map mEffect,
                     Map mVopEffect, int mPowerModule) {
            this.mName = mName;
            this.mRange = mRange;
            this.mType = mType;
            this.mElement = mElement;
            this.mEffect = mEffect;
            this.mVopEffect = mVopEffect;
            this.mPowerModule = mPowerModule;
        }

        public String getName() {
            return mName;
        }

        public String getRange() {
            return mRange;
        }

        public String getType() {
            return mType;
        }

        public String getElement() {
            return mElement;
        }

        public Map getEffect() {
            return mEffect;
        }

        public Map getVopEffect() {
            return mVopEffect;
        }

        public int getPowerModule() {
            return mPowerModule;
        }

        public void setPowerModule(int module) {
            this.mPowerModule = module;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static ArrayList<Spell> getSpellList(Context context) {

        ArrayList<Spell> spellArrayList = new ArrayList<>();
        ArrayMap<SPV, String> effectMap = new ArrayMap<>();
        ArrayMap<SPV, String> vopEffectMap = new ArrayMap<>();

        effectMap.put(SPV.DROP, "кровоточащая рана средней тяжести");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_triple_blade),
                "напр",
                "боевое",
                effectMap
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "ожоги, оглушен, средняя тяжесть");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_fireball),
                "напр",
                "боевое",
                "огонь",
                effectMap,
                effectMap,
                1
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "обморожение, оглушен, легкие ранения");
        vopEffectMap.put(SPV.DROP, "обморожение, легкие ранения");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_ice_block),
                "напр",
                "боевое",
                null,
                effectMap,
                vopEffectMap,
                1
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "ожоги, оглушен, средняя тяжесть");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_pillar_of_fire),
                "область",
                "боевое",
                "огонь",
                effectMap,
                effectMap,
                1
        ));

        spellArrayList.add(new Spell(
                context.getString(R.string.spells_ring_of_fire),
                "область",
                "боевое",
                "огонь",
                effectMap,
                effectMap,
                1
        ));

        spellArrayList.add(new Spell(
                context.getString(R.string.spells_wall_of_fire),
                "область",
                "боевое",
                "огонь",
                effectMap,
                effectMap,
                1
        ));

        effectMap.clear();
        vopEffectMap.clear();
        effectMap.put(SPV.DROP, "отправлен в полет, оглушен");
        vopEffectMap.put(SPV.DROP, "отправлен в полет");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_ring_of_shaab),
                "массовое",
                "боевое",
                null,
                effectMap,
                vopEffectMap,
                1
        ));

        effectMap.clear();
        vopEffectMap.clear();
        effectMap.put(SPV.DROP, "ожоги, оглушен, средняя тяжесть");
        vopEffectMap.put(SPV.DROP, "не действует");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_personal_kiss_of_echidna),
                "перс",
                "боевое",
                null,
                effectMap,
                vopEffectMap,
                1
        ));

        spellArrayList.add(new Spell(
                context.getString(R.string.spells_mass_kiss_of_echidna),
                "массовое",
                "боевое",
                null,
                effectMap,
                vopEffectMap,
                1
        ));

        effectMap.clear();
        vopEffectMap.clear();
        effectMap.put(SPV.DROP, "оглушен, тяжелое ранение");
        vopEffectMap.put(SPV.BLOCK, "среднее ранение");
        vopEffectMap.put(SPV.BURST, "тяжелое ранение");
        vopEffectMap.put(SPV.DROP, "оглушен, тяжелое ранение");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_white_sword),
                "особое",
                "боевое",
                null,
                effectMap,
                vopEffectMap,
                1
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "недееспособен");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_spear_of_suffering),
                "напр",
                "боевое",
                effectMap
        ));

        spellArrayList.add(new Spell(
                context.getString(R.string.spells_spear_of_light),
                "напр",
                "боевое",
                effectMap
        ));

        effectMap.clear();
        vopEffectMap.clear();
        effectMap.put(SPV.BURST, "переохлаждение, удушье, декомпрессия, ударная волна, оглушен, средняя тяжесть");
        effectMap.put(SPV.DROP, "переохлаждение, удушье, декомпрессия, ударная волна, оглушен, средняя тяжесть");
        vopEffectMap.put(SPV.DROP, "переохлаждение, удушье, декомпрессия, ударная волна, оглушен, средняя тяжесть");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_vacuum_blow),
                "массовое",
                "боевое",
                null,
                effectMap,
                vopEffectMap,
                1
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "смерть, если пошевелишься");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_taiga),
                "напр",
                "боевое",
                effectMap
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "смерть особо неприглядным образом");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_haze_transylvania),
                "напр",
                "боевое",
                effectMap
        ));

        effectMap.clear();
        vopEffectMap.clear();
        effectMap.put(SPV.DROP, "оглушен");
        vopEffectMap.put(SPV.DROP, "смерть");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_sparkling_wall),
                "область",
                "боевое",
                null,
                effectMap,
                vopEffectMap,
                2
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "кучка пепла");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_lash_of_shaab),
                "особое",
                "боевое",
                effectMap
        ));

        spellArrayList.add(new Spell(
                context.getString(R.string.spells_bethlehem_fire),
                "напр",
                "боевое",
                null,
                effectMap,
                effectMap,
                2
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "отправлен в полет");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_telekinesis),
                "напр",
                "универсальное",
                effectMap
        ));

        effectMap.clear();
        vopEffectMap.clear();
        effectMap.put(SPV.DROP, "нельзя двигаться и колдовать 3 хода");
        vopEffectMap.put(SPV.BURST, "замедлен");
        vopEffectMap.put(SPV.DROP, "нельзя двигаться и колдовать 3 хода");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_press),
                "напр",
                "универсальное",
                null,
                effectMap,
                vopEffectMap,
                1
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "двигаться и колдовать нельзя");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_shock),
                "напр",
                "универсальное",
                effectMap
        ));

        vopEffectMap.clear();
        vopEffectMap.put(SPV.BURST, "обратная трансформация, блок на трансформацию до конца боя");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_gray_wolf),
                "напр",
                "универсальное",
                null,
                null,
                vopEffectMap,
                1
        ));

        effectMap.clear();
        effectMap.put(SPV.DROP, "обездвижен");
        spellArrayList.add(new Spell(
                context.getString(R.string.spells_fetters),
                "напр",
                "универсальное",
                effectMap
        ));

        vopEffectMap.clear();
        vopEffectMap.put()
    }
}
