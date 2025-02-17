FreeGenerationButton _freeGenerationButton;

void InitButtons()
{
	InitLetterToWorkButtons();
    InitFreeGenerationButton();
}

void InitFreeGenerationButton()
{
    textSize(_fontSize);
    float textWidth = textWidth("Free Generation");
    Vector2 buttonHitboxDimensions = new Vector2(textWidth * 1.2f, _fontSize * 1.2f);

	// We place the button between the wpm display and the word series
    float rightFrameSide = width/2 + _innerFrameWidth/2; // The right side of the main inner frame
    float yPos = (_yWpm + _yWordSeries) / 2;
    float xPos = rightFrameSide - buttonHitboxDimensions.x/2;
    Vector2 buttonPos = new Vector2(xPos, yPos);
	_freeGenerationButton = new FreeGenerationButton(buttonPos, buttonHitboxDimensions);
}




void CheckEveryButtonPressed()
{
    // We created a vector representing the mouse position
    Vector2 mousePos = new Vector2(mouseX, mouseY);

    boolean currentButtonClicked; // Tells if the current button we're checking has been clicked

    // We check for every letter to work buttons if it has been pressed
    for (LetterToWorkButton button : _letterToWorkButtonArray)
    {
        currentButtonClicked = button.CheckClicked(mousePos);

        // We don't check the other buttons if this one has been clicked
        if (currentButtonClicked)
        {
            break;
        }
    }

    _freeGenerationButton.CheckClicked(mousePos);
}
