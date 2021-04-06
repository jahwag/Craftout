package com.mojang.sdk;

import org.lwjgl.opengl.GL;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Window {
    private long window;

    private int width;
    private int height;

    private Map<SpriteType, Texture> textures;
    private Font font;
    private Map<Integer, TextRenderer> textSizes;
    private Map<Integer, Text> texts;
    private int nextText;
    private Map<Integer, Sprite> sprites;
    private int nextSprite;
    private ArrayList<DebugLine> debugLines;

    private static class Text {
        private TextRenderer renderer;
        public String text;
        public float x;
        public float y;

        public Text(String text, TextRenderer renderer, float x, float y) {
            this.text = text;
            this.renderer = renderer;
            this.x = x;
            this.y = y;
        }

        public void draw() {
            glPushMatrix();
            glTranslatef(x, y + renderer.getSize() / 2.0f, 0.0f);
            renderer.draw(text);
            glPopMatrix();
        }
    }

    private static class Sprite {
        private Texture texture;
        public float x;
        public float y;

        public Sprite(Texture texture, float x, float y) {
            this.texture = texture;
            this.x = x;
            this.y = y;
        }

        public void draw() {
            glPushMatrix();
            glTranslatef(x, y, 0.0f);
            texture.draw();
            glPopMatrix();
        }
    }

    private static class DebugLine {
        public float x1;
        public float y1;
        public float x2;
        public float y2;

        public DebugLine(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public void draw() {
            glColor3f(1.0f, 1.0f, 1.0f);
            glDisable(GL_TEXTURE_2D);
            glBegin(GL_LINES);
            glVertex3f(x1, y1, 0);
            glVertex3f(x2, y2, 0);
            glEnd();
        }
    }

    /**
     * Construct a window with a width, height and title. The values given
     * here cannot changed later.
     */
    public Window(int width, int height, String title) throws IOException {
        this.width = width;
        this.height = height;

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        window = glfwCreateWindow(width, height, title, 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create window");
        }
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        GL.createCapabilities();
        glfwShowWindow(window);

        loadTextures();
        loadFont();

        nextText = 0;
        texts = new TreeMap<>();
        nextSprite = 0;
        sprites = new TreeMap<>();
        debugLines = new ArrayList<>();

        setupInput();
    }

    private void loadTextures() throws IOException {
        textures = new TreeMap<>();
        textures.put(SpriteType.BALL, new Texture("resources/ball.png"));
        textures.put(SpriteType.BRICK, new Texture("resources/brick.png"));
        textures.put(SpriteType.PADDLE_LEFT, new Texture("resources/paddle_left.png"));
        textures.put(SpriteType.PADDLE_MID, new Texture("resources/paddle_mid.png"));
        textures.put(SpriteType.PADDLE_RIGHT, new Texture("resources/paddle_right.png"));
    }

    private void loadFont() throws IOException {
        font = new Font("resources/Mojangles.ttf");
        textSizes = new TreeMap<>();
    }

    private void setupInput() {
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            boolean pressed;
            if (action == GLFW_PRESS) {
                pressed = true;
            } else if (action == GLFW_RELEASE) {
                pressed = false;
            } else {
                return;
            }

            switch (key) {
                case GLFW_KEY_A:
                    Input.left = pressed;
                    break;
                case GLFW_KEY_D:
                    Input.right = pressed;
                    break;
                case GLFW_KEY_SPACE:
                    Input.fire = pressed;
                    break;
            }
        });
    }

    /**
     * Check whether the window is still open. Once closed by the user,
     * it cannot be re-opened through the same Window instance.
     */
    public boolean isOpen() {
        return !glfwWindowShouldClose(window);
    }

    /** Draw a frame containing all valid sprites and texts. */
    public void draw() {
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION_MATRIX);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glScalef(2.0f / width, -2.0f / height, 1);
        glTranslatef(-width / 2.0f, -height / 2.0f, 0);

        for (Text text : texts.values()) {
            text.draw();
        }

        for (Sprite sprite : sprites.values()) {
            sprite.draw();
        }

        for (DebugLine line : debugLines) {
            line.draw();
        }
        debugLines.clear();

        glfwSwapBuffers(window);
    }

    /**
     * Create a sprite. The type given here will remain, but the sprite can be
     * moved later using the moveSprite() method.
     */
    public int createSprite(SpriteType type, float x, float y) {
        Texture texture = textures.get(type);
        Sprite spriteObject = new Sprite(texture, x, y);
        int id = nextSprite++;
        sprites.put(id, spriteObject);
        return id;
    }

    /**
     * Move a sprite. Passing in an invalid ID is a valid call, and will
     * result in an InvalidID return value. If the sprite is valid, it will be
     * moved and Success is returned.
     */
    public OperationResult moveSprite(int id, float x, float y) {
        Sprite sprite = sprites.get(id);
        if (sprite == null) {
            return OperationResult.InvalidID;
        }
        sprite.x = x;
        sprite.y = y;
        return OperationResult.Success;
    }

    /** Remove a sprite, if it is valid. This invalidates the ID. */
    public void removeSprite(int id) {
        sprites.remove(id);
    }

    /** Check whether a given ID represents a valid sprite. */
    public boolean isValidSprite(int id) {
        return sprites.containsKey(id);
    }

    /** Returns the number of valid sprites. */
    public int countValidSprites() {
        return sprites.size();
    }

    /**
     * Create a text. The size given here will remain, but the string can be
     * updated later using the updateText() method and the text can be moved
     * using the moveText method() method.
     */
    public int createText(String text, int size, float x, float y) {
        Integer key = size;
        TextRenderer renderer;
        if (!textSizes.containsKey(key)) {
            renderer = font.createRendererForSize(size);
            textSizes.put(key, renderer);
        } else {
            renderer = textSizes.get(key);
        }
        Text textObject = new Text(text, renderer, x, y);
        int id = nextText++;
        texts.put(id, textObject);
        return id;
    }

    /**
     * Change a text string. Passing in an invalid ID is a valid call, and
     * will result in an InvalidID return value. If the text is valid, it will
     * be changed and Success is returned.
     */
    public OperationResult updateText(int id, String text) {
        Text textObject = texts.get(id);
        if (textObject == null) {
            return OperationResult.InvalidID;
        }
        textObject.text = text;
        return OperationResult.Success;
    }

    /**
     * Move a text. Passing in an invalid ID is a valid call, and will
     * result in an InvalidID return value. If the text is valid, it will be
     * moved and Success is returned.
     */
    public OperationResult moveText(int id, float x, float y) {
        Text text = texts.get(id);
        if (text == null) {
            return OperationResult.InvalidID;
        }
        text.x = x;
        text.y = y;
        return OperationResult.Success;
    }

    /** Remove a text, if it is valid. This invalidates the ID. */
    public void removeText(int id) {
        texts.remove(id);
    }

    /** Check whether a given ID represents a valid text. */
    public boolean isValidText(int id) {
        return texts.containsKey(id);
    }

    /** Returns the number of valid texts. */
    public int countValidTexts() {
        return texts.size();
    }

    /** Get the time passed since the window was created. */
    public double getSecondsSinceCreation() {
        return glfwGetTime();
    }

    /** Draw a debug line for one frame. */
    public void debugDrawLine(float x1, float y1, float x2, float y2) {
        debugLines.add(new DebugLine(x1, y1, x2, y2));
    }
}