package main.java;

import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.sound.sampled.*;
import java.io.*;

import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_HEIGHT*SCREEN_WIDTH)/UNIT_SIZE;
    static final int DELAY = 100;

    final int[] x = new int [GAME_UNITS];
    final int[] y = new int [GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    Clip clip;


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void bgmusic(){
        try {
            AudioInputStream aud = AudioSystem.getAudioInputStream(new File("D://skull/prjt/snakejava/res/audio/bgretro.wav"));
            clip = AudioSystem.getClip();
            clip.open(aud);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public void startGame(){
        bgmusic();
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw (Graphics g){
        if (running) {
            g.setColor(Color.cyan);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        }
        else {
            gameOver(g);
        }
        g.setColor(Color.red);
        g.setFont(new Font("Serif", Font.PLAIN, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
    }
    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move(){
        for (int i = bodyParts; i>0;i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
    public void apple(){
        if((x[0] == appleX) && (y[0] == appleY)){
            try {
                AudioInputStream aud = AudioSystem.getAudioInputStream(new File("D://skull/prjt/snakejava/res/audio/bell.wav"));
                Clip clip = AudioSystem.getClip();
                clip.open(aud);
                clip.loop(0);

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    public void checkCollisions(){
        for (int i = bodyParts; i>0; i--){
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        if (x[0] < 0){
            x[0] = SCREEN_WIDTH;
        }
        if (x[0] > SCREEN_WIDTH){
            x[0] = 0;
        }
        if (y[0] < 0){
            y[0] = SCREEN_HEIGHT;
        }
        if (y[0] > SCREEN_HEIGHT){
            y[0] = 0;
        }
        if (!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g){
        if (clip != null) {
            clip.stop();
        }
        try {
            AudioInputStream aud = AudioSystem.getAudioInputStream(new File("D://skull/prjt/snakejava/res/audio/gameover.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(aud);
            clip.loop(0);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        g.setColor(Color.GRAY);
        g.setFont(new Font("Serif", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
        g.setFont(new Font("Serif", Font.PLAIN, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
        g.setFont(new Font("Serif", Font.PLAIN, 30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press Spacebar to restart", (SCREEN_WIDTH - metrics3.stringWidth("Press Spacebar to restart"))/2, SCREEN_HEIGHT - 150);
    }

    public void restartGame(){
        bgmusic();
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        newApple();
        running = true;
        timer.restart();
        requestFocusInWindow();

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            move();
            apple();
            checkCollisions();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if (direction!= 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction!= 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction!= 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction!= 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        restartGame();
                    }
                    break;
            }
        }
    }
}