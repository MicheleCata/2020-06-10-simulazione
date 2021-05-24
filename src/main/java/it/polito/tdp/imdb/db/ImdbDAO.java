package it.polito.tdp.imdb.db;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenze;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(Map<Integer, Actor> idMap){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				idMap.put(actor.getId(), actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> listAllGeneri() {
		String sql = "SELECT DISTINCT(genre) "
				+ "FROM movies_genres";
		
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				result.add(res.getString("genre"));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Actor> getVertici(String genere,Map<Integer, Actor> idMap ) {
		String sql = "SELECT DISTINCT r.actor_id as id "
				+ "FROM movies m, roles r, movies_genres mg "
				+ "where m.`id`=r.`movie_id` AND m.id = mg.`movie_id` AND mg.genre = ?";
		
		List<Actor> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(idMap.get(res.getInt("id")));
			}
		
			conn.close();
			return result;
		
		} catch (SQLException e) {
		e.printStackTrace();
		return null;
		}
		
	}
	
	public List<Adiacenze> getAdiacenze(String genere, Map<Integer, Actor> idMap) {
		String sql = "SELECT  r1.actor_id as a1, r2.actor_id as a2, Count(DISTINCT r1.movie_id) as peso "
				+ "FROM roles r1, roles r2, movies m, movies_genres mg "
				+ "where r1.`movie_id`= r2.`movie_id` AND r1.actor_id>r2.actor_id AND r1.`movie_id`=m.id "
				+ "AND m.id = mg.`movie_id` AND mg.`genre`=? "
				+ "group by r1.actor_id, r2.actor_id";
		
		List<Adiacenze> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Actor a1= idMap.get(res.getInt("a1"));
				Actor a2= idMap.get(res.getInt("a2"));
				
				if (a1!=null && a2!=null)
					result.add(new Adiacenze(a1,a2,res.getInt("peso")));
			}
			conn.close();
			return result;
		
		} catch (SQLException e) {
		e.printStackTrace();
		return null;
		}
	}
	
	
	
	
	
	
}
