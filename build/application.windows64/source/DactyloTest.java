import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.*; 
import java.text.DecimalFormat; 
import ddf.minim.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class DactyloTest extends PApplet {





public static PApplet mainPApplet;

public void setup()
{
	mainPApplet = this;

	
  
	InitLettersToWork();
  
	InitDisplay();

	InitCharToIndex();

	GenerateNewWordSeries();
	
	InitInteraction();

	InitButtons();
}

public void draw()
{
	DrawEverything();
}

public void Trigger_OnNewSeries()
{
	// Triggers this method for all sections
	Display_OnNewSeries();
	Interaction_OnNewWordSeries();
}
FreeGenerationButton _freeGenerationButton;

public void InitButtons()
{
	InitLetterToWorkButtons();
    InitFreeGenerationButton();
}

public void InitFreeGenerationButton()
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




public void CheckEveryButtonPressed()
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
// SETTINGS
int _fontSize = 30;
DecimalFormat _df = new DecimalFormat("0.0");
// The frame :
	final float _frameWidthProportion = 0.95f;
	final float _frameHeightProportion = 0.95f;
	final float _frameInnerWidthProportion = 0.95f; // The width of the inner frame proportionnal to the main one
	final float _frameInnerHeightProportion = 0.95f;; // The heigth of the inner frame proportionnal to the main one
// Colors :
	final int _green = 0xff00F051;
	final int _red = 0xffD32C0B;
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
public void InitDisplay()
{
	InitYPositions();
	InitFrame();
	InitLettersToWorkDisplay();
	background(225);
}
public void InitYPositions()
{
	// We set the y position for the different things we want to display

	_yCurrentLetterToWork = height *2/6;
	_yWordSeries = height *3/6;
	_yWpm = _yCurrentLetterToWork;
}
public void InitFrame()
{
	_frameWidth = width * _frameWidthProportion;
	_frameHeight = height * _frameHeightProportion;
	_innerFrameWidth = _frameWidth * _frameInnerWidthProportion;
	_innerFrameHeight = _frameWidth * _frameInnerHeightProportion;
}
public void InitLettersToWorkDisplay()
{
	_lettersToWorkDisplayWidth = _innerFrameWidth;
	_letterFrameWidth = _lettersToWorkDisplayWidth / _alphabet.length;
}


// ON NEW SERIES
public void Display_OnNewSeries()
{
	_wordSeriesTextLines = SetWordSeriesTextLines();

	ResetCurseurChrono();
}
public List<String> SetWordSeriesTextLines()
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
public void DrawEverything()
{
	DrawMainFrame();
	DrawButtons();
	DrawLastWpmPerformance();
	DrawCurrentLetterToWork(_currentLetterToWork, _letterFrameWidth, _currentLetterToWorkFrameColor);

	DrawWordSeries();
}
public void DrawMainFrame()
{
	// This method draws the main frame

	fill(255);
	stroke(0);
	strokeWeight(2);
	rectMode(CENTER);
	rect(width/2, height/2, _frameWidth, _frameHeight);
}
public void DrawButtons()
{
	DrawLetterToWorkButtons();

	_freeGenerationButton.DrawButton();
}
public void DrawCurrentLetterToWork(LetterStats currentLetterToWork, float frameWidth, int frameColor)
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
public void DrawLastWpmPerformance()
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
		fill(0xff5F5F5F);
		noStroke();
		rect(x, _yWpm, textWidth, _fontSize * 150/100, _fontSize * 50/100);

		// We draw the text
		textAlign(CENTER,CENTER);
		fill(255);
		text(_wpmTextDisplayed, x, _yWpm);
	}
}
public void DrawWordSeries()
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
						fill(0xffD30000);
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
public void DrawCurrentCharacterInText(char character, float x, float y, float textLineHeight)
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
public void DrawCursor(float x, float y, float xSize, float ySize)
{
	// It's a grey frame that contains the letter
	fill(100);
	rectMode(CORNER);
	strokeWeight(1);
	stroke(0);
	rect(x, y, xSize, ySize);
}

// OTHER
public void SetLastWpmPerformanceText(float wpmPerformance)
{
	_wpmTextDisplayed = _df.format(wpmPerformance) + " WPM";
}
public void CheckCursorBlinkChrono()
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
public void ResetCurseurChrono()
{
	_cursorBlinkChrono = System.currentTimeMillis();
}
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
enum GeneratingModes {WeakerLetter, UserLetterChoice, Free}

