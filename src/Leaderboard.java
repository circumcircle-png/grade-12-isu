package src;

public class Leaderboard implements Comparable<Leaderboard>{
    private int score;
    private int time;
    private String name;
    // leaderboard class to save your winning stats
    public Leaderboard(String n, int sc, int t){
        this.name = n;
        this.score = sc;
        this.time = t;
    }
    public int compareTo(Leaderboard lb){
        //Description: used for sort, compares this leaderboard and another by score, descending order
        // parameters: another leaderboard object
        // Return: integer, of comparison
        return lb.score - this.score;
    }
    // getter for time
    public int getTime(){
        return time;
    }
    // getter for name
    public String getName(){
        return name;
    }
    // getter for score
    public int getScore(){
        return score;
    }
    
    public String toString(){
        //description: to string, turns the values into to string to display in leaderboard
        //parameters: none
        // return String, its toString for a reason
        return String.format("%-10.9s%-5d%4d", name, score, time);
    }
}
