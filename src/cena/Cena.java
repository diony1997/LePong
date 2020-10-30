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
    public float auxX, auxY, posPlayer, anguloX, anguloY;
    public boolean start;
    public int tela;
    private GLU glu;

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
        tela = 0;
        auxX = auxY = 1;
        posPlayer = 0;
        start = false;
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
        dadosObjeto(gl, 20, 5, Color.WHITE, "Movimente com as setas");

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

        if (start) {
            movimentarBola();
        } else {
            if (tela == 0) {
                anguloX = posPlayer;
            }
        }
        desenharFundo(gl);
        desenharBola(gl, glut);
        desenharPlayer(gl);

        gl.glFlush();
    }

    public void movimentarBola() {
        anguloX += auxX;
        anguloY += auxY;

        if (anguloX > 88 && tela != 0) {
            auxX = -1;
        }
        if (anguloX < -88 && tela != 0) {
            auxX = 1;
        }
        if (anguloY > 78 && tela != 0) {
            auxY = -1;
        }
        if (anguloY < -80 && tela != 0) {
            fim();
        }
    }

    public void fim() {
        anguloY = - 72;
        tela = 0;
        start = false;
        auxX = auxY = 1;
    }
    
    public void desenharFundo(GL2 gl){
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
            return "Jogando";
        } else if (tela == 0) {
            return "Tela Inicial";
        } else {
            return "Pausado";
        }
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
