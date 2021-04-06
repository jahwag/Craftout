package com.mojang.sdk;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SpriteTests {

    @Test
    public void testSprites() {
        Window window = null;
        try {
            window = new Window(10, 10, "Test");
        } catch (IOException e) {
            fail("Unable to create window");
        }
        int id = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 1);
        assertTrue(window.isValidSprite(id));
        assertFalse(window.isValidSprite(id + 1));
        assertEquals(window.moveSprite(id, 1,1), OperationResult.Success);
        window.removeSprite(id);
        assertEquals(window.countValidSprites(), 0);
        assertFalse(window.isValidSprite(id));
        assertEquals(window.moveSprite(id, 1,1), OperationResult.InvalidID);
        window.removeSprite(id);
        assertEquals(window.countValidSprites(), 0);

        int newId = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 1);
        assertTrue(window.isValidSprite(newId));
        assertFalse(window.isValidSprite(id));
        assertEquals(window.moveSprite(id, 1,1), OperationResult.InvalidID);
        assertEquals(window.moveSprite(newId, 1,1), OperationResult.Success);
        window.removeSprite(newId);
        assertEquals(window.countValidSprites(), 0);
        assertFalse(window.isValidSprite(newId));
        assertEquals(window.moveSprite(newId, 1, 1), OperationResult.InvalidID);
        window.removeSprite(newId);
        assertEquals(window.countValidSprites(), 0);

        int id2 = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 1);
        int id3 = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 2);
        int id4 = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 3);
        int id5 = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 4);

        window.removeSprite(id2);
        assertEquals(window.countValidSprites(), 3);
        window.removeSprite(id4);
        assertEquals(window.countValidSprites(), 2);

        int id6 = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 3);
        int id7 = window.createSprite(SpriteType.BALL, 0, 0);
        assertEquals(window.countValidSprites(), 4);

        window.removeSprite(id3);
        assertEquals(window.countValidSprites(), 3);
        window.removeSprite(id5);
        assertEquals(window.countValidSprites(), 2);
        window.removeSprite(id6);
        assertEquals(window.countValidSprites(), 1);
        window.removeSprite(id7);
        assertEquals(window.countValidSprites(), 0);
    }

}
