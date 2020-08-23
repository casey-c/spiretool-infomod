package InfoMod.modules.cardplays;

import InfoMod.InfoMod;
import basemod.BaseMod;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.exordium.JawWorm;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

//@SpireInitializer
public class CardPlaysOverlay implements RenderSubscriber {
    private boolean visible = false;

    private static final String IMG_URL = "images/screen.png";
    private Texture img = null;

    //--------------------------------------------------------------
    // Strings to display (use the update functions (see bottom of class)
    //--------------------------------------------------------------

    private String turn_number;
    private String turn_attacks, turn_skills, turn_powers, turn_tot;
    private String combat_attacks_avg, combat_skills_avg, combat_powers_avg, combat_tot_avg;
    private String combat_attacks_tot, combat_skills_tot, combat_powers_tot, combat_tot_tot;

    private String act1_attacks_avg, act1_skills_avg, act1_powers_avg, act1_tot_avg;
    private String act1_attacks_tot, act1_skills_tot, act1_powers_tot, act1_tot_tot;

    private String act2_attacks_avg, act2_skills_avg, act2_powers_avg, act2_tot_avg;
    private String act2_attacks_tot, act2_skills_tot, act2_powers_tot, act2_tot_tot;

    private String act3_attacks_avg, act3_skills_avg, act3_powers_avg, act3_tot_avg;
    private String act3_attacks_tot, act3_skills_tot, act3_powers_tot, act3_tot_tot;

    private String act4_attacks_avg, act4_skills_avg, act4_powers_avg, act4_tot_avg;
    private String act4_attacks_tot, act4_skills_tot, act4_powers_tot, act4_tot_tot;

    //--------------------------------------------------------------
    // Positioning information
    //--------------------------------------------------------------

    // NOTE: must call setupPostions() after the game has time to learn what the Settings.WIDTH etc. are (they are 0
    //   before SpireInitializer can see it and thus these will be wrong if not waited for the proper values)
    private float cx, cy;
    private int img_w, img_h;
    private float img_w2, img_h2;

    private float title_x, title_y;
    private float thx_turn,
            thx_combavg, thx_combtot,
            thx_act1avg, thx_act1tot,
            thx_act2avg, thx_act2tot,
            thx_act3avg, thx_act3tot,
            thx_act4avg, thx_act4tot;
    private float thy, thgy;

    private float x_left_headers, x_turn,
            x_combavg, x_combtot,
            x_act1avg, x_act1tot,
            x_act2avg, x_act2tot,
            x_act3avg, x_act3tot,
            x_act4avg, x_act4tot;

    private float y_attack, y_skills, y_powers, y_total;

    private float thgx_comb, thgx_act1, thgx_act2, thgx_act3, thgx_act4;

    private float bottom_right_desc_x, bottom_right_tcp_y, bottom_right_apt_y, bottom_right_apc_y, bottom_right_details_x;
    //--------------------------------------------------------------

    public CardPlaysOverlay() {
        BaseMod.subscribe(this);

        // Setup the image information
        img = ImageMaster.loadImage(IMG_URL);
        if (img != null) {
            img_w = img.getWidth();
            img_h = img.getHeight();

            img_w2 = (float)img_w / 2.0f;
            img_h2 = (float)img_h / 2.0f;

            // Setup all the positioning information
            setupPositions();
            setupDefaults();
        }
        else {
            System.out.println("OJB: ERROR---------------------------------");
            System.out.println("OJB: failed to load image at " + IMG_URL);
            System.out.println("OJB: --------------------------------------");
        }

    }

