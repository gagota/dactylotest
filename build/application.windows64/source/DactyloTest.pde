import java.io.*;
import java.text.DecimalFormat;
import ddf.minim.*;

public static PApplet mainPApplet;

void setup()
{
	mainPApplet = this;

	size(1280, 720);
  
	InitLettersToWork();
  
	InitDisplay();

	InitCharToIndex();

	GenerateNewWordSeries();
	
	InitInteraction();

	InitButtons();
}

void draw()
{
	DrawEverything();
}

void Trigger_OnNewSeries()
{
	// Triggers this method for all sections
	Display_OnNewSeries();
	Interaction_OnNewWordSeries();
}
