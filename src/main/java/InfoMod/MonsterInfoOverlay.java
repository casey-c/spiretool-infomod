package InfoMod;

import basemod.BaseMod;
import basemod.interfaces.PreRenderSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class MonsterInfoOverlay implements RenderSubscriber, PreRenderSubscriber {
    private boolean visible = false;

    private static final String IMG_URL = "images/monster_screen.png";
    private Texture img = null;


    //--------------------------------------------------------------
    // Positioning information
    //--------------------------------------------------------------

    // NOTE: must call setupPostions() after the game has time to learn what the Settings.WIDTH etc. are (they are 0
    //   before SpireInitializer can see it and thus these will be wrong if not waited for the proper values)
    private float cx, cy;
    private int img_w, img_h;
    private float img_w2, img_h2;
    private float smart_width, smart_line_spacing;

    private float left_x, right_x;
    private float top_header_y;
    private float notes_header_y;
    private float header_body_spacing;

    private float right_ai_body_y;
    private float right_notes_body_y;

    private float move_vertical_spacing;
    private float move_title_detail_horizontal_gap;
    private float move_detail_horizontal_gap;
    private float m0_y, m1_y, m2_y, m3_y, m4_y, m5_y;
    private float md0_x, md1_x, md2_x, md3_x, md4_x, md5_x;


    //--------------------------------------------------------------
    // Text contents
    //--------------------------------------------------------------

    private String monster_name = "Darkling (48 - 56 HP)";
    private String currentlySelectedMonsterID = "";

    //------------------------------------------------------------------------------------------------------------------

    public MonsterInfoOverlay() {
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

    // Setup all the constants for this overlay's positioning information
    private void setupPositions() {
        cx = Settings.WIDTH / 2.0f;
        cy = Settings.HEIGHT / 2.0f;

        left_x = cx - 689.0f * Settings.scale;
        right_x = cx + 246.0f * Settings.scale;

        top_header_y = cy + 317.0f * Settings.scale;
        notes_header_y = cy - 178.0f * Settings.scale;

        header_body_spacing = 62.0f * Settings.scale;
        right_ai_body_y = top_header_y - header_body_spacing;
        right_notes_body_y = notes_header_y - header_body_spacing;

        // For smart text rendering (i.e. the AI and notes section)
        smart_width = 445.0f * Settings.scale;
        smart_line_spacing = 30.0f * Settings.scale;

        // Moves
        m0_y = top_header_y - header_body_spacing - (56.0f * Settings.scale); // with additional (lazy) shift down to try and center
        move_vertical_spacing = 93.0f * Settings.scale;

        m1_y = m0_y - move_vertical_spacing;
        m2_y = m1_y - move_vertical_spacing;
        m3_y = m2_y - move_vertical_spacing;
        m4_y = m3_y - move_vertical_spacing;
        m5_y = m4_y - move_vertical_spacing;

        // Move details
        move_title_detail_horizontal_gap = 210.0f * Settings.scale;
        move_detail_horizontal_gap = 93.0f * Settings.scale;

        md0_x = left_x + move_title_detail_horizontal_gap;
        md1_x = md0_x + move_detail_horizontal_gap;
        md2_x = md1_x + move_detail_horizontal_gap;
        md3_x = md2_x + move_detail_horizontal_gap;
        md4_x = md3_x + move_detail_horizontal_gap;
        md5_x = md4_x + move_detail_horizontal_gap;
    }

    // Setup some default values for the text strings
    private void setupDefaults() {
        // todo: remove this (deprecated? useless?)
    }

    // Show / hide this overlay
    public void toggleVisibility() {
        //visible = !visible;

        // use our special methods instead (these will swap the visibility bool as well, but do other stuff like sound
        if (visible)
            hide();
        else
            show();
    }

    // TODO: figure out the current monster and integrate with some sort of data storage mechanism to recall the proper
    //   text for the associated monster

    private void renderMoveText(SpriteBatch sb, float move_y, MonsterInfo.Move move) {
        if (move != null) {
            RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, move.move_name, left_x, move_y, smart_width, smart_line_spacing, Settings.CREAM_COLOR);

            RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, move.d0, md0_x, move_y, smart_width, smart_line_spacing, move.c0);
            RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, move.d1, md1_x, move_y, smart_width, smart_line_spacing, move.c1);
            RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, move.d2, md2_x, move_y, smart_width, smart_line_spacing, move.c2);
            RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, move.d3, md3_x, move_y, smart_width, smart_line_spacing, move.c3);
            RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, move.d4, md4_x, move_y, smart_width, smart_line_spacing, move.c4);
            RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, move.d5, md5_x, move_y, smart_width, smart_line_spacing, move.c5);
        }

    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        if (!visible || img == null)
            return;

        // Get the current monster to display, if it exists (otherwise jaw worm for testing)
        MonsterInfo curr_monster;
        if (MonsterInfoDatabase.hasMonsterID(currentlySelectedMonsterID))
            curr_monster = MonsterInfoDatabase.getMonsterByID(currentlySelectedMonsterID);
        else
            curr_monster = MonsterInfoDatabase.getMonsterByID("NULL");

        if (curr_monster == null)
            return;

        // Render the image
        sb.setColor(Color.WHITE);
        sb.draw(img, cx - img_w2, cy - img_h2, img_w2, img_h2, img_w, img_h, Settings.scale, Settings.scale,
                0.0F, 0, 0, img_w, img_h, false, false);


        // HEADERS

        //FontHelper.renderFontLeftTopAligned(sb, FontHelper.bannerFont, monster_name, left_x, top_header_y, Settings.GOLD_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.bannerFont, curr_monster.getNameAndHP(), left_x, top_header_y, Settings.GOLD_COLOR);


        FontHelper.renderFontLeftTopAligned(sb, FontHelper.bannerFont, "AI", right_x, top_header_y, Settings.GOLD_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.bannerFont, "Notes", right_x, notes_header_y, Settings.GOLD_COLOR);

        // TODO: hide / show notes header/body if no notes exist (similar to moves? or keep dark gray? idk)

        // AI text
        RenderingUtils.renderSmartText(sb,
                FontHelper.tipBodyFont,
                //"On turn 1, 50% chance of either #wNip or #wHarden.  Afterwards, has a 30-40-30 split between #wNip, #wChomp, and #wHarden, but #wHarden and #wChomp both cannot be used twice in a row, and #wNip cannot be used three times in a row. The middle Darkling cannot #wChomp and has a 50-50 split instead. When \"half-dead\", uses #w???, followed by #wReincarnate.\n",
                curr_monster.getAI(),
                //50.0f,
                //540.0f,
                right_x,
                right_ai_body_y,
                smart_width, //445.0f
                smart_line_spacing,
                //30.0f,
                //Settings.CREAM_COLOR);
                RenderingUtils.OJB_GRAY_COLOR);

        // Notes text
        RenderingUtils.renderSmartText(sb,
                FontHelper.tipBodyFont,
                //"None",
                curr_monster.getNotes(),
                right_x,
                right_notes_body_y,
                smart_width,
                smart_line_spacing,
                RenderingUtils.OJB_GRAY_COLOR);


        // MOVES (these are potentially null)
        MonsterInfo.Move m0 = curr_monster.getMove(0);
        MonsterInfo.Move m1 = curr_monster.getMove(1);
        MonsterInfo.Move m2 = curr_monster.getMove(2);
        MonsterInfo.Move m3 = curr_monster.getMove(3);
        MonsterInfo.Move m4 = curr_monster.getMove(4);
        MonsterInfo.Move m5 = curr_monster.getMove(5);

        if (m0 != null) renderMoveText(sb, m0_y, m0);
        if (m1 != null) renderMoveText(sb, m1_y, m1);
        if (m2 != null) renderMoveText(sb, m2_y, m2);
        if (m3 != null) renderMoveText(sb, m3_y, m3);
        if (m4 != null) renderMoveText(sb, m4_y, m4);
        if (m5 != null) renderMoveText(sb, m5_y, m5);



        /*
        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "Perplexing Glare", left_x, m0_y, smart_width, smart_line_spacing, Settings.CREAM_COLOR);
        //RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "[ MOVE 0 ]", left_x, m0_y, smart_width, smart_line_spacing, RenderingUtils.OJB_DARK_GRAY_COLOR);

        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "Encourage", left_x, m1_y, smart_width, smart_line_spacing, Settings.CREAM_COLOR);
        //RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "[ MOVE 1 ]", left_x, m1_y, smart_width, smart_line_spacing, RenderingUtils.OJB_DARK_GRAY_COLOR);

        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "Chomp", left_x, m2_y, smart_width, smart_line_spacing, Settings.CREAM_COLOR);
        //RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "[ MOVE 2 ]", left_x, m2_y, smart_width, smart_line_spacing, RenderingUtils.OJB_DARK_GRAY_COLOR);

        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "[ MOVE 3 ]", left_x, m3_y, smart_width, smart_line_spacing, RenderingUtils.OJB_DARK_GRAY_COLOR);
        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "[ MOVE 4 ]", left_x, m4_y, smart_width, smart_line_spacing, RenderingUtils.OJB_DARK_GRAY_COLOR);
        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "[ MOVE 5 ]", left_x, m5_y, smart_width, smart_line_spacing, RenderingUtils.OJB_DARK_GRAY_COLOR);

        // MOVE DETAILS
        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "Block 10", md0_x, m0_y, smart_width, smart_line_spacing, Settings.BLUE_TEXT_COLOR);
        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "Weak 2", md1_x, m0_y, smart_width, smart_line_spacing, RenderingUtils.OJB_DEBUFF_COLOR);

        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "#uParty: #uStr #u+5", md0_x, m1_y, smart_width, smart_line_spacing, Settings.CREAM_COLOR);
        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "#bMinions: #bBlock #b10", md2_x, m1_y, smart_width, smart_line_spacing, Settings.CREAM_COLOR);

        RenderingUtils.renderSmartText(sb, FontHelper.tipBodyFont, "8x2", md0_x, m2_y, smart_width, smart_line_spacing, Settings.RED_TEXT_COLOR);
         */

    }

    public void hide() {
        if (visible)
            SoundHelper.playMapCloseSound();

        visible = false;
    }

    public void show() {
        visible = true;
        //AbstractDungeon.actionManager.addToBottom(new SFXAction());
        SoundHelper.playMapSelectSound();
    }

    private boolean mouseDownRight = false;

    @Override
    public void receiveCameraRender(OrthographicCamera orthographicCamera) {
        // This pre render can fire before the dungeon is even made, so it's possible to crash here
        if (!CardCrawlGame.isInARun() || CardCrawlGame.dungeon == null)
            return;

        // Don't activate if left click is also down (we probably have a card in hand already?)
        if (InputHelper.isMouseDown) {
            mouseDownRight = false;
            return;
        }

        // Start tracking the mouse down
        if (InputHelper.isMouseDown_R) {
            mouseDownRight = true;
        }
        else {
            // We already had the mouse down, and now we released, so fire our click event
            if (mouseDownRight) {
                mouseDownRight = false;

                // Do the magic test
                AbstractRoom room = AbstractDungeon.getCurrRoom();
                if (room != null) {
                    MonsterGroup monsters = room.monsters;
                    if (monsters != null) {
                        AbstractMonster hovered = monsters.hoveredMonster;
                        if (hovered != null) {
                            String name = hovered.name;
                            String id = hovered.id;
                            System.out.println("OJB: right clicked on: " + name + " (" + id + ")");

                            // Close if already open and clicked on same monster as is visible
                            if (visible && currentlySelectedMonsterID == id)
                                hide();
                            else {
                                currentlySelectedMonsterID = id;

                                // Show this overlay
                                monster_name = name + " (" + id + ")";
                                show();
                            }

                        }
                        else {
                            System.out.println("OJB: not hovered on a monster");
                            hide();
                        }
                    }
                }
            }
            else {
                //System.out.println("OJB: inputhelper mouse is not down | mouseDownRight is NOT set");
            }
        }
    }
}
