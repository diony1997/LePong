package input;

import cena.Cena;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL2;

/**
 *
 * @author Siabreu
 */
public class KeyBoard implements KeyListener {

    private Cena cena;

    public KeyBoard(Cena cena) {
        this.cena = cena;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
            if (!cena.start) {
                cena.start = true;
                cena.tela = 1;
            } else {
                cena.start = false;
            }
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:

                break;
            case KeyEvent.VK_DOWN:

                break;
            case KeyEvent.VK_RIGHT:
                if (cena.start || cena.tela == 0) {
                    if (cena.posPlayer < 84) {
                        cena.posPlayer += 3;
                    }
                }

                break;
            case KeyEvent.VK_LEFT:
                if (cena.start || cena.tela == 0) {
                    if (cena.posPlayer > -84) {
                        cena.posPlayer -= 3;
                    }
                }
                break;

        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
