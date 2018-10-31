package net.victium.xelg.notatry.utilities;

import android.util.ArrayMap;

import net.victium.xelg.notatry.enums.SPV;

import java.util.Map;

public class SpellsUtil {

    private static final String BATTLE_FORM = "боевая форма";
    private static final String LIGHT_DAMAGE = "легкое ранение";
    private static final String MEDIUM_DAMAGE = "ранение средней тяжести";
    private static final String HEAVY_DAMAGE = "тяжелое ранение";
    private static final String YOU_DIE = "вы мертвы";
    private static final String NO_EFFECT = "Не действует";

    private static final String TARGET_PERSONAL = "напр";
    private static final String TARGET_AREA = "область";
    private static final String TARGET_MASS = "массовое";
    private static final String TYPE_BATTLE = "боевое";
    private static final String TYPE_UNIVERSAL = "универсальное";
    private static final String TYPE_MENTAL = "ментальное";
    private static final String SPEED_FAST = "быстрое";
    private static final String SPEED_SLOW = "медленное";

    private static final String VAMPIRE = "Вампир";

    private SpellsUtil(){}

    public static class Spell {

        // TODO(19) Добавить минимальную стоимость заклинаний, отметить с фиксированной стоимостью

        private String mName;
        private String mTarget;
        private String mType;
        private String mElement;
        private String mSpeed;
        private Map mEffect;
        private boolean mCanDodge;

