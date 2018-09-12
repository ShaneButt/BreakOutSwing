import java.util.ArrayList;
import java.util.Random;

public class Level {
	private float width;
	private float height;
	private float brickHeight;
	private float brickWidth;
	public int numBricks = 0;
	
	private ArrayList<GameObj[][]> Levels = new ArrayList<GameObj[][]>(5);
	 
	
	public Level(float width, float height, float brickWidth, float brickHeight)
	{
		this.width = width;
		this.height = height;
		this.brickWidth = brickWidth;
		this.brickHeight = brickHeight;
		Init();
	}
	
	public void Init() // sets the levels up
	{
		for(int Level = 1; Level <= 5; Level++)
		{
			
			int bricksInRow = 5;
			int numRows = (Level * 2) - 1;
			GameObj[][] levelSetup = new GameObj[numRows][bricksInRow];	
			/* {  
			 * {BRICK_1, BRICK_2, ...} -- Row 0
			 * {BRICK_1, BRICK_2, ...} -- Row 1
			 * {BRICK_1, BRICK_2, ...} -- Row 2
			 * {BRICK_1, BRICK_2, ...} -- Row 3
			 * {BRICK_1, BRICK_2, ...} -- Row 4
			 * } 
			 */
			for(int row = 1; row <= numRows; row++)
			{
				for(int brick = 1; brick <= bricksInRow; brick++)
				{
					int var = 1;
					if(brick%2==1) var = var * -1;
					float x = (width/5) + (brick * (brickWidth + 2));
					float y = (height/2) - ( (row) * (brickHeight + 2));
					GameObj Brick = new GameObj(x, y, new Random().nextInt(3) + 1, brickWidth, brickHeight, Colour.ORANGE, true);
					levelSetup[row-1][brick-1] = Brick;
					numBricks++;
					Levels.add(levelSetup);
				}
			}
		}
	}
	
	public GameObj[][] getLevelPoints(int level) // Generates 
	{
		//printArrayList();
		return Levels.get(level-1);
	}
	
	public void printArrayList()
	{
		for(int i = 0; i < Levels.size(); i++)
		{
			for(int brick = 0; brick < Levels.get(i).length; brick++)
			{
				System.out.printf("Level: (%d), Brick: %d has position: %f, %f\n", i, brick, Levels.get(i)[0][brick].getTopX(), Levels.get(i)[0][brick].getTopY());
			}
		}
	}
	
	public ArrayList<GameObj[][]> getLevels()
	{
		return this.Levels;
	}
	
}