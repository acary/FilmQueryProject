package com.skilldistillery.filmquery.app;

import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
//		app.test();
//		app.testActor();
//		app.testFilmActors();
		app.launch();
	}

	private void test() {
		Film film = db.findFilmById(1);
		System.out.println(film);
	}

	private void testActor() {
		Actor actor = db.findActorById(1);
		System.out.println(actor);
	}

	private void testFilmActors() {
		List<Actor> actors = db.findActorsByFilmId(2);
		for (Actor actor : actors) {
			System.out.println(actor);
		}
	}

	private void launch() {
		Scanner input = new Scanner(System.in);

		startUserInterface(input);

		input.close();
	}

	private void startUserInterface(Scanner input) {
		int selection = 0;
		boolean patron = true;
		
		do {
			System.out.println("\n*********** Welcome to the Virtual Cinema! ***********");
			System.out.println("Please make a selection:\n");
			System.out.println("1: Look up film by ID");
			System.out.println("2: Look up film by search keyword");
			System.out.println("3: Exit");
			for(;;) {
				try {
					selection = input.nextInt();
					switch(selection) {
					case 1:
						System.out.println("Enter a film ID:");
						int filmId = input.nextInt();
						Film film = db.findFilmById(filmId);
						if (film == null) {
							System.out.println("No film found by id: " + filmId);
						}
						else {
							System.out.println(film);
							System.out.println("\nOptions:");
							System.out.println("1: Return to the main menu");
							System.out.println("2: View all film details");
							int subSelection = input.nextInt();
							switch (subSelection) {
							case 1:
								break;
							case 2:
								System.out.println("\nViewing all details for film: " + filmId + "\n");
								Film filmDetails = db.findFilmDetailsById(filmId);
								System.out.println(filmDetails.details());
								break;
							default:
								System.out.println("Please make a valid selection.");
								break;
							}
						}
						break;
					case 2:
						System.out.println("Enter a search keyword:");
						String searchTerm = input.next();
						List<Film> filmByKeyword = db.findFilmByKeyword(searchTerm);
						if (filmByKeyword == null) {
							System.out.println("No film found by keyword: " + searchTerm);
						}
						else {
							for (Film filmByKey : filmByKeyword) {
								System.out.println(filmByKey);
							}
						}
						break;
					case 3:
						System.out.println("Thank you for visiting! Goodbye.\n");
						System.exit(0);
					default:
						System.out.println("Please make a valid selection.");
					}
					break;
				}
				catch(Exception e) {
					System.out.println("Please provide valid input. Restart application and try again.");
					patron = false;
					break;
				}
			}
		} while (patron);
		
	}

}
