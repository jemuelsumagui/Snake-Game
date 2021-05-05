import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class GameSnake {
	 
	//Metodo principale per avviare il programma
	public static void main(String[] args) {
		//Creo il frame dove aggiungere tutte le componenti
		GameFrame frame = new GameFrame();
		
		//Sarebbe la barra della finestra in cui posso chiudere o rimpicciolirla
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//La grandezza del frame non è variabile
		frame.setResizable(false);
		
		//Permette di regolare la grandezza del frame in base alle componenti interne
		frame.pack();
		
		//La rendo visibile
		frame.setVisible(true);
		
		//La posizione in cui si apre il frame è indifferente, la posiziono al centro
		frame.setLocationRelativeTo(null);
		
		//Creo un icona del frame
		ImageIcon snakeImage = new ImageIcon("snakeIcon.png");
		
		//Inserisco l'icona come icone del frame in alto a sinistra
		frame.setIconImage(snakeImage.getImage());
	}
}
