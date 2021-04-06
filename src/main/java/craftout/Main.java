package craftout;

import com.mojang.sdk.Input;
import com.mojang.sdk.SpriteType;
import com.mojang.sdk.Window;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Window window = new Window(640, 360, "Craftout");

            int ballID = window.createSprite(SpriteType.BALL, 118, 19);
            int textID = window.createText("Hello World", 20, 10, 10);

            while (window.isOpen()) {
                if (Input.isButtonPressed(Input.Button.LEFT)) {
                    window.debugDrawLine(10, 33, 173, 33);
                }
                window.draw();
            }

            window.removeSprite(ballID);
            window.removeText(textID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
