package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Team(res.getString("team"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Match> listMatch(Season season, List<Team> teams) {
		
		String sql = "SELECT * FROM matches WHERE season = ?" ;
		
		List<Match> result = new ArrayList<Match>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, season.getSeason());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				int h = teams.indexOf(new Team(res.getString("HomeTeam")));
				Team home = teams.get(h);
				
				int a = teams.indexOf(new Team(res.getString("AwayTeam")));
				Team away = teams.get(a);
				
				Match m = new Match(res.getInt("match_id"),
						season, res.getString("Div"),
						res.getDate("Date").toLocalDate(),
						home, away, res.getInt("FTHG"), res.getInt("FTAG"), res.getString("FTR"));
				result.add(m) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;	
		}
	}
	
	}