// SETTINGS
int wordSeriesLength = 15; // The number of words we want to generate in each series
char[] _alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','é','è','ç'}; // Letters we want to work

// INFOS
GeneratingModes _generatingMode = GeneratingModes.WeakerLetter; // the way we want to generate our word series
LetterStats[] _lettersToWork = new LetterStats[_alphabet.length]; // The stats of the letters we want to work
LetterStats[] _lettersToWorkOrdered = new LetterStats[_alphabet.length]; // Same array but ordered by wpm mean
String[] _wordsDrawn; // An array of all the words drawn
LetterStats _currentLetterToWork; // We letter we chose to work for this word series
HashMap<Character, Integer> _charToIndex = new HashMap<Character, Integer>(); // Gives the index of a character in the alphabet


public void InitLettersToWork()
{
    try {
        for (int i=0; i < _lettersToWork.length; i++)
        {
            _lettersToWork[i] = new LetterStats(_alphabet[i]);
        }
    }
    catch (Exception e) {
        println(e);
    }

    _lettersToWorkOrdered = OrderLetters(_lettersToWork.clone());

    InitCharToIndex();
}
public void InitCharToIndex()
{
    for (int i=0; i < _alphabet.length; i++)
    {
        _charToIndex.put(_alphabet[i], i);
    }
}

public void GenerateNewWordSeries()
{
    println("GENERATING");

    // The series generating depends on the generating mode
    switch(_generatingMode)
    {
        case WeakerLetter :
            _currentLetterToWork = FindWeakerLetter();
            _wordsDrawn = GenerateNewWordSeriesForLetter(_currentLetterToWork, wordSeriesLength);
            break;
        case UserLetterChoice :
            // the current letter to work has been set by the button pressed
            _wordsDrawn = GenerateNewWordSeriesForLetter(_currentLetterToWork, wordSeriesLength);
            break;
    }

    // We trigger the event for all sections
    Trigger_OnNewSeries();
}

public LetterStats FindWeakerLetter()
{
    // We want to find the letter with the weaker mean, or one that is calibrating

    LetterStats weakerLetter = _lettersToWork[0];
    for (int i = 1; i < _alphabet.length; i++)
    {
		if ( _lettersToWork[i].calibrating || (_lettersToWork[i].wpmMean < weakerLetter.wpmMean) ) // We want to prioritize the letters that are calibrating
		{
			weakerLetter = _lettersToWork[i];
		}
	}
	return weakerLetter;
}

public String[] GenerateNewWordSeriesForLetter(LetterStats letter, int wordSeriesLength)
{
    // First we want to know how much words is there in the file we want to read
    int dataBaseLength = FindNumberOfWordsInLetterFile(letter);

    // We then want to draw the numbers of the words' lines we want to pick
    int[] indexsDrawn = DrawNumbers(wordSeriesLength, 0, dataBaseLength);

    // We pick the words from the numbers that have been drawn
    String[] wordsDrawn = FindWordsInFileFromLines(letter, indexsDrawn);
    
    return wordsDrawn;
}

public int FindNumberOfWordsInLetterFile(LetterStats letter)
{
    int numberOfWords = 0;
    // There is one word per line, so we want to count the number of lines in the file
    try {
        BufferedReader buffer = new BufferedReader(new FileReader(sketchPath()+ "/Bibliotheque/" + letter.character + ".txt"));

        while (buffer.readLine() != null) 
        {
            numberOfWords ++;
        }

        buffer.close();
    }
    catch (Exception e) {
        println(e);
    }

    return numberOfWords;
}

public int[] DrawNumbers(int seriesLength, int min, int max)
{
    // This method draw "seriesLength" numbers between "min" and "max" without duplicate

    // First we verify that the input is correct
    if(min > max)
    {
        println("WARNING : input incorrect in 'DrawNumbers', min > max");
    }
    else if (seriesLength > (max - min))
    {
        println("WARNING : input incorrect in 'DrawNumbers', seriesLength > (max - min)");
    }

    int dataBaseLength = max-min;

    /* We want to know if a number has already been drawn,
    so we create this array */
    boolean[] alreadyDrawn = new boolean[dataBaseLength];
    /* if alreadyDrawn[i] = true, it means that the number i+min has already been drawn
    So there's this offset to take account of */

    // We initialize this array
    for (int i=0; i < dataBaseLength; i++)
    {
        alreadyDrawn[i] = false;
    }

    int[] draw = new int[seriesLength];

    int currentDraw;
    for (int i=0; i < seriesLength; i++)
    {
        do
        {
            currentDraw = new Random().nextInt(dataBaseLength) + min;
        }while(alreadyDrawn[currentDraw - min]); // We repeat if this value has already been drawn

        // We can add the new draw to the list
        draw[i] = currentDraw;
    }

    return draw;
}

