package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private SimpleWeightedGraph<Actor, DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map<Integer, Actor> idMap;
	
	private Simulator sim;
	
	public Model() {
		dao = new ImdbDAO();
		idMap = new HashMap<Integer,Actor>();
		dao.listAllActors(idMap);
	}
	
	public List<String> getGeneri() {
		return dao.listAllGeneri();
	}
	
	public void creaGrafo(String genere) {
		grafo = new SimpleWeightedGraph<Actor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, dao.getVertici(genere, idMap));
		
		for (Adiacenze a: dao.getAdiacenze(genere, idMap)) {
			if (this.grafo.containsVertex(a.getA1()) && this.grafo.containsVertex(a.getA2())) {
				Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getNumFilm());
			}
		}
		
		System.out.format("Grafo creato con %d vertici e %d archi\n",
 				this.grafo.vertexSet().size(), this.grafo.edgeSet().size()); 
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Actor> getAttori(){
		List<Actor> attori = new ArrayList<>(grafo.vertexSet());
		Collections.sort(attori);
		return attori;
	}

	public Graph<Actor,DefaultWeightedEdge> getGrafo() {
		
		return this.grafo;
	}
	
	public List<Actor> getConnessioni(Actor a){
		ConnectivityInspector<Actor,DefaultWeightedEdge> ci = new ConnectivityInspector<Actor,DefaultWeightedEdge>(grafo);
		List<Actor> attoriSimili = new ArrayList<>(ci.connectedSetOf(a));
		attoriSimili.remove(a);
		Collections.sort(attoriSimili);
		
		return attoriSimili;
	}
	
	public void simula(int n) {
		sim = new Simulator(n,grafo);
		sim.init();
		sim.run();
	}
	
	public Collection<Actor> getAttoriIntervistati(){
		if (sim==null) 
			return null;
		return sim.getAttoriIntervistati();
	}
	
	public Integer getPause() {
		if (sim==null) 
			return null;
		return sim.getPause();
	}

}
