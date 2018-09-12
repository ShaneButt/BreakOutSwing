import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
/**
 * Model of the game of breakout
 */

public class Model extends Observable
{
  // Boarder
  private static final int B              = 6;  // Border offset
  private static final int M              = 40; // Menu offset
  
  // Size of things
  private static final float BALL_SIZE    = 30; // Ball side
  private static final float BRICK_WIDTH  = 50; // Brick size
  private static final float BRICK_HEIGHT = 30;

  private static final int BAT_MOVE       = 5; // Distance to move bat
   
  // Scores
  private static final int HIT_BRICK      = 200;  // Score
  private static final int HIT_BOTTOM     = -50;// Score

  private GameObj ball;          // The ball
  private List<GameObj> bricks = new ArrayList<GameObj>();  // The bricks
  private GameObj bat;           // The bat
  
  @SuppressWarnings("unused")
  private boolean runGame = true; // Game running
  private boolean fast = false;   // Sleep in run loop
  
  private int score = 0;

  private final float W;         // Width of area
  private final float H;         // Height of area
  
  private int LEVEL	= 2;
  private Level LEVEL_DESIGN;

  public Model( int width, int height )
  {
    this.W = width; this.H = height;
    LEVEL_DESIGN = new Level(W, H, BRICK_WIDTH, BRICK_HEIGHT);
  }

  /**
   * Create in the model the objects that form the game
   */

  public void createGameObjects()
  {
    synchronized( Model.class )
    {
      ball   = new GameObj(3 * W/4, H/2, 100, BALL_SIZE, BALL_SIZE, Colour.RED);
      bat    = new GameObj(W/2, H - BRICK_HEIGHT*1.5f, 100, BRICK_WIDTH*3, BRICK_HEIGHT/4, Colour.GRAY);
      drawLevel();
    }
  }
  
  public void drawLevel()
  {
	  synchronized( Model.class ) 
	  {
		  int numRows = (LEVEL * 2) - 1;
		  int bricksInRow = 5;
		  int totalBricks = numRows * bricksInRow;
		  System.out.printf("Total bricks: %d\n", totalBricks);
		  for(int row = 0; row < numRows; row++)
		  {
			  System.out.printf("Row: %d\n",  row);
			  GameObj[][] levelCoords = LEVEL_DESIGN.getLevelPoints(( (row+1) * 5) - 1);
			  for(int Brick = 0; Brick < bricksInRow; Brick++)
		      {
				 	System.out.printf("Brick: %d/%d\t@(%f,%f)\n",
				 			(row*bricksInRow) + (Brick+1), 
				 			totalBricks, 
				 			levelCoords[row][Brick].getTopX(), 
				 			levelCoords[row][Brick].getTopY()
				 		);
		    	  GameObj brick = levelCoords[row][Brick];
		    	  this.bricks.add(brick);
		      }  
		  }
	      
	  }
	  
  }
  
  private ActivePart active  = null;

  /**
   * Start the continuous updates to the game
   */
  public void startGame()
  {
    synchronized ( Model.class )
    {
      stopGame();
      active = new ActivePart();
      Thread t = new Thread( active::runAsSeparateThread );
      t.setDaemon(true);   // So may die when program exits
      t.start();
    }
  }

  /**
   * Stop the continuous updates to the game
   * Will freeze the game, and let the thread die.
   */
  public void stopGame()
  {  
    synchronized ( Model.class )
    {
      if ( active != null ) { active.stop(); active = null; }
    }
  }

  public GameObj getBat()             { return bat; }

  public GameObj getBall()            { return ball; }

  public List<GameObj> getBricks()    { return bricks; }

  /**
   * Add to score n units
   * @param n units to add to score
   */
  protected void addToScore(int n)    { score += n; }
  
  public int getScore()               { return score; }

  /**
   * Set speed of ball to be fast (true/ false)
   * @param fast Set to true if require fast moving ball
   */
  public void setFast(boolean fast)   
  { 
    this.fast = fast; 
  }

