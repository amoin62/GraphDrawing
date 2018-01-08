package energyModels.data.voter;

import java.util.HashMap;
import java.util.Map;

import threadedSim.peer.Peer;
import threadedSim.util.ScoreCount;

public class Voter extends Peer{
	public static Map<Voter, Voter> factory = new HashMap<Voter, Voter>();
		
	private ScoreCount<Voter> profile;
	
	public Voter(int id) {
		super(id);
		this.profile = new ScoreCount<Voter>();
	}
	
	public static Voter getVoter(int voterID){
		Voter voter = new Voter(voterID);
		Voter res = Voter.factory.get(voter);
		if(res == null){
			Voter.factory.put(voter, voter);
			return voter;
		}else{
			return res;
		}
	}
	
	public ScoreCount<Voter> getProfile() {
		return profile;
	}
		
}
