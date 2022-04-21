package snakeladder.game;

import snakeladder.utility.ServicesRandom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class DiceRoller {

    int numDice;

    private int numRolls = 0;
    private int total = 0;

    private List<LinkedList<Integer>> dieValues = new ArrayList<>();

    public DiceRoller(int numDice) {
        this.numDice = numDice;
    }

    public void setupInitialDieValues(Properties properties, int numberOfPlayers) {
        for (int i = 0; i < numberOfPlayers; i++) {
            if (properties.getProperty("die_values." + i) != null) {
                LinkedList<Integer> dieValueForPlayer = new LinkedList<>();
                String dieValuesString = properties.getProperty("die_values." + i);
                String[] dieValueStrings = dieValuesString.split(",");
                for (String numString : dieValueStrings) {
                    dieValueForPlayer.add(Integer.parseInt(numString));
                }
                dieValues.add(dieValueForPlayer);

            } else {
                System.out.println("All players need to be set a die value for the full testing mode to run. " +
                        "Switching off the full testing mode");
                dieValues = null;
                break;
            }
        }
        System.out.println("dieValues = " + dieValues);
    }

    public int getDieValues(int puppetNum) {
        if (dieValues == null) {
            return ServicesRandom.get().nextInt(6) + 1;
        }

        return dieValues.get(puppetNum).size() > 0 ? dieValues.get(puppetNum).removeFirst() :
                ServicesRandom.get().nextInt(6) + 1;
    }

    public void registerRoll(int rollValue) {
        numRolls++;
        total += rollValue;
    }

    public void resetValues() {
        numRolls = 0;
        total = 0;
    }

    public void setNumDice(int numDice) {
        this.numDice = numDice;
    }

    public int getNumDice() {
        return numDice;
    }

    public int getNumRolls() {
        return numRolls;
    }

    public void setNumRolls(int numRolls) {
        this.numRolls = numRolls;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
