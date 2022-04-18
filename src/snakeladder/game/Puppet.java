package snakeladder.game;

import ch.aplu.jgamegrid.*;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class Puppet extends Actor
{
  private GamePane gamePane;
  private int cellIndex = 0;
  private int nbSteps;
  private Connection currentCon = null;
  private int y;
  private int dy;
  private boolean isAuto;
  private String puppetName;

  private List<Integer> playerDieValues;

  // The number of rolls this Puppet object has had in the game.
  private int nbRollsPuppet = 0;

  Puppet(GamePane gamePane, String puppetImage, boolean isAuto, String puppetName)
  {
    super(puppetImage);
    this.gamePane = gamePane;
    this.isAuto = isAuto;
    this.puppetName = puppetName;

    playerDieValues = new ArrayList<>();
  }

  public boolean isAuto() {
    return isAuto;
  }

  public void setAuto(boolean auto) {
    isAuto = auto;
  }

  public String getPuppetName() {
    return puppetName;
  }

  public void setPuppetName(String puppetName) {
    this.puppetName = puppetName;
  }

  public void setupPlayerDieValues(String[] dieValueStrings){
    for (int j = 0; j < dieValueStrings.length; j++) {
      this.playerDieValues.add(Integer.parseInt(dieValueStrings[j]));
    }
  }

  void go(int nbSteps)
  {
    if (cellIndex == 100)  // after game over
    {
      cellIndex = 0;
      setLocation(gamePane.startLocation);
    }
    this.nbSteps = nbSteps;
    setActEnabled(true);
  }

  void resetToStartingPoint() {
    cellIndex = 0;
    setLocation(gamePane.startLocation);
    setActEnabled(true);
  }

  int getCellIndex() {
    return cellIndex;
  }

  private void moveToNextCell()
  {
    int tens = cellIndex / 10;
    int ones = cellIndex - tens * 10;
    if (tens % 2 == 0)     // Cells starting left 01, 21, .. 81
    {
      if (ones == 0 && cellIndex > 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() + 1, getY()));
    }
    else     // Cells starting left 20, 40, .. 100
    {
      if (ones == 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() - 1, getY()));
    }
    cellIndex++;
  }

  public void act()
  {
    if ((cellIndex / 10) % 2 == 0)
    {
      if (isHorzMirror())
        setHorzMirror(false);
    }
    else
    {
      if (!isHorzMirror())
        setHorzMirror(true);
    }

    // Animation: Move on connection
    if (currentCon != null)
    {
      int x = gamePane.x(y, currentCon);
      setPixelLocation(new Point(x, y));
      y += dy;

      // Check end of connection
      if ((dy > 0 && (y - gamePane.toPoint(currentCon.locEnd).y) > 0)
        || (dy < 0 && (y - gamePane.toPoint(currentCon.locEnd).y) < 0))
      {
        gamePane.setSimulationPeriod(100);
        setActEnabled(false);
        setLocation(currentCon.locEnd);
        cellIndex = currentCon.cellEnd;
        setLocationOffset(new Point(0, 0));
        currentCon = null;
        gamePane.getGSM().verifyGameStatus(cellIndex);
      }
      return;
    }

    // Normal movement
    if (nbSteps > 0)
    {
      moveToNextCell();

      if (cellIndex == 100)  // Game over
      {
        setActEnabled(false);
        gamePane.getGSM().verifyGameStatus(cellIndex);
        return;
      }

      nbSteps--;
      if (nbSteps == 0)
      {
        // Check if on connection start
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null)
        {
          gamePane.setSimulationPeriod(50);
          y = gamePane.toPoint(currentCon.locStart).y;
          if (currentCon.locEnd.y > currentCon.locStart.y)
            dy = gamePane.animationStep;
          else
            dy = -gamePane.animationStep;
          if (currentCon instanceof Snake)
          {
            gamePane.getGSM().getNP().showStatus("Digesting...");
            gamePane.getGSM().getNP().playSound(GGSound.MMM);
          }
          else
          {
            gamePane.getGSM().getNP().showStatus("Climbing...");
            gamePane.getGSM().getNP().playSound(GGSound.BOING);
          }
        }
        else
        {
          setActEnabled(false);
          gamePane.getGSM().verifyGameStatus(cellIndex);
        }
      }
    }
  }


}
