import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GamePanel extends JPanel implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//larghezza schermo di gioco
	static final int BASE_SCHERMO = 600;
	
	//altezza schermo di gioco
	static final int ALTEZZA_SCHERMO = 600;
	
	//unità di misura che mi permette di poter poi dividere lo schermo di gioco
	static final int UNITA_MISURA = 15;
	
	//Numero di caselle di gioco
	static final int UNITA_GIOCO = UNITA_MISURA*UNITA_MISURA;
	
	// Questi array x e y saranno le coordinate del nostro serpente
	final int x[] = new int[UNITA_GIOCO];
	final int y[] = new int[UNITA_GIOCO];
	
	//Lunghezza iniziale del corpo del serpente
	int corpoSerp;
	
	//Tengono conto delle mele mangiate e quindi del punteggio
	static int meleMangiate;
	static int score;
	
	//Coordinate mela generate randomicamente
	int melaX;
	int melaY;

	//Direzione iniziale del serpente a inizio del gioco determinata da un valore char
	char direzione;
	char prossimaDirezione = 'D';
	//Mi dice se il gioco si è avviato
	boolean running = false;
	
	//Servono per avviare il timer che darà la velocità e vita al gioco
	static Timer timer;
	
	//Variabile che mi serve per randomizzare la posizione della mela
	Random random;
	
	//E'un oggetto che implementa l'interfaccia MouseListener, e mi serve nella schermata Menu
	MouseMenu mouseMenu;
	
	//E' un oggetto che implementa l'interfaccia MouseListener, e mi serve nella schermata GameOver
	MouseGameOver mouseGameOver;
	
	//E' un oggetto figlio della classe KeyAdapter, che implementa un metodo per il controllo con la tastiera
	MyKeyAdapter keyAdapter;
	
	//Servono per cambiare da menu a gioco, è una enumerazione e i suoi attributi sono come delle costanti
	public static enum STATO{
		MENU,
		GIOCO,
	};
	
	//creo oggetto STATO per passare da menù a gioco
	static STATO stato;

	//Sono degli oggetti di tipo Menu e GameOver, che serviranno per disegnare le due schermate
	private Menu menu;
	private GameOver gameOver;
	
	//ArrayList che servono per la manipolazione della classifica
	public static ArrayList<NameScore> classifica;
	
	//Costruttore del Pannello, nessun parametro
	GamePanel(){
		//Istanzio l'oggetto random
		random = new Random();
		
		//Imposto la grandezza del pannello che riempirà il mio frame
		this.setPreferredSize(new Dimension(BASE_SCHERMO, ALTEZZA_SCHERMO));
		
		//Imposto come sfondo il colore nero
		this.setBackground(Color.black);
		
		//Mi dice se le componenti del pannello sono "focussabili", ovvero posso manipolarli?
		this.setFocusable(true);
		
		//Aggiungo la possibilità di usare il mouse come input
		this.addMouseListener(mouseMenu=new MouseMenu());
		this.addMouseListener(mouseGameOver=new MouseGameOver());
		
		//Attivo il mouse per la schermata menu, in quanto è la prima schermata del programma
		mouseMenu.enable=true;
		
		//Istanziando l'oggetto menu, praticamente lo sto disegnando nel pannello, vd. classe Menu.java
		menu = new Menu();
		
		//La varibile stato è impostata su menù, sarà utile questo valore perchè mi permetterà
		//di passare dal menù al gameplay
		stato=STATO.MENU;
		
	}

	//Metodo per iniziare la partita, è importante il parametro velocità, che lo passerò al timer
	public void startGame(int velocita) {
		//Questi print sono per la verifica su console che giri bene questo metodo
		System.out.println(classifica);
		System.out.println(velocita);
		
		//Utilizzo il metodo getClassifica, vedi più avanti, per creare l'arrayList della classifica
		//dato il file txt, se questo file non esiste me lo crea da solo
		getClassifica();
		
		//Così aggiungo e attivo l'oggetto per prendere in input la tastiera, utile per muovere il serpente
		this.addKeyListener(keyAdapter=  new  MyKeyAdapter());
		
		//Disattivo i controlli con il mouse, che durante il gioco non mi servono
		mouseMenu.enable=false;
		
		//Creo un nuovo serpente attraverso questo metodo, vedi più avanti
		newSnake();
		
		//Creo randomicamente la mela con questo metodo, vedi più avanti
		newApple();
		
		//Dico che il gioco è avviato
		running=true;
		
		//Con il timer avvio praticamente il gioco, poichè scandisce, con il parametro velocità, l'avanzare del gioco
		//il this invece sono gli eventi che accadono ad ogni "ticchettio" del timer e si riferisce all'ActionPerfomed
		//che è il metodo implementato, poichè GamePanel implementa l'interfaccia ActionListener
		timer = new Timer(velocita, this);
		
		//Aziono il timer così do vita a tutto quanto
		timer.start();
	}
	 
	//Metodo per colorare le componenti, è fondamentale
	public void paintComponent(Graphics g) {
		//mi permette di disegnare le componenti del pannello
		super.paintComponent(g);
		
		//richiamo il metodo di disegno, cos' mi disegna tutto quello che scrivo in draw(g)
		draw(g);
	}
	
	//metodo creazione arrayList per la classifica
	public void getClassifica() {
		//Creo oggetto file da classifica.txt, che sta dentro la cartella del progetto
		File f = new File("classifica.txt"); 
		//Se non esiste il file classifica lo creo
		if(!f.exists()) {
			try {
				//creo un nuovo file
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Metodo per salvare da File a Arraylist!!!
		try {
			//Uso un oggetto scanner sul file f, che ho caricato o creato
			Scanner scF = new Scanner(f);
			
			//Istanzio la mia variabile classifica come un ArrayList
			classifica= new ArrayList<NameScore>();
			
			//Finchè lo scanner può scannerizzare il file
			while(scF.hasNext()) {
				//Mi salvo come stringa ogni linea del file scannerizzato dallo scanner, serve anche per passarmi 
				//tutto il contenuto del file
				String record=scF.nextLine();
				
				//Mi scannerizzo ogni singola linea del file
				Scanner scR= new Scanner(record);
				
				//Imposto un carattere come delimitatore in ogni riga che scannerizzo
				scR.useDelimiter("#");
				
				//Creo variabili player e score in cui salvare i dati del file txt
				//Saranno poi i parametri per creare l'ArrayList
				String player;
				int score;
				
				//Da questa riga mi prendo solo la stringa prima del delimitatore
				player=scR.next();
				
				//Dallo scannere prendo solo il valore numerico separato dalla stringa dal delimitatore
				score=scR.nextInt();
				
				//Questi due dati saranno gli attributi di un determinato giocatore, che inserisco
				//man mano nella classifica
				classifica.add(new NameScore(player, score));
				
				//chiudo lo scanner da prassi
				scR.close();
			}
			
			//Chiudo pure lo scanner del file
			scF.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Metodo per ripristinare il serpente ad ogni inizio partita
	public void newSnake() {
		
		//Determino la posizione del serpente iniziale
		for(int i=0;i<=corpoSerp;i++) {
			x[i]=UNITA_MISURA*(corpoSerp-i);
			y[i]=UNITA_MISURA;
		}
		
		//Lunghezza del corpo del serpente iniziale
		corpoSerp=3;
		
		//inizializzo le mele mangiate ad inizio partita
		meleMangiate=0;
		
		//Inizializzo anche il punteggio iniziale
		score=0;
		
		//Inizializzo come direzione di default iniziale del serpente a destra
		direzione='D';
	}
	
	//Metodo per disegnare gli oggetti
	public void draw(Graphics g) {
		
		//Nel caso in cui sono nel game play mi disegna ciò
		if(stato==STATO.GIOCO) {
			//Se il gameplay è affettivamente avviato
			if(running) {
				
				//Disegno le bande limite di gioco
				//Linea a sinistra
				g.drawLine(UNITA_MISURA, UNITA_MISURA, UNITA_MISURA, ALTEZZA_SCHERMO-UNITA_MISURA);
				//Linea a destra
				g.drawLine(BASE_SCHERMO-UNITA_MISURA, UNITA_MISURA, BASE_SCHERMO-UNITA_MISURA, ALTEZZA_SCHERMO-UNITA_MISURA);
				//Linea superiore
				g.drawLine(UNITA_MISURA, UNITA_MISURA, BASE_SCHERMO-UNITA_MISURA, UNITA_MISURA);
				//Linea inferiore
				g.drawLine(UNITA_MISURA, BASE_SCHERMO-UNITA_MISURA, BASE_SCHERMO-UNITA_MISURA, ALTEZZA_SCHERMO-UNITA_MISURA);
					
				//Disegno la mela
				g.setColor(Color.red);
				g.fillOval(melaX, melaY, UNITA_MISURA, UNITA_MISURA);
				
				
				//Disegno il serpente
				for(int i=0; i<corpoSerp;i++) {
						g.setColor(Color.green);
						g.fillRect(x[i], y[i], UNITA_MISURA, UNITA_MISURA);
						
						//Mi permette di "nascondere" la testa se oltrepassa i bordi di gioco
						if(x[0]<UNITA_MISURA) {
							g.setColor(Color.BLACK);
							g.fillRect(x[0], y[0], UNITA_MISURA, UNITA_MISURA);
						}
						//Collisione della testa con il bordo dx
						if(x[0]>BASE_SCHERMO-2*UNITA_MISURA) {
							g.setColor(Color.BLACK);
							g.fillRect(x[0], y[0], UNITA_MISURA, UNITA_MISURA);
						}
						//Collisione della testa con il bordo superiore
						if(y[0]<UNITA_MISURA) {
							g.setColor(Color.BLACK);
							g.fillRect(x[0], y[0], UNITA_MISURA, UNITA_MISURA);
						}
						//Collisione della testa con il bordo inferiore
						if(y[0]>ALTEZZA_SCHERMO-2*UNITA_MISURA) {
							g.setColor(Color.BLACK);
							g.fillRect(x[0], y[0], UNITA_MISURA, UNITA_MISURA);
						}
				}
				
				//Visualizzo il punteggio
				//Mi sono importato un font esterno in questo modo
				Font gameFont = null;
				//Per non darmi errore con l'esportazione in file jar, sposto il file ttf dentro la cartella src
				//poi dove ho scritto new File("Retro Gaming.ttf") scrivo this.getClass().getResourceAsStream("/Retro Gaming.ttf") ???
				try {
					//creo il font dal file ttf
					gameFont= Font.createFont(Font.TRUETYPE_FONT, new File("Retro Gaming.ttf"));
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Retro Gaming.ttf")));
				} catch (IOException | FontFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Il metodo deriveFont serve per la dimensione del testo con quel font
				g.setFont(gameFont.deriveFont(15f));
				g.setColor(Color.white);
				g.drawString("Score: "+score, UNITA_MISURA, UNITA_MISURA-2);
				g.setFont(gameFont.deriveFont(10f));
				g.drawString("Press SPACE to pause ", 440, UNITA_MISURA-2);
			}
			//else riferito al running ovvero quando perdo, ovvero quando running è false
			else {
				//Stampo il GameOver e la classifica, che ho scritto nella classe GameOver
				//attraverso il suo metodo render
				gameOver.render(g);
			}
		//Se non sto in gioco, quindi sto inizialmente in modalità menu
		} else if(stato==STATO.MENU) {
			//uso il metodo render della classe menu per disegnarmi le varie componenti del menu
			//vedere classe Menu.java
			menu.render(g);
		}
	}
	
	//Metodo per randomizzare la posizione della mela
	public void newApple() {
		//uso l'oggetto random per randomizzare le coordinate della mela
		melaX = UNITA_MISURA + (random.nextInt((int)((BASE_SCHERMO-UNITA_MISURA*2)/UNITA_MISURA))*UNITA_MISURA);
		melaY = UNITA_MISURA + (random.nextInt((int)((ALTEZZA_SCHERMO-UNITA_MISURA*2)/UNITA_MISURA))*UNITA_MISURA);
		
		//Mi serve per non far apparire la mela nei punti in cui ci sta il corpo del serpente
		for(int i=0;i<corpoSerp;i++) {
			if(melaX==x[i] && melaY==y[i]) {
				newApple();
			}
		}
	}
	
	//Metodo per muovere il serpente
	public void move() {
		//Serve per far muovere tutto il serpente e non solo la testa
		//traslo il resto del corpo del serpente, lavoro con gli indici degli array
		for (int i=corpoSerp; i>0; i--) {
			x[i]=x[i-1];
			y[i]=y[i-1];
		}
		
		//devo ricordare che il mio pannello ha il centro (0,0) in alto a destra
		//in questo modo capisco quand'è che la testa del serpente va giù, sù, destra e sinistra
		direzione=prossimaDirezione;
		switch(direzione) {
		case 'W':
			
			y[0] = y[0] - UNITA_MISURA;
			break;
		case 'S':
			y[0] = y[0] + UNITA_MISURA;
			break;
		case 'D':
			x[0] = x[0] + UNITA_MISURA;
			break;
		case 'A':
			x[0] = x[0] - UNITA_MISURA;
			break;
		default:
			break;
		}
	}
	
	//Metodo per vedere se il serpenta ha mangiato la mela
	public void controlloMela() {
		if(x[0]==melaX && y[0]==melaY) {
			corpoSerp++;
			System.out.println(corpoSerp);
			meleMangiate++;
			score=meleMangiate*10;
			newApple();
		}
	}
	
	//Metodo per controllare le collissioni con il muro e le parti del corpo del serpente
	public void controlloCollisioni() {
		
		//Collisione della testa con il suo corpo
		for(int i=1; i<corpoSerp+1; i++) {
			if((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
				System.out.println("Collisione corpo");
			}
		}
		
		//Collisione della testa con il bordo sx
		if(x[0]<UNITA_MISURA) {
			running=false;
			System.out.println("Collisioine Muro");
		}
		//Collisione della testa con il bordo dx
		if(x[0]>BASE_SCHERMO-2*UNITA_MISURA) {
			running=false;
			System.out.println("Collisione Muro");
		}
		//Collisione della testa con il bordo superiore
		if(y[0]<UNITA_MISURA) {
			running=false;
			System.out.println("Collisione Muro");
		}
		//Collisione della testa con il bordo inferiore
		if(y[0]>ALTEZZA_SCHERMO-2*UNITA_MISURA) {
			running=false;
			System.out.println("Collisione Muro");
		}
		
		//Se succede tutto ciò fermo tutto
		if(!running) {
			timer.stop();
			//Nel costruttore di gameOver si aprirà il JOptionPane per l'inserimento del nome del giocatore
			gameOver = new GameOver();
			
			//Disabilito l'input mouse per il menu
			mouseMenu.enable=false;
			
			//Attivo quello del gameOver
			mouseGameOver.enable=true; //Attivo il mouse del GameOver
			
		}
	}
	
	//Metodo dell'interfaccia ActionListener, è importantissimo perchè dice praticamente cosa deve fare il programma
	//ad ogni intervallo di tempo del timer, in questo caso deve muovere il serpente, controllare costantemente
	//se il serpente mangia mela e se naturalmente va incontro a collissioni
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(running) {
				
				move();
				controlloMela();
				controlloCollisioni();
		}
		//Permette di continuare a disegnare le componenti costantemente
		repaint();
	}
	
	//classe interna per input della tastiera
	public class MyKeyAdapter extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(running) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					
					if((direzione!='D') && (direzione!='A')) {
						prossimaDirezione='A';
						System.out.println("SX");
					}
					break;
				case KeyEvent.VK_RIGHT:
					if((direzione!='A') && (direzione != 'D')) {
						prossimaDirezione='D';
						System.out.println("DX");
					}
					break;
				case KeyEvent.VK_UP:
					if((direzione!='S') && (direzione != 'W')) {
						prossimaDirezione='W';
						System.out.println("UP");
					}
					break;
				case KeyEvent.VK_DOWN:
					if((direzione!='W') && (direzione != 'S')) {
						prossimaDirezione='S';
						System.out.println("DO");
					}
					break;
				case KeyEvent.VK_SPACE:
					timer.stop();
					JOptionPane.showMessageDialog(null, "Press ENTER to continue\n");
					
					break;
				case KeyEvent.VK_ENTER:
					timer.start();
				}
			}
		}
	}
	
	//Classe interna che implementa l'interfaccia MousListener
	public class MouseMenu implements MouseListener{
		boolean enable;
		@Override
		public void mousePressed(MouseEvent e) {
			//Mi prendo le coordinate in cui clicco
			int mx = e.getX();
			int my = e.getY();
			//enable controlla che il MouseListener non funzione al di fuori del menu
			if(enable) {
			//pulsante Start
				if(mx >= GamePanel.BASE_SCHERMO/5 +120 && mx <= GamePanel.BASE_SCHERMO/5 +270) {
					if(my >= 150 && my<= 200) {
						//Premere pulsante Start
						stato=STATO.GIOCO;
						Object[] options = {"EASY", "NORMAL", "HARD", "EXTREME"};
						Object result;
						ImageIcon snakeIcon = new ImageIcon("snakeIcon.png");
						result = JOptionPane.showInputDialog(null, "Select Speed: ", "SNAKE SPEED",
								 JOptionPane.INFORMATION_MESSAGE, snakeIcon, options, options[1]);
						 
						 if (result == "EASY") startGame(100);
						 else if (result == "NORMAL") startGame(70);
						 else if (result == "HARD") startGame(50);
						 else if (result == "EXTREME") startGame(35);
					}
				}
			
				//pulsante esci
				if(mx >= GamePanel.BASE_SCHERMO/5 +120 && mx <= GamePanel.BASE_SCHERMO/5 +270) {
					if(my >= 350 && my<= 400) {
						System.exit(1);
					}
				}
			}
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
		@Override
		public void mouseClicked(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
	//Viene attivato quando sto nella schermata del GameOver
	public class MouseGameOver implements MouseListener{
		boolean enable;
		@Override
		//public Rectangle pulsanteNewGame= new Rectangle(100, 480, 150, 50);
		//public Rectangle pulsanteExit= new Rectangle(350, 480, 150, 50);
		public void mousePressed(MouseEvent e) {
			int mx = e.getX();
			int my = e.getY();
				if(enable) {
					if(mx >= 100 && mx <= 250) {
						if(my >= 480 && my<= 530) {
							stato=STATO.GIOCO;
							Object[] options = {"EASY", "NORMAL", "HARD", "EXTREME"};
							Object result;
							ImageIcon snakeIcon = new ImageIcon("snakeIcon.png");
							result = JOptionPane.showInputDialog(null, "Select Speed: ", "SNAKE SPEED",
									 JOptionPane.INFORMATION_MESSAGE, snakeIcon, options, options[1]);
							 
							 if (result == "EASY") startGame(100);
							 else if (result == "NORMAL") startGame(70);
							 else if (result == "HARD") startGame(50);
							 else if (result == "EXTREME") startGame(35);
						}
					}
					if(mx >= 350 && mx <= 500) {
						if(my >= 480 && my<= 530) {
							System.exit(1);
						}
					}
					
					
				}
			}
		@Override
		public void mouseClicked(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	
	
}
