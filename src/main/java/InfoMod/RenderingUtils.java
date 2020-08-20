package InfoMod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;


/*
  This utility class holds all the rendering and graphics specific functions and defines that may be needed. It
  currently just stores some of my own desired color schemes as well as some modified versions of the text rendering
  code from the base game.
 */
public class RenderingUtils {
    public static final Color OJB_GRAY_COLOR = new Color(0.6f, 0.6f, 0.6f, 1.0f);
    public static final Color OJB_DARK_GRAY_COLOR = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    public static final Color OJB_DEBUFF_COLOR = new Color(0.208f, 0.714f, 0.259f, 1.0f);
    public static final Color OJB_BLOCK_COLOR = Settings.BLUE_TEXT_COLOR;
    public static final Color OJB_DAMAGE_COLOR = Settings.RED_TEXT_COLOR;
    public static final Color OJB_BUFF_COLOR = new Color(1.0f, 0.875f, 0.0f, 1.0f);

    public static final Color OJB_DIM_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.75f);

    public static final Color OJB_GREEN_BUTTON_COLOR = new Color(0.212f, 0.369f, 0.224f, 1.0f);
    public static final Color OJB_RED_BUTTON_COLOR = new Color(0.502f, 0.0f, 0.0f, 1.0f);

    // UNUSED COLORS
    //public static final Color OJB_BUFF_COLOR = new Color(1.0f, 0.353f, 0.498f, 1.0f); // nice pinkish / too close to red
    //public static final Color OJB_BROWN_COLOR = new Color(0.827f, 0.553f, 0.373f, 1.0f);


    // Modified version can return null (instead of white) and can use newer colors
    // TODO: verify if the .cpy() copies are actually required (i doubt it, but original codebase does it)
    private static Color identifyColor(String word) {
        // note: fixed the word.length bug (original could crash on out of bounds)
        if (word.length() > 1 && word.charAt(0) == '#') {
            switch(word.charAt(1)) {
                // Currently:
                //    #w    white (cream)
                //    #g    gray (darker than the cream - 60% black)
                //    #r    red
                //    #d    debuff
                //    #u    buff ("upgrade")
                //    #b    block
                case 'w':
                    return Settings.CREAM_COLOR.cpy();
                case 'g':
                    return OJB_GRAY_COLOR.cpy();
                case 'r':
                    return Settings.RED_TEXT_COLOR.cpy();
                case 'd':
                    return OJB_DEBUFF_COLOR.cpy();
                case 'u':
                    return OJB_BUFF_COLOR.cpy();
                case 'b':
                    return Settings.BLUE_TEXT_COLOR.cpy();
                default:
                    return null;
                    // NOTE: old version returned white; this returns null to let the modified smartText function handle it
                    // a little bit better and more flexible
                    //return Color.WHITE;
            }
        } else {
            return null;
        }
    }

    // Converts a string like "Hello World" to "#gHello #gWorld" so it can be painted green (or other colors)
    public static String colorify(String color, String str) {
        StringBuilder sb = new StringBuilder();

        for (String w : str.split(" ")) {
            sb.append(color);
            sb.append(w);
            sb.append(" ");
        }

        return sb.toString();
    }

    // Modified version of the base code to handle my own special font colors and rendering
    public static void renderSmartText(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor) {
        if (msg != null) {
            float curWidth = 0.0F;
            float curHeight = 0.0F;

            GlyphLayout layout = FontHelper.layout;

            layout.setText(font, " ");
            float spaceWidth = layout.width;


            String[] var8 = msg.split(" ");
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String word = var8[var10];
                if (word.equals("NL")) {
                    curWidth = 0.0F;
                    curHeight -= lineSpacing;
                } else if (word.equals("TAB")) {
                    curWidth += spaceWidth * 5.0F;
                } else {
                    Color color = identifyColor(word);

                    // No color specified for this word
                    if (color == null) {
                        font.setColor(baseColor);
                    }
                    // Use the result of the identify color method
                    else {
                        word = word.substring(2, word.length());
                        color.a = baseColor.a;
                        font.setColor(color);
                    }

                    layout.setText(font, word);
                    if (curWidth + layout.width > lineWidth) {
                        curHeight -= lineSpacing;
                        font.draw(sb, word, x, y + curHeight);
                        curWidth = layout.width + spaceWidth;
                    } else {
                        font.draw(sb, word, x + curWidth, y + curHeight);
                        curWidth += layout.width + spaceWidth;
                    }
                }
            }

            layout.setText(font, msg);
        }
    }

    public static void renderRainbowTextTipFont(SpriteBatch sb, String msg, float x, float y) {

        float r = (MathUtils.cosDeg((float) (System.currentTimeMillis() / 10L % 360L)) + 1.25F) / 2.3F;
        float g = (MathUtils.cosDeg((float)((System.currentTimeMillis() + 1000L) / 10L % 360L)) + 1.25F) / 2.3F;
        float b = (MathUtils.cosDeg((float)((System.currentTimeMillis() + 2000L) / 10L % 360L)) + 1.25F) / 2.3F;
        Color c = new Color(r, g, b, 1.0f);

        FontHelper.renderFontLeftTopAligned(
        //renderSmarterText(
                sb,
                //FontHelper.tipBodyFont,
                FontHelper.topPanelAmountFont,
                //FontHelper.charDescFont,
                //FontHelper.charTitleFont,
                //FontHelper.topPanelInfoFont,
                msg,
                x,
                y,
                c);
    }

    public static String fixBadPoundSigns(String input) {
        String cleaned = input;
        cleaned = cleaned.replaceAll("# ", "#");

        while ((cleaned.endsWith("#")) && cleaned.length() > 0) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }

    // IMPORTANT: use these "smarter" font rendering calls if you have unsanitized strings needing rendering
    //     i.e. letting users type whatever they want

    // Original code will crash with how it handles # symbols. (it expects a two character pair #w, with [w] being a
    // color e.g. r for r, w for white, b for blue, etc.)
    // This smarter code will strip any # that end a string
    public static void renderSmarterText(SpriteBatch sb, BitmapFont font, String msg, float x, float y, Color baseColor) {
        // Strip # from the end of the string
        String cleaned = fixBadPoundSigns(msg);
        FontHelper.renderSmartText(sb, font, cleaned, x, y, baseColor);
    }

    public static void renderSmarterText(SpriteBatch sb, BitmapFont font, String msg, float x, float y, float lineWidth, float lineSpacing, Color baseColor) {
        String cleaned = fixBadPoundSigns(msg);
        FontHelper.renderSmartText(sb, font, cleaned, x, y, lineWidth, lineSpacing, baseColor);
    }

    public static float getSmarterWidth(BitmapFont font, String msg, float lineWidth, float lineSpacing) {
        String cleaned = fixBadPoundSigns(msg);
        return FontHelper.getSmartWidth(font, cleaned, lineWidth, lineSpacing);
    }

    // basically the same code called as MasterDeckViewScreen.open() from TopPanel
    public static void openCustomScreen(String soundID) {
        AbstractDungeon.closeCurrentScreen();

        AbstractDungeon.player.releaseCard();
        CardCrawlGame.sound.play(soundID);

        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.isScreenUp = true;

        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.hideCombatPanels();
        AbstractDungeon.overlayMenu.showBlackScreen();
    }

    // pretend to be the master deck view and let the base game handle the rest.
    // (4_000_000 iq)
    //
    // (this is like my 5th attempt at this custom screen stuff -- this one seems to work well enough, but there are
    //   some bugs (i know at least of one: open/close pair while on card rewards screen can hide all rewards)
    public static void closeCustomScreen(String soundID) {
        CardCrawlGame.sound.play(soundID);
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW;
        AbstractDungeon.closeCurrentScreen();
    }
}
