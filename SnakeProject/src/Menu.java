import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
//Questa classe serve solo per disegnare il pannello Menù del gioco
public class Menu {
		public Rectangle pulsanteStart = new Rectangle(225, 150, 150, 50);
		public Rectangle pulsanteEsci= new Rectangle(225, 350, 150, 50);
		Font gameFont;
		
		public void render(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			try {
				gameFont= Font.createFont(Font.TRUETYPE_FONT, new File("Retro Gaming.ttf"));
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Retro Gaming.ttf")));
			} catch (IOException | FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			g.setFont(gameFont.deriveFont(30f));
			g.setColor(Color.white);
			g.drawString("SNAKE GAME", 200, 100);
			
			
			g.setFont(gameFont.deriveFont(25f));
			g.drawString("Start", pulsanteStart.x+32, pulsanteStart.y+35);
			
			g.drawString("Exit", pulsanteEsci.x+43, pulsanteEsci.y+35);
			
			g2d.draw(pulsanteStart);
			g2d.draw(pulsanteEsci);
			
			
		}

	}