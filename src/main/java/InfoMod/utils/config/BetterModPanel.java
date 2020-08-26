package InfoMod.utils.config;

import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

public class BetterModPanel extends ModPanel {
    private static Texture BACKGROUND = new Texture("images/screens/config.png");
    private static final int BG_WIDTH = 1300;
    private static final int BG_HEIGHT = 900;
    private static final float BG_WIDTH_2 = 650.0f;
    private static final float BG_HEIGHT_2 = 450.0f;

    @Override
    public void renderBg(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(BACKGROUND,
                (float) Settings.WIDTH / 2.0F - BG_WIDTH_2,
                Settings.OPTION_Y - BG_HEIGHT_2,
                BG_WIDTH_2,
                BG_HEIGHT_2,
                (float)BG_WIDTH,
                (float)BG_HEIGHT,
                Settings.scale,
                Settings.scale,
                0.0F,
                0,
                0,
                BG_WIDTH,
                BG_HEIGHT,
                false,
                false);
    }

}
