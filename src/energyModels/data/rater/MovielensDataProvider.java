package energyModels.data.rater;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import energyModels.data.DataProvider;

import threadedSim.util.ScoreCount;

public class MovielensDataProvider implements DataProvider {

	static String dataBaseHost = "gossple2";
	public static String dataBaseName = "movielens100k";
	static String dataBaseUserName = "amoin";
	static String dataBasePassword = "xM5YQjGvEVPKesDb";

	private Connection dbConnect;

	private List<UserInfo> peers = new ArrayList<UserInfo>();

	private int nextGiveIndex = 0;
	private int nbNodesFetch;
	private Set<Integer> profileSlice;
	private Set<Integer> searchSlice;

	private Set<Integer> alreadyKnownNodes;

	@SuppressWarnings("unchecked")
	public MovielensDataProvider(int howManyNodes, Set<Integer> profileSlice,
			Set<Integer> searchSlice, String loadFile) {
		this(howManyNodes, profileSlice, searchSlice);
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(loadFile);
			in = new ObjectInputStream(fis);
			this.alreadyKnownNodes = new HashSet<Integer>(
					((Map<Integer, List<Integer>>) in.readObject()).keySet());
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	public MovielensDataProvider(int howManyNodes, Set<Integer> profileSlice,
			Set<Integer> searchSlice) {
		this.nbNodesFetch = howManyNodes;
		this.profileSlice = profileSlice;
		this.searchSlice = searchSlice;
		this.alreadyKnownNodes = null;
	}

	public int fetchInformation() {
		if (this.connectToDataBase()) {
			try {
				System.out.println("getting random peers from data base ...");
				if (this.alreadyKnownNodes != null) {
					for (Integer p : this.alreadyKnownNodes) {
						this.peers.add(new UserInfo(p, null, null));
					}
				}
				if (this.peers.size() < this.nbNodesFetch) {
					String query = "SELECT user FROM users ORDER BY RAND() LIMIT 0, "
							+ this.nbNodesFetch;
					Statement s = this.dbConnect.createStatement();
					ResultSet rs = s.executeQuery(query);
					while (rs.next()) {
						int nodeId = rs.getInt(1);
						if (this.alreadyKnownNodes == null) {
							this.peers.add(new UserInfo(nodeId, null, null));
						} else if (!this.alreadyKnownNodes.contains(nodeId)) {
							this.peers.add(new UserInfo(nodeId, null, null));
							if (this.peers.size() == this.nbNodesFetch) {
								break;
							}
						}
					}
					rs.close();
				}
				if (this.peers.size() > this.nbNodesFetch) {
					this.peers.subList(this.nbNodesFetch, this.peers.size())
							.clear();
				}
				System.out.println("getting peer profile from data base ...");
				int count = 0;
				PreparedStatement profileFetch = this.dbConnect
						.prepareStatement("SELECT movie, rating, slice FROM ratings WHERE user = ?");
				for (UserInfo pNum : this.peers) {
					if ((count % 1000) == 0) {
						System.out.println(count);
					}
					count++;
					profileFetch.setInt(1, pNum.userId);
					ResultSet rs = profileFetch.executeQuery();
					ScoreCount<Film> cache = new ScoreCount<Film>();
					ScoreCount<Film> search = new ScoreCount<Film>();
					while (rs.next()) {
						int FilmNum = rs.getInt(1);
						Film film = Film.getFilm(FilmNum);
						double score = rs.getDouble(2);
						int slice = rs.getInt(3);
						if (this.profileSlice.contains(slice)) {
							cache.setValue(film, score);
						} else if (this.searchSlice.contains(slice)) {
							search.setValue(film, score);
						}
					}
					pNum.setCache(cache);
					pNum.setSearch(search);
				}
			} catch (SQLException e) {
				System.err.println("database error  in " + this.getClass());
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			System.err.println("connexion to database failed in "
					+ this.getClass());
			return 0;
		}
		return this.peers.size();
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

	public void clear() {
		this.peers = null;
		this.alreadyKnownNodes = null;
		this.profileSlice = null;
		this.searchSlice = null;
		try {
			this.dbConnect.close();
		} catch (SQLException e) {
		}
	}

	public UserInfo giveData() {
		if (nextGiveIndex >= this.peers.size()) {
			return null;
		}
		if ((this.nextGiveIndex % 1000) == 0) {
			System.out.println("giving data to node " + nextGiveIndex);
		}
		UserInfo res = this.peers.get(nextGiveIndex);
		this.nextGiveIndex++;
		return res;
	}

}
