package it.polito.tdp.newufosightings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	SimpleWeightedGraph<State, DefaultWeightedEdge> grafo;
	Map<String, State> idMap;
	
	public Model() {
		idMap = new HashMap<>();
	}
	
	public List<String> loadAllShapes(int year) {
		NewUfoSightingsDAO dao = new NewUfoSightingsDAO();		
		return dao.loadAllShapess(year);
	}
	
	public void creaGrafo(int year,String shape) {
		
		//Creo grafo
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//
		NewUfoSightingsDAO dao = new NewUfoSightingsDAO();	
		
		//Carico Nodi
		List<State> stati = dao.loadAllStates(idMap);
		Graphs.addAllVertices(grafo, stati);
		
		//Creo Archi
		List<Confine> confini = dao.getConfini();
		
		//Confinanti con peso
		List<Confinanti> confinanti = dao.getAllArchi(year, confini, shape);
		
		for (Confinanti c : confinanti) {
			
			//Ottengo dati
			State source = idMap.get(c.getId1());
			State target = idMap.get(c.getId2());
			int weight = c.getPesoArco();
			
			if (grafo.containsVertex(source) && grafo.containsVertex(target))
			Graphs.addEdge(grafo, source, target, weight);

		}
		
		//Stampo cose
		System.out.println("GRAFO CREATO!");
		System.out.println("#NODI: "+grafo.vertexSet().size());
		System.out.println("#ARCHI :"+grafo.edgeSet().size());
	}

}
