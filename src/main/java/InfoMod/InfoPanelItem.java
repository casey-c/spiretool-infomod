package InfoMod;

import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;

public class InfoPanelItem extends TopPanelItem {
    private static final Texture IMG = new Texture("images/icon.png");
    public static final String ID = "ojb_infomod:InfoIcon";

    private int potion_chance = 40;

    boolean isHovered = false;

    public InfoPanelItem() {
        super(IMG, ID);
    }

    public void setPotionChance(int p) {
        this.potion_chance = p;
    }

    @Override
    protected void onClick() {
        System.out.println("OJB: info panel item clicked!");
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        float titleY = (float)Settings.HEIGHT - 26.0F * Settings.scale;

        // Font stuff
        FontHelper.renderFontLeftTopAligned(
                sb,
                FontHelper.tipBodyFont,
                //CharStat.formatHMSM(CardCrawlGame.playtime),
                "!Potions: " + potion_chance + "%",
                //(float)Settings.WIDTH - 320.0F * Settings.scale,
                //(float)Settings.WIDTH - 533.0F * Settings.scale,
                (float)Settings.WIDTH - 475.0F * Settings.scale,
                titleY,
                Settings.CREAM_COLOR);
    }

    @Override
    protected void onHover() {
        super.onHover();

        float TIP_Y = (float)Settings.HEIGHT - 120.0F * Settings.scale;
        float TOP_RIGHT_TIP_X = 1550.0F * Settings.scale;

        // Tool tip
        TipHelper.renderGenericTip(TOP_RIGHT_TIP_X, TIP_Y, "Event Chances", "TODO!");


        // Debug (fires only on change to avoid spamming logger)
        if (!isHovered) {
            isHovered = true;
            System.out.println("OJB: hovered");
        }
    }

    @Override
    protected void onUnhover() {
        super.onUnhover();

        if (isHovered) {
            isHovered = false;
            System.out.println("OJB: unhovered");
        }
    }
}
