package energyModels.data.voter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import energyModels.data.DataProvider;


public class VoterDataProvider implements DataProvider{
	static String dataBaseHost = "gossple2";
	//static String dataBaseName = "slashdot_feb09";
	static String dataBaseName = "epinions";
	static String dataBaseUserName = "amoin";
	static String dataBasePassword = "xM5YQjGvEVPKesDb";

	private Connection dbConnect;
	private int nbVoters;
	private Map<Integer, Voter> voters;
				
	public VoterDataProvider(int nbVoters){
		this.nbVoters = nbVoters;
		this.voters = new HashMap<Integer, Voter>();
	}
	
	@Override
	public UserInfo giveData() {
			return null;
	}

	@Override
	public void clear() {
		try {
			if(this.dbConnect != null){
				this.dbConnect.close();
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.voters = null;
	}

	@Override
	public int fetchInformation() {
		if(this.connectToDataBase()){
			int nbUsers = 0;
			try {
				Statement s = this.dbConnect.createStatement();
				ResultSet res = s.executeQuery("SELECT voter_ID FROM voters WHERE num_received_pos_votes > 20 LIMIT 0," + nbVoters);
				while(res.next()){
					int id = res.getInt(1);
					voters.put(id, new Voter(id));
					nbUsers++;
				}
				res.close();
				int nbProfiles = 0;
				PreparedStatement ps = 
						this.dbConnect.prepareStatement("SELECT voted_ID, vote FROM votes WHERE voter_ID = ?");
				for(Entry<Integer, Voter> en : this.voters.entrySet()){
					Voter v = en.getValue();
					ps.setInt(1, en.getKey());
					ResultSet rs = ps.executeQuery();
					while(rs.next()){
						int votedID = rs.getInt(1);
						int vote = rs.getInt(2);
						if(this.voters.containsKey(votedID)){
							v.getProfile().addValue(this.voters.get(votedID), (double)vote);
						}
					}
					nbProfiles++;
					if(nbProfiles % 1000 == 0){
						System.out.println("I imported " + nbProfiles + " profiles.");
					}
					rs.close();
				}
				System.out.println("I imported " + nbProfiles + " profiles.");
				return nbUsers;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		return 0;
	}
	
	public double resolveConflictsOld(){
		int voterCounter = 0;
		int nbVotes = 0;
		int nbTwoWayEvents = 0;
		int nbConflicts = 0;
		for(Voter v : this.voters.values()){
			nbVotes += v.getProfile().size();
		}
		for(Voter v : this.voters.values()){
			for(Voter voted : v.getProfile().getItems()){
				double vote1 = v.getProfile().getValue(voted);
				double vote2 = voted.getProfile().getValue(v);
				if(vote1 * vote2 == 1.0 ){
					nbTwoWayEvents++;
					//To get statistics, the following line must be commented.
					//v.getProfile().remove(voted);
				}else if(vote1 * vote2 == -1.0){
					nbTwoWayEvents++;
					nbConflicts++;
					voted.getProfile().remove(v);
					v.getProfile().remove(voted);
				}
			}
			voterCounter++;
			if(voterCounter % 1000 == 0){
				System.out.println("Conflict statistics computed for " + voterCounter + " users.");
			}
		}
		System.out.println("Conflict statistics computed for " + voterCounter + " users.");
		
		System.out.println("Total number of votes = " + nbVotes);
		System.out.println("Number of two-way events = " + nbTwoWayEvents);
		System.out.println("Percentage of two-way events = " + (double)(nbTwoWayEvents*2)/(double)nbVotes);
		System.out.println("Number of conflicts = " + nbConflicts);
		System.out.println("Percentage of conflicts = " + (double)nbConflicts/(double)nbTwoWayEvents);
		return (double)(nbTwoWayEvents * 2) / ((double)nbVotes);
	}
	
	public double resolveConflicts(){
		int voterCounter = 0;
		int nbVotes = 0;
		int nbTwoWayEvents = 0;
		int nbConflicts = 0;
		for(Voter v : Voter.factory.values()){
			nbVotes += v.getProfile().size();
		}
		for(Voter v : Voter.factory.values()){
			for(Voter voted : v.getProfile().getItems()){
				double vote1 = v.getProfile().getValue(voted);
				double vote2 = voted.getProfile().getValue(v);
				if(vote1 * vote2 != 0.0 ){
					nbTwoWayEvents++;
					//To get statistics, the following line must be commented.
					//v.getProfile().remove(voted);
					if(vote1 * vote2 < 0.0){
						nbConflicts++;
						voted.getProfile().remove(v);
						v.getProfile().remove(voted);
					}else{
						double sign = vote1 / Math.abs(vote1);
						double max = Math.max(Math.abs(vote1), Math.abs(vote2));
						voted.getProfile().remove(v);
						voted.getProfile().addValue(v, max*sign);
						v.getProfile().remove(voted);
						v.getProfile().addValue(voted, max*sign);
						
					}
				}
			}
			voterCounter++;
			if(voterCounter % 1000 == 0){
				System.out.println("Conflict statistics computed for " + voterCounter + " users.");
			}
		}
		System.out.println("Conflict statistics computed for " + voterCounter + " users.");
		
		System.out.println("Total number of votes = " + nbVotes);
		System.out.println("Number of two-way events = " + nbTwoWayEvents/2.0);
		System.out.println("Percentage of two-way events to the votes = " + (double)(nbTwoWayEvents)/(double)nbVotes);
		System.out.println("Number of conflicts = " + nbConflicts);
		System.out.println("Percentage of conflicts = " + (double)nbConflicts/(double)nbTwoWayEvents);
		return (double)(nbTwoWayEvents) / ((double)nbVotes);
	}
	
	@SuppressWarnings("unused")
	public void fetchVotersFromFile(String fileName){
		try {
			FileInputStream fis = new FileInputStream(fileName);
			BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
			String line;
			line = bf.readLine();
			StringTokenizer token = new StringTokenizer(line);
			token.nextToken();
			token.nextToken();
			String format = token.nextToken();
			System.out.println("format = " + format);
			String field = token.nextToken();
			System.out.println("field = " + field);
			String symmetry = token.nextToken();
			System.out.println("symmetry = " + symmetry);
			if((format.trim().equals("coordinate")) && ((symmetry.trim().equals("general"))||
					(symmetry.trim().equals("skew-symmetric")) || (symmetry.trim().equals("symmetric")))){
				boolean comment = true;
				while(comment){
					line = bf.readLine();
					comment = line.startsWith("%");
				}
				token = new StringTokenizer(line);
				int rows = Integer.parseInt(token.nextToken());
				int columns = Integer.parseInt(token.nextToken());
				int edges = Integer.parseInt(token.nextToken());
				int counter = 0;
				while((line=bf.readLine())!= null){
					token = new StringTokenizer(line);
					int i = Integer.parseInt(token.nextToken());
					Voter voter = Voter.getVoter(i-1);
					int j = Integer.parseInt(token.nextToken());
					Voter voted = Voter.getVoter(j-1);
					double weight;
					if(field.equals("pattern")){
						weight = 1.0;
					}else{
						weight = Double.parseDouble(token.nextToken());
					}
					//if(i != j){
						weight = 1.0;
						voter.getProfile().addValue(voted, weight);
					//}
					counter++;
					if(counter % 10000 == 0){
						System.out.println(counter + " edges read.");
					}
				}
				System.out.println(counter + " edges read.");
			}
			bf.close();
			System.out.println("voters size = " + Voter.factory.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<Integer, Voter> getVoters() {
		return voters;
	}

	// should be used only while importing the voters table.
	public int importer(){
		if(this.connectToDataBase()){
			try {
				int numVotes =0;
				String query = "SELECT * from votes LIMIT " + nbVoters;
				Statement s = this.dbConnect.createStatement();
				ResultSet rs = s.executeQuery(query);
				while(rs.next()){
					numVotes++;
					int voterID = rs.getInt(1);
					int votedID = rs.getInt(2);
					int vote = rs.getInt(3);
					Voter voter = Voter.getVoter(voterID);
					Voter voted = Voter.getVoter(votedID);
					voter.getProfile().addValue(voted, (double)vote);
					if(numVotes % 10000 == 0){
						System.out.println("I read " + numVotes + " from the table.");
					}
				}
				System.out.println("I read " + numVotes + " from the table.");
				
				//query = "INSERT IGNORE INTO voters VALUES (?,?,?,?)";
				query = "SELECT vote FROM votes WHERE votted_ID = ?";
				PreparedStatement ps = this.dbConnect.prepareStatement(query);
				PreparedStatement psRes = this.dbConnect.prepareStatement("UPDATE votters SET num_received_votes = ?, " +
						"num_received_pos_votes = ?, num_received_neg_votes = ? WHERE votter_ID = ?");
				int batched = 0;
				for(Entry<Voter, Voter> en : Voter.factory.entrySet()){
					int nbPosVotes = 0;
					int nbNegVotes = 0;
					int nbVotes = 0;
					ps.setInt(1, en.getKey().getPeerId());
					rs = ps.executeQuery();
					while(rs.next()){
						nbVotes++;
						if(rs.getInt(1) > 0){
							nbPosVotes++;
						}else{
							nbNegVotes++;
						}
					}
					psRes.setInt(4, en.getKey().getPeerId());
					psRes.setInt(1, nbVotes);
					psRes.setInt(2, nbPosVotes);
					psRes.setInt(3, nbNegVotes);
					psRes.addBatch();
					batched++;
					if(batched % 1000 == 0){
						psRes.executeBatch();
						psRes.clearBatch();
						System.out.println("I imported " + batched + " votters.");
					}
				}
				rs.close();
				for(Entry<Voter, Voter> en : Voter.factory.entrySet()){
					int nbPosVotes = 0;
					int nbNegVotes = 0;
					int nbVotes = 0;
					for(Entry<Voter, Double> e : en.getValue().getProfile().getMap().entrySet()){
						nbVotes++;
						if(e.getValue() > 0){
							nbPosVotes++;
						}else{
							nbNegVotes++;
						}
					}
					ps.setInt(1, en.getKey().getPeerId());
					ps.setInt(2, nbVotes);
					ps.setInt(3, nbPosVotes);
					ps.setInt(4, nbNegVotes);
					ps.addBatch();
					batched++;
					if(batched % 1000 == 0){
						ps.executeBatch();
						ps.clearBatch();
						System.out.println("I imported " + batched + " votters.");
					}
				}
				ps.executeBatch();
				ps.clearBatch();
				System.out.println("I imported " + batched + " votters.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Voter.factory.size();
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
}
