package tsp;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GreedySolver implements TSPSolver {

	private String filename;
	private String bestTour;
	private double bestCost;
	private GreedyCity[] cities;
	private double[][] costMatrix;

	public GreedySolver(String filename) {

		this.filename = filename;
		bestTour = "";
		bestCost = 0;

	}

	public String solve() {
		try {
			scanFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		getCostMatrix();
		nearestNeighbor();

		String outfile = writeFile();

		return outfile;

	}

	/*
	 * Creates matrix with indices being the distance between city x-1 and city y-1
	 * example: the distance between city 3 and 5 is stored in the costMatrix at
	 * index[2][4]
	 */
	private void getCostMatrix() {
		costMatrix = new double[cities.length][cities.length];
		GreedyCity cityI, cityJ;
		for (int i = 0; i < cities.length; i++) {
			cityI = cities[i];
			for (int j = 0; j < cities.length; j++) {
				cityJ = cities[j];
				costMatrix[i][j] = pathCost(cityI.getPoint(), cityJ.getPoint());
			}
		}

	}

	// uses Greedy algorithm to arrive at the best tour
	private void nearestNeighbor() {

		ArrayList<GreedyCity> citiesVisited = new ArrayList<GreedyCity>();
		ArrayList<GreedyCity> citiesUnvisited = new ArrayList<GreedyCity>(Arrays.asList(cities));
		citiesVisited.add(cities[0]);
		GreedyCity currentCity = cities[0];
		citiesUnvisited.remove(cities[0]);
		GreedyCity nextCity = null;
		double closestDistance = 999999999;
		double distance = 0;

		// loops while there are still cities that haven't been visited
		while (!citiesUnvisited.isEmpty()) {
			// loops through each city that hasn't been visited yet to check if its the next
			// closest city
			for (GreedyCity closestCity : citiesUnvisited) {
				distance = costMatrix[currentCity.getCityID() - 1][closestCity.getCityID() - 1];

				if (closestDistance > distance) {
					nextCity = closestCity;
					closestDistance = distance;

				}
			}
			// updates bestCost with distance to
			bestCost += closestDistance;
			// changes currentCity to the next visited city
			currentCity = nextCity;

			// updates citiesVisited and citiesUnvisted
			citiesVisited.add(nextCity);
			closestDistance = 999999999;
			citiesUnvisited.remove(nextCity);
		}
		bestCost += costMatrix[currentCity.getCityID() - 1][0];
		citiesVisited.add(cities[0]);

		for (GreedyCity c : citiesVisited) {
			bestTour = bestTour + c.getCityID();
		}
	}

	// calculates euclidean distance between two cities
	private double pathCost(Point a, Point b) {
		double distance = (Math.pow((a.getX() - b.getX()), 2) + Math.pow(a.getY() - b.getY(), 2));
		return Math.sqrt(distance);
	}

	/*
	 * reads in file creates city object for each city and stores them all into a
	 * City array called cities
	 */
	private void scanFile() throws FileNotFoundException {
		Scanner in = new Scanner(new File(filename));
		int cityID;
		float x, y;
		int n;

		in.nextLine();
		in.nextLine();
		in.nextLine();
		in.next();
		n = in.nextInt();
		in.nextLine();
		in.nextLine();
		in.nextLine();
		cities = new GreedyCity[n];

		for (int i = 0; i < n; i++) {
			cityID = in.nextInt();
			x = Float.parseFloat(in.next());
			y = Float.parseFloat(in.next());
			cities[i] = new GreedyCity(cityID, x, y);

		}
		in.close();

	}

	// writes bestTour to output file
	private String writeFile() {

		char[] tour = bestTour.toCharArray();
		int dimension = (tour.length - 1);
		String outfile = filename.substring(0, filename.indexOf("."));
		outfile = (outfile + ".tour");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outfile);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		writer.println("NAME: " + outfile);
		writer.println("TYPE: TOUR");
		writer.println("DIMENSION: " + dimension);
		writer.println("TOUR_SECTION");

		for (int i = 0; i < tour.length; i++) {
			if (i != dimension)
				writer.println(tour[i]);
			else
				writer.println("-" + tour[i]);
		}

		writer.close();

		return outfile;
	}
}

// GreedyCity object used to store cityID as an int and its (x,y) location in a
// point
class GreedyCity {

	private int cityID;
	private Point a;

	GreedyCity(int cityID, float x, float y) {
		this.cityID = cityID;
		this.a = new Point();
		a.setLocation(x, y);

	}

	public Point getPoint() {
		return a;
	}

	public int getCityID() {
		return cityID;
	}

	public String toString() {
		return (cityID + ": " + a.getX() + " " + a.getY());
	}

}
