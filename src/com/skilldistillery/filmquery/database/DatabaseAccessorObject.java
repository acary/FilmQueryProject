package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {

	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=US/Mountain";

	private String user = "student";
	private String pass = "student";

	/***** UPDATE FILM *****/
	public boolean updateFilm(Film film) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "UPDATE film SET title=? " + " WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, film.getTitle());
			stmt.setLong(2, film.getId());
			int updateCount = stmt.executeUpdate();
			if (updateCount == 1) {
				conn.commit(); // COMMIT TRANSACTION
			}
			stmt.close();
			conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} // ROLLBACK TRANSACTION ON ERROR
				catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			return false;
		}
		return true;
	}
	
	/***** DELETE FILM *****/
	public boolean deleteFilm(Film film) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "DELETE FROM film WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, film.getId());
			int updateCount = stmt.executeUpdate();
			conn.commit(); // COMMIT TRANSACTION
			stmt.close();
			conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			return false;
		}
		return true;
	}
	
	/***** CREATE FILM *****/
	public Film createFilm(Film film) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "INSERT INTO film (title, language_id) " + " VALUES (?, 1)";
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, film.getTitle());
			int updateCount = stmt.executeUpdate();
			if (updateCount == 1) {
				ResultSet keys = stmt.getGeneratedKeys();
				if (keys.next()) {
					int newFilmId = keys.getInt(1);
					film.setId(newFilmId);
					System.out.println("Added new film with ID: " + film.getId());
				}
				keys.close();
			} else {
				film = null;
			}
			conn.commit(); // COMMIT TRANSACTION
			stmt.close();
			conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			throw new RuntimeException("Error inserting film " + film);
		}
		return film;
	}

	/***** CREATE ACTOR *****/
	public Actor createActor(Actor actor) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "INSERT INTO actor (first_name, last_name) " + " VALUES (?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, actor.getFirstName());
			stmt.setString(2, actor.getLastName());
			int updateCount = stmt.executeUpdate();
			if (updateCount == 1) {
				ResultSet keys = stmt.getGeneratedKeys();
				if (keys.next()) {
					int newActorId = keys.getInt(1);
					actor.setId(newActorId);
					if (actor.getFilms() != null && actor.getFilms().size() > 0) {

						sql = "INSERT INTO film_actor (film_id, actor_id) VALUES (?,?)";
						stmt = conn.prepareStatement(sql);
						for (Film film : actor.getFilms()) {
							stmt.setInt(1, film.getId());
							stmt.setInt(2, newActorId);
							updateCount = stmt.executeUpdate();
						}
					}
				}
				keys.close();
			} else {
				actor = null;
			}
			conn.commit(); // COMMIT TRANSACTION
			stmt.close();
			conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			throw new RuntimeException("Error inserting actor " + actor);
		}
		return actor;
	}

	/***** SAVE ACTOR *****/
	public boolean saveActor(Actor actor) {
		Connection conn = null;
		try {
			/*
			 * Assume all except actor's id (PK) may have changed update actor's: first
			 * name, last name, current list of films in the database
			 */
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "UPDATE actor SET first_name=?, last_name=? " + " WHERE id=?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, actor.getFirstName());
			stmt.setString(2, actor.getLastName());
			stmt.setInt(3, actor.getId());
			int updateCount = stmt.executeUpdate();
			if (updateCount == 1) {

				// Replace actor's film list
				sql = "DELETE FROM film_actor WHERE actor_id = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, actor.getId());
				updateCount = stmt.executeUpdate();

				sql = "INSERT INTO film_actor (film_id, actor_id) VALUES (?,?)";
				stmt = conn.prepareStatement(sql);
				for (Film film : actor.getFilms()) {
					stmt.setInt(1, film.getId());
					stmt.setInt(2, actor.getId());
					updateCount = stmt.executeUpdate();
				}
				conn.commit(); // COMMIT TRANSACTION
				stmt.close();
				conn.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} // ROLLBACK TRANSACTION ON ERROR
				catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			return false;
		}
		return true;
	}

	/***** DELETE ACTOR *****/
	public boolean deleteActor(Actor actor) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "DELETE FROM film_actor WHERE actor_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actor.getId());
			int updateCount = stmt.executeUpdate();

			sql = "DELETE FROM actor WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actor.getId());
			updateCount = stmt.executeUpdate();
			conn.commit(); // COMMIT TRANSACTION
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			return false;
		}
		return true;
	}

	public List<Film> findFilmsByActorId(int actorId) {
		List<Film> films = new ArrayList<>();
		try {
			String sqltxt;
			sqltxt = "SELECT film.id" + " FROM actor JOIN film_actor fa ON actor.id = fa.actor_id"
					+ "           JOIN film          ON film.id = fa.film_id" + " WHERE actor.id = ?";

			Connection conn = DriverManager.getConnection(URL, user, pass);
			PreparedStatement s = conn.prepareStatement(sqltxt);
			s.setInt(1, actorId);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				films.add(new Film(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
						rs.getInt("release_year"), rs.getString("rating")));
			}
			rs.close();
			s.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		return films;
	}

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
