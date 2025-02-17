public abstract class RectButton extends Button
{
    protected Vector2 _buttonHitboxDimensions;
    protected Vector2 _anchor; // (0,0) represent the center, (1,1) represent the bottom right, (-1,-1) represents the top left etc...
    protected Vector2 _buttonCenter;

    // CONSTRUCTORS
    public RectButton()
    {
        super(Vector2.Zero());

        this._buttonHitboxDimensions = Vector2.One();
        this._anchor = new Vector2(0, 0); // We put the anchor in the center by default

        CalculateButtonsCenter();
    }
    public RectButton(Vector2 buttonPos, Vector2 buttonHitboxDimensions)
    {
        super(buttonPos);

        this._buttonHitboxDimensions = buttonHitboxDimensions;
        this._anchor = new Vector2(0, 0); // We put the anchor in the center by default

        CalculateButtonsCenter();
    }
    public RectButton(Vector2 buttonPos, Vector2 buttonHitboxDimensions, Vector2 anchor)
    {
        super(buttonPos);

        this._buttonHitboxDimensions = buttonHitboxDimensions;
        this._anchor = anchor;

        CalculateButtonsCenter();
    }

    // GETTERS AND SETTERS
    public Vector2 GetDimensions()
    {
        return _buttonHitboxDimensions;
    }

    // OTHER
    protected boolean CheckPointerCollision(Vector2 mousePos)
    {
        return (CollisionDetection.PointAndRect(mousePos, _buttonCenter, _buttonHitboxDimensions));
    }
    protected void CalculateButtonsCenter()
    {
        // The button's center depends on where its anchor is
        _buttonCenter = new Vector2(_buttonPos.x - _anchor.x * _buttonHitboxDimensions.x, _buttonPos.y - _anchor.y * _buttonHitboxDimensions.y);
    }
}
