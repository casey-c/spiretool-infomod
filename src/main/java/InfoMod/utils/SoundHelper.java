package InfoMod.utils;

import InfoMod.InfoMod;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SoundHelper {
    // TODO: queue as SfxAction instead of directly playing?

    public static void playMapSelectSound() {
        int roll = MathUtils.random(3);
        switch (roll) {
            case 0: {
                CardCrawlGame.sound.play("MAP_SELECT_1");
                break;
            }
            case 1: {
                CardCrawlGame.sound.play("MAP_SELECT_2");
                break;
            }
            case 2: {
                CardCrawlGame.sound.play("MAP_SELECT_3");
                break;
            }
            default: {
                CardCrawlGame.sound.play("MAP_SELECT_4");
            }
        }
    }

    public static void playMapHoveredSound() {
        int roll = MathUtils.random(3);
        switch (roll) {
            case 0: {
                CardCrawlGame.sound.play("MAP_HOVER_1");
                break;
            }
            case 1: {
                CardCrawlGame.sound.play("MAP_HOVER_2");
                break;
            }
            case 2: {
                CardCrawlGame.sound.play("MAP_HOVER_3");
                break;
            }
            default: {
                CardCrawlGame.sound.play("MAP_HOVER_4");
            }
        }
    }

    public static void playUIHoverSound() {
        CardCrawlGame.sound.play("UI_HOVER");
    }

    public static void playMapCloseSound() {
        CardCrawlGame.sound.play("MAP_CLOSE");
    }

}