public String[] FindWordsInFileFromLines(LetterStats letter, int[] indexsDrawn)
{
    String[] words = new String[indexsDrawn.length];

    // We sort the index array to use it properly;
    indexsDrawn = sort(indexsDrawn);

    try
    {
        BufferedReader buffer = new BufferedReader(new FileReader(sketchPath()+ "/Bibliotheque/" + letter.character + ".txt"));

        int lineindex = 0; // The number of the line in the file
        // We read the first line before entering the loop because it has the 0 index
        String fileLine = buffer.readLine();

        for (int i=0; i < indexsDrawn.length; i++)
        {
            // We want to continue scrolling through the file until we aren't on the wanted index
			while (lineindex != indexsDrawn[i])
			{
				fileLine = buffer.readLine();
				lineindex ++;
			} 
            // When we are on the index we want, we pick up the word from that line
			words[i] = fileLine;
		}
    }
    catch (Exception e) {
        println(e);
    }

    return words;
}

public String SetWordSeriesFullText(String[] wordsDrawn)
{
    // This method constructs the word series full text 
    String wordSeriesFullText = "";
    // We make sure there's at least one word drawn
    if (wordsDrawn.length > 0)
    {
        for (String word : wordsDrawn)
        {
            wordSeriesFullText += word + " "; // We add a space after each word
        }
        // We remove the last space
        wordSeriesFullText = wordSeriesFullText.substring(0, wordSeriesFullText.length() - 1);
    }
    else
    {
        println("WARNING : Error in 'SetWordSeriesFullText' : No words drawn");
    }


    return wordSeriesFullText;
}

// SETTINGS
final float _maxCharacterTime = 3000; // (in milliseconds) We don't record times that are higher than this value, because it means the user made a pause

// INFOS
// Keys :
boolean _shiftPressed = false; // Tells if the shift key is being pressed
boolean _comaPressed = false; // Tells if the coma key is being pressed
boolean _tremaPressed = false; // Tells if the trema key is being pressed
// Chrono :
boolean _wordSeriesChronoStarted = false; // Tells if the first character has been typed, so the chrono is launched
long _wordSeriesChrono; // Used to mesure the time taken to type the word series
long _characterChrono; // Used to mesure the time taken to type the current character

// Text :
int _cursorIndexInText = 0; // Tells which character we are checking in the text  
String _wordSeriesFullText; // The complete string with all the text the user have to type
char _currentTextCharacter; // The current character the user has to type
boolean[] _missed;

// COMPONENTS
Minim _minim; // plays the sounds
AudioSample _wrongCharacterSound; // sound played when the wrong character is typed
AudioSample _rightCharacterSound;  // sound played when the right character is typed


// INITIALIZATIONS
public void InitInteraction()
{
    InitSounds();
}
public void InitSounds()
{
    _minim = new Minim(this);
  	_rightCharacterSound = _minim.loadSample("Sons/typing_machine_1.wav", 512);
  	_wrongCharacterSound = _minim.loadSample("Sons/cowbell.wav", 512);
  	_wrongCharacterSound.setGain(-2);
}

// INTERACTION
public void keyReleased()
{
    // We record if the shift key has been released
	if (keyCode == SHIFT){
		_shiftPressed = false;
	}
}
public void keyPressed()
{
	CheckCharacterMatch();

    // We start the chrono if it hasn't been
    if (!_wordSeriesChronoStarted)
    {
        StartChrono();
    }
}
public void mousePressed() {
    CheckEveryButtonPressed();
}

