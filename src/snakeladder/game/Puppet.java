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

  public void setupPlayerDieValues(String[] dieValueStrings){
    for (int j = 0; j < dieValueStrings.length; j++) {
      this.playerDieValues.add(Integer.parseInt(dieValueStrings[j]));
    }
  }

  public List<Integer> getPlayerDieValues() {
    return playerDieValues;
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

  /* Some parts of the code are duplicated from moveToNextCell. This is because implementing moving back in the
   * same method would require a lot of cascading if else statements which makes the code much less readable and
   * quite confusing to understand. */
  public void moveToPreviousCell() {
    int tens = cellIndex / 10;
    int ones = cellIndex - tens * 10;
    if (tens % 2 == 0)     // Cells starting left 01, 21, .. 81
    {
      if (ones == 0 && cellIndex > 0)
        setLocation(new Location(getX(), getY() + 1));
      else
        setLocation(new Location(getX() - 1, getY()));
    }
    else     // Cells starting left 20, 40, .. 100
    {
      if (ones == 0)
        setLocation(new Location(getX(), getY() + 1));
      else
        setLocation(new Location(getX() + 1, getY()));
    }
    cellIndex--;
  }

  public void act()
  {
    GameSessionManager gsm = gamePane.getGSM();
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
        gsm.handleCheckGameStatusRequest(cellIndex);
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
        gsm.handleCheckGameStatusRequest(cellIndex);
        return;
      }

      nbSteps--;
      if (nbSteps == 0)
      {
        // After moving via dice roll, the moving puppet stops at this cell and check if any other puppets are on the
        // same cell. If so, the other puppets are pushed back by one cell.
        if(gamePane.checkOtherPuppetAtCell(cellIndex)) {
          gamePane.shiftOtherPuppetsBackwards();
        }

        // Check if on connection start
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null)
        {
          gamePane.setSimulationPeriod(50);
          y = gamePane.toPoint(currentCon.locStart).y;
          if (currentCon.locEnd.y > currentCon.locStart.y)
            dy = gamePane.animationStep;
          else
            dy = -gamePane.animationStep;

          // Instead of Puppet directly telling NavigationPane to output, Puppet goes through GameSessionManager which
          // masks the implementation of the output as well as NavigationPane from it. Lower coupling is achieved
          gsm.connectionOutput(currentCon);
        }
        else
        {
          setActEnabled(false);
          gsm.handleCheckGameStatusRequest(cellIndex);
        }
      }
    }
  }


}
