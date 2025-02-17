public class FreeGenerationButton extends RectButton
{
    String _buttonText = "Free Generation";

    public FreeGenerationButton(Vector2 buttonPos, Vector2 buttonHitboxDimensions)
    {
        super(buttonPos, buttonHitboxDimensions);
    }
    
    public void DrawButton()
    {
        //rectMode(CENTER);
        //fill(255);
        //strokeWeight(1);
        //stroke(0);
        //rect(_buttonCenter.x, _buttonCenter.y, _buttonHitboxDimensions.x,  _buttonHitboxDimensions.y, _buttonHitboxDimensions.y/5);

        //textSize(_fontSize);
        //textAlign(CENTER, CENTER);
        //fill(0);
        //text(_buttonText, _buttonCenter.x, _buttonCenter.y - _fontSize*0.1f);
    }

    public void OnClicked()
    {
        println("TODO");
    }
}
