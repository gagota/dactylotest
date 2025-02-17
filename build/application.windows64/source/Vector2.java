public class Vector2
{
    // Classic vectors
    public static Vector2 Zero() {return new Vector2(0,0);} 
    public static Vector2 One() {return new Vector2(1,1);} 
    public static Vector2 Right() {return new Vector2(1,0);}  // The down position is positiv in processing 
    public static Vector2 Down() {return new Vector2(0,1);} 

    // Attributes
    public float x;
    public float y;


    // Constructors
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    public Vector2(Vector2 otherVector)
    {
        this.x = otherVector.x;
        this.y = otherVector.y;
    }

    // Utilities
    public static Vector2 Clone(Vector2 vector)
    {
        return new Vector2(vector);
    }
    public Vector2 Clone()
    {
        return new Vector2(this);
    }
}
