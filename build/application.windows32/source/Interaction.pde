
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
void InitInteraction()
{
    InitSounds();
}
void InitSounds()
{
    _minim = new Minim(this);
  	_rightCharacterSound = _minim.loadSample("Sons/typing_machine_1.wav", 512);
  	_wrongCharacterSound = _minim.loadSample("Sons/cowbell.wav", 512);
  	_wrongCharacterSound.setGain(-2);
}

// INTERACTION
void keyReleased()
{
    // We record if the shift key has been released
	if (keyCode == SHIFT){
		_shiftPressed = false;
	}
}
void keyPressed()
{
	CheckCharacterMatch();

    // We start the chrono if it hasn't been
    if (!_wordSeriesChronoStarted)
    {
        StartChrono();
    }
}
void mousePressed() {
    CheckEveryButtonPressed();
}

// VERIFICATION TYPED LETTER
void CheckCharacterMatch()
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
void ValidateCharacter()
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
void DenyCharacter()
{
    // We play the sound
    _wrongCharacterSound.trigger();

    // We store that this Character has been missed
	_missed[_cursorIndexInText] = true;

    // We reset the pointer animation loop
	ResetCurseurChrono();
}
boolean CheckEndOfText()
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
void StartChrono()
{
    println("Start Chronos");
    // We initialize the two chronometers
	_wordSeriesChrono = System.currentTimeMillis();
	_characterChrono = System.currentTimeMillis();
    _wordSeriesChronoStarted = true;
}
void CalculateWpmCharacter()
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
void ResetCharacterChrono()
{
    _characterChrono = System.currentTimeMillis();
}
long CalculateCharacterTime()
{
    long time = System.currentTimeMillis() - _characterChrono;
    ResetCharacterChrono();

    return time;
}
void CalculateTextWpmRecord()
{
    // Calculates the wpm record for the entire text the user just typed

	float wpmPerformance = MillisToWpm(System.currentTimeMillis() - _wordSeriesChrono, _wordSeriesFullText);
	SetLastWpmPerformanceText(wpmPerformance);
}

// CALLED WHEN NEW WORD SERIES
void Interaction_OnNewWordSeries()
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
void SetCurrentTextCharacter()
{
    // We get the character at _cursorIndexInText from _wordSeriesFullText
    _currentTextCharacter = _wordSeriesFullText.charAt(_cursorIndexInText);
}
void InitMissed()
{
    _missed = new boolean[_wordSeriesFullText.length()];
    for (int i=0; i < _missed.length; i++)
    {
        _missed[i] = false;
    }
}

// OTHER
void IncrementCursorIndexInText()
{
    _cursorIndexInText++;
    if (!CheckEndOfText())
    {
        SetCurrentTextCharacter();
    }
}
void SerializeWpmRecords()
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
