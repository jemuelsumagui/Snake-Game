import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


public class GameOver {
	public Rectangle pulsanteNewGame= new Rectangle(100, 480, 150, 50);
	public Rectangle pulsanteExit= new Rectangle(350, 480, 150, 50);
	String nomePlayer;
	Random random;
	String line;
	Font gameFont;
	Boolean newHighscore=false;
	
	//Gestire in questa classe la classifica
	public GameOver() {
		
		System.out.println("Game Over");
		random = new Random();
		//numero randomico da affiancare al nome di default del giocatore
		int r=random.nextInt(99);
		ImageIcon snake = new ImageIcon("snakeIcon.png");
		nomePlayer=(String)JOptionPane.showInputDialog(null, "Insert Name: ", 
				"Player Name", JOptionPane.QUESTION_MESSAGE, snake, null, "Player"+r);
		nomePlayer=nomePlayer.replaceAll("#", "_");
		NameScore newPlayer= new NameScore(nomePlayer, GamePanel.score);
		System.out.println(newPlayer);
		
		//Se il noem del player non è nulla allora ordino la classifica
		//vedi metodo dopo
		if(nomePlayer!=null) {
			sortClassifica(newPlayer);
		}else
		{
			System.out.println("Hai annullato l'inserimento dello score");
		}
		//questo metodo mi risalva l'arratyList sul file txt
		updateClassifica();
	}

	public void sortClassifica(NameScore n) {
		//Aggiungo il nuovo giocatore
		int last=GamePanel.classifica.size();
		//la funzione compareto mi da 0 se le due stringe comparate sono "equivalenti"
		if(n.name.compareTo("")==0) {
			System.out.println("Stringa nome vuota");
			n.name="Default";
		}
		for(int i=0; i<GamePanel.classifica.size();i++) {
			if(n.score>GamePanel.classifica.get(i).score) {
				GamePanel.classifica.add(i, n);
				if(i==0) newHighscore=true;
				break;
			}
			if(n.score==GamePanel.classifica.get(i).score) {
				GamePanel.classifica.add(i+1, n);
				break;
			}
			if(n.score<=GamePanel.classifica.get(last-1).score) {
				GamePanel.classifica.add(n);
				break;
			}
		}
		if(GamePanel.classifica.size()==0) {
			GamePanel.classifica.add(n);
			newHighscore=true;
		}
	}
	
	//Metodo per riscrivere il file txt
	public void updateClassifica() {
		try {
			//Il printerwriter mi permette di risalvare l'arrayList nel file txt
			PrintWriter outputFile=new PrintWriter("classifica.txt");
			for(int index=0; index<GamePanel.classifica.size();index++) {
				//Mi passo tutti gli elementi della lista e li stampa nel file su ogni sua riga
				outputFile.println(GamePanel.classifica.get(index).name+"#"+GamePanel.classifica.get(index).score);
			}
			outputFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Questo è il metodo per disegnare gli elementi della schermata GameOver
	public void render(Graphics g) {
		//g2d serve per disegnare figura in 2D
		Graphics2D g2d = (Graphics2D) g;
		try {
			gameFont= Font.createFont(Font.TRUETYPE_FONT, new File("Retro Gaming.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Retro Gaming.ttf")));
		} catch (IOException | FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		g.setColor(Color.red);
		g.setFont(gameFont.deriveFont(30f));
		g.drawString("Game Over", (GamePanel.BASE_SCHERMO/2)-100,30);
		
		
		
		
		//Scrivere no. score e name
		if(GamePanel.classifica.size()!=0) {
			g.setColor(Color.red);
			g.setFont(gameFont.deriveFont(25f));
			g.drawString("no.", 50, 80);
			g.setFont(gameFont.deriveFont(25f));
			g.drawString("score", 200, 80);
			g.setFont(gameFont.deriveFont(25f));
			g.drawString("name", 350, 80);
		}
		
		//Per scrivere la classifica 
		g.setFont(gameFont.deriveFont(20f));
		g.setColor(Color.white);
		for(int i=0; i<GamePanel.classifica.size(); i++) {
			if(i!=7) {
				//devo disegnare separatamente i tre dati
				g.drawString(""+(i+1), 60, 130+(i*50));
				g.drawString(""+GamePanel.classifica.get(i).score, 200, 130+(i*50));
				g.drawString(GamePanel.classifica.get(i).name, 350, 130+(i*50));
			}else {
				break;
			}
		}
		
		//bottone new Game
		g.setColor(Color.white);
		//Lo disegno con il metodo in 2d
		g2d.draw(pulsanteNewGame);
		g.setColor(Color.white);
		//g.setFont(new Font("arial", Font.BOLD, 20));
		g.setFont(gameFont.deriveFont(20f));
		g.drawString("New Game", 120, 510);
		
		//bottone exit
		g.setColor(Color.white);
		g2d.draw(pulsanteExit);
		g.setFont(gameFont.deriveFont(20f));
		g.drawString("Exit", 400, 510);
		
		//Nuovo Highscore
		if(newHighscore) {
			g.setColor(Color.YELLOW);
			g.setFont(gameFont.deriveFont(10f));
			g.drawString("New Highscore",230, 105);
		}
		
	}
	
}

