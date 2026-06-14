package src;
import java.util.*;
public class SortByTime implements Comparator <Leaderboard>{
    // Description: compare by time, ascending order
    // parameters: two leaderboard objects
    // return: int, for collections.sort
    public int compare(Leaderboard lb1, Leaderboard lb2){
        return lb1.getTime()-lb2.getTime();
    }
    
}