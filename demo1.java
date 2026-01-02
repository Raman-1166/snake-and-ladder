import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;

class FDemo extends JFrame {
    JPDemo jp1;

    FDemo() {
        super.setTitle("Snake & Ladder");
        jp1 = new JPDemo();
        add(jp1);
    }
}

class JPDemo extends JPanel implements ActionListener {
    ImageIcon img1, img2, img3, img4, img5;
    ImageIcon img6, img7, img8, img9, img10;
    ImageIcon img11, img12, img13, img14, img15, img16, img17, img18;
    Image swt, start, start1, player1, player2, reset;
    Image snakegame;
    Image dice, dice1, dice2, dice3, dice4, dice5, dice6;
    Image about, softwaves;
    JButton b1, b2, b3, b4;
    JTextField tx1, tx2, tx3;

    // Initial coordinates for Square 1 (Bottom Left)
    // Grid starts at x=200. Col 0 is x=215 approx inside.
    // Row 0 is at bottom (y=735-margin). Logic is 675 - row*73.
    int px1 = 200;
    int py1 = 650;
    int px2 = 245; // Slight offset for P2
    int py2 = 650;
    int player1Pos = 0, player2Pos = 0, i = 1;
    int stepsRemaining = 0;

    Timer moveTimer;
    Clip player;
    Clip dicer;
    Clip back;
    Clip win;
    Clip ladder;
    Clip snake;

    JPDemo() {
        setPreferredSize(new Dimension(990, 735)); // Fixed size
        setBackground(Color.black);

        img1 = new ImageIcon("swt.jpg");
        img2 = new ImageIcon("start.jpg");
        img3 = new ImageIcon("dice.jpg");
        img4 = new ImageIcon("dice1.jpg");
        img5 = new ImageIcon("dice2.jpg");
        img6 = new ImageIcon("dice3.jpg");
        img7 = new ImageIcon("dice4.jpg");
        img8 = new ImageIcon("dice5.jpg");
        img9 = new ImageIcon("dice6.jpg");
        img10 = new ImageIcon("start1.jpg");
        img11 = new ImageIcon("player1.png");
        img12 = new ImageIcon("player2.png");
        img13 = new ImageIcon("reset.jpg");
        img14 = new ImageIcon("snakegame.jpg");
        img15 = new ImageIcon("about.jpg");
        img16 = new ImageIcon("softwaves.jpg");
        img17 = new ImageIcon("dice1.jpg");

        swt = img1.getImage();
        start = img2.getImage();
        dice = img3.getImage();
        dice1 = img4.getImage();
        start1 = img10.getImage();
        player1 = img11.getImage();
        player2 = img12.getImage();
        reset = img13.getImage();
        snakegame = img14.getImage();
        about = img15.getImage();
        softwaves = img16.getImage();
        dice1 = img17.getImage();

        // Scale icons to fit buttons
        Image aboutImg = img15.getImage().getScaledInstance(100, 30, Image.SCALE_SMOOTH);
        img15 = new ImageIcon(aboutImg);

        Image resetImg = img13.getImage().getScaledInstance(100, 25, Image.SCALE_SMOOTH);
        img13 = new ImageIcon(resetImg);

        setLayout(null);

        b1 = new JButton(img15);
        b1.setBounds(50, 110, 100, 30);
        b1.setBackground(new Color(4, 129, 255));
        b1.addActionListener(this);
        add(b1);

        b2 = new JButton(img13);
        b2.setBounds(50, 160, 100, 25);
        b2.addActionListener(this);
        add(b2);

        Font f = new Font("Bauhaus 93", Font.ITALIC, 20);

        tx1 = new JTextField("Snake & Ladder");
        tx1.setBounds(50, 210, 150, 35);
        tx1.setBackground(Color.black);
        tx1.setForeground(Color.green);
        tx1.setFont(f);
        add(tx1);

        tx2 = new JTextField("Player 1");
        tx2.setBounds(60, 260, 140, 35);
        tx2.setBackground(Color.black);
        tx2.setForeground(Color.green);
        tx2.setFont(f);
        add(tx2);

        tx3 = new JTextField("Player 2");
        tx3.setBounds(60, 330, 140, 35);
        tx3.setBackground(Color.black);
        tx3.setForeground(Color.green);
        tx3.setFont(f);
        add(tx3);

        b3 = new JButton("ROLL");
        b3.setBounds(55, 485, 90, 30);
        b3.setFont(f);
        b3.setForeground(Color.red);
        b3.addActionListener(this);
        add(b3);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(softwaves, 0, 0, 990, 735, this); // Scaled background
        g.setColor(new Color(4, 129, 255));
        g.fillRect(0, 0, 200, 735);
        g.drawImage(swt, 200, 0, 790, 735, this); // Scaled board
        g.drawImage(dice, 120, 400, 50, 50, this); // Scaled dice
        g.drawImage(player1, 10, 250, 50, 50, this); // Scaled player icon

        // Draw players
        g.drawImage(player1, px1, py1, 40, 40, this);
        g.drawImage(player2, px2, py2, 40, 40, this);

        g.drawImage(player2, 10, 325, 50, 50, this); // Scaled player icon
        g.drawImage(snakegame, 40, 400, 50, 50, this);
    }

