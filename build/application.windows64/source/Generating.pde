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


void InitLettersToWork()
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
void InitCharToIndex()
{
    for (int i=0; i < _alphabet.length; i++)
    {
        _charToIndex.put(_alphabet[i], i);
    }
}

void GenerateNewWordSeries()
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

LetterStats FindWeakerLetter()
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

String[] GenerateNewWordSeriesForLetter(LetterStats letter, int wordSeriesLength)
{
    // First we want to know how much words is there in the file we want to read
    int dataBaseLength = FindNumberOfWordsInLetterFile(letter);

    // We then want to draw the numbers of the words' lines we want to pick
    int[] indexsDrawn = DrawNumbers(wordSeriesLength, 0, dataBaseLength);

    // We pick the words from the numbers that have been drawn
    String[] wordsDrawn = FindWordsInFileFromLines(letter, indexsDrawn);
    
    return wordsDrawn;
}

int FindNumberOfWordsInLetterFile(LetterStats letter)
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

int[] DrawNumbers(int seriesLength, int min, int max)
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

String[] FindWordsInFileFromLines(LetterStats letter, int[] indexsDrawn)
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

String SetWordSeriesFullText(String[] wordsDrawn)
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
