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

  private boolean toEndTurn = false;

  Puppet(GamePane gamePane, String puppetImage, boolean isAuto, String puppetName, int numDice)
  {
    super(puppetImage);
    this.gamePane = gamePane;
    this.isAuto = isAuto;
    this.puppetName = puppetName;
    statTracker = new StatTracker(numDice);
  }

  void go(int nbSteps, boolean minDiceRoll)
  {
    this.minDiceRoll = minDiceRoll;
    if (cellIndex == 100)  // after game over
    {
      cellIndex = 0;
      setLocation(gamePane.startLocation);
    }
    this.nbSteps = nbSteps;
    toEndTurn = true;
    setActEnabled(true);
  }

  void resetToStartingPoint() {
    cellIndex = 0;
    setLocation(gamePane.startLocation);
    setActEnabled(true);
  }

  private void moveToNextCell()
  {
    /*int tens = cellIndex / 10;
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
    }*/
    cellIndex++;
    setLocation(GamePane.cellToLocation(cellIndex));
  }

  private void endTurn() {
    if (toEndTurn) {
      gamePane.getSC().handleEndTurnRequest();
      toEndTurn = false;
    }
  }

  boolean moveToPreviousCell() {
    cellIndex--;
    setLocation(GamePane.cellToLocation(cellIndex));

    // If a puppet was shifted backwards onto a connection by an opponent, they need to be able to act temporarily
    // in order to traverse the connection. Hence, true is returned.
    if((currentCon = gamePane.getConnectionAt(getLocation())) != null) {
      prepareToTraverseConnection();
      setActEnabled(true);
      toEndTurn = true;
      return true;
    }
    // False is returned because this puppet can just simply be moved back and doesn't need to act.
    return false;
  }

  // Perform pre-computation for traversing the connection. Also requests SC to handle the output (display and sound)
  // of the connection.
  void prepareToTraverseConnection() {
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
    // masks the implementation of the output as well as NavigationPane from it. Lower coupling is achieved.
    gamePane.getSC().connectionOutput(currentCon);
  }

  // Method for animating the Puppet traversing on a connection. Note: act must be enabled for this method to work.
  void traverseConnection() {
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
      endTurn();
    }
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
      traverseConnection();
      return;
    }

    // Normal movement
    if (nbSteps > 0)
    {
      moveToNextCell();

      if (cellIndex == 100)  // Game over
      {
        setActEnabled(false);
        endTurn();
        return;
      }

      nbSteps--;
      if (nbSteps == 0)
      {

        /* After moving via dice roll, the moving puppet stops at this cell and check if any opponents (other puppets)
         * are on the same cell. If so, they are pushed back.
         *
         * In the case that the opponents are pushed back into a connection, they need to be allowed to act to traverse
         * the connection. Hence, the responsibility of ending the turn is transferred from the current moving puppet
         * to the opponent that gets shifted back.
         *
         * If there are no puppets being shifted backwards, then proceed to send a request to SC for
         * ending the turn as usual. */
        if(gamePane.checkAndShiftOtherPuppetAtCell(cellIndex)) {
          toEndTurn = false;
        }

        // Check if on connection start. And also, only proceed if the connection is not (downwards and minDiceRoll
        // is true). This condition ensures that a player does not travel down a connection if they rolled minDiceRoll.
        if ((currentCon = gamePane.getConnectionAt(getLocation())) != null &&
              !(currentCon.locEnd.y > currentCon.locStart.y && minDiceRoll == true)) {
          // A connection was identified and conditions are satisfied to traverse it, call prepareToTraverseConnection
          // to do some pre-computation before moving on the connection.
          prepareToTraverseConnection();
        } else {
          // At this point, the minDiceRoll was rolled with currentCon being a downwards connection. Thus, reset
          // the connection so the Puppet doesn't traverse it.
          currentCon = null;
          setActEnabled(false);

          endTurn();
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
