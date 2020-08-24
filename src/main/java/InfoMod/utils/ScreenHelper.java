package InfoMod.utils;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class ScreenHelper {
//    // Singleton pattern
//    private static class ScreenHelperHolder {
//        private static final ScreenHelper INSTANCE = new ScreenHelper();
//    }
//
//    private static ScreenHelper getInstance() {
//        return ScreenHelperHolder.INSTANCE;
//    }

//    private static boolean inCustomScreen = false;
//    private static AbstractDungeon.CurrentScreen prevScreen = null;

    // basically the same code called as MasterDeckViewScreen.open() from TopPanel
//    public static void openCustomScreen(String soundID) {
//        if (!CardCrawlGame.isInARun())
//            return;
//
//        // Store this for later
//        prevScreen = AbstractDungeon.screen;
//
//        // Base game screen closing
//        AbstractDungeon.closeCurrentScreen();
//
//        AbstractDungeon.player.releaseCard();
//        CardCrawlGame.sound.play(soundID);
//
//        AbstractDungeon.dynamicBanner.hide();
//        AbstractDungeon.isScreenUp = true;
//
//        AbstractDungeon.overlayMenu.proceedButton.hide();
//        AbstractDungeon.overlayMenu.hideCombatPanels();
//        AbstractDungeon.overlayMenu.showBlackScreen();
//
//        inCustomScreen = true;
//    }

    public static void openCustomScreen(String soundID) {
        if (!CardCrawlGame.isInARun())
            return;

        // Close / hide the existing screens

        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
            AbstractDungeon.closeCurrentScreen();
            //AbstractDungeon.deckViewScreen.open();
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
        } else if (!AbstractDungeon.isScreenUp) {
//            AbstractDungeon.deckViewScreen.open();
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
            // TODO: (may need to look at map view?)
            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW;
//            AbstractDungeon.screenSwap = false;
//            if (AbstractDungeon.previousScreen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
//                AbstractDungeon.previousScreen = null;
//            }
//            AbstractDungeon.closeCurrentScreen();
//            CardCrawlGame.sound.play("DECK_CLOSE", 0.05f);
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.DEATH) {
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.DEATH;
            AbstractDungeon.deathScreen.hide();
            //AbstractDungeon.deckViewScreen.open();
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD) {
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.BOSS_REWARD;
            AbstractDungeon.bossRelicScreen.hide();
            //AbstractDungeon.deckViewScreen.open();
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP) {
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.SHOP;
            //AbstractDungeon.deckViewScreen.open();
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP && !AbstractDungeon.dungeonMapScreen.dismissable) {
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.MAP;
            //AbstractDungeon.deckViewScreen.open();
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SETTINGS || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {
//            if (AbstractDungeon.previousScreen != null) {
//                AbstractDungeon.screenSwap = true;
//            }
            AbstractDungeon.closeCurrentScreen();
            //AbstractDungeon.deckViewScreen.open();
        }
        // TODO: ignore this for now?
//        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.INPUT_SETTINGS && clickedDeckButton) {
//            if (AbstractDungeon.previousScreen != null) {
//                AbstractDungeon.screenSwap = true;
//            }
//            AbstractDungeon.closeCurrentScreen();
//            AbstractDungeon.deckViewScreen.open();
//        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD) {
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.CARD_REWARD;
            AbstractDungeon.dynamicBanner.hide();
            //AbstractDungeon.deckViewScreen.open();
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.GRID;
            AbstractDungeon.gridSelectScreen.hide();
            //AbstractDungeon.deckViewScreen.open();
        } else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.HAND_SELECT;
            //AbstractDungeon.deckViewScreen.open();
        }

        // Open our custom screen
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.isScreenUp = true;

        // TODO:
        //AbstractDungeon.screen = AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW;

        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.hideCombatPanels();
        AbstractDungeon.overlayMenu.showBlackScreen();
        //AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);

        CardCrawlGame.sound.play(soundID);
        //InputHelper.justClickedLeft = false;
    }

    // pretend to be the master deck view and let the base game handle the rest.
    // (4_000_000 iq)
    //
    // (this is like my 5th attempt at this custom screen stuff -- this one seems to work well enough, but there are
    //   some bugs (i know at least of one: open/close pair while on card rewards screen can hide all rewards)
    public static void closeCustomScreen(String soundID) {
        if (!CardCrawlGame.isInARun())
            return;

//        if (!inCustomScreen)
//            return;

        CardCrawlGame.sound.play(soundID);

        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW;
        AbstractDungeon.closeCurrentScreen();

        // TODO: see if this spoof is still needed?
//        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW;
//        AbstractDungeon.closeCurrentScreen();
//
//        // Restore the previous screen
//        if (prevScreen != null)
//            AbstractDungeon.screen = prevScreen;
//
//        inCustomScreen = false;
    }
}