    // TODO: better, more flexible and non hardcoded values
    //   this is absolutely disgusting right now but hey i guess it works
    private void setupPositions() {
        cx = Settings.WIDTH / 2.0f;
        cy = Settings.HEIGHT / 2.0f;

        //title_x = cx - 582.0f * s;
        title_x = cx - 598.0f * Settings.scale;
        //title_y = cy + 247.0f * s;
        title_y = cy + 254.0f * Settings.scale;

        // top headers
        thx_turn = cx - (483.0f * Settings.scale);
        thx_combavg = cx - 355.0f * Settings.scale;

        thx_combtot = cx - 273.0f * Settings.scale;
        thx_act1avg = thx_combtot + (117.0f * Settings.scale);

        thx_act1tot = thx_act1avg + (89.0f * Settings.scale);

        thx_act2avg = thx_act1tot + (117.0f * Settings.scale);
        thx_act2tot = thx_act2avg + (89.0f * Settings.scale);

        thx_act3avg = thx_act2tot + (117.0f * Settings.scale);
        thx_act3tot = thx_act3avg + (89.0f * Settings.scale);

        thx_act4avg = thx_act3tot + (117.0f * Settings.scale);
        thx_act4tot = thx_act4avg + (89.0f * Settings.scale);

        thy = cy + 154.0f * Settings.scale;
        thgy = cy + 187.0f * Settings.scale;

        // table content locations
        // 117 gap between different groups (R->L)
        // 89 gap inside group
        x_left_headers = cx - 575.0f * Settings.scale;
        x_turn = cx - 460.0f * Settings.scale;

        x_combavg = cx - 343.0f * Settings.scale;
        x_combtot = cx - 254.0f * Settings.scale;

        x_act1avg = cx - 137.0f * Settings.scale;
        x_act1tot = cx - 48.0f * Settings.scale;

        x_act2avg = cx + 69.0f * Settings.scale;
        x_act2tot = cx + 158.0f * Settings.scale;

        x_act3avg = cx + 275.0f * Settings.scale;
        x_act3tot = cx + 364.0f * Settings.scale;

        x_act4avg = cx + 481.0f * Settings.scale;
        x_act4tot = cx + 570.0f * Settings.scale;

        // 29 pixel y spacing
        y_attack = cy + 100.0f * Settings.scale;
        y_skills = cy + 71.0f * Settings.scale;
        y_powers = cy + 42.0f * Settings.scale;
        y_total = cy - 7.0f * Settings.scale; // 13 original

        // top headers (group overtop)
        // 3 to the left of the avg value for the first, with additional right shift for the acts
        thgx_comb = x_combavg - (3.0f * Settings.scale);
        thgx_act1 = x_act1avg - (3.0f * Settings.scale) + (33.0f * Settings.scale);
        thgx_act2 = x_act2avg - (3.0f * Settings.scale) + (32.0f * Settings.scale);
        thgx_act3 = x_act3avg - (3.0f * Settings.scale) + (29.0f * Settings.scale);
        thgx_act4 = x_act4avg - (3.0f * Settings.scale) + (30.0f * Settings.scale);

        // bottom right text (not in table)
        //private float bottom_right_desc_x, bottom_right_tcp_y, bottom_right_apt_y, bottom_right_apc_y, bottom_right_details_x;
        bottom_right_desc_x = cx + (238.0f * Settings.scale);
        bottom_right_details_x = cx + (542.0f * Settings.scale);

        // (38 vertical spacing)
        bottom_right_tcp_y = cy - (138.0f * Settings.scale);
        bottom_right_apt_y = cy - (176.0f * Settings.scale);
        bottom_right_apc_y = cy - (214.0f * Settings.scale);
    }

    private void setupDefaults() {
        turn_attacks = turn_skills = turn_powers = turn_tot = "0";
        combat_attacks_avg = combat_skills_avg = combat_powers_avg = combat_tot_avg = "0.00";
        combat_attacks_tot = combat_skills_tot = combat_powers_tot = combat_tot_tot = "0";
        turn_number = "Turn 1";
    }

    public void toggleVisibility() {
        visible = !visible;
        System.out.println("OJB: overlay toggled visibility to " + visible);
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        if (!visible || img == null)
            return;

        // Render the image
        sb.setColor(Color.WHITE);
        sb.draw(img, cx - img_w2, cy - img_h2, img_w2, img_h2, img_w, img_h, Settings.scale, Settings.scale,
                0.0F, 0, 0, img_w, img_h, false, false);


        FontHelper.renderFontLeftTopAligned(sb, FontHelper.bannerFont, "Cards Played (EARLY WIP - NOT IMPLEMENTED)", title_x, title_y, Settings.GOLD_COLOR);

        // Top row headers (grouped over top)
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "This Combat", thgx_comb, thgy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Act I", thgx_act1, thgy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Act II", thgx_act2, thgy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Act III", thgx_act3, thgy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Act IV", thgx_act4, thgy, Settings.CREAM_COLOR);

