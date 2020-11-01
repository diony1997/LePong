package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Siabreu
 */
public class Cena implements GLEventListener {

    private float xMin, xMax, yMin, yMax, zMin, zMax;
    private TextRenderer textRenderer;
    public float auxX, auxY, posPlayer, anguloX, anguloY, anguloObt;
    public boolean start, pause, fase, teste;
    public int tela, score;
    private GLU glu;
    private double x1, x2, x3, x4, y1, y2, y3, y4;

    @Override
    public void init(GLAutoDrawable drawable) {
        //dados iniciais da cena
        glu = new GLU();
        GL2 gl = drawable.getGL().getGL2();
        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -100;
        xMax = yMax = zMax = 100;
        anguloX = posPlayer;
        anguloY = -72;
        tela = score = 0;
        auxX = auxY = 1;
        posPlayer = 0;
        anguloObt = 0;
        start = pause = fase = teste = false;
        textRenderer = new TextRenderer(new Font("Comic Sans MS Negrito", Font.BOLD, 15));

        //Habilita o buffer de profundidade
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        //obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();
        //objeto para desenho 3D
        GLUT glut = new GLUT();
        //define a cor da janela (R, G, G, alpha)
        gl.glClearColor(0, 0, 0, 0);
        //limpa a janela com a cor especificada
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glLoadIdentity(); //ler a matriz identidade

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        dadosObjeto(gl, 20, 580, Color.WHITE, "MODO: " + printStart(start));
        dadosObjeto(gl, 460, 580, Color.WHITE, "Pontuação: " + teste);
        dadosObjeto(gl, 20, 5, Color.WHITE, "Movimente com as setas, comece com espaço e pause com ESC.");

        //Parte de iluminação
        float[] posLuz = {-50f, 0f, 100f, 1};
        float[] corLuz = {1f, 1f, 1f, 1};
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 64);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, corLuz, 0);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, corLuz, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLuz, 0);
        gl.glShadeModel(GL2.GL_SMOOTH);

        if (!pause) {
            if (start) {
                movimentarBola();
            } else {
                if (tela == 0) {
                    anguloX = posPlayer;
                }
            }
        }

        desenharObstaculo(gl);
        desenharFundo(gl);
        desenharBola(gl, glut);
        desenharPlayer(gl);

        if (pause) {
            textRenderer = new TextRenderer(new Font("Comic Sans MS Negrito", Font.BOLD, 48));
            dadosObjeto(gl, 175, 400, Color.WHITE, "R -> Resume");
            dadosObjeto(gl, 200, 320, Color.WHITE, "E -> Exit");
            textRenderer = new TextRenderer(new Font("Comic Sans MS Negrito", Font.BOLD, 15));
        }

        gl.glFlush();
    }

    public void movimentarBola() {
        anguloX += auxX;
        anguloY += auxY;
        //anguloObt += 0.01;

        if (anguloObt == 360) {
            anguloObt = 1;
        }
        /*
        if (score == 200 && !fase) {
            auxX = auxX * 2;
            auxY = auxY * 2;
            fase = true;
        }
         */
        if (anguloX > 88 && tela != 0) {
            auxX = auxX * -1;
        }
        if (anguloX < -88 && tela != 0) {
            auxX = auxX * -1;
        }
        if (anguloY > 78 && tela != 0) {
            auxY = auxY * -1;
        }
        //Colisão com o player
        if (anguloY < -71 && (anguloX <= (posPlayer + 15) && anguloX >= (posPlayer - 15)) && tela != 0) {
            score += 100;
            auxY = auxY * -1;
        }
        //evitar que a bola entre no player
        if (anguloY < -73 && (anguloX == (posPlayer + 15) && anguloX == (posPlayer - 15)) && tela != 0) {
            auxX = auxX * -1;
        }
        if (anguloY < -88 && tela != 0) {
            fim();
        }
        //Desenho dinamico
        //ponto x′ = x cos(θ)− y sin(θ),
        //      y' = x sin(θ) + y cos(θ).
        x1 = (-36 * Math.cos(anguloObt)) - (-36 * Math.sin(anguloObt)); //-30
        y1 = (-36 * Math.sin(anguloObt)) + (-36 * Math.cos(anguloObt)); //-30
        x2 = (36 * Math.cos(anguloObt)) - (-36 * Math.sin(anguloObt));
        y2 = (36 * Math.sin(anguloObt)) + (-36 * Math.cos(anguloObt)); //
        x3 = (36 * Math.cos(anguloObt)) - (36 * Math.sin(anguloObt)); //30
        y3 = (36 * Math.sin(anguloObt)) + (36 * Math.cos(anguloObt)); //30  
        x4 = (-36 * Math.cos(anguloObt)) - (36 * Math.sin(anguloObt)); //-30
        y4 = (-36 * Math.sin(anguloObt)) + (36 * Math.cos(anguloObt)); //30 

        if (x1 <= x2) {
            if (y1 < y3) {
                teste = checarObst(x1, x2, x3, x4, y1, y2, y3, y4);
            } else {
                teste = checarObst(x2, x3, x4, x1, y2, y3, y4, y1);
            }
        } else {
            if (y3 > y1) {
                teste = checarObst(x4, x1, x2, x3, y4, y1, y2, y3);
            } else {
                teste = checarObst(x3, x4, x1, x2, y3, y4, y1, y2);
            }
        }
        /* versão estatica
        //colisão com obstaculo Baixo
        if((anguloX <= 30 && anguloX >= -30) &&(anguloY >= -36 && anguloY < -30)){
            auxY = auxY * -1;
        }
        //Alto
        if((anguloX <= 30 && anguloX >= -30) &&(anguloY <= 36 && anguloY > 30)){
            auxY = auxY * -1;
        }
        //esquerda
        if((anguloX >= -36 && anguloX < -30) &&(anguloY <= 30 && anguloY >= -30)){
            auxX = auxX * -1;
        }
        //direita
        if((anguloX <= 36 && anguloX > 30) &&(anguloY <= 30 && anguloY >= -30)){
            auxX = auxX * -1;
        }
         */
    }

    public boolean checarObst(double x1, double x2, double x3, double x4, double y1, double y2, double y3, double y4) {
        double c1, c2, c3, c4;
        c1 = (x1 * (anguloY - y2) + anguloX * (y2 - y1) + x2 * (y1 - anguloY)) / 2;
        c2 = (x3 * (anguloY - y2) + anguloX * (y2 - y3) + x2 * (y3 - anguloY)) / 2;
        c3 = (x4 * (anguloY - y3) + anguloX * (y3 - y4) + x3 * (y4 - anguloY)) / 2;
        c4 = (x1 * (anguloY - y4) + anguloX * (y4 - y1) + x4 * (y1 - anguloY)) / 2;

        if ((c1 + c2 + c3 + c4) > 0) {
            return false;
        }
        return true;
    }

    public void fim() {
        anguloY = - 72;
        tela = 0;
        start = false;
        auxX = auxY = 1;
        score = 0;
        fase = false;
    }

    public void desenharFundo(GL2 gl) {
        gl.glColor3f(0.5f, 0.5f, 0.5f);
        gl.glPointSize(200f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(95f, -80f);
        gl.glVertex2f(95f, 85f);
        gl.glVertex2f(-95f, 85f);
        gl.glVertex2f(-95f, -80f);
        gl.glEnd();
    }

    public String printStart(boolean start) {
        if (start) {
            if (pause) {
                return "Pausado";
            }
            return "Jogando";
        }
        return "Tela Inicial";
    }

    public void desenharObstaculo(GL2 gl) {
        gl.glPushMatrix();
        gl.glColor3f(0f, 0f, 0f);
        gl.glPointSize(200f);
        gl.glRotatef(anguloObt, 0, 0f, 1f);
        gl.glBegin(GL2.GL_POLYGON);

        gl.glVertex2f((float) x2, (float) y2);
        gl.glVertex2f((float) x3, (float) y3);
        gl.glVertex2f((float) x4, (float) y4);
        gl.glVertex2f((float) x1, (float) y1);
        gl.glEnd();
        gl.glPopMatrix();
    }

    public void desenharBola(GL2 gl, GLUT glut) {
        gl.glPushMatrix();
        gl.glTranslatef(anguloX, anguloY, 0f);
        gl.glColor3f(0f, 0f, 0.6f);
        glut.glutSolidSphere(8, 20, 16);
        gl.glPopMatrix();
    }

    public void desenharPlayer(GL2 gl) {
        gl.glColor3f(1f, 1f, 1f);
        gl.glPointSize(200f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f((15f + posPlayer), -85f);
        gl.glVertex2f((15f + posPlayer), -80f);
        gl.glVertex2f((-15f + posPlayer), -80f);
        gl.glVertex2f((-15f + posPlayer), -85f);
        gl.glEnd();
    }

    public void dadosObjeto(GL2 gl, int xPosicao, int yPosicao, Color cor, String frase) {
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        //Retorna a largura e altura da janela
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);
        textRenderer.setColor(cor);
        textRenderer.draw(frase, xPosicao, yPosicao);
        textRenderer.endRendering();
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //obtem o contexto grafico Opengl
        GL2 gl = drawable.getGL().getGL2();

        //evita a divisao por zero
        if (height == 0) {
            height = 1;
        }
        //calcula a proporcao da janela (aspect ratio) da nova janela
        //float aspect = (float) width / height;

        //seta o viewport para abranger a janela inteira
        //gl.glViewport(0, 0, width, height);
        //ativa a matriz de projecao
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); //ler a matriz identidade

        //Projeção ortogonal
        //true:   aspect >= 1 configura a altura de -1 para 1 : com largura maior
        //false:  aspect < 1 configura a largura de -1 para 1 : com altura maior
//        if(width >= height)            
//            gl.glOrtho(xMin * aspect, xMax * aspect, yMin, yMax, zMin, zMax);
//        else        
//            gl.glOrtho(xMin, xMax, yMin / aspect, yMax / aspect, zMin, zMax);
        //projecao ortogonal sem a correcao do aspecto
        gl.glOrtho(xMin, xMax, yMin, yMax, zMin, zMax);

        //ativa a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); //ler a matriz identidade
        System.out.println("Reshape: " + width + ", " + height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
}
