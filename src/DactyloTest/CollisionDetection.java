public class CollisionDetection
{
    public static boolean PointAndRect(Vector2 pointPos, Vector2 rectPos, Vector2 rectDimensions)
    {
        boolean xVerif = (pointPos.x < rectPos.x + rectDimensions.x/2) & (pointPos.x > rectPos.x - rectDimensions.x/2);
        boolean yVerif = (pointPos.y < rectPos.y + rectDimensions.y/2) & (pointPos.y > rectPos.y - rectDimensions.y/2);

        return xVerif & yVerif;
    }
}