        //------------------------------------------------------------------------------------
        // Top row headers (main)
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, turn_number, thx_turn, thy, Settings.CREAM_COLOR);

        // combat
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Avg.", thx_combavg, thy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Total", thx_combtot, thy, Settings.CREAM_COLOR);

        // act 1
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Avg.", thx_act1avg, thy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Total", thx_act1tot, thy, Settings.CREAM_COLOR);

        // act 2
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Avg.", thx_act2avg, thy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Total", thx_act2tot, thy, Settings.CREAM_COLOR);

        // act 3
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Avg.", thx_act3avg, thy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Total", thx_act3tot, thy, Settings.CREAM_COLOR);

        // act 4
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Avg.", thx_act4avg, thy, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Total", thx_act4tot, thy, Settings.CREAM_COLOR);

        //------------------------------------------------------------------------------------
        // Left Headers
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Attacks:", x_left_headers, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Skills:", x_left_headers, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Powers:", x_left_headers, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "Total:", x_left_headers, y_total, Settings.CREAM_COLOR);

        //------------------------------------------------------------------------------------
        // This turn and combat

        // Current Turn
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, turn_attacks, x_turn, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, turn_skills, x_turn, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, turn_powers, x_turn, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, turn_tot, x_turn, y_total, Settings.CREAM_COLOR);

        // Combat Avg
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_attacks_avg, x_combavg, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_skills_avg, x_combavg, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_powers_avg, x_combavg, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_tot_avg, x_combavg, y_total, Settings.CREAM_COLOR);

        // Combat Total
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_attacks_tot, x_combtot, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_skills_tot, x_combtot, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_powers_tot, x_combtot, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, combat_tot_tot, x_combtot, y_total, Settings.CREAM_COLOR);

        //-----------------------------------------------------------------------------------------
        // Act1

        // Avg
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1avg, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1avg, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1avg, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1avg, y_total, Settings.CREAM_COLOR);

        // Total
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1tot, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1tot, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1tot, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act1tot, y_total, Settings.CREAM_COLOR);

        //-----------------------------------------------------------------------------------------
        // Act2

        // Avg
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2avg, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2avg, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2avg, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2avg, y_total, Settings.CREAM_COLOR);

        // Total
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2tot, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2tot, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2tot, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act2tot, y_total, Settings.CREAM_COLOR);

        //-----------------------------------------------------------------------------------------
        // Act3

        // Avg
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3avg, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3avg, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3avg, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3avg, y_total, Settings.CREAM_COLOR);

        // Total
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3tot, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3tot, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3tot, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act3tot, y_total, Settings.CREAM_COLOR);

        //-----------------------------------------------------------------------------------------
        // Act4

        // Avg
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4avg, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4avg, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4avg, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4avg, y_total, Settings.CREAM_COLOR);

        // Total
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4tot, y_attack, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4tot, y_skills, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4tot, y_powers, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "0", x_act4tot, y_total, Settings.CREAM_COLOR);

        // Bottom right
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "Total Cards Played:", bottom_right_desc_x, bottom_right_tcp_y, Settings.BLUE_TEXT_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "Average Per Turn:", bottom_right_desc_x, bottom_right_apt_y, Settings.BLUE_TEXT_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "Average Per Combat:", bottom_right_desc_x, bottom_right_apc_y, Settings.BLUE_TEXT_COLOR);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "0", bottom_right_details_x, bottom_right_tcp_y, Settings.BLUE_TEXT_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "0", bottom_right_details_x, bottom_right_apt_y, Settings.BLUE_TEXT_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "0", bottom_right_details_x, bottom_right_apc_y, Settings.BLUE_TEXT_COLOR);
    }

    private String floatToString(float f) {
        return String.format("%.2f", f);
    }

    public void updateTurnNumber(int turn) {
        turn_number = "Turn " + turn;
    }

    public void updateTurnCards(int attacks, int skills, int powers, int tot) {
        turn_attacks = "" + attacks;
        turn_skills = "" + skills;
        turn_powers = "" + powers;
        turn_tot = "" + tot;
    }

    public void updateCombatCards(int attacks_tot, int skills_tot, int powers_tot, int tot_tot, float attacks_avg, float skills_avg, float powers_avg, float tot_avg) {
        combat_attacks_tot = "" + attacks_tot;
        combat_skills_tot = "" + skills_tot;
        combat_powers_tot = "" + powers_tot;
        combat_tot_tot = "" + tot_tot;

        combat_attacks_avg = floatToString(attacks_avg);
        combat_skills_avg = floatToString(skills_avg);
        combat_powers_avg = floatToString(powers_avg);
        combat_tot_avg = floatToString(tot_avg);
    }

    //public static void initialize() { System.out.println("OJB: init card plays overlay"); }
}
