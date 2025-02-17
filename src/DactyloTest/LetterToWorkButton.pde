LetterToWorkButton[] _letterToWorkButtonArray; // The array with all the buttons in the scene

// Display
    float _yLettersToWork;
	float _lettersToWorkDisplayWidth;
	float _letterFrameWidth;
	String _wpmTextDisplayed = "";
	color _currentLetterToWorkFrameColor;

public class LetterToWorkButton extends RectButton
{
    LetterStats _letterToWork;
    public color frameColor;

    // CONSTRUCTORS
    public LetterToWorkButton(Vector2 buttonPos, Vector2 buttonHitboxDimensions, LetterStats letterToWork)
    {
        super(buttonPos, buttonHitboxDimensions);

        _letterToWork = letterToWork;
    }

    // GETTERS AND SETTERS

    // OTHER
    public void DrawButton()
    {
        // We set the frame color, depending on the wpm mean or if it's calibrating
        color currentFrameColor;
        if(_letterToWork.calibrating){
			// If the letter is calibrating, we grey its frame
			currentFrameColor = color(175);
		}
		else{
			// If it has a wpm mean, we color its frame depending on his wpm mean relative to the best wpm mean
			float gradient = _letterToWork.wpmMean / _bestWpmMean;
			currentFrameColor = lerpColor(_red, _green, gradient);
		}

		if(_letterToWork == _currentLetterToWork)
		{
			// We save the frame color of this letter to draw it in "DrawCurrentLetterToWork"
			_currentLetterToWorkFrameColor = currentFrameColor;
		}

        // We draw the frame
		rectMode(CENTER);
		fill(currentFrameColor);
		stroke(0);
		strokeWeight(1);
		rect(_buttonPos.x, _buttonPos.y, _buttonHitboxDimensions.x, _buttonHitboxDimensions.y);

		// We draw the letter character
		textAlign(CENTER,CENTER);
		fill(0);
		textSize(_buttonHitboxDimensions.y * 0.7f); // We want the letter to fit in the frame
		text(Character.toUpperCase(_letterToWork.character), _buttonPos.x, _buttonPos.y - (_buttonHitboxDimensions.y * 0.1f)); // _buttonHitboxDimensions.y * 0.1f is to correct an offset

		// We draw the wpm for the current letter (only if it's not calibrating)
		if(!_letterToWork.calibrating)
		{
            float yWpm = _buttonPos.y + _buttonHitboxDimensions.y; // We place it just beneath
            float fontSizeWPM = _buttonHitboxDimensions.y * 0.7f/2;

			// WPM frame
			rectMode(CENTER);
			fill(#5F5F5F);
			stroke(0);
			strokeWeight(1);
			rect(_buttonPos.x, yWpm, _buttonHitboxDimensions.x, fontSizeWPM * 1.5f, fontSizeWPM * .5f);

			// WPM text
            String texteWPM = _df.format(_letterToWork.wpmMean);
			textSize(fontSizeWPM);
			fill(255);
			text(texteWPM, _buttonPos.x, yWpm);
		}
    }
	public void SetButtonAppearance()
	{
		// Sets the frame color

		if(_letterToWork.calibrating){
			// If the letter is calibrating, we grey its frame
			frameColor = color(175);
		}
		else{
			// If it has a wpm mean, we color its frame depending on his wpm mean relative to the best wpm mean
			float gradient = _letterToWork.wpmMean / _bestWpmMean;
			frameColor = lerpColor(_red, _green, gradient);
		}

		if(_letterToWork == _currentLetterToWork)
		{
			// We save the frame color of this letter to draw it in "DrawCurrentLetterToWork"
			_currentLetterToWorkFrameColor = frameColor;
		}
	}

    public void OnClicked()
    {
        _generatingMode = GeneratingModes.UserLetterChoice;
        _currentLetterToWork = _letterToWork;
        GenerateNewWordSeries();
    }
}



public void InitLetterToWorkButtons()
{
    // We set some variables for the display
    _yLettersToWork = height *1/6;
    _lettersToWorkDisplayWidth = _innerFrameWidth;
	_letterFrameWidth = _lettersToWorkDisplayWidth / _alphabet.length;

    float leftSideX = width/2 - _lettersToWorkDisplayWidth/2; // The left side of all the buttons together
	color currentFrameColor; // Used as a buffer for later
	int currentLetterToWorkIndex=0; // on retiens la position de la lettre à travailler pour dessiner son cadra par dessus

    // We create all the buttons
    _letterToWorkButtonArray = new LetterToWorkButton[_lettersToWork.length];
    for (int i=0; i < _letterToWorkButtonArray.length; i++)
    {
        // We calculate the position of the button
        float xButtonPos = leftSideX + _letterFrameWidth * (i + 0.5f); // The x position in the window of the center of the current letter
        Vector2 buttonPos = new Vector2(xButtonPos, _yLettersToWork);
        Vector2 buttonHitboxDimensions = new Vector2(_letterFrameWidth, _letterFrameWidth);
        _letterToWorkButtonArray[i] = new LetterToWorkButton(buttonPos, buttonHitboxDimensions, _lettersToWork[i]);
    }
}
public void DrawLetterToWorkButtons()
{
    for (LetterToWorkButton button : _letterToWorkButtonArray)
    {
        button.DrawButton();
    }
    // We draw a special frame for the current letter to work, on top of the others
    int currentLetterToWorkIndex = _charToIndex.get(_currentLetterToWork.character);
    LetterToWorkButton currentLetterToWorkButton = _letterToWorkButtonArray[currentLetterToWorkIndex];
	Vector2 currentButtonPos = currentLetterToWorkButton.GetPos();
	Vector2 currentButtonDimensions = currentLetterToWorkButton.GetDimensions();
	noFill();
	strokeWeight(5);
	stroke(0);
	rect(currentButtonPos.x, currentButtonPos.y, currentButtonDimensions.x, currentButtonDimensions.y);
	strokeWeight(1);
	stroke(255);
	rect(currentButtonPos.x, currentButtonPos.y, currentButtonDimensions.x, currentButtonDimensions.y);
}