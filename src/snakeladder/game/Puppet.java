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
  private boolean minDiceRoll;
  private String puppetName;
  private StatTracker statTracker;

  Puppet(GamePane gamePane, String puppetImage, boolean isAuto, String puppetName, int numDice)
  {
    super(puppetImage);
    this.gamePane = gamePane;
    this.isAuto = isAuto;
    this.puppetName = puppetName;
    statTracker = new StatTracker(numDice);
  }

  void go(int nbSteps, boolean mindDiceRoll)
  {
    this.minDiceRoll = mindDiceRoll;
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

  /* Puppet does its own Location arithmetic instead of using GamePane's static cellToLocation method so that it can
   * calculate Location independently and is not more tightly coupled with GamePane and reliant on its static method.
   * GamePane's cellToLocation method is used in the construction of Connection objects which we have no access to.
   * With this decision, if GamePane's cellToLocation method is modified */
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
    else     // Cells starting right 20, 40, .. 100
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
    if (tens % 2 == 0)     // Cells starting left 01 (01-10), 21, .. 81
    {
      // When moving back from a cell like 21, 41 or etc. the player needs to move downwards.
      if (ones == 1 && cellIndex > 0)
        setLocation(new Location(getX(), getY() + 1));
      // Otherwise, they move backwards towards the left.
      else
        setLocation(new Location(getX() - 1, getY()));
    }
    else     // Cells starting right 20 (20-11), 40, .. 100
    {
      // When moving back from a cell like 11, 31 or etc. the player needs to move downwards.
      if (ones == 1)
        setLocation(new Location(getX(), getY() + 1));
      // Otherwise, they move backwards towards the right.
      else
        setLocation(new Location(getX() + 1, getY()));
    }
    cellIndex--;
  }

  public void act()
  {
    SLOPController sc = gamePane.getSC();
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
        sc.handleCheckGameStatusRequest(cellIndex);
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
        sc.handleCheckGameStatusRequest(cellIndex);
        return;
      }

      nbSteps--;
      if (nbSteps == 0)
      {
        /* After moving via dice roll, the moving puppet stops at this cell and check if any other puppets are on the
         * same cell. If so, the other puppets are pushed back by one cell. This does method calls to GamePane, but we
         * have no choice because we don't have access to the calling context of act(). */
        if(gamePane.checkOtherPuppetAtCell(cellIndex)) {
          gamePane.shiftOtherPuppetsBackwards();
        }

        // Check if on connection start, proceed if either min dice roll wasn't rolled or connection moves upwards
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null &&
              (currentCon.locEnd.y < currentCon.locStart.y || minDiceRoll == false))
        {
          gamePane.setSimulationPeriod(50);

          // Find the y coordinate of the starting location of the connection.
          y = gamePane.toPoint(currentCon.locStart).y;

          // Larger y-coordinate means downwards. If the end location of the connection is downwards.
          if (currentCon.locEnd.y > currentCon.locStart.y) {
            dy = gamePane.animationStep;
            statTracker.addTraversal("down");
          // If the end location of the connection is upwards.
          } else {
            dy = -gamePane.animationStep;
            statTracker.addTraversal("up");
          }

          // Instead of Puppet directly telling NavigationPane to output, Puppet goes through GameSessionManager which
          // masks the implementation of the output as well as NavigationPane from it. Lower coupling is achieved
          sc.connectionOutput(currentCon);
        }
        else
        {
          // in case min dice roll check occured, reset currentcon to null so no animation is played
          currentCon = null;
          minDiceRoll = false;
          setActEnabled(false);
          sc.handleCheckGameStatusRequest(cellIndex);
        }
      }
    }
  }

  public boolean isAuto() {
    return isAuto;
  }

  public String getPuppetName() {
    return puppetName;
  }

  int getCellIndex() {
    return cellIndex;
  }
  void addRoll(int totalRoll){
    statTracker.addRoll(totalRoll);
  }
  void printStats(){
    statTracker.printStats(puppetName);
  }
}
