package InfoMod.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CustomInputProcessor {
    private class CustomProcessor implements InputProcessor {
        private CustomInputProcessor parent;

        public CustomProcessor(CustomInputProcessor parent) {
            this.parent = parent;
        }

        // ignore other events for now
        @Override public boolean keyUp(int i) { return false; }
        @Override public boolean touchDown(int i, int i1, int i2, int i3) { return false; }
        @Override public boolean touchUp(int i, int i1, int i2, int i3) { return false; }
        @Override public boolean touchDragged(int i, int i1, int i2) { return false; }
        @Override public boolean mouseMoved(int i, int i1) { return false; }
        @Override public boolean scrolled(int i) { return false; }

        @Override
        public boolean keyTyped(char c) {
            parent.handleKeyTyped(c);
            return true;
        }

        @Override
        public boolean keyDown(int i) {
            parent.handleKeyDown(i);
            return true;
        }
    }

    private InputProcessor oldInputProcessor;
    private CustomProcessor processor;

    private boolean active = false;
    private Consumer<Character> keyTypedHandler;
    private Consumer<Integer> keyDownHandler;

    public CustomInputProcessor() {
        processor = new CustomProcessor(this);
    }

    private void handleKeyTyped(char c) {
        if (keyTypedHandler != null)
            keyTypedHandler.accept(c);
    }

    private void handleKeyDown(int i) {
        if (keyDownHandler != null)
            keyDownHandler.accept(i);
    }

    public void setKeyTypedHandler(Consumer<Character> keyTypedHandler) {
        this.keyTypedHandler = keyTypedHandler;
    }

    public void setKeyDownHandler(Consumer<Integer> keyDownHandler) {
        this.keyDownHandler = keyDownHandler;
    }

    public void start() {
        if (active) {
            System.out.println("OJB WARNING: input processor calling start() without stopping previous");
            // TODO, probably can just stop() first and then continue
            return;
        }

        oldInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(processor);

        active = true;
    }

    // Note that this also clears all existing handlers
    public void reset() {
        if (!active)
            return;

        keyDownHandler = null;
        keyTypedHandler = null;

        Gdx.input.setInputProcessor(this.oldInputProcessor);
        active = false;
    }
}
