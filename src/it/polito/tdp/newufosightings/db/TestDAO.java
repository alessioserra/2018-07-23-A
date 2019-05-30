package it.polito.tdp.newufosightings.db;

import java.util.HashMap;
import java.util.Map;

import it.polito.tdp.newufosightings.model.State;

public class TestDAO {

	public static void main(String[] args) {

		Map<String, State> idMap = new HashMap<>();
		
		NewUfoSightingsDAO dao = new NewUfoSightingsDAO();

		System.out.println(dao.loadAllStates(idMap));
	}

}
