package snakeladder.game;

import ch.aplu.jgamegrid.*;
import snakeladder.utility.PropertiesLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("serial")
public class GamePane extends GameGrid
{
  private GameSessionManager gsm;

  private int numberOfPlayers = 1;
  private int currentPuppetIndex = 0;
  private List<Puppet> puppets =  new ArrayList<>();
  private List<Boolean> playerManualMode;

  private ArrayList<Connection> connections = new ArrayList<Connection>();
  final Location startLocation = new Location(-1, 9);  // outside grid
  final int animationStep = 10;
  public static final int NUMBER_HORIZONTAL_CELLS = 10;
  public static final int NUMBER_VERTICAL_CELLS = 10;
  private final int MAX_PUPPET_SPRITES = 4;

  GamePane(Properties properties)
  {
    setSimulationPeriod(100);
    setBgImagePath("sprites/gamepane_blank.png");
    setCellSize(60);
    setNbHorzCells(NUMBER_HORIZONTAL_CELLS);
    setNbHorzCells(NUMBER_VERTICAL_CELLS);
    doRun();
    createSnakesLadders(properties);
    setupPlayers(properties);
    setBgImagePath("sprites/gamepane_snakeladder.png");
  }

  void setupPlayers(Properties properties) {
    numberOfPlayers = Integer.parseInt(properties.getProperty("players.count"));
    playerManualMode = new ArrayList<>();
    for (int i = 0; i < numberOfPlayers; i++) {
      playerManualMode.add(Boolean.parseBoolean(properties.getProperty("players." + i + ".isAuto")));
    }
    System.out.println("playerManualMode = " + playerManualMode);
  }

  void createGui()
  {
    for (int i = 0; i < numberOfPlayers; i++) {
      boolean isAuto = playerManualMode.get(i);
      int spriteImageIndex = i % MAX_PUPPET_SPRITES;
      String puppetImage = "sprites/cat_" + spriteImageIndex + ".gif";

      Puppet puppet = new Puppet(this, puppetImage, isAuto, "Player " + (i + 1));
      addActor(puppet, startLocation);
      puppets.add(puppet);
    }
  }

  void createSnakesLadders(Properties properties) {
    connections.addAll(PropertiesLoader.loadSnakes(properties));
    connections.addAll(PropertiesLoader.loadLadders(properties));
  }

  Puppet getPuppet()
  {
    return puppets.get(currentPuppetIndex);
  }

  void switchToNextPuppet() {
    currentPuppetIndex = (currentPuppetIndex + 1) % numberOfPlayers;
  }

  List<Puppet> getAllPuppets() {
    return puppets;
  }

  void resetAllPuppets() {
    for (Puppet puppet: puppets) {
      puppet.resetToStartingPoint();
    }
  }

  public int getNumberOfPlayers() {
    return numberOfPlayers;
  }

  public boolean checkOtherPuppetAtCell(int currentCell) {
    /* For each player in the game that is not the current player trying to move, if they are on the cell that
     * the current player is trying to move to, then the method returns true. The method returns false otherwise. */
    for(int i = 0; i < numberOfPlayers; i++) {
      if(i != currentPuppetIndex) {
        if(puppets.get(i).getCellIndex() == currentCell) {
          return true;
        }
      }
    }
    return false;
  }

  public void shiftOtherPuppetsBackwards() {
    for (int i = 0; i < numberOfPlayers; i++) {
      if (i != currentPuppetIndex) {
        puppets.get(i).moveToPreviousCell();
      }
    }
  }

  public void toggleConnection() {
    for(int i = 0; i < connections.size(); i++){
      connections.get(i).reverseStartEnd();
    }
  }

  public boolean moreUpwardsConnections(int numDice) {
    int lowestPossibleRoll = numDice * 1, highestPossibleRoll = numDice * 6;
    int lowestPossibleCell, highestPossibleCell;

    int numUpwardsConnections = 0, numDownwardsConnections = 0;
    Puppet currentPuppet;
    Connection currentConnection;

    // For all players that are not the current player (opponents), determine the lowest and highest possible cells
    // they are able to reach within their turn.
    for(int i = 0; i < numberOfPlayers; i++) {
      if(i != currentPuppetIndex) {
        currentPuppet = puppets.get(i);
        lowestPossibleCell = currentPuppet.getCellIndex() + lowestPossibleRoll;
        highestPossibleCell = currentPuppet.getCellIndex() + highestPossibleRoll;
        // Check all the connections that start at a cell available to the opponent in this current turn.
        for(int j = lowestPossibleCell; j < highestPossibleCell; j++) {
          // If a connection exists on a cell. Check if they're an upwards or downwards connection and increment the
          // respective counters.
          if((currentConnection = getConnectionAt(cellToLocation(j))) != null) {
            if (currentConnection.getCellEnd() > currentConnection.getCellStart()) {
              numUpwardsConnections++;
            } else {
              numDownwardsConnections++;
            }
          }
        }
      }
    }
    // If the amount of upwards connections is more than or equal to downwards connections for the opponent,
    // return true. Otherwise, return false.
    if(numUpwardsConnections >= numDownwardsConnections) {
      return true;
    } else {
      return false;
    }
  }

  Connection getConnectionAt(Location loc)
  {
    for (Connection con : connections)
      if (con.locStart.equals(loc))
        return con;
    return null;
  }

  static Location cellToLocation(int cellIndex)
  {
    int index = cellIndex - 1;  // 0..99

    int tens = index / NUMBER_HORIZONTAL_CELLS;
    int ones = index - tens * NUMBER_HORIZONTAL_CELLS;

    int y = 9 - tens;
    int x;

    if (tens % 2 == 0)     // Cells starting left 01, 21, .. 81
      x = ones;
    else     // Cells starting left 20, 40, .. 100
      x = 9 - ones;

    return new Location(x, y);
  }
  
  int x(int y, Connection con)
  {
    int x0 = toPoint(con.locStart).x;
    int y0 = toPoint(con.locStart).y;
    int x1 = toPoint(con.locEnd).x;
    int y1 = toPoint(con.locEnd).y;
    // Assumption y1 != y0
    double a = (double)(x1 - x0) / (y1 - y0);
    double b = (double)(y1 * x0 - y0 * x1) / (y1 - y0);
    return (int)(a * y + b);
  }

  public GameSessionManager getGSM() {
    return gsm;
  }

  public void setGsm(GameSessionManager gsm) {
    this.gsm = gsm;
  }

  public int getCurrentPuppetIndex() {
    return currentPuppetIndex;
  }
}


