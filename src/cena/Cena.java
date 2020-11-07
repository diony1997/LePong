package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Siabreu
 */
public class Cena implements GLEventListener {

    private float xMin, xMax, yMin, yMax, zMin, zMax;
    private TextRenderer textRenderer;
    public float auxX, auxY, posPlayer, anguloX, anguloY, anguloObt;
    public boolean start, pause, fase, teste, telaInicial, reset, ready, destroy, gameOver;
    private double contLuz;
    public int tela, score, vidas, cont, qtdTxt;
    private GLU glu;
    private Random random;
    final JFXPanel fxPanel = new JFXPanel();
    private String tema = "src/res/musicas/tema1.mp3";
    private String tema1 = "src/res/musicas/start.mp3";
    private String tema2 = "src/res/musicas/tema2.mp3";
    private String tema3 = "src/res/musicas/gameover.mp3";
    private Texture[] vetTextures;
    private static final String txtInicio = "src/res/textura/paredeMetal.jpg";
    private static final String txtFundo = "src/res/textura/base.png";
    private static final String txtFim = "src/res/textura/paredeMetal2.jpg";

    Media hit = new Media(new File(tema).toURI().toString());
    Media hit2 = new Media(new File(tema1).toURI().toString());
    Media hit3 = new Media(new File(tema2).toURI().toString());
    Media hit4 = new Media(new File(tema3).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(hit);
    MediaPlayer mpStart = new MediaPlayer(hit2);
    MediaPlayer mpTema2 = new MediaPlayer(hit3);
    MediaPlayer mpGG = new MediaPlayer(hit4);

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
        tela = score = cont = 0;
        auxX = auxY = 1;
        posPlayer = 0;
        vidas = 5;
        anguloObt = 0;
        vetTextures = new Texture[3];
        carregarTextura(gl, txtInicio, 0);
        carregarTextura(gl, txtFundo, 1);
        carregarTextura(gl, txtFim, 2);
        contLuz = 0;
        start = pause = fase = teste = reset = ready = destroy = gameOver = false;
        telaInicial = true;
        random = new Random();
        //Habilita o buffer de profundidade
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if (reset) {
            init(drawable);
        }
        //obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();
        //objeto para desenho 3D
        GLUT glut = new GLUT();
        //define a cor da janela (R, G, G, alpha)
        gl.glClearColor(0, 0, 0, 0);
        //limpa a janela com a cor especificada
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glLoadIdentity(); //ler a matriz identidade

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        if (destroy) {
            esvaziarTxt(gl);
        }

        // Face FRONTAL
        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 26));
        dadosObjeto(gl, 20, 927, Color.WHITE, "MODO: " + printStart(start));
        dadosObjeto(gl, 935, 927, Color.WHITE, "Membros: ");
        dadosObjeto(gl, 935, 892, Color.WHITE, "Dados Coletados:");
        dadosObjeto(gl, 1165, 892, Color.CYAN, ""+score);
        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 24));
        dadosObjeto(gl, 20, 7, Color.WHITE, "Movimente com as setas, comece com espaço e pause com ESC.");

        System.out.println(anguloX);
        System.out.println(anguloY);
        anguloObt += 0.1;
        vidas(gl, glut);
        iluminacao(gl);

        musica(cont);
        if (!pause) {
            desenharBola(gl, glut);

            if (start) {
                movimentarBola();
            } else {
                if (tela == 0) {
                    anguloX = posPlayer;
                }
            }
        }

        if (score >= 200) {
            desenharObstaculo(gl, glut);
        }
        if (telaInicial) {
            desenharTelaInicial(gl);
            cont = 1;
        }
        desenharFundo(gl, glut);
        desenharPlayer(gl);        

        if (vidas == 0) {
            gameOver = true;
            desenharTelaFinal(gl);
            if (cont == 1) {
                musica(5);
                musica(9);
                cont = 11;
            } else {
                musica(8);
                musica(9);
                cont = 11;
            }
            if (ready) {
                fim();
            }
        }
        
        if (pause) {
            gl.glPushMatrix();
            gl.glTranslatef(0, 0, 40);
            textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 48));
