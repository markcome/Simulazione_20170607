package it.polito.tdp.seriea.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Link extends DefaultWeightedEdge{

	private boolean usato;

	public Link() {
		super();
		this.usato = false;
	}
	
	/**
	 * Ricevo come parametro il valore booleano da assegnare a "this.usato"
	 * @param usato
	 */
	public void setUsato(boolean usato) {
		this.usato = usato;
	}
}
