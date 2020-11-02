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

        if (cena.vidas == 0) {
            if (e.getKeyCode() == KeyEvent.VK_E) {
                cena.ready = true;
            }
        }

        if (cena.telaInicial && !cena.pause) {
            if (e.getKeyCode() == KeyEvent.VK_E) {
                cena.telaInicial = false;
                cena.musica(2);
                cena.musica(3);
            }
        }

        if (cena.pause) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_R:
                    cena.pause = !cena.pause;
                    if (cena.cont == 1) {
                        cena.musica(3);
                    } else {
                        cena.musica(6);
                    }
                    break;
                case KeyEvent.VK_E:
                    System.exit(0);
                    break;
                case KeyEvent.VK_M:
                    if(cena.cont == 1){
                        cena.musica(5);
                    } else {
                        cena.musica(8);
                    }
                    cena.reset = !cena.reset;
                    break;
            }
        } else if (!cena.telaInicial) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    break;
                case KeyEvent.VK_DOWN:
                    break;
                case KeyEvent.VK_SPACE:
                    if (!cena.start && !cena.telaInicial) {
                        cena.start = true;
                        cena.tela = 1;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if ((cena.start || cena.tela == 0) && !cena.pause) {
                        if (cena.posPlayer < 73) {
                            cena.posPlayer += 4;
                        }
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if ((cena.start || cena.tela == 0) && !cena.pause) {
                        if (cena.posPlayer > -73) {
                            cena.posPlayer -= 4;
                        }
                    }
                    break;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (true) {
                cena.pause = !cena.pause;
                if (cena.pause) {
                    if (cena.cont == 1) {
                        cena.musica(4);
                    } else {
                        cena.musica(7);
                    }
                } else {
                    if (cena.cont == 1) {
                        cena.musica(3);
                    } else {
                        cena.musica(6);
                    }
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
