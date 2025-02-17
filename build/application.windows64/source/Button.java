public abstract class Button
{
    protected boolean _isActive = true;
    protected Vector2 _buttonPos;

    // CONSTRUCTORS
    public Button(Vector2 buttonPos)
    {
        _buttonPos = buttonPos;
    }

    // ABSTRACT METHODS
    public abstract void DrawButton(); // Draws the visuals of the button
    public abstract void OnClicked(); // The action that the button should do
    protected abstract boolean CheckPointerCollision(Vector2 mousePos); // Checks if the pointer is inside the button's hitBox

    // OTHER
    public boolean CheckClicked(Vector2 mousePos)
    {
        if (_isActive)
        {
            if (CheckPointerCollision(mousePos))
            {
                // The mouse is indeed inside the button's hitbox
                OnClicked();

                return true;
            }
        }
        else
        {
            DactyloTest.println("The button isn't active");
        }

        return false;
    }
    
    // GETTERS AND SETTERS
    public Vector2 GetPos()
    {
        return _buttonPos;
    }
    public void SetButtonPos(Vector2 position)
    {
        _buttonPos = position;
    }
    public void SetButtonPos(float xPosition, float yPosition)
    {
        _buttonPos = new Vector2(xPosition, yPosition);
    }
    public void SetActive(boolean activeState)
    {
        _isActive = activeState;
    }
}
