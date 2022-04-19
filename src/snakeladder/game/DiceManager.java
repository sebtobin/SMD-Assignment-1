package snakeladder.game;

import ch.aplu.jgamegrid.GameGrid;
import snakeladder.utility.ServicesRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DiceManager {

    int numDice;

    private int numRolls = 0;
    private int total = 0;

    private List<List<Integer>> dieValues = new ArrayList<List<Integer>>();

    public DiceManager(int numDice) {
        this.numDice = numDice;
    }

    public void setupInitialDieValues(Properties properties, List<Puppet> players, int numberOfPlayers) {
        for (int i = 0; i < numberOfPlayers; i++) {
            if (properties.getProperty("die_values." + i) != null) {
                String dieValuesString = properties.getProperty("die_values." + i);
                String[] dieValueStrings = dieValuesString.split(",");
                players.get(i).setupPlayerDieValues(dieValueStrings);

            } else {
                System.out.println("All players need to be set a die value for the full testing mode to run. " +
                        "Switching off the full testing mode");
                dieValues = null;
                break;
            }
        }
        System.out.println("dieValues = " + dieValues);
    }
    public int getDieValues(Puppet puppet) {
        List<Integer> currentPuppetDieValues = puppet.getPlayerDieValues();
        if (dieValues == null) {
            return ServicesRandom.get().nextInt(6) + 1;
        }

        int presetDieValue = currentPuppetDieValues.get(0);
        currentPuppetDieValues.remove(0);

        return presetDieValue;
    }

    public void registerRoll(int rollValue){
        numRolls++;
        total += rollValue;
    }

    public void resetValues(){
        numRolls = 0;
        total = 0;
    }
    public void setNumDice(int numDice) {
        this.numDice = numDice;
    }
    public int getNumDice()
    {
        return numDice;
    }

    public int getNumRolls()
    {
        return numRolls;
    }

    public void setNumRolls(int numRolls)
    {
        this.numRolls = numRolls;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }
}
