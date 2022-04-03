package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {

	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=US/Mountain";

	private String user = "student";
	private String pass = "student";

	public String findFilmCategory(int filmId) {
		String category = null;
		String sqltxt;
		sqltxt = "SELECT c.name" + " FROM film_category fc" + " JOIN category c ON fc.category_id = c.id"
				+ " WHERE fc.film_id = ?";

		try (Connection conn = DriverManager.getConnection(URL, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sqltxt)) {
			stmt.setInt(1, filmId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					category = rs.getString(1);
				}
				return category;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return category;
	}

	public String findLangageByLanguageId(int langId) {
		String result = null;
		try {
			String sqltxt;
			sqltxt = "SELECT l.name" + " FROM film f" + " JOIN language l ON l.id = ?";
			Connection conn = DriverManager.getConnection(URL, user, pass);
			PreparedStatement s = conn.prepareStatement(sqltxt);
			s.setInt(1, langId);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
			}
			rs.close();
			s.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		return result;
	}

	public List<Actor> findActorsByFilmId(int filmId) {
		List<Actor> actors = new ArrayList<>();
		try {
			String sqltxt;
			sqltxt = "SELECT a.id, a.first_name, a.last_name, film.id" + " FROM film_actor f"
					+ " JOIN actor a ON f.actor_id = a.id" + " JOIN film ON f.film_id = film.id" + " WHERE film.id = ?";

			Connection conn = DriverManager.getConnection(URL, user, pass);
			PreparedStatement s = conn.prepareStatement(sqltxt);
			s.setInt(1, filmId);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				actors.add(new Actor(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name")));
			}
			rs.close();
			s.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		return actors;
	}

	public Actor findActorById(int actorId) {
		Actor actor = null;
		String sqltxt;
		sqltxt = "SELECT * FROM actor WHERE id = ?";

		try (Connection conn = DriverManager.getConnection(URL, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sqltxt)) {
			stmt.setInt(1, actorId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					actor = new Actor(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"));
				}
				return actor;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return actor;
	}

	@Override
	public Film findFilmById(int filmId) {
		Film film = null;
		String sqltxt;
		sqltxt = "SELECT * FROM film WHERE id = ?";

		try (Connection conn = DriverManager.getConnection(URL, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sqltxt)) {
			stmt.setInt(1, filmId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					film = new Film();
					film.setId(rs.getInt("id"));
					film.setTitle(rs.getString("title"));
					film.setDescription(rs.getString("description"));
					film.setReleaseYear(rs.getInt("release_year"));
					film.setLanguage(findLangageByLanguageId(rs.getInt("language_id")));
					film.setLanguageId(rs.getString("language_id"));
					film.setRentalDuration(rs.getInt("rental_duration"));
					film.setRentalRate(rs.getInt("rental_rate"));
					film.setLength(rs.getInt("length"));
					film.setReplacementCost(rs.getInt("replacement_cost"));
					film.setRating(rs.getString("rating"));
					film.setSpecialFeatures(rs.getString("special_features"));
					film.setActors(findActorsByFilmId(rs.getInt("id")));
					film.setCategory(findFilmCategory(rs.getInt("id")));
				}
				return film;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return film;
	}

	@Override
	public List<Film> findFilmByKeyword(String keyword) {
		List<Film> films = new ArrayList<>();
		Film film = null;
		String sqltxt;
		sqltxt = "SELECT * FROM film WHERE description LIKE ? OR title LIKE ?";

		try (Connection conn = DriverManager.getConnection(URL, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sqltxt)) {
			stmt.setString(1, "%" + keyword + "%");
			stmt.setString(2, "%" + keyword + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					film = new Film();
					film.setId(rs.getInt("id"));
					film.setTitle(rs.getString("title"));
					film.setDescription(rs.getString("description"));
					film.setReleaseYear(rs.getInt("release_year"));
					film.setLanguage(findLangageByLanguageId(rs.getInt("language_id")));
					film.setLanguageId(rs.getString("language_id"));
					film.setRentalDuration(rs.getInt("rental_duration"));
					film.setRentalRate(rs.getInt("rental_rate"));
					film.setLength(rs.getInt("length"));
					film.setReplacementCost(rs.getInt("replacement_cost"));
					film.setRating(rs.getString("rating"));
					film.setSpecialFeatures(rs.getString("special_features"));
					film.setActors(findActorsByFilmId(rs.getInt("id")));
					film.setCategory(findFilmCategory(rs.getInt("id")));
					films.add(film);
				}
				return films;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return films;
	}
	
	@Override
	public Film findFilmDetailsById(int filmId) {
		Film film = null;
		String sqltxt;
		sqltxt = "SELECT * FROM film WHERE id = ?";

		try (Connection conn = DriverManager.getConnection(URL, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sqltxt)) {
			stmt.setInt(1, filmId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					film = new Film();
					film.setId(rs.getInt("id"));
					film.setTitle(rs.getString("title"));
					film.setDescription(rs.getString("description"));
					film.setReleaseYear(rs.getInt("release_year"));
					film.setLanguage(findLangageByLanguageId(rs.getInt("language_id")));
					film.setLanguageId(rs.getString("language_id"));
					film.setRentalDuration(rs.getInt("rental_duration"));
					film.setRentalRate(rs.getInt("rental_rate"));
					film.setLength(rs.getInt("length"));
					film.setReplacementCost(rs.getInt("replacement_cost"));
					film.setRating(rs.getString("rating"));
					film.setSpecialFeatures(rs.getString("special_features"));
					film.setActors(findActorsByFilmId(rs.getInt("id")));
					film.setCategory(findFilmCategory(rs.getInt("id")));
				}
				return film;
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
		return film;
	}

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Driver not found.");
			throw new RuntimeException("Unable to load MySQL driver class");
		}
	}

}
