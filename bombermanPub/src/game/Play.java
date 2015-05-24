package game;

//import org.lwjgl.input.Mouse;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

import role.Role;

/**
 * 
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Play extends BasicGameState {
	
   static boolean explosion = false;
   static boolean firstTime = true;
   static final int MAX_ITEM = 5;
   
	private Image indestructible;
	private Image destructible;
	private Image background;
	private Image imagetest;
	private Image bonusBombAmt;
	private Image bonusBombRadius;
	private Image imageBombe;
	private Image[] hero1;
	private Image[] hero2;
	private boolean[][] movesMatrix = new boolean[Map.WIDTH][Map.HEIGHT];
	private boolean[][] boom = new boolean[Map.WIDTH][Map.HEIGHT];
	private int[][] tmpBoom = new int[Map.WIDTH][Map.HEIGHT];
	private int[][] Bloc = new int[Map.WIDTH][Map.HEIGHT];
	private int[][] bonusMatrix = new int[Map.WIDTH][Map.HEIGHT];

	
	private Map map = null;
	private ArrayList<Bomb> bombList = new ArrayList<Bomb>();
	private ArrayList<Fire> explosionList = new ArrayList<Fire>();
	private Player[] playerList;
	private Player p;
	private Player p1;
	private Player p2;
	private Role networkAccess;
	
	private Music mainMusic;
	private Music gameOverMusic;
	private Music gameWinMusic;
	
	public Play(int state, Role networkAccess){
	   this.networkAccess = networkAccess; 
	   
	}
	
	/**
	 * Méthode de Slick: Méthode de Slick: Initialisation
	 * @GameContainer gc, la fenêtre de Slick
	 * @StateBasedGame sbg, l'état du jeu
	 * @throws SlickException
	 */
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) 
			throws SlickException {
		
		map = new Map();
		map.initMap();
		
		indestructible = new Image("res/indestructible.png");
		destructible = new Image("res/destructible2.png");
		background = new Image("res/background_tile.png");
		imagetest = new Image("res/bonus_bomb_amt.png");
		hero1 = new Image[4];
		hero2 = new Image[4];
	   bonusBombAmt =  new Image("res/bonus_bomb_amt.png");
	   bonusBombRadius = new Image("res/bonus_radius.png");
		
		hero1[0] = new Image("res/hero_down.png");
		hero1[1] = new Image("res/hero_up.png");
		hero1[2] = new Image("res/hero_left.png");
		hero1[3] = new Image("res/hero_right.png");
		
		hero2[0] = new Image("res/hero2_down.png");
		hero2[1] = new Image("res/hero2_up.png");
		hero2[2] = new Image("res/hero2_left.png");
		hero2[3] = new Image("res/hero2_right.png");
		
		imageBombe = new Image("res/bomb.png");

		mainMusic = new Music("res/mainMusic.ogg");
		gameOverMusic = new Music("res/gameOver.ogg");
		gameWinMusic = new Music("res/VictoryMusic.ogg");
		mainMusic.loop();
		
		p1 = new Player(hero1, 1, 1, 0);
		p2 = new Player(hero2, 25, 17, 1);
		if(networkAccess.amITheServer())
			p = p1;
		else
			p = p2;
		
		playerList = new Player[2];
		playerList[0] = p1;
		playerList[1] = p2;
	}

	/*** 
	 * Cette méthode permet de dessiner la carte du jeu en fonction
	 * d'une carte stockée dans une base de données. Elle fait appel à 
	 * la variable map de type Map.
	 * @param Map map, la carte à dessiner
	 * @param Graphics g, nécessaire pour dessiner dans la méthode render
    *
	 */
	private void drawMap(Map map, Graphics g)
	{
		for(int i = 0; i < Map.WIDTH; i++)
		{
			for(int j = 0; j < Map.HEIGHT; j++)
			{	
				boom[i][j] = true;
				bonusMatrix[i][j] = 0;
				switch(map.getConstructibles()[i][j])
				{
				case 0:
					g.drawImage(background, i * Map.ELEMENT_SIZE ,
											j * Map.ELEMENT_SIZE);
					movesMatrix[i][j] = true;
					Bloc[i][j] = 0;
					tmpBoom[i][j] = 0;
					break;
				case 1:
					g.drawImage(indestructible, i * Map.ELEMENT_SIZE ,
												j * Map.ELEMENT_SIZE);
					movesMatrix[i][j] = false;
					Bloc[i][j] = 1;
					tmpBoom[i][j] = 1;
					break;
				case 2: 
					g.drawImage(destructible, i * Map.ELEMENT_SIZE ,
											  j * Map.ELEMENT_SIZE);
					movesMatrix[i][j] = false;
					Bloc[i][j] = 2;
					tmpBoom[i][j] = 2;
					break;
				}
			}
		}
	    if(networkAccess.amITheServer()) {
	         createBonus();
	    } else {
	       for (int i = 0; i < bonusMatrix.length; i++) {
	          for (int j = 0; j < bonusMatrix[0].length; j++) {
	             bonusMatrix[i][j] = 10;
	          }
	       }
	    }
	}
	
	/**
	 * Cette méthode crée les différents bonus de manière aléatoire
	 */
   private void createBonus()
   {
      Random r = new Random();
      int coordX, coordY, bonusType;
      int bonusPlaced = 0;
      coordX = 0 + r.nextInt(Map.WIDTH);
      coordY = 0 + r.nextInt(Map.HEIGHT);
      bonusType = 1 + r.nextInt(2);
      
      int bonusAmt = 25; // 25 max, parfois moins car 2 fois le même tirage
      
      while(bonusPlaced < bonusAmt)
      {
         if(Bloc[coordX][coordY] == 2) //A placer sous des blocs 
                                 //destructibles
         {
            bonusMatrix[coordX][coordY] = bonusType;
            bonusPlaced++;
         }
         coordX = 0 + r.nextInt(Map.WIDTH);
         coordY = 0 + r.nextInt(Map.HEIGHT);
         bonusType = 1 + r.nextInt(2);
      }
   }
	
   /**
    * Cette méthode met à jour la carte du jeu
    * @param Map map, la carte à dessiner
    * @param Graphics g, nécessaire pour dessiner dans la méthode render
    */
	private void MiseaJour(Map map, Graphics g)
	{
		for(int i = 0; i < Map.WIDTH; i++)
		{
			for(int j = 0; j < Map.HEIGHT; j++)
			{	
				
				switch(Bloc[i][j])
				{
				case 0:
					g.drawImage(background, i * Map.ELEMENT_SIZE ,
											j * Map.ELEMENT_SIZE);
		         if(bonusMatrix[i][j] == 1) //bonus quantite
		            g.drawImage(bonusBombAmt, i * Map.ELEMENT_SIZE, 
	                                      j * Map.ELEMENT_SIZE);
	             if(bonusMatrix[i][j] == 2) //bonus rayon
	               g.drawImage(bonusBombRadius, i * Map.ELEMENT_SIZE, 
	                                         j * Map.ELEMENT_SIZE);
					break;
				case 1:
					g.drawImage(indestructible, i * Map.ELEMENT_SIZE ,
												j * Map.ELEMENT_SIZE);
					break;
				case 2: 
					g.drawImage(destructible, i * Map.ELEMENT_SIZE ,
											  j * Map.ELEMENT_SIZE);
					break;
				default:
					g.drawImage(imagetest, i * Map.ELEMENT_SIZE ,
							  j * Map.ELEMENT_SIZE);
				}
			}
		}
	}
	
	/***
	 * Permet d'afficher l'inventaire des joueurs
	 * @param Graphics g, nécessaire pour dessiner dans la méthode render
	 * @throws SlickException
	 */
	private void drawInventory(Graphics g) throws SlickException
	{
		g.drawImage(new Image("res/score.png"), 0, 570);
		
		if(p1.getBombAmt() == 0)
			g.setColor(Color.red);
		else if(p1.getBombAmt() == MAX_ITEM)
			g.setColor(Color.green);
		else
			g.setColor(Color.white);
		
		g.drawString(" : " + p1.getBombAmt(), 45, 615);
		
		if(p1.getFirePower() == MAX_ITEM)
			g.setColor(Color.green);
		else
			g.setColor(Color.white);
		g.drawString(" : " + p1.getFirePower(), 200, 615);
		
		if(p2.getBombAmt() == 0)
			g.setColor(Color.red);
		else if(p2.getBombAmt() == MAX_ITEM)
			g.setColor(Color.green);
		else
			g.setColor(Color.white);
		g.drawString(" : " + p2.getBombAmt(), 450, 615);
		
		if(p2.getFirePower() == MAX_ITEM)
			g.setColor(Color.green);
		else
			g.setColor(Color.white);	
		g.drawString(" : " + p2.getFirePower(), 590, 615);
	}
	
	/**
	 * Cette méthode determine la mort d'un joueur
	 * @param StateBasedGame sbg, l'état du jeu
	 */
	private void testDead(StateBasedGame sbg)
	{
		if(!playerList[p.getNumber()].alive && 
				playerList[(p.getNumber() +1 ) % 2].alive)
		{
			networkAccess.send(new PlayerNetworkData(p));
			mainMusic.stop();
			gameOverMusic.loop();
			sbg.enterState(2);

		}
		if(playerList[p.getNumber()].alive && 
				!playerList[(p.getNumber() + 1) % 2].alive)
		{
			mainMusic.stop();
			gameWinMusic.loop();
			sbg.enterState(0);
		}
	}

	/**
	 * Méthode de Slick: Mis à jour de la fenêtre
	 * @param Graphics g, nécessaire pour dessiner dans la méthode render
	 * @GameContainer gc, la fenêtre de Slick
	 * @throws SlickException
	 */
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {

		if(firstTime){
			drawMap(map, g);
			drawInventory(g);
			firstTime = false;
		}else{
			if(explosion){
				for(int i = 0; i < Map.WIDTH; i++)
				{
					for(int j = 0; j < Map.HEIGHT; j++)
					{	
						Bloc[i][j] = tmpBoom[i][j];
					}
				}
				explosion = false;
			}
				MiseaJour(map, g);
				drawInventory(g);
				
		}

		
		for(Player p : playerList)
		{
		   if (!p.alive) {
		      continue;
		   }
			switch(p.getOrientation())
			{
			case 0: 	
				g.drawImage(p.hero_down, p.X() * Map.ELEMENT_SIZE, 
						                (p.Y() * Map.ELEMENT_SIZE) -15 );
				break;
			case 1: 
				g.drawImage(p.hero_up, p.X() * Map.ELEMENT_SIZE, 
						              (p.Y() * Map.ELEMENT_SIZE) -15 );
				break;
			case 2:
				g.drawImage(p.hero_left, p.X() * Map.ELEMENT_SIZE, 
						                (p.Y() * Map.ELEMENT_SIZE) -15 );
				break;
			case 3: 
				g.drawImage(p.hero_right, p.X() * Map.ELEMENT_SIZE, 
						                 (p.Y() * Map.ELEMENT_SIZE) -15 );
				break;
				
			}
		}
		
				
		if(!(bombList.isEmpty()))
		{
			for(Bomb b : bombList)
			{
				if(!b.getExploded())
				{
					g.drawImage(b.bomb, b.X() * Map.ELEMENT_SIZE, 
							            b.Y() * Map.ELEMENT_SIZE);
					movesMatrix[b.X()][b.Y()] = false;
				}

			}
		}
		if(!(explosionList.isEmpty()))
		{
			boolean top, down, right, left;
			for(Fire f : explosionList)
			{
				top = down = left = right = true;
				movesMatrix[f.X()][f.Y()] = true;
				
				if(!f.getBoom())
				{
					for(int i = 1; i <= f.getRadius() ; i++)
					{

						if(top && (movesMatrix[f.X()][f.Y()-i] || 
								Bloc[f.X()][f.Y()-i] == 2))
						{
							g.drawImage(f.fire, (f.X()) * Map.ELEMENT_SIZE, 
									            (f.Y()-i) * Map.ELEMENT_SIZE);
							movesMatrix[f.X()][f.Y()-i] = true;
							if(Bloc[f.X()][f.Y()-i] == 2)
							{
								top = false;
								tmpBoom[f.X()][f.Y()-i] = 0;
							}
							if(p.X() == f.X() && p.Y() == (f.Y()-i))
								playerList[p.getNumber()].alive = false;
						}
						else
						{
							top = false;
						}
						

						if(right && (movesMatrix[f.X()+i][f.Y()] || 
								Bloc[f.X()+i][f.Y()] == 2))
						{
							g.drawImage(f.fire, (f.X()+i) * Map.ELEMENT_SIZE, 
									            (f.Y()) * Map.ELEMENT_SIZE);
							movesMatrix[f.X()+i][f.Y()] = true;
							if(Bloc[f.X()+i][f.Y()] == 2)
							{
								right = false;
								tmpBoom[f.X()+i][f.Y()] = 0;
							}
							if(p.X() == f.X()+i && p.Y() == (f.Y()))
								playerList[p.getNumber()].alive = false;
						}
						else
						{
							right = false;
						}

						if(down && (movesMatrix[f.X()][f.Y()+i] || 
								Bloc[f.X()][f.Y()+i] == 2))
						{
							g.drawImage(f.fire, (f.X()) * Map.ELEMENT_SIZE, 
									            (f.Y()+i) * Map.ELEMENT_SIZE);
							movesMatrix[f.X()][f.Y()+i] = true;
							if(Bloc[f.X()][f.Y()+i] == 2)
							{
								down = false;
								tmpBoom[f.X()][f.Y()+i] = 0;
							}
							if(p.X() == f.X() && p.Y() == (f.Y()+i))
								playerList[p.getNumber()].alive = false;
						}
						else
						{
							down = false;
						}

						if(left && (movesMatrix[f.X()-i][f.Y()] || 
								Bloc[f.X()-i][f.Y()] == 2))
						{
							g.drawImage(f.fire, (f.X()-i) * Map.ELEMENT_SIZE, 
									            (f.Y()) * Map.ELEMENT_SIZE);
							movesMatrix[f.X()-i][f.Y()] = true;
							if(Bloc[f.X()-i][f.Y()] == 2)
							{
								left = false;
								tmpBoom[f.X()-i][f.Y()] = 0;
							}
							if(p.X() == f.X()-i && p.Y() == (f.Y()))
								playerList[p.getNumber()].alive = false;
						}
						else
						{
							left = false;
						}
						
					
						g.drawImage(f.fire, (f.X()) * Map.ELEMENT_SIZE, 
								            (f.Y()) * Map.ELEMENT_SIZE);
						movesMatrix[f.X()][f.Y()] = true;
						if(p.X() == f.X() && p.Y() == (f.Y()))
							playerList[p.getNumber()].alive = false;
					}
				}
			}			
		}
	}

	/**
	 * Méthode de Slick: Mis à jour des données
	 * @GameContainer gc, la fenêtre de Slick
	 * @throws SlickException
	 */
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		
		Input input = gc.getInput();
		Boolean hasChanged = false;
		
		if(input.isKeyPressed(Input.KEY_DOWN))
			if(p.Y() + 1 <= Map.HEIGHT)
				if(movesMatrix[p.X()][p.Y() + 1] == true) {
					p.setY(p.Y() +1);
					p.setOrientation(0);
					hasChanged = true;
	             if(bonusMatrix[p.X()][p.Y()] != 0)
	             {
	                if(bonusMatrix[p.X()][p.Y()] == 1 && 
	                		p.getBombAmt() < MAX_ITEM)
	                   p.setBombAmt(p.getBombAmt() +1);
	                if(bonusMatrix[p.X()][p.Y()] == 2 && 
	                		p.getFirePower() < MAX_ITEM)
	                   p.setFirePower(p.getFirePower() +1);
	                bonusMatrix[p.X()][p.Y()] = 0;
	             }
				}

		if(input.isKeyPressed(Input.KEY_UP))
			if(p.Y() - 1 >= 0)
				if(movesMatrix[p.X()][p.Y() - 1] == true) {
					p.setY(p.Y() - 1);
					p.setOrientation(1);
					hasChanged = true;
	              if(bonusMatrix[p.X()][p.Y()] != 0)
	                {
	                   if(bonusMatrix[p.X()][p.Y()] == 1 && 
	                		   p.getBombAmt() < MAX_ITEM)
	                      p.setBombAmt(p.getBombAmt() +1);
	                   if(bonusMatrix[p.X()][p.Y()] == 2 && 
	                		   p.getFirePower() < MAX_ITEM)
	                      p.setFirePower(p.getFirePower() +1);
	                   bonusMatrix[p.X()][p.Y()] = 0;
	                }
				}
		
		if(input.isKeyPressed(Input.KEY_LEFT))
			if(p.X() - 1 >= 0)
				if(movesMatrix[p.X() - 1][p.Y()] == true) {
					p.setX(p.X() - 1);
					p.setOrientation(2);
					hasChanged = true;
               if(bonusMatrix[p.X()][p.Y()] != 0)
               {
                  if(bonusMatrix[p.X()][p.Y()] == 1 && 
                		  p.getBombAmt() < MAX_ITEM)
                     p.setBombAmt(p.getBombAmt() +1);
                  if(bonusMatrix[p.X()][p.Y()] == 2 && 
                		  p.getFirePower() < MAX_ITEM)
                     p.setFirePower(p.getFirePower() +1);
                  bonusMatrix[p.X()][p.Y()] = 0;
               }
				}
		
		if(input.isKeyPressed(Input.KEY_RIGHT))
			if(p.X() + 1 <= Map.WIDTH)
				if(movesMatrix[p.X() + 1][p.Y()] == true) {
					p.setX(p.X() + 1);
					p.setOrientation(3);
					hasChanged = true;
               if(bonusMatrix[p.X()][p.Y()] != 0)
               {
                  if(bonusMatrix[p.X()][p.Y()] == 1 && 
                		  p.getBombAmt() < MAX_ITEM)
                     p.setBombAmt(p.getBombAmt() +1);
                  if(bonusMatrix[p.X()][p.Y()] == 2 && 
                		  p.getFirePower() < MAX_ITEM)
                     p.setFirePower(p.getFirePower() +1);
                  bonusMatrix[p.X()][p.Y()] = 0;
               }
			}
					
		if(input.isKeyPressed(Input.KEY_SPACE))
		{
			if(p.getBombAmt() > 0)
			{
				bombList.add(new Bomb(new Image("res/bomb.png"), 5, 
                  p.getFirePower(), p, p.X(), p.Y()));
				networkAccess.send(new BombNetworkData(p.getFirePower(), 
						p.X(), p.Y()));
				p.setBombAmt(p.getBombAmt() - 1);
				hasChanged = true;
			}	
		}
		if(!bombList.isEmpty())
		{
			if(bombList.get(0).getExploded() == true) {
				explosionList.add(new Fire(new Image("res/explosion.png"),
						3, bombList.get(0).getRadius(), bombList.get(0).X(), 
						bombList.get(0).Y()));
				bombList.remove(0);
				hasChanged = true;
			}
		}
		if(!explosionList.isEmpty())
		{
			if(explosionList.get(0).getBoom() == true) {
				explosionList.remove(0);
				explosion = true;
				hasChanged = true;
			}
		}

		//On test si un des 2 joueurs est mort
		testDead(sbg);
		
		if (hasChanged) {
		    networkAccess.send(new PlayerNetworkData(p));
		    networkAccess.send(new BlocNetworkData(Bloc));
		    networkAccess.send(new BonusNetworkData(bonusMatrix));
		}
		
		
	}

	/**
    * @return l'état du jeu
    */
	@Override
	public int getID() {
		return 1;
	}
	
	/**
    * @return the p1
    */
	public Player getP1() {
	   return p1;
	}
	
	/**
    * @return the p2
    */
	public Player getP2() {
	   return p2;
	}

	/**
	 * Méthode appelé par le récepteur de données du réseau qui met à jour les bombes
	 * @param bomb
	 */
   public void networkUpdate(BombNetworkData bomb) {
     
      Player other = null;
      if (networkAccess.amITheServer()) {
         other = p2;
      } else {
         other = p1;
      }
      bombList.add(new Bomb(imageBombe, 5, bomb.getRadius(), other, 
    		  bomb.getX(), bomb.getY())); 
   }
   
   /**
    * Méthode appelé par le récepteur de données du réseau qui met à la carte
    * @param blocNetworkData
    */
   public void networkUpdate(BlocNetworkData blocNetworkData) {
      int[][] b = blocNetworkData.getBloc();
      for (int i = 0; i < b.length; i++) {
         for (int y = 0; y < b[0].length; y++) {
            if (this.Bloc[i][y] > b[i][y]) {
               this.Bloc[i][y] = b[i][y];
            }
         }
      }
   }
   
   /**
    * Méthode appelé par le récepteur de données du réseau qui met à jour les bonus
    * @param bonusNetworkData
    */
   public void networkUpdate(BonusNetworkData bonusNetworkData) {
      int[][] b = bonusNetworkData.getBonus();
      for (int i = 0; i < b.length; i++) {
         for (int y = 0; y < b[0].length; y++) {
            if (this.bonusMatrix[i][y] > b[i][y]) {
               this.bonusMatrix[i][y] = b[i][y];
            }
         }
      }
   }

   /**
    * @return the bloc
    */
   public int[][] getBloc() {
      return Bloc;
   }

   /**
    * @return the bonusMatrix
    */
   public int[][] getBonusMatrix() {
      return bonusMatrix;
   }
}