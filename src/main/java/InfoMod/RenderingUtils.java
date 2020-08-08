package InfoMod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class RenderingUtils {
    public static final Color OJB_BROWN_COLOR = new Color(0.827f, 0.553f, 0.373f, 1.0f);
    public static final Color OJB_GRAY_COLOR = new Color(0.6f, 0.6f, 0.6f, 1.0f);
    public static final Color OJB_DARK_GRAY_COLOR = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    public static final Color OJB_DEBUFF_COLOR = new Color(0.208f, 0.714f, 0.259f, 1.0f);
    public static final Color OJB_BLOCK_COLOR = Settings.BLUE_TEXT_COLOR;
    public static final Color OJB_DAMAGE_COLOR = Settings.RED_TEXT_COLOR;

    // nice pinkish that unfortunately blends too much with red
    //public static final Color OJB_BUFF_COLOR = new Color(1.0f, 0.353f, 0.498f, 1.0f);

    // alternate yellow color
    public static final Color OJB_BUFF_COLOR = new Color(1.0f, 0.875f, 0.0f, 1.0f);

    // Modified version can return null (instead of white) and can use newer colors
    // TODO: verify if the .cpy() copies are actually required (i doubt it, but original codebase does it)
    private static Color identifyColor(String word) {
        // note: fixed the word.length bug (original could crash on out of bounds)
        if (word.length() > 1 && word.charAt(0) == '#') {
            switch(word.charAt(1)) {

                // TODO: use the colors I actually want to use
                // Currently:
                //    #w    white (cream)
                //    #g    gray (darker than the cream - 60% black)
                //    #r    red
                //    #d    debuff
                //    #u    buff ("upgrade")
                //    #b    block

//                case 'b':
//                    return Settings.BLUE_TEXT_COLOR.cpy();
//                case 'g':
//                    return Settings.GREEN_TEXT_COLOR.cpy();
//                case 'p':
//                    return Settings.PURPLE_COLOR.cpy();
//                case 'r':
//                    return Settings.RED_TEXT_COLOR.cpy();
//                case 'y':
//                    return Settings.GOLD_COLOR.cpy();
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
                    //return Color.WHITE;
            }
        } else {
            return null;
            //return Color.WHITE;
        }
    }

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
                    //orb = identifyOrb(word);
                    //if (orb == null) {

                    //Color color = identifyColor(word).cpy();
                    //Color color = Settings.GOLD_COLOR.cpy();
                    Color color = identifyColor(word);

                    // No color specified for this word
                    if (color == null) {
                        //color = baseColor.cpy();
                        font.setColor(baseColor);
                    }
                    // Use the result of the identify color method
                    else {
                        word = word.substring(2, word.length());
                        color.a = baseColor.a;
                        font.setColor(color);
                    }

                    // OJB: original code pulls off #r type identifier strings for the colors
                    // (white is assumed to have no identifer, while others are assumed to do so)
//                    if (!color.equals(Color.WHITE)) {
//                        // keep it for now
//                        //word = word.substring(2, word.length());
//
//                        color.a = baseColor.a;
//                        font.setColor(color);
//                    } else {
//                        font.setColor(baseColor);
//                    }

                    layout.setText(font, word);
                    if (curWidth + layout.width > lineWidth) {
                        curHeight -= lineSpacing;
                        font.draw(sb, word, x, y + curHeight);
                        curWidth = layout.width + spaceWidth;
                    } else {
                        font.draw(sb, word, x + curWidth, y + curHeight);
                        curWidth += layout.width + spaceWidth;
                    }
//                    } else {
//                        sb.setColor(new Color(1.0F, 1.0F, 1.0F, baseColor.a));
//                        if (curWidth + CARD_ENERGY_IMG_WIDTH > lineWidth) {
//                            curHeight -= lineSpacing;
//                            sb.draw(orb, x - (float)orb.packedWidth / 2.0F + 13.0F * Settings.scale, y + curHeight - (float)orb.packedHeight / 2.0F - 8.0F * Settings.scale, (float)orb.packedWidth / 2.0F, (float)orb.packedHeight / 2.0F, (float)orb.packedWidth, (float)orb.packedHeight, Settings.scale, Settings.scale, 0.0F);
//                            curWidth = CARD_ENERGY_IMG_WIDTH + spaceWidth;
//                        } else {
//                            sb.draw(orb, x + curWidth - (float)orb.packedWidth / 2.0F + 13.0F * Settings.scale, y + curHeight - (float)orb.packedHeight / 2.0F - 8.0F * Settings.scale, (float)orb.packedWidth / 2.0F, (float)orb.packedHeight / 2.0F, (float)orb.packedWidth, (float)orb.packedHeight, Settings.scale, Settings.scale, 0.0F);
//                            curWidth += CARD_ENERGY_IMG_WIDTH + spaceWidth;
//                        }
//                    }
                }
            }

            layout.setText(font, msg);
        }
    }
}
