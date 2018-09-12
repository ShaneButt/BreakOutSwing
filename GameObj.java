import java.awt.Rectangle;

/**
 * An Object in the game, represented as a rectangle. Which holds details of
 * shape, plus possible direction of travel. Would be better to use inheritance.
 */
public class GameObj {
	// All the variables below are vital to the state of the object
	private int BrickResistance = 1;
	private boolean destroyed = false;
	protected boolean canSee = true; // Can see
	private float topX = 0.0f; // Top left corner X
	private float topY = 0.0f; // Top left corner Y
	public float width = 0.0f; // Width of object
	public float height = 0.0f; // Height of object
	private Colour colour = Colour.GRAY; // Colour of object
	private int dirX = 1; // Direction X (1 or -1)
	private int dirY = 1; // Direction Y (1 or -1)

	/**
	 * Constructor for a game object (x,y width, height, colour)
	 * 
	 * @param x
	 *            co-ordinate of the game object
	 * @param y
	 *            co-ordinate of the game object
	 * @param widthIs
	 *            width of the game object
	 * @param heightIs
	 *            height of the game object
	 * @param c
	 *            Colour of the game object
	 */

	public GameObj(float x, float y, int resistance, float widthIs, float heightIs, Colour c) {
		this.setTopX(x);
		this.setTopY(y);
		this.setResistance(resistance);
		this.width = widthIs;
		this.height = heightIs;
		this.colour = c;
	}
	
	public GameObj(float x, float y, int resistance, float widthIs, float heightIs, Colour c, boolean isVisible) {
		this.setTopX(x);
		this.setTopY(y);
		this.setResistance(resistance);
		this.width = widthIs;
		this.height = heightIs;
		this.colour = c;
		this.canSee = isVisible;
	}
	
	public void Hit()
	{
		setResistance(this.BrickResistance - 1);
		if (this.BrickResistance <= 0)
		{
			this.setDestroyed(true);
		}
		this.changeColour();
	}
	
	public void changeColour()
	{
		if (this.BrickResistance == 3)
		{
			this.colour = Colour.GREEN;
		}
		else if(this.BrickResistance == 2)
		{
			this.colour = Colour.ORANGE;
		}
		else { 
			this.colour = Colour.BLUE;
		}
	}
	
	public void setDestroyed(boolean state)
	{
		this.destroyed = state;
	}
	
	public boolean isDestroyed()
	{
		return this.destroyed;
	}
	
	public void setResistance(int resist)
	{
		this.BrickResistance = resist;
	}
	
	public int getResistance()
	{
		return this.BrickResistance;
	}

	/**
	 * Set the game object visibility
	 * 
	 * @param state
	 *            is visible true or false
	 */
	public void setVisibility(boolean state) {
		this.canSee = state;
	}

	/**
	 * Is the game object visible
	 * 
	 * @return visibility true/false
	 */
	public boolean isVisible() {
		return this.canSee;
	}

	public float getTopX() {
		return this.topX;
	}

	public void setTopX(float topX) {
		this.topX = topX;
	}
	
	public float getTopY() {
		return this.topY;
	}

	public void setTopY(float topY) {
		this.topY = topY;
	}
	
	public float getH() {
		return this.height;
	}

	public void setH(float height) {
		this.height = height;
	}
	
	public float getW() {
		return this.width;
	}

	public void setW(float width) {
		this.width = width;
	}

	/**
	 * The X co-ordinate of the top left hand corner of the Game Object
	 * 
	 * @return x co-ordinate of the game Object
	 */
	public float getX() {
		return this.getTopX();
	}

	/**
	 * The Y co-ordinate of the top left hand corner of the Game Object
	 * 
	 * @return y co-ordinate of the game Object
	 */

	public float getY() {
		return this.getTopY();
	}

	/**
	 * The width of the game object
	 * 
	 * @return The width of the game Object
	 */

	public float getWidth() {
		return this.width;
	}

	/**
	 * The height of the game object
	 * 
	 * @return The height of the game Object
	 */

	public float getHeight() {
		return this.height;
	}

	/**
	 * The colour of the game object
	 * 
	 * @return The colour of the game object
	 */
	public Colour getColour() {
		return this.colour;
	}

	/**
	 * Move object by X units The actual direction moved is flipped by
	 * changeDirectionX()
	 * 
	 * @param units
	 *            units to move
	 */

	public void moveX(float units) {
		this.setTopX(this.getTopX() + (units * this.dirX));
	}

	/**
	 * Move object by Y units The actual direction moved is flipped by
	 * changeDirectionY()
	 * 
	 * @param units
	 *            units to move
	 */
	public void moveY(float units) {
		this.setTopY(this.getTopY() + (units * this.dirY));
	}

	/**
	 * Change direction of future moves in the X direction
	 */
	public void changeDirectionX() {
		this.dirX = -this.dirX;
	}

	/**
	 * Change direction of future moves in the Y direction
	 */
	public void changeDirectionY() {
		this.dirY = -this.dirY;
	}

	/**
	 * Detect a collision between two GameObjects Would be good to know where the
	 * object is hit
	 * 
	 * @param obj
	 *            Game object to see if 'hit' by
	 * @return collision True/ False
	 */

	public boolean hitBottom(GameObj obj) {
		System.out.println("Bottom called");
		Rectangle rect = this.getRect();
		int left = (int) obj.getTopX() + (int) obj.getWidth();
		int top = (int) obj.getTopY();
		if(rect.contains(left, top - 1))
		{
			obj.changeDirectionY();
			return true;
		}
		return false;
	}

	public boolean hitTop(GameObj obj) {
		System.out.println("Top called");
		Rectangle rect = this.getRect();
		int left = (int) obj.getTopX();
		int bottom = (int) obj.getTopY() + (int) obj.getHeight();
		if(rect.contains(left, bottom + 1))
		{
			obj.changeDirectionY();
			return true;
		}
		return false;
	}

	public boolean hitLeft(GameObj obj) {
		System.out.println("Left called");
		Rectangle rect = this.getRect();
		int right = (int) obj.getTopX() + (int) obj.getWidth();
		int top = (int) obj.getTopY();
		if(rect.contains(right + 1, top))
		{
			obj.changeDirectionX();
			return true;
		}
		return false;
	}

	public boolean hitRight(GameObj obj) {
		System.out.println("Right called");
		Rectangle rect = this.getRect();
		int left = (int) obj.getTopX();
		int top = (int) obj.getTopY();
		if(rect.contains(left - 1, top))
		{
			obj.changeDirectionX();
			return true;
		}
		return false;
	}
	
	public Rectangle getRect()
	{
		return new Rectangle((int) this.topX, (int) this.topY, (int) this.width, (int) this.height);
	}
	
	public boolean hitBy(GameObj obj) {
		return !(
				(this.getTopX() > (obj.getTopX() + obj.width)) 
				|| 
				((this.getTopX() + this.width) < obj.getTopX())
				|| 
				(this.getTopY() > (obj.getTopY() + obj.height))
				|| 
				((this.getTopY() + this.height) < obj.getTopY())
			);

	}

}
