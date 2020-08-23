package InfoMod.modules.cardplays;

import InfoMod.InfoMod;
import basemod.BaseMod;
import basemod.interfaces.OnCardUseSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostEnergyRechargeSubscriber;
import basemod.interfaces.PreMonsterTurnSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpireInitializer
public class CardPlays implements OnCardUseSubscriber, OnStartBattleSubscriber, PreMonsterTurnSubscriber, PostEnergyRechargeSubscriber {

    private CardPlaysOverlay overlay;

    private int current_turn = 1;

    private int cards_played_this_turn = 0;
    private int cards_played_this_combat = 0;

    private int turn_attacks = 0;
    private int turn_skills = 0;
    private int turn_powers = 0;

    private int combat_attacks = 0;
    private int combat_skills = 0;
    private int combat_powers = 0;

    private float combat_attacks_avg = 0.0f;
    private float combat_skills_avg = 0.0f;
    private float combat_power_avg = 0.0f;
    private float combat_tot_avg = 0.0f;


    public static void initialize() {
        System.out.println("OJB: init card plays");
    }

    public CardPlays() {
        BaseMod.subscribe(this);
        System.out.println("OJB: created card plays");

        overlay = new CardPlaysOverlay();
    }

    private void recomputeCombatAvg() {
        // TODO
    }

    private void recomputeActAvg() {
        // TODO
    }

    @Override
    public void receiveCardUsed(AbstractCard abstractCard) {
        ++cards_played_this_turn;
        ++cards_played_this_combat;

        if (abstractCard.type == AbstractCard.CardType.ATTACK) {
            ++turn_attacks;
            ++combat_attacks;
        }
        else if (abstractCard.type == AbstractCard.CardType.SKILL) {
            ++turn_skills;
            ++combat_skills;
        }
        else if (abstractCard.type == AbstractCard.CardType.POWER) {
            ++turn_powers;
            ++combat_powers;
        }

        recomputeCombatAvg();

        overlay.updateTurnCards(turn_attacks, turn_skills, turn_powers, cards_played_this_turn);
        overlay.updateCombatCards(combat_attacks, combat_skills, combat_powers, cards_played_this_combat, combat_attacks_avg, combat_skills_avg, combat_power_avg, combat_tot_avg);

        // TODO: update all the other things that change here
        //recomputeActAvg(); // TODO

        System.out.println("OJB: card used | this turn: " + cards_played_this_turn + " | this combat: " + cards_played_this_combat);
    }

    // For showing / hiding the card stats overlay
    public void toggleVisibility() {
        overlay.toggleVisibility();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        current_turn = 1;

        // reset
        cards_played_this_turn = 0;
        cards_played_this_combat = 0;

        turn_attacks = 0;
        turn_skills = 0;
        turn_powers = 0;

        combat_attacks = 0;
        combat_skills = 0;
        combat_powers = 0;

        combat_attacks_avg = 0.0f;
        combat_skills_avg = 0.0f;
        combat_power_avg = 0.0f;
        combat_tot_avg = 0.0f;

        // update the overlay
        overlay.updateTurnNumber(current_turn);
        overlay.updateTurnCards(turn_attacks, turn_skills, turn_powers, cards_played_this_turn);
        overlay.updateCombatCards(combat_attacks, combat_skills, combat_powers, cards_played_this_combat, combat_attacks_avg, combat_skills_avg, combat_power_avg, combat_tot_avg);

        System.out.println("OJB: battle started, reset turn to: " + current_turn);

    }

    @Override
    public boolean receivePreMonsterTurn(AbstractMonster abstractMonster) {
        ++current_turn;
        overlay.updateTurnNumber(current_turn);

        System.out.println("OJB: turn ended, set turn to: " + current_turn);
        return true;
    }

    @Override
    public void receivePostEnergyRecharge() {
        System.out.println("OJB: turn started");
        turn_attacks = turn_skills = turn_powers = cards_played_this_turn = 0;
        overlay.updateTurnCards(turn_attacks, turn_skills, turn_powers, cards_played_this_turn);
    }
}
