package InfoMod.modules.potions;

import InfoMod.InfoMod;
import InfoMod.utils.RenderingUtils;
import InfoMod.utils.config.Config;
import InfoMod.utils.integration.SlayTheRelicsIntegration;
import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;

public class PotionPanelItem extends TopPanelItem {
    private static final Texture IMG = new Texture("images/null.png");
    public static final String ID = "ojb_infomod:PotionPanelIcon";

    private static final Texture TEX_POTION = new Texture("images/potion.png");

    private static final float ITEM_WIDTH = 213.0f;
    private static final float TEXT_SHIFT = 422.0f;

    private int potion_chance;

    private ArrayList<PowerTip> tips;
    private PowerTip future_tip;

    public PotionPanelItem() {
        super(IMG, ID);

        hb_w = ITEM_WIDTH;
        this.hitbox.width = ITEM_WIDTH;

        future_tip = new PowerTip("Potion Chances", "TBD");
        PowerTip info_tip = new PowerTip("Info", "The chance for a potion to drop from any combat (including boss, elite, event, and normal combats) starts at 40% at the beginning of each Act. NL NL When a potion drops, the chance decreases by 10%, and when it doesn't drop, the chance increases by 10%.");

        tips = new ArrayList<>();
        tips.add(future_tip);
        tips.add(info_tip);
    }

    private String buildFutureChances(int p) {
        float f = ((float) p) / 100.0f;

        float after2 = (1.0f - f) * (0.9f - f);
        float after3 = after2 * (0.8f - f);
        float after4 = after3 * (0.7f - f);
        float after5 = after4 * (0.6f - f);

        // Relic overrides
        AbstractPlayer player = AbstractDungeon.player;
        if (player != null) {
            // Note: should be no need for WBS since it should be already all 100%

            // Sozu sets all future chance to zero
            if (player.hasRelic("Sozu")) {
                after2 = after3 = after4 = after5 = 1.0f;
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("After multiple fights: NL NL ");

        builder.append("2:      ");
        builder.append(String.format("%.02f", ((1.0f - after2) * 100.0f)));
        builder.append("% NL ");

        builder.append("3:      ");
        builder.append(String.format("%.02f", ((1.0f - after3) * 100.0f)));
        builder.append("% NL ");

        builder.append("4:      ");
        builder.append(String.format("%.02f", ((1.0f - after4) * 100.0f)));
        builder.append("% NL ");

        builder.append("5:      ");
        builder.append(String.format("%.02f", ((1.0f - after5) * 100.0f)));
        builder.append("% NL ");

        return builder.toString();
    }

    public void setPotionChance(int p) {
        this.potion_chance = p;
        future_tip.body = buildFutureChances(p);

        if (!Config.getBool(Config.ConfigOptions.SHOW_POTIONS))
            return;

//        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_POTIONS) == false)
//            return;

        SlayTheRelicsIntegration.update("potionPanelItem", hitbox, tips);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

//        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_POTIONS) == false)
//            return;
        if (!Config.getBool(Config.ConfigOptions.SHOW_POTIONS))
            return;

        // Font stuff
        //float textX = (float) Settings.WIDTH - TEXT_SHIFT * Settings.scale;
        //float textY = (float)Settings.HEIGHT - 26.0F * Settings.scale;
//        String text = SaveableManager.potionChanceCustom.customText;
//        float textX = SaveableManager.potionChanceCustom.x;
//        float textY = SaveableManager.potionChanceCustom.y;

//        String text = ConfigHelper.getInstance().getString(ConfigHelper.StringSettings.POTION_CHANCE);
//        float textX = (float)ConfigHelper.getInstance().getInt(ConfigHelper.IntSettings.POTION_X);
//        float textY = (float)ConfigHelper.getInstance().getInt(ConfigHelper.IntSettings.POTION_Y);

        String text = Config.getString(Config.ConfigOptions.POTION_TEXT);
        float textX = (float)Config.getInt(Config.ConfigOptions.POTION_X);
        float textY = (float)Config.getInt(Config.ConfigOptions.POTION_Y);

        sb.setColor(Color.WHITE);

        if (Config.getBool(Config.ConfigOptions.SHOW_POTION_IMG)) {
            sb.draw(TEX_POTION,
                    textX - (64.0f * Settings.scale),
                    textY - (39.0f * Settings.scale),
                    0.0f,
                    0.0f,
                    TEX_POTION.getWidth() * Settings.scale,
                    TEX_POTION.getHeight() * Settings.scale,
                    1.0f,
                    1.0f,
                    0.0f,
                    0,
                    0,
                    TEX_POTION.getWidth(),
                    TEX_POTION.getHeight(),
                    false,
                    false
                    );
        }

        // Special rainbow text is locked behind the Terr80 setting
        //if ((potion_chance == 80) && ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.TERR80)) {
        if ((potion_chance == 80) && Config.getBool(Config.ConfigOptions.TERR80)) {
            //RenderingUtils.renderRainbowTextTipFont(sb, "!Potions: " + potion_chance + "%", textX, textY);
            RenderingUtils.renderRainbowTextTipFont(sb,  text + potion_chance + "%", textX, textY);
        }
        else {
            //FontHelper.renderFontLeftTopAligned(
            RenderingUtils.renderSmarterText(
                    sb,
                    FontHelper.topPanelAmountFont,
                    text + potion_chance + "%",
                    textX,
                    textY,
                    Settings.CREAM_COLOR);
        }

    }

    @Override
    protected void onHover() {
        super.onHover();

//        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_POTIONS) == false)
//            return;
        if (!Config.getBool(Config.ConfigOptions.SHOW_POTIONS))
            return;

        float TIP_Y = (float) Settings.HEIGHT - 120.0F * Settings.scale;
        float TOP_RIGHT_TIP_X = Math.min(1550.0F, InputHelper.mX) * Settings.scale;

        // Add to render queue
        TipHelper.queuePowerTips(TOP_RIGHT_TIP_X, TIP_Y, tips);
    }

    @Override
    protected void onClick() {
        // do nothing
    }
}
