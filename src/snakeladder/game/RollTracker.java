package snakeladder.game;

import java.util.HashMap;

public class RollTracker
{
    private final int NUM_SIDE_DICE = 6;
    private HashMap<Integer, Integer> rollFrequencies;
    private HashMap<String, Integer> connectionFrequencies;

    public RollTracker(int numDice)
    {
        rollFrequencies = new HashMap<>();
        for (int i = numDice; i <= NUM_SIDE_DICE * numDice; i++){
            rollFrequencies.put(i, 0);
        }
        connectionFrequencies = new HashMap<>();
        connectionFrequencies.put("up", 0);
        connectionFrequencies.put("down", 0);
    }
    public void addRoll(int value){
        rollFrequencies.put(value, rollFrequencies.get(value) + 1);
    }
    public void addConnectionTraversal(String direction){
        connectionFrequencies.put(direction, connectionFrequencies.get(direction) + 1);
    }
    public void printRolls(String puppetName){
        System.out.print(puppetName + " rolled: ");
        int numPossibleRolls = rollFrequencies.size(), i = 1;
        for (Integer roll : rollFrequencies.keySet()){
            System.out.print(roll+"-"+rollFrequencies.get(roll));
            if (i < numPossibleRolls){
                System.out.print(", ");
            }
            i++;
        }
        System.out.println();
    }
    public void printConnectionTraversals(String puppetName){
        System.out.println(puppetName+" traversed: up-" + connectionFrequencies.get("up")
                + ", down-" + connectionFrequencies.get("down"));
    }
}
