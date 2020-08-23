package InfoMod.utils;

import basemod.BaseMod;
import basemod.interfaces.PreRenderSubscriber;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.HashMap;
import java.util.function.Consumer;

/*

these registration functions are watch() and watchHB()
TODO: less godawful design, deregistration (hence why they're in a map with an ID, so they can be removed later, etc.)

 */
@SpireInitializer
public class RightClickWatcher implements PreRenderSubscriber {
    private static class HitboxMapObject {
        private Hitbox hb;
        private Consumer<Object> onRightClick;
        public Object item;

        public HitboxMapObject(Hitbox hb, Object item, Consumer<Object> onRightClick) {
            this.hb = hb;
            this.onRightClick = onRightClick;
            this.item = item;
        }

        public void rightClick() {
            if (hb.hovered)
                onRightClick.accept(item);
        }
    }

    private static class NonHitboxMapObject {
        private Consumer<Object> onRightClick;
        public Object item;

        public NonHitboxMapObject(Object item, Consumer<Object> onRightClick) {
            this.onRightClick = onRightClick;
            this.item = item;
        }

        public void rightClick() {
            onRightClick.accept(item);
        }
    }

    private HashMap<String, HitboxMapObject> trackedHB;
    private HashMap<String, NonHitboxMapObject> trackedNonHB;

    private boolean mouseDownRight = false;

    // Singleton pattern
    private static class RightClickWatcherHolder {
        private static final RightClickWatcher INSTANCE = new RightClickWatcher();
    }

    private static RightClickWatcher getInstance() {
        return RightClickWatcherHolder.INSTANCE;
    }

    public RightClickWatcher() {
        trackedHB = new HashMap<>();
        trackedNonHB = new HashMap<>();
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new RightClickWatcherHolder();
    }

    public static void watchHB(String id, Hitbox hb, Object item, Consumer<Object> onRightClick) {
        RightClickWatcher watcher = getInstance();

        HitboxMapObject obj = new HitboxMapObject(hb, item, onRightClick);
        watcher.trackedHB.put(id, obj);
    }

    public static void watch(String id, Object item, Consumer<Object> onRightClick) {
        RightClickWatcher watcher = getInstance();

        NonHitboxMapObject obj = new NonHitboxMapObject(item, onRightClick);
        watcher.trackedNonHB.put(id, obj);
    }

    private void rightClickHandler() {
        System.out.println("RIGHT CLICK HANDLER SPOTTED A RIGHT CLICK");

        for (HitboxMapObject obj : trackedHB.values())
            obj.rightClick();
        for (NonHitboxMapObject obj : trackedNonHB.values())
            obj.rightClick();
    }

    @Override
    public void receiveCameraRender(OrthographicCamera orthographicCamera) {
        // This pre render can fire before the dungeon is even made, so it's possible to crash here unless we handle this case
        if (!CardCrawlGame.isInARun() || CardCrawlGame.dungeon == null)
            return;

        // Only if the config is set do we try and handle clicks
////        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_MONSTER_DETAILS) == false)
////            return;
//        if (!Config.getBool(Config.ConfigOptions.SHOW_MONSTER_DETAILS))
//            return;

        // Special Case: Don't activate if left click is also down (we probably have a card in hand already?)
//        if (InputHelper.isMouseDown) {
//            mouseDownRight = false;
//            return;
//        }

        if (InputHelper.isMouseDown_R) {
            mouseDownRight = true;
        } else {
            // We already had the mouse down, and now we released, so fire our right click event
            if (mouseDownRight) {
                rightClickHandler();
                mouseDownRight = false;
            }
        }
    }
}
