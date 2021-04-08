package craftout.components;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtension.class)
class VelocityTest {

    @Random
    float x;

    @Random
    float y;

    Velocity velocity;

    @BeforeEach
    void setUp() {
        this.velocity = new Velocity(x, y);
    }

    @Test
    void dx() {
        assertEquals(x, velocity.getX());
    }

    @Test
    void dy() {
        assertEquals(y, velocity.getY());
    }

}