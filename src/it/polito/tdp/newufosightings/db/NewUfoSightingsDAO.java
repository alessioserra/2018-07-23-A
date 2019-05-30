package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Confinanti;
import it.polito.tdp.newufosightings.model.Confine;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings() {
		String sql = "SELECT * FROM sighting";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> loadAllStates(Map<String, State> idMap) {
		String sql = "SELECT * FROM state";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				
				//Aggiungo ad IdMap
				idMap.put(state.getId(), state);
				
				result.add(state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	//Dato l'anno ottengo tutte le forme
	public List<String> loadAllShapess(int year) {
		String sql = "SELECT DISTINCT shape FROM sighting WHERE DATETIME LIKE ? ";
		List<String> result = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			//Set parameter (sintassi per LIKE)
			st.setString(1, year+"%");
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if (rs.getString("shape").compareTo("")!=0)
				result.add(rs.getString("shape"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Confine> getConfini(){
			
			String sql = "SELECT state1, state2 FROM neighbor WHERE state1>state2";
			List<Confine> result = new ArrayList<>();

			try {
				Connection conn = ConnectDB.getConnection();
				PreparedStatement st = conn.prepareStatement(sql);
				
				ResultSet rs = st.executeQuery();

				while (rs.next()) {
					result.add(new Confine(rs.getString("state1").toUpperCase(),
							             rs.getString("state2").toUpperCase()));
				}
				conn.close();
				return result;

			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Errore connessione al database");
				throw new RuntimeException("Error Connection Database");
			}
	}
	
	//Ottengo tutti gli archi e i pesi,
	//Se la riga appartiene ai confini la aggiungo alla lista di ritorno
	//Altrimenti no
	public List<Confinanti> getAllArchi(int year ,List<Confine> confini, String shape) {
		String sql = "SELECT s1.state AS id1, s2.state AS id2, COUNT(*) AS cnt FROM sighting s1, sighting s2 WHERE s1.shape=? AND s2.shape=? AND s1.state>s2.state AND s1.DATETIME LIKE ? GROUP BY s1.state,s2.state";
		List<Confinanti> result = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			//Set parameter (sintassi per LIKE)
			st.setString(1, shape);
			st.setString(2, shape);
			st.setString(3, year+"%");
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				String id1 = rs.getString("id1").toUpperCase();
				String id2 = rs.getString("id2").toUpperCase();
				Confine c = new Confine(id1, id2);
				
				//Se è confine
				if (confini.contains(c)) {
					result.add( new Confinanti(rs.getString("id1"),rs.getString("id2"),rs.getInt("cnt")));
				}

			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
}
