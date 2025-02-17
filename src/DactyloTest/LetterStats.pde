import java.util.*;

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
    void CheckWpmRecordsFile() throws IOException
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
    List<Float> Deserialize(int wpmListLengh) throws IOException
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

float MillisToWpm(long millis, String texte)
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
float MillisToWpm(float millis)
{
    // Version for single letters
	float numberOfWords = 1 / 5.0f;
	float timeInMinutes = millis / 60000;
    float wpm = numberOfWords / timeInMinutes;
	return wpm;
}

LetterStats[] OrderLetters(LetterStats[] letterArray)
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

List<LetterStats> OrderLetters(List<LetterStats> letterList)
{
    return OrderLetters(letterList, true);
}
List<LetterStats> OrderLetters(List<LetterStats> letterList, boolean seperateCalibrating)
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

float FindBestWpmMean(LetterStats[] sortedLetterArray)
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