//            dadosObjeto(gl, 470, 620, Color.YELLOW, "R -> Resume");
//            dadosObjeto(gl, 470, 520, Color.YELLOW, "M -> Main Menu");
//            dadosObjeto(gl, 470, 420, CotelaIniciallor.YELLOW, "E -> Exit");
            dadosObjeto(gl, !telaInicial && !gameOver ? 470 : 820, !telaInicial && !gameOver ? 620 : 820, !telaInicial && !gameOver ? Color.YELLOW : Color.RED, "R -> Resume");
            dadosObjeto(gl, !telaInicial && !gameOver ? 470 : 820, !telaInicial && !gameOver ? 520 : 720, !telaInicial && !gameOver ? Color.YELLOW : Color.RED, "M -> Main Menu");
            dadosObjeto(gl, !telaInicial && !gameOver ? 470 : 820, !telaInicial && !gameOver ? 420 : 620, !telaInicial && !gameOver ? Color.YELLOW : Color.RED, "E -> Exit");
            gl.glPopMatrix();
        }

        gl.glFlush();
    }

    private void carregarTextura(GL2 gl, String fileName, int indice) {
        Texture tex = null;

        //carrega o arquivo da imagem
        try {
            tex = TextureIO.newTexture(new File(fileName), true);
        } catch (IOException e) {
            System.out.println("\n=============\nErro na leitura do arquivo "
                    + fileName + "\n=============\n");
        }

        tex.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        tex.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

        //GL.GL_REPEAT ou GL.GL_CLAMP        
        tex.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        tex.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);

        //GL.GL_MODULATE ou GL.GL_DECAL ou GL.GL_BLEND
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);
        vetTextures[indice] = tex;
    }

    public void musica(int cont) {
        switch (cont) {
            case 0:
                mpStart.setVolume(0.2);
                mpStart.play();
                break;
            case 2:
                mpStart.stop();
                break;
            case 3:
                mediaPlayer.setVolume(0.2);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
                break;
            case 4:
                mediaPlayer.pause();
                break;
            case 5:
                mediaPlayer.stop();
                break;
            case 6:
                mpTema2.setVolume(0.2);
                mpTema2.setCycleCount(MediaPlayer.INDEFINITE);
                mpTema2.play();
                break;
            case 7:
                mpTema2.pause();
                break;
            case 8:
                mpTema2.stop();
                break;
            case 9:
                mpGG.setVolume(0.2);
                mpGG.play();
                break;
            case 10:
                mpGG.stop();
                break;
        }
    }

    public void vidas(GL2 gl, GLUT glut) {
        if (vidas > 0) {
            for (int i = 1; i <= vidas; i++) {
                gl.glPushMatrix();
                gl.glTranslatef(64 + (i * 6), 95, 0f);
                gl.glRotatef(anguloObt, 0, 1, 0);
                gl.glColor3f(1f, 1f, 1f);
                glut.glutSolidSphere(2, 20, 16);
                gl.glPopMatrix();
            }
        }
    }

    public void movimentarBola() {
        float posBolax = anguloX;
        float posBolaY = anguloY;
        anguloX += auxX;
        anguloY += auxY;

        if (anguloObt == 360) {
            anguloObt = 1;
        }

        if (score >= 200 && !fase) {
//            auxX = auxX * 1.5f;
//            auxY = auxY * 1.5f;
            musica(5);
            musica(6);
            cont = 2;
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cena.class.getName()).log(Level.SEVERE, null, ex);
            }
            fase = true;
        }

        float randomX = auxX > 0 ? (-1 * (random.nextFloat() * ((fase ? 1.9f : 1.4f) - (fase ? 1.5f : 1)) + (fase ? 1.5f : 1))) : (random.nextFloat() * ((fase ? 1.9f : 1.4f) - (fase ? 1.5f : 1)) + (fase ? 1.5f : 1));
        float randomY = auxY > 0 ? (-1 * (random.nextFloat() * ((fase ? 1.9f : 1.4f) - (fase ? 1.5f : 1)) + (fase ? 1.5f : 1))) : (random.nextFloat() * ((fase ? 1.9f : 1.4f) - (fase ? 1.5f : 1)) + (fase ? 1.5f : 1));

        if (anguloX > 84 && tela != 0) {
            anguloX = posBolax;
            anguloY = posBolaY;
            auxX = randomX;

        }
        if (anguloX < -84 && tela != 0) {
            anguloX = posBolax;
            anguloY = posBolaY;
            auxX = randomX;

        }
        if (anguloY > 74 && tela != 0) {
            anguloX = posBolax;
            anguloY = posBolaY;
            auxY = randomY;
        }

        //Colisão com o player
        if ((posBolaY < -74 && posBolaY > -80) && (posBolax <= (posPlayer + 15) && posBolax >= (posPlayer - 15))) {
            score += 50;
            anguloX = posBolax;
            anguloY = -72;
            auxY = randomY;
        }

        if (anguloY < -88 && tela != 0) {
            morte();
        }

        // Colisão quadrado versão estatica
        //colisão com obstaculo Baixo
        if (score >= 200) {
            //Baixo
            if ((posBolax < 12 && posBolax > -12) && (anguloY >= -14 && anguloY < -8)) {
                anguloX = posBolax;
                anguloY = -15;
                auxY = randomY;
            }
            //Alto
            if ((posBolax < 12 && posBolax > -12) && (anguloY <= 14 && anguloY > 8)) {

                anguloX = posBolax;
                anguloY = 15;
                auxY = randomY;
            }
            //esquerda
            if ((posBolax >= -14 && posBolax < -8) && (anguloY < 12 && anguloY > -12)) {
                anguloX = -15;
                anguloY = posBolaY;
                auxX = randomX;
            }
            //direita
            if ((posBolax <= 14 && posBolax > 8) && (anguloY < 12 && anguloY > -12)) {
                anguloX = 15;
                anguloY = posBolaY;
                auxX = randomX;
            }

        }

    }

    public void iluminacao(GL2 gl) {
        //Parte de iluminação
        float[] corLuz = {1f, 1f, 1f, 1f};
        if (!fase) {

            gl.glPushMatrix();
            gl.glRotatef(anguloObt * 10f, 0, 0, 1);
            gl.glTranslatef(0, -50, 0);
            float[] posLuz = {0f, 0, 300, 1};
            gl.glEnable(GL2.GL_LIGHTING);
            gl.glDisable(GL2.GL_LIGHT1);
            gl.glEnable(GL2.GL_LIGHT0);
            gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 64);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, corLuz, 0);
            gl.glEnable(GL2.GL_COLOR_MATERIAL);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, corLuz, 1);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posLuz, 0);

            gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF, 4f);
            gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_EXPONENT, 1);

            gl.glShadeModel(GL2.GL_SMOOTH);
            gl.glPopMatrix();
            desenharCirculo(gl);
        } else {
            if (contLuz >= 0 && contLuz <= 4) {
                corLuz[0] = 0f;
                corLuz[1] = 0f;
                corLuz[2] = 0f;
                corLuz[3] = 1f;
            } else if (contLuz > 4 && contLuz <= 9) {
                corLuz[0] = 1f;
                corLuz[1] = 0f;
                corLuz[2] = 0f;
                corLuz[3] = 1f;
            } else {
                contLuz = 0;
            }
            float[] posLuz = {0f, 0f, 100f, 1};

            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, corLuz, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, posLuz, 0);
            gl.glShadeModel(GL2.GL_SMOOTH);
            gl.glEnable(GL2.GL_LIGHT1);

            float[] posBola = {anguloX, anguloY, 100f, 1};
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posBola, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, corLuz, 0);
            gl.glShadeModel(GL2.GL_SMOOTH);
            desenharCirculo2(gl);
            contLuz++;
        }

    }

    public void morte() {
        anguloY = - 72;
        tela = 0;
        start = false;
        vidas--;
        if (score >= 200) {
//            auxX = auxY = 1.5f;
            auxY = Math.abs(auxY);
        } else {
            auxX = auxY = 1;
        }
    }

    public void desenharCirculo(GL2 gl) {
        gl.glPushMatrix();
        gl.glRotatef(anguloObt * 10f, 0, 0, 1);

        gl.glTranslatef(0, -50, 6);
        gl.glColor3f(1f, 1f, 0);
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i <= 300; i++) {
            double angle = 2 * Math.PI * i / 300;
            double x = 20 * Math.cos(angle);
            double y = 20 * Math.sin(angle);
            gl.glVertex2d(x, y);
        }
        gl.glEnd();
        gl.glPopMatrix();
    }

    public void desenharCirculo2(GL2 gl) {
        float[] cor = {1, 1, 0};
       
        if(contLuz >= 0 && contLuz < 4){
        gl.glPushMatrix();
        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f(cor[0], cor[1], cor[2]);
        gl.glVertex3f(-6f + anguloX, anguloY, 6f);
        gl.glVertex3f(6f + anguloX, anguloY, 6f);
        gl.glVertex3f(0f, 0f, 8f);

        gl.glVertex3f(anguloX, 6f + anguloY, 6f);
        gl.glVertex3f(+anguloX, -6f + anguloY, 6f);
        gl.glVertex3f(0f, 0f, 8f);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(anguloX, anguloY, 6);

        gl.glColor3f(cor[0], cor[1], cor[2]);
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i <= 300; i++) {
            double angle = 2 * Math.PI * i / 300;
            double x = 10 * Math.cos(angle);
            double y = 10 * Math.sin(angle);
            gl.glVertex2d(x, y);
        }
        gl.glEnd();
        gl.glPopMatrix();
        }
        contLuz++;
    }

    public void fim() {
        gameOver = false;
        anguloY = - 72;
        tela = 0;
        start = false;
        auxX = auxY = 1;
        score = 0;
        fase = false;
        vidas = 5;
        musica(10);
        if (cont == 1) {
            musica(5);
        } else {
            musica(8);
        }
        musica(3);
        cont = 1;
        ready = false;
    }

    public String printStart(boolean start) {
        if (start) {
            if (pause) {
                return "Pausado";
            }
            return "Jogando";
        }
        return "Parado";
    }

    public void desenharObstaculo(GL2 gl, GLUT glut) {
        float[] corLuz = {1f, 1f, 1f, 1f};
        gl.glPushMatrix();
        gl.glBegin(gl.GL_TRIANGLES);

        gl.glColor3f(1f, 1f, 1f);

        gl.glVertex3f(-8f, -8f, 8f);
        gl.glVertex3f(8f, -8f, 8f);
        gl.glVertex3f(-8f, 8f, 8f);

        gl.glVertex3f(-8f, 8f, 8f);
        gl.glVertex3f(8f, -8f, 8f);
        gl.glVertex3f(8f, 8f, 8f);
        gl.glEnd();
        gl.glPushMatrix();
        if (contLuz >= 0 && contLuz <= 4) {
                corLuz[0] = 0f;
                corLuz[1] = 0f;
                corLuz[2] = 0f;
                corLuz[3] = 1f;
            } else if (contLuz > 4 && contLuz <= 9) {
                corLuz[0] = 1f;
                corLuz[1] = 0f;
                corLuz[2] = 0f;
                corLuz[3] = 1f;
            }
        gl.glColor3f(corLuz[0], corLuz[1], corLuz[2]);
        gl.glTranslatef(0, 0, 8f);
        glut.glutSolidSphere(6, 20, 16);
        gl.glPopMatrix();
        gl.glPopMatrix();
    }

    public void desenharFundo(GL2 gl, GLUT glut) {

        gl.glPushMatrix();
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);

        vetTextures[1].bind(gl);
        vetTextures[1].enable(gl);
        gl.glBegin(gl.GL_TRIANGLES);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 10f);
        //Parede esquerda

        gl.glColor3f(1f, 1f, 1f);
        gl.glVertex3f(-94, -85, 8f);
        gl.glVertex3f(-90, -85, 8f);
        gl.glVertex3f(-94, 80, 8f);

        gl.glVertex3f(-94, 80, 8f);
        gl.glVertex3f(-90, -85, 8f);
        gl.glVertex3f(-90, 80, 8f);

        //Teto
        gl.glVertex3f(-94, 80, 8f);
        gl.glVertex3f(94, 80, 8f);
        gl.glVertex3f(-94, 84, 8f);

        gl.glVertex3f(-94, 84, 8f);
        gl.glVertex3f(94, 80, 8f);
        gl.glVertex3f(94, 84, 8f);

        //Parede direita
        gl.glVertex3f(90, -85, 8f);
        gl.glVertex3f(94, -85, 8f);
        gl.glVertex3f(94, 80, 8f);

        gl.glVertex3f(90, -85, 8f);
        gl.glVertex3f(94, 80, 8f);
        gl.glVertex3f(90, 80, 8f);

        //Chao
        gl.glColor3f(0f, 0f, 0f);
        gl.glVertex3f(-94, -85, 8f);
        gl.glVertex3f(94, -95, 8f);
        gl.glVertex3f(94, -85, 8f);

        gl.glVertex3f(-94, -85, 8f);
        gl.glVertex3f(-94, -95, 8f);
        gl.glVertex3f(94, -95, 8f);

        gl.glPopMatrix();
        gl.glTranslatef(0, 0, 5f);
        //fundo
        gl.glColor3f(1f, 1f, 1f);
        gl.glTexCoord3f(0f, 0f, 1f);
        gl.glVertex3f(-90f, -85f, 1f);
        gl.glTexCoord3f(1f, 0f, 1f);
        gl.glVertex3f(90f, -85f, 1f);
        gl.glTexCoord3f(0f, 1f, 1f);
        gl.glVertex3f(-90f, 80f, 1f);

        gl.glTexCoord3f(0f, 1f, 1f);
        gl.glVertex3f(-90f, 80f, 1f);
        gl.glTexCoord3f(1f, 0f, 1f);
        gl.glVertex3f(90f, -85f, 1f);
        gl.glTexCoord3f(1f, 1f, 1f);
        gl.glVertex3f(90f, 80f, 1f);

        gl.glEnd();
        vetTextures[1].disable(gl);

        gl.glPopMatrix();
    }

    public void desenharTelaInicial(GL2 gl) {

        gl.glPushMatrix();
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);

        vetTextures[0].bind(gl);
        vetTextures[0].enable(gl);
        gl.glTranslatef(0, 0, 20);

        gl.glBegin(gl.GL_TRIANGLES);

        gl.glColor3f(0f, 1f, 1f);
        gl.glTexCoord2f(0f, 0f);
        gl.glVertex2f(-100f, -100f);
        gl.glTexCoord2f(1f, 0f);
        gl.glVertex2f(100f, -100f);
        gl.glTexCoord2f(0f, 1f);
        gl.glVertex2f(-100f, 100f);
        //fundo

        gl.glTexCoord2f(0f, 1f);
        gl.glVertex2f(-100f, 100f);
        gl.glTexCoord2f(1f, 0f);
        gl.glVertex2f(100f, -100f);
        gl.glTexCoord2f(1f, 1f);
        gl.glVertex2f(100f, 100f);
        gl.glEnd();

        vetTextures[0].disable(gl);

        textRenderer = new TextRenderer(new Font("Arial Black", Font.PLAIN, 80));
        dadosObjeto(gl, 140, 640, Color.WHITE, "Metal Pong");
        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 36));
        dadosObjeto(gl, 60, 500, Color.WHITE, "Você e seu esquadrão infiltraram na base inimiga!!!");
        dadosObjeto(gl, 60, 440, Color.WHITE, "Colete as informações com os seus aliados");
        dadosObjeto(gl, 60, 380, Color.WHITE, "Faça com que eles permaneçam o");
        dadosObjeto(gl, 660, 380, Color.RED, "MAXIMO POSSIVEL");
        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 34));
        dadosObjeto(gl, 140, 260, Color.WHITE, "Tecle");
        dadosObjeto(gl, 240, 260, Color.YELLOW, "E");
        dadosObjeto(gl, 280, 260, Color.WHITE, "para iniciar");

        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 15));

        gl.glPopMatrix();
    }

    public void desenharTelaFinal(GL2 gl) {

        gl.glPushMatrix();
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);

        vetTextures[2].bind(gl);
        vetTextures[2].enable(gl);
        gl.glTranslatef(0, 0, 20);
        gl.glBegin(gl.GL_TRIANGLES);
        //fundo
        gl.glColor3f(1f, 1f, 0f);
        gl.glTexCoord3f(0f, 0f, 10f);
        gl.glVertex3f(-100f, -100f, 10f);
        gl.glTexCoord3f(1f, 0f, 10f);
        gl.glVertex3f(100f, -100f, 10f);
        gl.glTexCoord3f(0f, 1f, 10f);
        gl.glVertex3f(-100f, 100f, 10f);

        gl.glTexCoord3f(0f, 1f, 10f);
        gl.glVertex3f(-100f, 100f, 10f);
        gl.glTexCoord3f(1f, 0f, 10f);
        gl.glVertex3f(100f, -100f, 10f);
        gl.glTexCoord3f(1f, 1f, 10f);
        gl.glVertex3f(100f, 100f, 10f);
        gl.glEnd();
        vetTextures[2].disable(gl);

        textRenderer = new TextRenderer(new Font("Arial Black", Font.BOLD, 48));
        dadosObjeto(gl, 140, 640, Color.BLACK, "GAME OVER");
        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 38));
        dadosObjeto(gl, 140, 450, Color.WHITE, "Informações adquiridas:");
        dadosObjeto(gl, 590, 450, Color.CYAN, "" + score);
        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 34));
        dadosObjeto(gl, 140, 260, Color.WHITE, "Tecle");
        dadosObjeto(gl, 240, 260, Color.YELLOW, "E");
        dadosObjeto(gl, 280, 260, Color.WHITE, "Para Reiniciar");

        textRenderer = new TextRenderer(new Font("Steamer", Font.BOLD, 15));

        gl.glPopMatrix();
    }

    public void desenharBola(GL2 gl, GLUT glut) {
        gl.glPushMatrix();
        gl.glTranslatef(anguloX, anguloY, 6f);
        gl.glColor3f(1f, 1f, 1f);
        glut.glutSolidSphere(8, 20, 16);
        gl.glPopMatrix();
    }

    public void desenharPlayer(GL2 gl) {
        gl.glColor3f(1f, 1f, 1f);
        gl.glPointSize(200f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f((14f + posPlayer), -85f, 8f);
        gl.glVertex3f((14f + posPlayer), -80f, 8f);
        gl.glVertex3f((-14f + posPlayer), -80f, 8f);
        gl.glVertex3f((-14f + posPlayer), -85f, 8f);
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

    public void esvaziarTxt(GL2 gl) {
        for (int i = 0; i < vetTextures.length; i++) {
            vetTextures[i].destroy(gl);
        }
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