        Spell(String mName, String mTarget, Map mEffect) {
            this.mName = mName;
            this.mTarget = mTarget;
            this.mType = TYPE_BATTLE;
            this.mEffect = mEffect;
            this.mCanDodge = true;
            this.mSpeed = SPEED_FAST;
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

        public void setType(String type) {
            this.mType = type;
        }

        public String getElement() {
            return mElement;
        }

        public Map getEffect() {
            return mEffect;
        }

        public boolean isCanDodge() {
            return mCanDodge;
        }

        public String  getSpeed() {
            return mSpeed;
        }

        void setElement() {
            this.mElement = "огонь";
        }

        void setDodgeRestricted() {
            this.mCanDodge = false;
        }

        void setSlowSpeed() {
            this.mSpeed = SPEED_SLOW;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static Spell getSpell(String spellName, String battleForm, String characterType) {

        Spell returnSpell;
        ArrayMap<SPV, String> effectArrayMap = new ArrayMap<>();

        switch (spellName) {
            case "Тройное лезвие":
                effectArrayMap.put(SPV.DROP, "Сильное кровотечение, " + MEDIUM_DAMAGE);
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                break;
            case "Файербол":
                effectArrayMap.put(SPV.DROP, "Ожоги, оглушен, " + MEDIUM_DAMAGE);
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setElement();
                break;
            case "Ледяная глыба":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.DROP, "Обморожение, замедлен, " + LIGHT_DAMAGE);
                } else {
                    effectArrayMap.put(SPV.DROP, "Обморожение, оглушен, замедлен, " + LIGHT_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                break;
            case "Столп огня (Подгорает)":
                effectArrayMap.put(SPV.DROP, "Ожоги, оглушен, " + MEDIUM_DAMAGE);
                returnSpell = new Spell(spellName, TARGET_AREA, effectArrayMap);
                returnSpell.setElement();
                returnSpell.setSlowSpeed();
                break;
            case "Кольцо огня":
                effectArrayMap.put(SPV.DROP, "Ожоги, оглушен, " + MEDIUM_DAMAGE);
                returnSpell = new Spell(spellName, TARGET_AREA, effectArrayMap);
                returnSpell.setElement();
                returnSpell.setSlowSpeed();
                break;
            case "Стена огня":
                effectArrayMap.put(SPV.DROP, "Ожоги, оглушен, " + MEDIUM_DAMAGE);
                returnSpell = new Spell(spellName, TARGET_AREA, effectArrayMap);
                returnSpell.setElement();
                returnSpell.setSlowSpeed();
                break;
            case "Кольцо холода":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                } else if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BURST, "Замедлен");
                    effectArrayMap.put(SPV.DROP, "Замедлен");
                } else {
                    effectArrayMap.put(SPV.DROP, "Обморожение, замедлен, " + LIGHT_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_MASS, effectArrayMap);
                returnSpell.setDodgeRestricted();
                break;
            case "Близзард":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                } else if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.DROP, "Порезы, " + MEDIUM_DAMAGE);
                } else {
                    effectArrayMap.put(SPV.DROP, "Обморожение, оглушен, многочисленные сильные порезы, " + HEAVY_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_AREA, effectArrayMap);
                returnSpell.setSlowSpeed();
                break;
            case "Поцелуй ехидны":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                } else {
                    effectArrayMap.put(SPV.DROP, "Ожоги, оглушен, " + MEDIUM_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setDodgeRestricted();
                returnSpell.setSlowSpeed();
                break;
            case "Черный дождь":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                } else {
                    effectArrayMap.put(SPV.DROP, "Ожоги, оглушен, " + MEDIUM_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_MASS, effectArrayMap);
                returnSpell.setDodgeRestricted();
                returnSpell.setSlowSpeed();
                break;
            case "Белый меч":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.BLOCK, MEDIUM_DAMAGE);
                    effectArrayMap.put(SPV.BURST, HEAVY_DAMAGE);
                    effectArrayMap.put(SPV.DROP, "Оглушен, " + HEAVY_DAMAGE);
                } else {
                    effectArrayMap.put(SPV.DROP, "Оглушен, " + HEAVY_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                break;
            case "Копье страданий":
                effectArrayMap.put(SPV.DROP, "Недееспособен");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                break;
            case "Копье света":
                effectArrayMap.put(SPV.DROP, "Недееспособен");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                break;
            case "Вакуумный удар":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.DROP, "Сбиты с ног, пропуск хода");
                } else if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.DROP, "Переохлаждение, удушье, декомпрессия, ударная волна, оглушен, " + MEDIUM_DAMAGE);
                } else {
                    effectArrayMap.put(SPV.BURST, "Переохлаждение, удушье, декомпрессия, ударная волна, оглушен, " + MEDIUM_DAMAGE);
                    effectArrayMap.put(SPV.DROP, "Переохлаждение, удушье, декомпрессия, ударная волна, оглушен, " + MEDIUM_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_MASS, effectArrayMap);
                returnSpell.setDodgeRestricted();
                returnSpell.setSlowSpeed();
                break;
            case "Тайга":
                effectArrayMap.put(SPV.DROP, "Если пошевелитесь: " + YOU_DIE);
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setDodgeRestricted();
                break;
            case "Марево Трансильвании":
                effectArrayMap.put(SPV.DROP, YOU_DIE + " особо неприглядным образом");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setDodgeRestricted();
                returnSpell.setSlowSpeed();
                break;
            case "Искрящаяся Стена":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.DROP, YOU_DIE);
                } else {
                    effectArrayMap.put(SPV.DROP, "Оглушен");
                }
                returnSpell = new Spell(spellName, TARGET_AREA, effectArrayMap);
                break;
            case "Плеть Шааба":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.BURST, HEAVY_DAMAGE);
                    effectArrayMap.put(SPV.DROP, HEAVY_DAMAGE);
                } else {
                    effectArrayMap.put(SPV.DROP, "Кучка пепла, " + YOU_DIE);
                }
                returnSpell = new Spell(spellName, TARGET_MASS, effectArrayMap);
                returnSpell.setDodgeRestricted();
                break;
            case "Телекинез":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                } else {
                    effectArrayMap.put(SPV.DROP, "Отправлен в полет");
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Пресс":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BURST, "Замедлен");
                    effectArrayMap.put(SPV.DROP, "Нельзя двигаться, колдовать, использовать базовые умения");
                } else {
                    effectArrayMap.put(SPV.DROP, "Нельзя двигаться, колдовать, использовать базовые умения");
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Шок":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                } else {
                    effectArrayMap.put(SPV.DROP, "Нельзя двигаться, колдовать и разговаривать");
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Серый волк":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BURST, "Обратная трансформация, способность к трансформации заблокирована на 10 ходов");
                    effectArrayMap.put(SPV.DROP, "Обратная трансформация, способность к трансформации заблокирована на 10 ходов");
                } else {
                    effectArrayMap.put(SPV.DROP, "Заблокирована способность к трансформации");
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Путы Захви":
                effectArrayMap.put(SPV.DROP, "Нельзя двигаться, колдовать и как-то действовать");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Серый молебен":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.BLOCK, "Замедлен");
                    effectArrayMap.put(SPV.BURST, "Оглушен");
                    effectArrayMap.put(SPV.DROP, "Кучка пепла, " + YOU_DIE);
                } else {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Серый молебен (массовое)":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.BURST, "Замедлен");
                    effectArrayMap.put(SPV.DROP, "Оглушен");
                } else {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                }
                returnSpell = new Spell(spellName, TARGET_MASS, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                returnSpell.setDodgeRestricted();
                break;
            case "Фриз":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BURST, "Для вас время застыло");
                    effectArrayMap.put(SPV.DROP, "Для вас время застыло");
                } else {
                    effectArrayMap.put(SPV.DROP, "Для вас время застыло");
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Кольцо Шааба":
                if (characterType.equals(VAMPIRE)) {
                    effectArrayMap.put(SPV.BURST, "Отправлен в полет");
                    effectArrayMap.put(SPV.DROP, "Отправлен в полет");
                } else if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BLOCK, "Отправлен в полет");
                    effectArrayMap.put(SPV.BURST, "Отправлен в полет, " + MEDIUM_DAMAGE);
                    effectArrayMap.put(SPV.DROP, "Отправлен в полет, оглушен, переломы, " + MEDIUM_DAMAGE);
                }
                returnSpell = new Spell(spellName, TARGET_MASS, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                returnSpell.setDodgeRestricted();
                break;
            case "Масс пресс":
                if (BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BURST, "Замедлен");
                    effectArrayMap.put(SPV.DROP, "Нельзя двигаться, колдовать, использовать базовые умения");
                } else {
                    effectArrayMap.put(SPV.DROP, "Нельзя двигаться, колдовать, использовать базовые умения");
                }
                returnSpell = new Spell(spellName, TARGET_MASS, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                returnSpell.setDodgeRestricted();
                break;
            case "Экспроприация":
                // COMPLETED(bug) эффект только если уровень атакующего выше ващего
                if (characterType.equals(VAMPIRE) || BATTLE_FORM.equals(battleForm)) {
                    effectArrayMap.put(SPV.BLOCK, NO_EFFECT);
                    effectArrayMap.put(SPV.BURST, NO_EFFECT);
                    effectArrayMap.put(SPV.DROP, NO_EFFECT);
                } else {
                    effectArrayMap.put(SPV.DROP, "Заблокированы все способности иного, включая базовые, если уровень атакующего выше вашего");
                }
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_UNIVERSAL);
                break;
            case "Сократ":
                effectArrayMap.put(SPV.SPECIAL, "Вы должны правдиво ответить на 1 вопрос");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Морфей":
                effectArrayMap.put(SPV.SPECIAL, "Медленно засыпаете в течении 5 секунд");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Игла страха":
                effectArrayMap.put(SPV.SPECIAL, "Вызывает панику и ужас, пытаетесь убежать из боя");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Длинный язык":
                effectArrayMap.put(SPV.SPECIAL, "Начинаете разбалтывать все сокровенное по подсказанной теме");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Внушение":
                effectArrayMap.put(SPV.SPECIAL, "Вам внушили какую-то мысль, идею, желание что-то сделать. Воспринимаете как свое собственное");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Отвод глаз":
                effectArrayMap.put(SPV.SPECIAL, "Игнорирует кастававшего или указанное им действие");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Ступор":
                effectArrayMap.put(SPV.SPECIAL, "Впадаете в ступор, после выхода из него - ничего не помните, включая 5 ходов перед заклинанием");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Опиум":
                effectArrayMap.put(SPV.SPECIAL, "Мгновенно засыпаете");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Танатос":
                effectArrayMap.put(SPV.SPECIAL, "Лишаетесь воли к жизни, ложитесь и начинаете добровольно помирать, через 10 ходов - остановка сердца");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Доминанта":
                effectArrayMap.put(SPV.SPECIAL, "Впадаете в ступор и подчиняетесь воле скастававшего мага, выполняете простые команды, но апатично, затем снова в ступор." +
                        " Не можете никому сказать, кто наложил на вас Доминанту");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            case "Деймос":
                effectArrayMap.put(SPV.SPECIAL, "Сильнейшая головная боль, невозможно мыслить и на чем-то сосредоточиться");
                returnSpell = new Spell(spellName, TARGET_PERSONAL, effectArrayMap);
                returnSpell.setType(TYPE_MENTAL);
                break;
            default:
                return null;
        }

        return returnSpell;
    }
}
