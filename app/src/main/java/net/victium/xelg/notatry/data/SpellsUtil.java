package net.victium.xelg.notatry.data;

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
}
