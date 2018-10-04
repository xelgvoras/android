package net.victium.xelg.notatry.data;

import android.util.ArrayMap;
import android.widget.Toast;

import net.victium.xelg.notatry.enums.SPV;

import java.util.ArrayList;
import java.util.Map;

public class SpellsUtil {

    private SpellsUtil(){}

    public static class Spell {

        private String mName;
        private String mTarget;
        private String mType;
        private String mElement;
        private Map mEffect;

        public Spell(String mName, String mTarget, String mType, Map mEffect) {
            this.mName = mName;
            this.mTarget = mTarget;
            this.mType = mType;
            this.mEffect = mEffect;
        }

        public String getName() {
            return mName;
        }

        public String getTarget() {
            return mTarget;
        }

        public String getType() {
            return mType;
        }

        public String getElement() {
            return mElement;
        }

        public void setElement(String element) {
            this.mElement = element;
        }

        public Map getEffect() {
            return mEffect;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static Spell getSpell(String spellName, String characterType) {

        Spell returnSpell;
        ArrayMap<SPV, String> effectArrayMap = new ArrayMap<>();

        switch (spellName) {
            case "Тройное лезвие":
                effectArrayMap.put(SPV.DROP, "Сильное кровотечение, ранение средней тяжести");
                returnSpell = new Spell(spellName, "напр", "боевое", effectArrayMap);
                break;
            default:
                return null;
        }

        return returnSpell;
    }
}