// VERIFICATION TYPED LETTER
public void CheckCharacterMatch()
{ 
    // We verify that the Character typed is the same as the one we have to type

	char toType = _wordSeriesFullText.charAt(_cursorIndexInText); // lettre au curseur
    boolean rightCharacter = false; // Tells if we have typed the right Character

    // We first verify the "normal" Characters from the keyboard
	if (key != CODED) 
	{
        // We verify the Characters with a "^";
		if (_comaPressed)
		{
            rightCharacter |= (toType == 'â' & key == 'a');
            rightCharacter |= (toType == 'ê' & key == 'e');
            rightCharacter |= (toType == 'î' & key == 'i');
            rightCharacter |= (toType == 'ô' & key == 'o');
            rightCharacter |= (toType == 'û' & key == 'u');
		}
		else if(_tremaPressed)
		{
            // We verify the characters with a "¨";
            rightCharacter |= (toType == 'ä' & key == 'a');
            rightCharacter |= (toType == 'ë' & key == 'e');
            rightCharacter |= (toType == 'ï' & key == 'i');
            rightCharacter |= (toType == 'ö' & key == 'o');
            rightCharacter |= (toType == 'ü' & key == 'u');
		}
		else
		{
			rightCharacter |= (key == toType); // a character without trema or coma
            rightCharacter |= (keyCode == ENTER || keyCode == RETURN ); // Allows to bypass impossible characters to type
		}

        // We act based on "rightCharacter"
        if (rightCharacter)
        {
            ValidateCharacter();
        }
        else
        {
            DenyCharacter();
        }

        // We then reset _comaPressed and _tremaPressed
        _comaPressed = false;
        _tremaPressed = false;
	}
    // We verify if we pressed coma, trema or shift
	else if (keyCode == 130)
	{
        // Trema
		if (_shiftPressed){
			_tremaPressed = true;
			println("Trema Pressed");
            _comaPressed = false; // We don't want both to be active
		}
        // Coma
		else{
			_comaPressed = true;
			System.out.println("Coma Pressed");
            _tremaPressed = false; // We don't want both to be active
		}
	}
    // Shift
	else if(keyCode == SHIFT)
	{
		_shiftPressed = true;
	}

    // We don't do anything else if it's another key that has been pressed
}
public void ValidateCharacter()
{
    // We play the sound
	_rightCharacterSound.trigger();

    // If the chronometer had started, 
	if(_wordSeriesChronoStarted){
        // We calculate the time it took to type this character
		CalculateWpmCharacter();
        ResetCharacterChrono();
	}

    // We put the cursor to the next character
	IncrementCursorIndexInText();
	
    // We reset the pointer animation loop
	ResetCurseurChrono();
}
public void DenyCharacter()
{
    // We play the sound
    _wrongCharacterSound.trigger();

    // We store that this Character has been missed
	_missed[_cursorIndexInText] = true;

    // We reset the pointer animation loop
	ResetCurseurChrono();
}
public boolean CheckEndOfText()
{
    // Checks if we have finished typing the whole text
	if (_cursorIndexInText > _wordSeriesFullText.length()-1)
	{
        // We have finished typing the text (the cursor is at the end)
		CalculateTextWpmRecord();
		SerializeWpmRecords();
		// On refait un texte
		GenerateNewWordSeries();

        return true;
	}
    else
    {
        return false;
    }
}


// CHRONOS
public void StartChrono()
{
    println("Start Chronos");
    // We initialize the two chronometers
	_wordSeriesChrono = System.currentTimeMillis();
	_characterChrono = System.currentTimeMillis();
    _wordSeriesChronoStarted = true;
}
public void CalculateWpmCharacter()
{
    // This method calculates and record the wpm performance on the letter that just has been typed

    // We get the time it took
	long tempsMillis = System.currentTimeMillis() - _characterChrono;
    // We find where to find the letterStat corresponding
	int letterIndex = _charToIndex.getOrDefault(_wordSeriesFullText.charAt(_cursorIndexInText), -1);

    // We check that the index found is valid
    // and that the time taken to type the letter isn't too long (if it's too long, it means that the user paused it's typing)
	if(letterIndex != -1 && tempsMillis < _maxCharacterTime){
        float wpmPerformance = MillisToWpm(tempsMillis);
		_lettersToWork[letterIndex].AddWpmRecord(wpmPerformance);
	}
	
}
public void ResetCharacterChrono()
{
    _characterChrono = System.currentTimeMillis();
}
public long CalculateCharacterTime()
{
    long time = System.currentTimeMillis() - _characterChrono;
    ResetCharacterChrono();

    return time;
}
public void CalculateTextWpmRecord()
{
    // Calculates the wpm record for the entire text the user just typed

	float wpmPerformance = MillisToWpm(System.currentTimeMillis() - _wordSeriesChrono, _wordSeriesFullText);
	SetLastWpmPerformanceText(wpmPerformance);
}

