package src;

public class Leaderboard implements Comparable<Leaderboard>{
    private int score;
    private int time;
    private String name;
    public Leaderboard(String n, int sc, int t){
        this.name = n;
        this.score = sc;
        this.time = t;
    }
    public int compareTo(Leaderboard lb){
        return lb.score - this.score;
    }
    public int getTime(){
        return time;
    }
    public String getName(){
        return name;
    }
    
}