    void playSound(String fileName, Clip clip) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(fileName));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound error: " + e);
        }
    }

    void updatePlayer1Position() {
        Point p = getCoordinates(player1Pos);
        px1 = p.x;
        py1 = p.y;
    }

    void updatePlayer2Position() {
        Point p = getCoordinates(player2Pos);
        px2 = p.x + 10; // Offset for P2
        py2 = p.y;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            JOptionPane.showMessageDialog(this, "Snake & Ladder Game developed by\nAdmin: Raman Chourasiya");
        } else if (e.getSource() == b2) {
            resetGame();
        } else if (e.getSource() == b3) {
            int random = (int)(Math.random() * 6) + 1;
            playSound("dicer.wav", dicer);

            switch (random) {
                case 1: img3 = new ImageIcon("dice1.jpg"); break;
                case 2: img3 = new ImageIcon("dice2.jpg"); break;
                case 3: img3 = new ImageIcon("dice3.jpg"); break;
                case 4: img3 = new ImageIcon("dice4.jpg"); break;
                case 5: img3 = new ImageIcon("dice5.jpg"); break;
                case 6: img3 = new ImageIcon("dice6.jpg"); break;
            }
            dice = img3.getImage();

            int currentPos = (i % 2 == 1) ? player1Pos : player2Pos;
            if (currentPos + random > 100) {
                stepsRemaining = 0;
                
            } else {
                stepsRemaining = random;
            }
            
            b3.setEnabled(false); // Disable roll button

            moveTimer = new Timer(230, new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    if (stepsRemaining > 0) {
                        if (i % 2 == 1) {
                            player1Pos++;
                            if (player1Pos > 100) {
                                player1Pos = 100;
                                stepsRemaining = 0;
                            }
                            updatePlayer1Position();
                        } else {
                            player2Pos++;
                            if (player2Pos > 100) {
                                player2Pos = 100;
                                stepsRemaining = 0;
                            }
                            updatePlayer2Position();
                        }
                        repaint();
                        stepsRemaining--;
                    } else {
                        ((Timer)ae.getSource()).stop();
                        finishTurn();
                    }
                }
            });
            moveTimer.start();
            repaint();
        }
    }

    Point getCoordinates(int pos) {
        int cell = pos - 1;
        if (cell < 0) return new Point(200, 650); // Base

        int row = cell / 10;
        int col = cell % 10;

        if (row % 2 == 1) {
            col = 9 - col;
        }

        int x = 215 + col * 79;
        int y = 675 - row * 73;
        return new Point(x, y);
    }

    void finishTurn() {
        int currentPos = (i % 2 == 1) ? player1Pos : player2Pos;
        checkLadderStep(currentPos);
    }

    void checkLadderStep(int currentPos) {
        int afterLadder = checkLadder(currentPos);
        if (afterLadder != currentPos) {
            playSound("ladder.wav", ladder);
            animatePlayer(currentPos, afterLadder, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    checkSnakeStep(afterLadder);
                }
            });
        } else {
            checkSnakeStep(currentPos);
        }
    }

    void checkSnakeStep(int currentPos) {
        int afterSnake = checkSnake(currentPos);
        if (afterSnake != currentPos) {
             playSound("snake.wav", snake); // Using back.wav for snake bite
             animatePlayer(currentPos, afterSnake, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    finalizeTurn(afterSnake);
                }
            });
        } else {
            finalizeTurn(currentPos);
        }
    }

    void finalizeTurn(int finalPos) {
        if (i % 2 == 1) {
            player1Pos = finalPos;
            updatePlayer1Position();
            if (player1Pos == 100) {
               showWinMessage("Player 1");
               return; // Stop turn
            }
        } else {
            player2Pos = finalPos;
            updatePlayer2Position();
             if (player2Pos == 100) {
               showWinMessage("Player 2");
               return; // Stop turn
            }
        }
        i++;
        b3.setEnabled(true);
        repaint();
    }

    void showWinMessage(String player) {
         playSound("win.wav", win);
         JOptionPane.showMessageDialog(this, player + " Wins! Game will reset in 10 seconds.");
         Timer t = new Timer(5000, new ActionListener() {
             public void actionPerformed(ActionEvent ae) {
                 resetGame();
             }
         });
         t.setRepeats(false);
         t.start();
    }

    void animatePlayer(int startPos, int endPos, ActionListener onComplete) {
        Point startPt = getCoordinates(startPos);
        Point endPt = getCoordinates(endPos);
        if (i % 2 == 0) { // Player 2 offset
             startPt.x += 10;
             endPt.x += 10;
        }

        final int steps = 40;
        final int delay = 20;
        final float dx = (endPt.x - startPt.x) / (float)steps;
        final float dy = (endPt.y - startPt.y) / (float)steps;

        Timer animTimer = new Timer(delay, null);
        animTimer.addActionListener(new ActionListener() {
            int count = 0;
            float cx = startPt.x;
            float cy = startPt.y;

            public void actionPerformed(ActionEvent e) {
                if (count < steps) {
                    cx += dx;
                    cy += dy;
                    if (i % 2 == 1) {
                        px1 = (int)cx;
                        py1 = (int)cy;
                    } else {
                        px2 = (int)cx;
                        py2 = (int)cy;
                    }
                    repaint();
                    count++;
                } else {
                    ((Timer)e.getSource()).stop();
                    if (onComplete != null) onComplete.actionPerformed(e);
                }
            }
        });
        animTimer.start();
    }

    void resetGame() {
        player1Pos = 0;
        player2Pos = 0;
        i = 1;
        updatePlayer1Position();
        updatePlayer2Position();
        b3.setEnabled(true); 
        repaint();
    }

    int checkLadder(int pos) {
        switch (pos) {
            case 4: return 25;
            case 13: return 46;
            case 33: return 49;
            case 42: return 63;
            case 50: return 69;
            case 62: return 81;
            case 74: return 92;
            default: return pos;
        }
    }

    int checkSnake(int pos) {
        switch (pos) {
            case 27: return 5;
            case 40: return 3;
            case 43: return 18;
            case 54: return 31;
            case 66: return 45;
            case 76: return 58;
            case 89: return 53;
            case 99: return 41;
            default: return pos;
        }
    }
}

class demo1 {
    public static void main(String args[]) {
        FDemo f = new FDemo();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fixed the constant access
        f.setResizable(false);
        f.pack(); // Pack instead of setBounds
        f.setLocationRelativeTo(null); // Center on screen
        f.setVisible(true);
    }
}
