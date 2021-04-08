package craftout.components;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtension.class)
class TranslationTest {

    @Random
    float x;

    @Random
    float y;

    Translation translation;

    @BeforeEach
    void setUp() {
        this.translation = new Translation(x, y);
    }

    @Test
    void x() {
        assertEquals(x, translation.getCenterX());
    }

    @Test
    void y() {
        assertEquals(y, translation.getCenterY());
    }

}