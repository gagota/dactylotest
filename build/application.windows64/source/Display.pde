// SETTINGS
int _fontSize = 30;
DecimalFormat _df = new DecimalFormat("0.0");
// The frame :
	final float _frameWidthProportion = 0.95f;
	final float _frameHeightProportion = 0.95f;
	final float _frameInnerWidthProportion = 0.95f; // The width of the inner frame proportionnal to the main one
	final float _frameInnerHeightProportion = 0.95f;; // The heigth of the inner frame proportionnal to the main one
// Colors :
	final color _green = #00F051;
	final color _red = #D32C0B;
// Chrono :
	float _cursorBlinkCycleDuration = 1000; // (in milliseconds) The time it takes for the cursor to make blink cycle

// INFOS
// Those are the different y position for the different components :
	float _yCurrentLetterToWork;
	float _yWordSeries;
	float _yWpm;
// The frame :
	float _frameWidth;
	float _frameHeight;
	float _innerFrameWidth; // The width of the frame included in the main one
	float _innerFrameHeight; // The width of the frame included in the main one
// Word Series :
	List<String> _wordSeriesTextLines = new ArrayList<String>(); // Each element of this list is a line of text to display from the word series. There's different lines because all of th text can't fit in the frame
	long _cursorBlinkChrono; // This chrono is used to make the cursor blink
	boolean _isCursorOn;

// AT START OF WINDOW
void InitDisplay()
{
	InitYPositions();
	InitFrame();
	InitLettersToWorkDisplay();
	background(225);
}
void InitYPositions()
{
	// We set the y position for the different things we want to display

	_yCurrentLetterToWork = height *2/6;
	_yWordSeries = height *3/6;
	_yWpm = _yCurrentLetterToWork;
}
void InitFrame()
{
	_frameWidth = width * _frameWidthProportion;
	_frameHeight = height * _frameHeightProportion;
	_innerFrameWidth = _frameWidth * _frameInnerWidthProportion;
	_innerFrameHeight = _frameWidth * _frameInnerHeightProportion;
}
void InitLettersToWorkDisplay()
{
	_lettersToWorkDisplayWidth = _innerFrameWidth;
	_letterFrameWidth = _lettersToWorkDisplayWidth / _alphabet.length;
}


// ON NEW SERIES
void Display_OnNewSeries()
{
	_wordSeriesTextLines = SetWordSeriesTextLines();

	ResetCurseurChrono();
}
List<String> SetWordSeriesTextLines()
{
	// This methods constructs each line that we'll display

	textSize(_fontSize);
	String currentLine = ""; // used to construct the current line of text
	String tempCurrentLine = ""; // used as a sort of buffer
	List<String> wordSeriesTextLines = new ArrayList<String>();

	for (int i=0; i < _wordsDrawn.length; i++)
	{
		// We want to place every word on a line

		// We create a temp line to see if it's too long for the frame
		tempCurrentLine = currentLine + _wordsDrawn[i] + " "; // We add a space after the word
		// We test if it's too long
		if (textWidth(tempCurrentLine) > _innerFrameWidth)
		{
			// It means we have filled the line
			// So we append the line to the list
			wordSeriesTextLines.add(currentLine);
			// And we begin the next one
			currentLine = _wordsDrawn[i] + " ";
		}
		else
		{
			// otherwise we can continue with this text line
			currentLine = tempCurrentLine;
		}
	}

	// We add the last text line even if it doesn't fill the frame width (because we need the last words of the list anyway)
	wordSeriesTextLines.add(currentLine);

	return wordSeriesTextLines;
}

