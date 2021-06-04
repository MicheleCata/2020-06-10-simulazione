package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulator {
	
	// Stato del sistema
	Graph<Actor, DefaultWeightedEdge> grafo;
	//INPUT
	private int days;
	//OUTPUT
	private int pause;
	private Map<Integer, Actor> attoriIntervistati;
	
	List<Actor> attoriDisponibili;
	
	//nel cotruttore inizializzo i valori di input
	public Simulator(int n, Graph<Actor,DefaultWeightedEdge> grafo) {
		this.days=n;
		this.grafo=grafo;
	}
	// inizializzo le varie strutture dati e imposto lo stato iniziale
	public void init() {
		attoriIntervistati = new HashMap<>();
		pause = 0;
		this.attoriDisponibili= new ArrayList<>(grafo.vertexSet());
	}
	
	public void run() {
		
		for (int i=1; i<=this.days; i++) {
			
			Random casuale = new Random();
			
			if (i==1 || !this.attoriIntervistati.containsKey(i-1)) {
				// se siamo al primo giorno di interviste o il giorno dopo una pausa 
				// si sceglie casualmente
				Actor attoreScelto =  attoriDisponibili.get(casuale.nextInt(attoriDisponibili.size()));
				attoriIntervistati.put(i, attoreScelto);
				attoriDisponibili.remove(attoreScelto);
			}
			
			if (i>=3 && attoriIntervistati.containsKey(i-1) &&  attoriIntervistati.containsKey(i-2)
					&& attoriIntervistati.get(i-1).gender.equals(attoriIntervistati.get(i-2).gender)) {
				// per due giorni di fila il produttore ha intervistato attori dello stesso genere 
				// -> 90% di probabilità che ci sia una pausa
				if (casuale.nextFloat()<=0.9) {
					pause++;
				}
			}
			// il produttore può farsi consigliare dall'ultimo intervistato
			if (casuale.nextFloat()<=0.6) {
				Actor attoreScelto =  attoriDisponibili.get(casuale.nextInt(attoriDisponibili.size()));
				attoriIntervistati.put(i, attoreScelto);
				attoriDisponibili.remove(attoreScelto);
			}
			else {
				Actor ultimo = attoriIntervistati.get(i-1);
				Actor consigliato = getConsigliato(ultimo);
				
				if (consigliato == null || (!attoriDisponibili.contains(consigliato))) {
					// se non c'è un vicino di grado massimo oppure è stato già intervistato
					// scelgo casualmente
					Actor attoreScelto =  attoriDisponibili.get(casuale.nextInt(attoriDisponibili.size()));
					attoriIntervistati.put(i, attoreScelto);
					attoriDisponibili.remove(attoreScelto);
				}
				else {
					attoriIntervistati.put(i, consigliato);
					attoriDisponibili.remove(consigliato);
				}
			}
		}
		
		
	}
	
	public int getPause() {
		return pause;
	}
	
	public Collection <Actor> getAttoriIntervistati() {
		return attoriIntervistati.values();
	}
	
	private Actor getConsigliato(Actor ultimo) {
		Actor consigliato = null;
		int grado=0;
		
		for (Actor vicino: Graphs.neighborListOf(grafo, ultimo)) {
			DefaultWeightedEdge e = grafo.getEdge(ultimo, vicino);
			if (grafo.getEdgeWeight(e)>grado) {
				consigliato =vicino;
				grado = (int) grafo.getEdgeWeight(e);
			}
		
		}
		return consigliato;
	}
	

}
