package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	//VARIABILI

	private SerieADAO dao;
	
	private List<Match> matches;
	private List<Season> seasons;
	private List<Team> teams;
	
	private DirectedWeightedMultigraph<Team, DefaultWeightedEdge> grafo;
	
	private List<Team> bestRicorsione;
	
	
	/////////////////////////////
	//PUNTO 1 - CREAZIONE GRAFO//
	/////////////////////////////
	
	public Model() {
		this.dao = new SerieADAO();
		this.seasons = new ArrayList<Season>(this.dao.listSeasons());	
	}
	
	public List<Season> getSeasons() {
		return seasons;
	}



	public void loadMatches(Season season) {	
		this.teams = new ArrayList<Team>(this.dao.listTeams());
		this.matches = new LinkedList<Match>(this.dao.listMatch(season, teams));
	}
	
	public boolean creaGrafo() {
		
		//Controllo se ho ottenuto i dati sulle partite
		if (matches == null || matches.isEmpty()){
			return false;
		}
		
		//Inizializzo il grafo
		this.grafo = new DirectedWeightedMultigraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		for(Match m: this.matches) {
			
			Team home = m.getHomeTeam();
			Team away = m.getAwayTeam();
			
			//Aggiungo i vertici
			if(!grafo.vertexSet().contains(home)){
				grafo.addVertex(m.getHomeTeam());
			}
			if(!grafo.vertexSet().contains(away)){
				grafo.addVertex(m.getAwayTeam());
			}
			
			//Aggiungo gli archi
			DefaultWeightedEdge edgeA = grafo.addEdge(home, away); //In un senso
			if(edgeA == null) {
				throw new RuntimeException("nullo");
			}
			DefaultWeightedEdge edgeB = grafo.addEdge(away, home); //Nell'altro
			if(m.getFthg() > m.getFtag()) {
				grafo.setEdgeWeight(edgeA, 1.0);
				grafo.setEdgeWeight(edgeB, -1.0);
			} else if (m.getFthg() < m.getFtag()) {
				grafo.setEdgeWeight(edgeA, -1.0);
				grafo.setEdgeWeight(edgeB, 1.0);
			} else {
				grafo.setEdgeWeight(edgeA, 0.0);
				grafo.setEdgeWeight(edgeB, 0.0);
			}
		}
		return true;
	}
	
	public List<Classifica> getClassifica() {
		
		List<Classifica> classifica = new LinkedList<Classifica>();
		
		for(Team t: grafo.vertexSet()) {
			
			Classifica c = new Classifica(t);
			
			for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(t)) {
				if(grafo.getEdgeWeight(e) > 0) {
					c.vittoria();
				} else if (grafo.getEdgeWeight(e) == 0) {
					c.pareggio();
				}
			}
			
			classifica.add(c);
		}
		Collections.sort(classifica);
		
		return classifica;
	}
	
	////////////////////////
	//PUNTO 2 - RICORSIONE//
	////////////////////////
	
	public List<Team> domino(Team team) {
		
		this.bestRicorsione = new ArrayList<Team>();
		
		List<Team> parziale = new ArrayList<Team>();
		List<Team> successori = new ArrayList<Team>();
		Set<DefaultWeightedEdge> archiDisponibili = grafo.edgeSet();
		
		//Preparo per la ricorsione
		for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(team)) {
			if(grafo.getEdgeWeight(e) < 0){
				successori.add(grafo.getEdgeTarget(e));
			}
		}
		this.recursive(parziale, successori, team, false, archiDisponibili);
		
		return this.bestRicorsione;
	}
	
	/**
	 * 
	 * @param parziale soluzione pariziale che si modifica ad ogni ricorsione
	 * @param successori possibili successori dell'ultimo elemento aggiungo
	 * @param lastTeam ultimo elemento aggiunto
	 * @param deveVincere TRUE se devo cercare un arco +1, FALSE se devo cercare un arco -1
	 * @param archiDisponibili set degli archi del grafo ancora disponibili
	 */
	private void recursive(List<Team> parziale, List<Team> successori, Team lastTeam, boolean deveVincere, Set archiDisponibili) {
		
		if(successori.isEmpty()) {
			if(parziale.size() > this.bestRicorsione.size()) {
				this.bestRicorsione = parziale;
			}
			return;
		}
		
		for(Team t: successori) {
			
			List<Team> successoriNuovi = new ArrayList<Team>();
			boolean deveVincereNuovo = false;
			DefaultWeightedEdge arcoSelezionato = grafo.getEdge(lastTeam, t);
			
			if(deveVincere) {
				for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(t)) {
					if(grafo.getEdgeWeight(e) > 0){
						successoriNuovi.add(grafo.getEdgeTarget(e));
					}
				}
				deveVincereNuovo = false;
			} else {
				for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(t)) {
					if(grafo.getEdgeWeight(e) < 0)
					successoriNuovi.add(grafo.getEdgeTarget(e));
				}
				deveVincereNuovo = true;
			}
			
			parziale.add(t);
			archiDisponibili.remove(arcoSelezionato);
			this.recursive(parziale, successoriNuovi, t, deveVincereNuovo, archiDisponibili);
			archiDisponibili.add(arcoSelezionato);
			parziale.remove(t);
		}
		
	}
}