// ACTUAL DRAWING
void DrawEverything()
{
	DrawMainFrame();
	DrawButtons();
	DrawLastWpmPerformance();
	DrawCurrentLetterToWork(_currentLetterToWork, _letterFrameWidth, _currentLetterToWorkFrameColor);

	DrawWordSeries();
}
void DrawMainFrame()
{
	// This method draws the main frame

	fill(255);
	stroke(0);
	strokeWeight(2);
	rectMode(CENTER);
	rect(width/2, height/2, _frameWidth, _frameHeight);
}
void DrawButtons()
{
	DrawLetterToWorkButtons();

	_freeGenerationButton.DrawButton();
}
void DrawCurrentLetterToWork(LetterStats currentLetterToWork, float frameWidth, color frameColor)
{
	float leftSide = width/2 - _innerFrameWidth/2; // The x coordinate of the left side of what we are about to draw

	// We draw the description
	textSize(_fontSize);
	String description = "Current letter to work : "; // Will be written after
	float descriptionWidth = textWidth(description); // The width that the description takes in the window
	textAlign(LEFT, CENTER);
	fill(0);
	text(description, leftSide, _yCurrentLetterToWork);

	// We draw the letter with its frame (we place it after the description)
	// Frame
	fill(frameColor);
	stroke(0);
	rect( leftSide + descriptionWidth + frameWidth/2, _yCurrentLetterToWork, frameWidth, frameWidth );
	// Letter
	textAlign(CENTER, CENTER);
	fill(0);
	textSize(frameWidth * 0.7f);
	text(Character.toUpperCase(currentLetterToWork.character), leftSide + descriptionWidth + frameWidth/2, _yCurrentLetterToWork - (frameWidth * 0.1f));

	// We draw the wpm mean text
	String wpmText;
	if (currentLetterToWork.calibrating)
	{
		// If it's calibrating we obviously don't draw the wpm mean
		wpmText = "Calibrating...";
	}
	else
	{
		wpmText = "Speed mean : " + _df.format(currentLetterToWork.wpmMean) + " wpm";
	}
	textSize(_fontSize);
	textAlign(LEFT, CENTER);
	fill(0);
	text(wpmText, leftSide, _yCurrentLetterToWork + frameWidth);
}
void DrawLastWpmPerformance()
{
	// This method draws the wpm mean that came from the last word set that have been typed

	// We don't want to display anything if there hasn't been any performance yet
	if(_wpmTextDisplayed != "")
	{
		textSize(_fontSize);
		float textWidth = textWidth("  " + _wpmTextDisplayed); // We want to add a little space to the sides for the text to fit in the frame
		float rightSide = width/2 + _innerFrameWidth/2; // The x coordinate in the wondow of the right side of what we want to draw
		float x = rightSide - textWidth/2; // The anchor of what we want to draw (in the center)
			
		// We draw the frame
		rectMode(CENTER);
		fill(#5F5F5F);
		noStroke();
		rect(x, _yWpm, textWidth, _fontSize * 150/100, _fontSize * 50/100);

		// We draw the text
		textAlign(CENTER,CENTER);
		fill(255);
		text(_wpmTextDisplayed, x, _yWpm);
	}
}
void DrawWordSeries()
{
	// We want to place each character one by one

	textSize(_fontSize);
	textAlign(LEFT, TOP);

	float textLineHeight = _fontSize * 1.5f;
	float yTextLineAnchor = _yWordSeries; // the y coordinate of where we anchor our character drawing
	float xCharAnchor; // The x coordinate of the current character's anchor
	int currentIndexInText = 0;

	for (String textLine : _wordSeriesTextLines)
	{
		// We want to center our line, so we calculate where to put the first character
		float textLineWidth = textWidth(textLine);
		xCharAnchor = width/2 - textLineWidth/2; // We put the anchor at the begining of our text

		// We display each character
		for (int i=0; i < textLine.length(); i++)
		{
			if (currentIndexInText == _cursorIndexInText)
			{
				// The letter is the one the user has to type
				DrawCurrentCharacterInText(textLine.charAt(i), xCharAnchor, yTextLineAnchor, textLineHeight);
			}
			else
			{
				if (currentIndexInText < _cursorIndexInText)
				{
					// The letter has already been typed (it's before the cursor)
					if (_missed[currentIndexInText])
					{
						// We draw the letter red if it has been missed
						fill(#D30000);
					}
					else
					{
						// We draw the letter grey if it hasn't been missed
						fill(100);
					}
				}
				else
				{
					// The letter hasn't been typed yet (it's after the cursor)
					fill(0);
				}

				// We draw our character
				text(textLine.charAt(i), xCharAnchor, yTextLineAnchor);
			}
			
			// We move the anchor at the right of the new character
			xCharAnchor += textWidth(textLine.charAt(i));
			currentIndexInText++;
		}

		// We lower the anchor for the next line
		yTextLineAnchor += textLineHeight;
	}

	// We then add a line at the bottom for the aestethics
	strokeWeight(1);
	stroke(150);
	float yLinePos = yTextLineAnchor + textLineHeight;
	float lineWidth = _innerFrameWidth / 2;
	line(width/2 - lineWidth/2, yLinePos, width/2 + lineWidth/2, yLinePos);
}
void DrawCurrentCharacterInText(char character, float x, float y, float textLineHeight)
{
	/* The current character the user has to type has a special treatment
	because we also draw the cursor that is blinking */

	// We check the chrono for the animation loop (it updates _isCursorOn)
	CheckCursorBlinkChrono();

	if (_isCursorOn)
	{
		// We draw the cursor
		DrawCursor(x, y, textWidth(character), textLineHeight);
		// We want to draw our character white
		fill(255);
	}
	else
	{
		// Just draw the letter as normal
		fill(0);
	}

	text(character, x, y);
}
void DrawCursor(float x, float y, float xSize, float ySize)
{
	// It's a grey frame that contains the letter
	fill(100);
	rectMode(CORNER);
	strokeWeight(1);
	stroke(0);
	rect(x, y, xSize, ySize);
}

// OTHER
void SetLastWpmPerformanceText(float wpmPerformance)
{
	_wpmTextDisplayed = _df.format(wpmPerformance) + " WPM";
}
void CheckCursorBlinkChrono()
{
	// This method updates "_isCursorOn"

	float currentBlinkCycleTime = ( System.currentTimeMillis() - _cursorBlinkChrono ) % _cursorBlinkCycleDuration;

	if(currentBlinkCycleTime < _cursorBlinkCycleDuration / 2)
	{
		// We are in the first half of the animation
		// So we show the cursor
		_isCursorOn = true;
	}
	else
	{
		// We are in the second half of the animation
		// So we don't show the cursor
		_isCursorOn = false;
	}
}
void ResetCurseurChrono()
{
	_cursorBlinkChrono = System.currentTimeMillis();
}
