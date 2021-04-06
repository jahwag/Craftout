package com.mojang.sdk;

import org.lwjgl.stb.STBTTFontinfo;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.mojang.sdk.Utils.loadFileIntoBuffer;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

class Font {
    private final ByteBuffer ttf;
    private final STBTTFontinfo info;

    public Font(String fileName) throws IOException {
        ttf = loadFileIntoBuffer(fileName);
        info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }
    }

    public TextRenderer createRendererForSize(int size) {
        return new TextRenderer(ttf, info, size);
    }

}
