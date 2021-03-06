/*
The ultimate goal for the JDBCSQLDemo.java to be integrated with the Query.java code. The Connection code will remain a seperate function.

Before the code is integreated with the Query.java file, this Query must effectively create a qArray.java.

A qArray.java basically is just a normal array that has handler methods, that hosts many qPlay elements.

A qPlay array is basically just a normal array that has handler methods, it stores a

int nextScore
int down
int yardline
int distance
int play

This qArray is then handed to the Calculator to be processed into a cArray, which is basically just a java representation of the
soon to be created .CSV file.
 */


package functions;

import runner.*;
import datastructures.*;
import functions.*;
import outputs.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//import com.theopentutorials.jdbc.db.DbUtil;
//import com.theopentutorials.jdbc.db.JDBCMySQLConnection;
//import com.theopentutorials.jdbc.to.Employee;


public class JDBCMySQLDemo {

    public static void main(String[] args) throws IOException {        
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         
    	new JDBCMySQLDemo();    
    }
    
    public JDBCMySQLDemo(){
        try {
            getQuery();        
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } 
    }
 
    public static ArrayList<qPlay> getQuery()  {      
        ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
         
        ArrayList<qPlay> fgPlayArray = new ArrayList<qPlay>();
        ArrayList<qPlay> gfiPlayArray = new ArrayList<qPlay>();
        ArrayList<qPlay> puntPlayArray = new ArrayList<qPlay>();

        String query = "SELECT * FROM field_goals";
        try {           
            connection = JDBCMySQLConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            if(Runner.deBugMode) System.out.println("query success");
            
            int j = 0;
            while (rs.next()) {
            	qPlay currPlay = new qPlay();

            	j++;
            	if(Runner.deBugMode) System.out.println("current fieldgoal: " + j);
            	if(rs.getInt("success") == 1) {
            		if(Runner.deBugMode) System.out.println("successful field goal. playid: " + rs.getInt("play_id") + ", distance: " + rs.getInt("distance"));
            	} else if (rs.getInt("success") == 0){
            		int fieldGoalInt = rs.getInt("play_id");
            		String tempQuery = "SELECT * FROM plays WHERE id = " + fieldGoalInt;
            		if(Runner.deBugMode) System.out.println(tempQuery);
                    ResultSet rs2 = null; Statement st2 = null; st2 = connection.createStatement(); rs2 = st2.executeQuery(tempQuery);
                     rs2.next();
                     int driveLook = rs2.getInt("drive_id");
                     
                     currPlay.setDistance(rs2.getInt("location")); 
                     currPlay.setDown(rs2.getInt("down"));
                     currPlay.setPlay(0);	// 0 is fieldgoal
                     currPlay.setYardline(rs2.getInt("distance"));
                     
            		if(Runner.deBugMode) System.out.println("drive is " + driveLook);
            		tempQuery = "SELECT * FROM drives WHERE id = " + driveLook; 
                    ResultSet rs3 = null; Statement st3 = null; st3 = connection.createStatement(); rs3 = st3.executeQuery(tempQuery);

                    rs3.next();
                    int gameLook = rs3.getInt("game_id");
                    
                    tempQuery = "SELECT * FROM drives WHERE game_id = " + gameLook;
                    ResultSet rs5 = null; Statement st5 = null; st5 = connection.createStatement(); rs5 = st5.executeQuery(tempQuery);
                    rs5.next();
                    boolean isEvenDrive = false;
                    int startDrive = rs5.getInt("id");
                    if(driveLook % 2 == 0) {
                    	isEvenDrive = true;
                    }
                    if(Runner.deBugMode) System.out.println("start drive is " + startDrive);
                    
            		tempQuery = "SELECT * FROM drives WHERE game_id = " + gameLook + " AND id > " + driveLook; 
                    ResultSet rs4 = null; Statement st4 = null; st4 = connection.createStatement(); rs4 = st4.executeQuery(tempQuery);

                    rs4.next();
                    int i = 0;
                    boolean keepLookingForNextScore = true;
                    while(rs4.next() && keepLookingForNextScore)
                    {
                		//if(Runner.deBugMode) System.out.println("i = " + i + " points: " + rs4.getInt("points"));
                		if(rs4.getInt("points") > 0) {
                			keepLookingForNextScore = false;
                			int nextScoreDrive = rs4.getInt("id");
                			if(Runner.deBugMode) System.out.println("next score's drive is " + nextScoreDrive);
                			boolean isNextScoreEvenDrive = false;
                			if(nextScoreDrive % 2 == 0) {
                				isNextScoreEvenDrive = true;	
                			} 
                			if(isNextScoreEvenDrive && isEvenDrive) {
                				if(Runner.deBugMode) System.out.println("same team got next score");
                			} else {
                				if(Runner.deBugMode) System.out.println("other team score got next score");

                			}
                			currPlay.setNextScore(rs4.getInt("points"));

                		}

                		i++;
                    }   
                    //done, now add play to arraylist
                    fgPlayArray.add(currPlay);
                    

            	}


            	
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
    	
        //ADD OTHER QUERIES FOR TWO OTHER PLAYTYPES HERE

        
        //convert fg arraylist to array and put it into the qArray constructor
        //qArray fgQueryArray = new qArray(fgPlayArray.toArray(new qPlay[fgPlayArray.size()]));
        
        
        
        //Combine all 3 arrays into 3D static array here
        
        
        
        //done
        return fgPlayArray;
    }
}