  /**
   * Move the bat. (-1) is left or (+1) is right
   * @param direction - The direction to move
   */
  public void moveBat( int direction )
  {
    // *[2]******************************************************[2]*
    // * Fill in code to prevent the bat being moved off the screen *
    // **************************************************************
    float dist = direction * BAT_MOVE;    // Actual distance to move
    float bat_size = bat.getWidth(); // width
    float bat_pos = bat.getX(); // left-hand side x pos
    float bat_right_pos = bat_pos+bat_size; // right-hand side x pos
    
    if( (direction == -1 && bat_pos - dist <= 0+ 2 * B) || (direction == 1 && bat_right_pos + dist >= W-B)){return;}
    else
    {
        //Debug.trace( "Model: Move bat = %6.2f", dist );
        bat.moveX(dist);
    }
  }
  
  /**
   * This method is run in a separate thread
   * Consequence: Potential concurrent access to shared variables in the class
   */
  class ActivePart
  {
    private boolean runGame = true;

    public void stop()
    {
      runGame = false;
    }

    @SuppressWarnings("unused")
	public void runAsSeparateThread()
    {
      final float S = 3; // Units to move (Speed)
      try
      {
        synchronized ( Model.class ) // Make thread safe
        {
          GameObj       ball   = getBall();     // Ball in game
          GameObj       bat    = getBat();      // Bat
          List<GameObj> bricks = getBricks();   // Bricks
        }
  
        while (runGame)
        {
          synchronized ( Model.class ) // Make thread safe
          {
            float x = ball.getX();  // Current x,y position
            float y = ball.getY();
            // Deal with possible edge of board hit
            if (x >= W - B - BALL_SIZE)  ball.changeDirectionX();
            if (x <= 0 + B            )  ball.changeDirectionX();
            if (y >= H - B - BALL_SIZE)  // Bottom
            { 
              ball.changeDirectionY(); addToScore( HIT_BOTTOM ); 
            }
            if (y <= 0 + M            )  ball.changeDirectionY();

            // As only a hit on the bat/ball is detected it is 
            //  assumed to be on the top or bottom of the object.
            // A hit on the left or right of the object
            //  has an interesting affect
    
            if (bricks.size() < 1)
            {
            	++LEVEL;
            	System.out.printf("Level: %d\n", LEVEL);
            	drawLevel();
            }
            
            boolean hit = false;
            GameObj brickHit;
            // *[3]******************************************************[3]*
            // * Fill in code to check if a visible brick has been hit      *
            // *      The ball has no effect on an invisible brick          *
            // **************************************************************
            for(int i = 0; i < bricks.size(); i++)
            {
                GameObj brick = bricks.get(i);
                brick.changeColour();
                Rectangle brickRect = brick.getRect();
                Rectangle ballRect = ball.getRect();
                if ( (brickRect.intersects(ballRect)) && brick.isVisible())
                {
                	//playSound(""); TO-DO: Add sound when hit
                	brick.Hit();
                	if(brick.isDestroyed()) {
                		addToScore(HIT_BRICK);
                		brick.setVisibility(false);
                	}
                	brick.hitBottom(ball);
                	brick.hitTop(ball);
                	brick.hitRight(ball);
                	brick.hitLeft(ball);
                }
            }
            
            
           if ( ball.hitBy(bat) )
              ball.changeDirectionY();
          }
          modelChanged();      // Model changed refresh screen
          Thread.sleep( fast ? 1 : 10 );
          ball.moveX(S);  ball.moveY(S);
        }
      } catch (Exception e) 
      { 
        Debug.error("Model.runAsSeparateThread - Error\n%s", 
                    e.getMessage() );
      }
    }
  }
  
  /**
   * Model has changed so notify observers so that they
   *  can redraw the current state of the game
   */
  public void modelChanged()
  {
    setChanged(); notifyObservers();
  }
  
  public void playSound(String fileName)
  {
	  try 
	  {
		  AudioInputStream audioIn = AudioSystem.getAudioInputStream(this.getClass().getResource(fileName));
		  Clip clip = AudioSystem.getClip();
		  clip.open(audioIn);
		  clip.start();
	  } 
	  catch (Exception e) {
		e.printStackTrace();
	}
	  
  }

}
