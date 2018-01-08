package energyModels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.StringTokenizer;


import energyModels.data.rater.MovielensDataProvider;


public class ResultRefiner {
	
	static String dataBaseHost = "gossple2";
	static String dataBaseName = MovielensDataProvider.dataBaseName;
	static String dataBaseUserName = "amoin";
	static String dataBasePassword = "xM5YQjGvEVPKesDb";
	static String[] genres = {"action","adventure","animation","children","comedy","crime","documentary"
		,"drama","fantasy","filmnoir","horror","musical","mystery","romance","scifiction","thriller","war","western"};
	
	private Connection dbConnect;
	private FileReader inputFile;
	private FileWriter outputFile;
				
	public ResultRefiner(String inputFile, String outputFile){
		try {
			this.outputFile = new FileWriter(outputFile, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.inputFile = new FileReader(inputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
			
	
	public void reportGenre(String genre){
		boolean knownGenre = false;
		for(int i=0; i<genres.length; i++){
			if(genres[i].equals(genre)){
				knownGenre = true;
			}
		}
		if(!knownGenre){
			throw new RuntimeException("The given string is not a valid genre!");
		}
		
		if(connectToDataBase()){
			try {
				int counter = 0;
				BufferedReader inputBuffer = new BufferedReader(inputFile);
				BufferedWriter outputBuffer = new BufferedWriter(outputFile);
				String line;
				PreparedStatement ps = this.dbConnect.prepareStatement("SELECT " + genre + " FROM movies WHERE movie = ?");
				System.out.println("Looking for movies of genre " + genre + " in the input file!");
				while((line = inputBuffer.readLine())!= null){
					StringTokenizer tok = new StringTokenizer(line);
					int movieID = Integer.parseInt(tok.nextToken());
					ps.setInt(1, -movieID);
					ResultSet rs = ps.executeQuery();
					while(rs.next()){
						if(rs.getInt(1)== 1){
							outputBuffer.write(line+"\n");
							outputBuffer.flush();
							counter++;
							if(counter % 10 == 0){
								System.out.println("I found " + counter + " movies of type " + genre + "!");
							}
						}
					}
				}
				System.out.println("I found " + counter + " movies of type " + genre + "!");
				outputBuffer.write("\n\n");
				outputBuffer.close();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}catch (NumberFormatException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private boolean connectToDataBase() {
		try {
			dbConnect = DriverManager.getConnection("jdbc:mysql://"
					+ dataBaseHost + "/" + dataBaseName, dataBaseUserName,
					dataBasePassword);
		} catch (SQLClientInfoException e) {
			System.err
					.println("Program.connectToDataBase : connexion string not valid");
			System.err.println(e);
			return false;
		} catch (SQLTimeoutException e) {
			System.err
					.println("Program.connectToDataBase : timeout during connexion");
			System.err.println(e);
			return false;
		} catch (SQLException e) {
			System.err
					.println("Program.connectToDataBase : error during connexion");
			System.err.println(e);
			return false;
		}
		return true;
	}
	
	public static void main(String[] args){
		ResultRefiner rf = new ResultRefiner("moviesCoordinates.txt","movieGenre.txt");
		//rf.reportGenre("action");
		//rf.reportGenre("adventure");
		//rf.reportGenre("animation");
		//rf.reportGenre("children");
		//rf.reportGenre("comedy");
		//rf.reportGenre("crime");
		//rf.reportGenre("documentary");
		//rf.reportGenre("drama");
		//rf.reportGenre("fantasy");
		//rf.reportGenre("filmnoir");
		//rf.reportGenre("horror");
		//rf.reportGenre("musical");
		//rf.reportGenre("mystery");
		//rf.reportGenre("romance");
		//rf.reportGenre("scifiction");
		//rf.reportGenre("thriller");
		//rf.reportGenre("war");
		rf.reportGenre("western");
	}

}