// CALLED WHEN NEW WORD SERIES
public void Interaction_OnNewWordSeries()
{
    // We reset the cursor index
    _cursorIndexInText = 0;

    // We construct the word series full text
    _wordSeriesFullText =  SetWordSeriesFullText(_wordsDrawn);

    // We save what is the current character the user has to type
    SetCurrentTextCharacter();

    // We initialize the _missed array
    InitMissed();

    // We tell that the chrono hasn't started yet
    _wordSeriesChronoStarted = false;
}
public void SetCurrentTextCharacter()
{
    // We get the character at _cursorIndexInText from _wordSeriesFullText
    _currentTextCharacter = _wordSeriesFullText.charAt(_cursorIndexInText);
}
public void InitMissed()
{
    _missed = new boolean[_wordSeriesFullText.length()];
    for (int i=0; i < _missed.length; i++)
    {
        _missed[i] = false;
    }
}

// OTHER
public void IncrementCursorIndexInText()
{
    _cursorIndexInText++;
    if (!CheckEndOfText())
    {
        SetCurrentTextCharacter();
    }
}
public void SerializeWpmRecords()
{
    // We save the wpm records for every letter to work
    try {
        for (LetterStats letter : _lettersToWork)
        {
            letter.Serialise();
        }
    } catch (Exception e) {
        println(e);
    }
}


// SETTINGS
int _wpmListLengh = 50; // The number of wpm records we want to record for each letter
// INFOS
float _bestWpmMean;


class LetterStats
{
    // This is an object that will contain all the information about a certain letter

    public char character;
    List<Float> _lastWpmRecords; // the list of the last wpm records the user took to press this letter
    public boolean calibrating; // Tells if the letter still needs calibrating, meaning that there is not enough wpm recorded
    public float wpmMean; // the mean of all the last wpm

    public LetterStats(char letterCharacter) throws IOException
    {
        character = letterCharacter;
        CheckWpmRecordsFile(); // Checks the letter file
        try {
            _lastWpmRecords = Deserialize(_wpmListLengh); // We read the values from the file
        }catch (Exception e) {
            println(e);
        }
    }

    public void AddWpmRecord(float wpmRecord)
    {
        // We add the record and remove the record the older
        _lastWpmRecords.add(wpmRecord);
        _lastWpmRecords.remove(0);
    }

    // SERIALIZATION
    public void CheckWpmRecordsFile() throws IOException
    {
        // We check if the wpm records file is created for this letter
        // and if not, we create it
        if (new File(sketchPath()+ "/LetterStats/" + character + ".txt").createNewFile())
        {
            System.out.println("File created for letter " + character);
        }
        else 
        {
            System.out.println("File already exists for letter " + character + ".");
        }
    }
    public List<Float> Deserialize(int wpmListLengh) throws IOException
    {
        // this method reads the wpm records from a file and return a list of them

        List<Float> wpmList = new ArrayList<Float>();
        
        // We also calculate the mean while we read the file;
        wpmMean = 0;

        // We init the file reading
        BufferedReader reader = new BufferedReader(new FileReader(sketchPath()+ "/LetterStats/" + character + ".txt"));
        String currentLine; // The current line the reader is reading
        float currentReadWpm; // The current wpm record the reader is reading

        for (int i = 0; i < wpmListLengh; i++)
        {
            currentLine = reader.readLine();
            // The number of writen wpm records in the file might be lesser than the number of letter wpm records we want to store
            // So we check if we're trying to read a blank line in the file
            if(currentLine != null)
            {
                currentReadWpm = parseFloat(currentLine);
                wpmMean += currentReadWpm;

                if(currentReadWpm == 0)
                {
                    // It means that this wpm record has not been recorded
                    calibrating = true;
                }
                else
                {
                    // We add this wpm record to the list
                    wpmList.add(currentReadWpm);
                }
            }
            else
            {
                // If we try to read a blank line, it means that we don't have enough wpm record, so we need to calibrate it
                calibrating = true;
            }
        }

        wpmMean /= wpmListLengh;

        reader.close();
        
        return wpmList;
    }
    public void Serialise() throws IOException
    {
        // We save the wpm records to the corresponding file
        BufferedWriter writer = new BufferedWriter(new FileWriter(sketchPath()+ "/LetterStats/" + character + ".txt"));

        // We write every wpm records one by one
        for (float wpmRecord : _lastWpmRecords)
        {
            writer.write(String.valueOf(wpmRecord));
            writer.newLine();
        }

        writer.close();
    }

    public String toString()
    {
        return "Letter " + character;
    }
}

