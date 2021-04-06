package com.mojang.sdk;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

class TextRenderer {
    private static final int BITMAP_W = 1024;
    private static final int BITMAP_H = 1024;

    private final STBTTFontinfo info;
    private final int size;
    private final int textureID;

    private final int ascent;
    private final int descent;
    private final int lineGap;

    private final float scale;

    private final STBTTBakedChar.Buffer cdata;

    public TextRenderer(ByteBuffer ttf, STBTTFontinfo info, int size) {
        textureID = glGenTextures();
        cdata = STBTTBakedChar.malloc(96);
        this.info = info;
        this.size = size;

        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(ttf, size, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

        MemoryStack stack = stackPush();
        IntBuffer pAscent  = stack.mallocInt(1);
        IntBuffer pDescent = stack.mallocInt(1);
        IntBuffer pLineGap = stack.mallocInt(1);

        stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

        ascent = pAscent.get(0);
        descent = pDescent.get(0);
        lineGap = pLineGap.get(0);

        scale = 20.0f/13.0f;

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    }

    public void draw(String text) {
        float scale = stbtt_ScaleForPixelHeight(info, size) / 13.0f * 20.0f;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);

            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            glBindTexture(GL_TEXTURE_2D, textureID);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glColor3f(1.0f, 1.0f, 1.0f);
            glTranslatef(0.0f, size/2.0f, 0.0f);

            glBegin(GL_QUADS);
            for (int i = 0, to = text.length(); i < to; ) {
                i += getCP(text, to, i, pCodePoint);

                int cp = pCodePoint.get(0);
                if (cp == '\n') {
                    y.put(0, y.get(0) + (ascent - descent + lineGap) * scale);
                    x.put(0, 0.0f);

                    continue;
                } else if (cp < 32 || 128 <= cp) {
                    continue;
                }

                stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, cp - 32, x, y, q, true);

                float x0 = q.x0();
                float x1 = q.x1();
                float y0 = q.y0();
                float y1 = q.y1();

                glTexCoord2f(q.s0(), q.t0());
                glVertex2f(x0 * this.scale, y0 * this.scale);

                glTexCoord2f(q.s1(), q.t0());
                glVertex2f(x1 * this.scale, y0 * this.scale);

                glTexCoord2f(q.s1(), q.t1());
                glVertex2f(x1 * this.scale, y1 * this.scale);

                glTexCoord2f(q.s0(), q.t1());
                glVertex2f(x0 * this.scale, y1 * this.scale);
            }
            glEnd();
        }

    }

    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    public int getSize() {
        return size;
    }

}
