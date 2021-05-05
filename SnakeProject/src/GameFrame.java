import javax.swing.JFrame;

public class GameFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Costruttore del frame
	GameFrame(){
		//Do il nome al mio frame richiamando il costruttore della classe JFrame
		
		super("Snake by Jemuel Sumagui");
		
		//Creo l'oggetto panel da inserire dentro il frame
		GamePanel gamePanel = new GamePanel();
		
		//Inserisco il pannello dentro il frame
		this.add(gamePanel);
	}

}