public float MillisToWpm(long millis, String texte)
{
    // Converts the time it took to right a text into wpm (word per minute)

    // We erase all the spaces
	int numberOfCharacters = texte.replaceAll(" ","").length();
    // We calculate the "number of words" (one "word" is 5 characters)
	float numberOfWords = numberOfCharacters / 5.0f;
	float timeInMinutes = millis / 60000.0f;
    float wpm = numberOfWords / timeInMinutes;
	return wpm;
}
public float MillisToWpm(float millis)
{
    // Version for single letters
	float numberOfWords = 1 / 5.0f;
	float timeInMinutes = millis / 60000;
    float wpm = numberOfWords / timeInMinutes;
	return wpm;
}

public LetterStats[] OrderLetters(LetterStats[] letterArray)
{
    // We want to sort the letter array
    // for that we first convert our array to a list
    int arrayLength = letterArray.length;
    println(letterArray);
    List<LetterStats> listToSort = new ArrayList<LetterStats>();
    for (int i = 0; i < arrayLength; ++i) {
        listToSort.add(letterArray[i]);
    }

    // We sort the list
    listToSort = OrderLetters(listToSort);

    // We then convert the list to an array
    for (int i=0; i < arrayLength; i++)
    {
        letterArray[i] = listToSort.get(i);
    }

    _bestWpmMean = FindBestWpmMean(letterArray);

    return letterArray;
}

public List<LetterStats> OrderLetters(List<LetterStats> letterList)
{
    return OrderLetters(letterList, true);
}
public List<LetterStats> OrderLetters(List<LetterStats> letterList, boolean seperateCalibrating)
{
    int listLength = letterList.size();
    
    // We don't have to sort if the list contains only 1 element
    if (listLength > 1)
	{
        // We seperate the "calibrating" letters from the others if needed (we need to do it at the first iteration)
        if (seperateCalibrating)
        {
            // We seperate the letters into the two lists
            List<LetterStats> calibratingList = new ArrayList<LetterStats>();
            List<LetterStats> listToSort = new ArrayList<LetterStats>();
            for (int i=0; i < listLength; i++)
            {
              print(letterList);
                if(letterList.get(i).calibrating)
                {
                    calibratingList.add(letterList.get(i));
                }
                else
                {
                    listToSort.add(letterList.get(i));
                }
            }

            listToSort = OrderLetters(listToSort, false);

            // Once the list to sort is sorted, we merge the two lists
            letterList = calibratingList;
            letterList.addAll(listToSort);
        }
        else
        {
            // In this case, there's no need to seperate the calibrating letters from the others
            // So we do a fast sort algorithm  
            LetterStats pivot = letterList.get(0);
            List<LetterStats> left = new ArrayList<LetterStats>();
            List<LetterStats> right = new ArrayList<LetterStats>();

            // we fill the right and left lists
            for (int i=1; i < listLength; i++)
            {
                if (letterList.get(i).wpmMean > pivot.wpmMean)
                {
                    left.add(letterList.get(i));
                }
                else
                {
                    right.add(letterList.get(i));
                }
            }

            // We sort the left and right lists
            left = OrderLetters(left, false);
            right = OrderLetters(right, false);

            // We merge everything
            letterList = left;
            letterList.add(pivot);
            letterList.addAll(right);
        }
	}
	return letterList;
}

public float FindBestWpmMean(LetterStats[] sortedLetterArray)
{
    // We consider that the array is already sorted
    // We want to find the letter that has the best wpm mean that is not calibrating
    for (int i=0; i < sortedLetterArray.length; i++)
    {
        if (!sortedLetterArray[i].calibrating)
        {
            return sortedLetterArray[i].wpmMean;
        }
    }

    return 0;
}
LetterToWorkButton[] _letterToWorkButtonArray; // The array with all the buttons in the scene

// Display
    float _yLettersToWork;
	float _lettersToWorkDisplayWidth;
	float _letterFrameWidth;
	String _wpmTextDisplayed = "";
	int _currentLetterToWorkFrameColor;

public class LetterToWorkButton extends RectButton
{
    LetterStats _letterToWork;
    public int frameColor;

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
        int currentFrameColor;
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
			fill(0xff5F5F5F);
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
	int currentFrameColor; // Used as a buffer for later
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
public class WeakerLetterGenerationButton extends RectButton
{
    public void DrawButton()
    {

    }

    public void OnClicked()
    {
    }
}
  public void settings() { 	size(1280, 720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "DactyloTest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
