package it.polito.tdp.seriea.model;

public class Classifica implements Comparable<Classifica>{
	
	private Team team;
	private int punti;
	
	public Classifica(Team team) {
		this.team = team;
		this.punti = 0;
	}

	public Team getTeam() {
		return team;
	}

	public int getPunti() {
		return punti;
	}
	
	public void vittoria() {
		this.punti += 3;
	}
	
	public void pareggio() {
		this.punti += 1;
	}

	@Override
	public String toString() {
		return String.format("%-10s %s", team, punti);
	}

	@Override
	public int compareTo(Classifica o) {
		return o.getPunti() - this.punti;
	}
	
	

}
