package com.skilldistillery.filmquery.database;

import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public interface DatabaseAccessor {
	public Film findFilmById(int filmId);

	public Film findFilmDetailsById(int filmId);

	public Actor findActorById(int actorId);

	public List<Actor> findActorsByFilmId(int filmId);

	public List<Film> findFilmByKeyword(String keyword);

	public Actor createActor(Actor actor);

	public boolean saveActor(Actor actor);

	public boolean deleteActor(Actor actor);

	public Film createFilm(Film film);

	public boolean deleteFilm(Film film);

	public boolean updateFilm(Film film);
}
