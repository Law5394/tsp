package tsp;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DynamicSolver implements TSPSolver{
	
	private String filename;
	private String bestTour;
	private double bestCost;
	private City[] cities;
	private ArrayList<String> permutations = new ArrayList<String>();
	private double[][] costMatrix;
	
	public DynamicSolver(String filename) {
		
		this.filename = filename;
		
	}
	
	public String solve() {
		try {
			scanFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		getCostMatrix();
		
		String initialPerm = getInitialPerm();
		permute(initialPerm, 0, cities.length-2);
		
		findBest();
		String outfile = writeFile();
		
		return outfile;
		
	}
	
	/*
	 * Creates matrix with indices being the distance between city x-1 and city y-1
	 * example: the distance between city 3 and 5 is stored in the costMatrix
	 * at index[2][4]
	 */
	private void getCostMatrix() {
		costMatrix = new double[cities.length][cities.length];
		City cityI, cityJ;
		for(int i = 0; i < cities.length; i++) {
			cityI = cities[i];
			for(int j = 0; j<cities.length;j++) {
				cityJ = cities[j];
				costMatrix[i][j] = pathCost(cityI.getPoint(), cityJ.getPoint());
			}
		}
		
	}
	
	
	/*
	 * generates all permutations with starting and ending cities being city 1
	 * and stores them in permutations
	 */
	private void permute(String tourPerm, int left, int right) {
		
		if(left == right) {
			permutations.add("1"+tourPerm+"1");
			
		}
		
		else {
			for(int i = left; i <= right; i++) {
				tourPerm = swap(tourPerm, left, i);
				permute(tourPerm, left+1, right);
				tourPerm = swap(tourPerm, left, i);
			}
		}
		
	}
	
	//swap method aids permute method
	private String swap(String tour, int left, int i) {
		char[] swapTour = tour.toCharArray();
		
		char temp = swapTour[left];
		swapTour[left] = swapTour[i];
		swapTour[i] = temp;
		tour = new String(swapTour);
		
		return tour;
	}
	
	/*
	 * finds tour cost for each permutation and updates 
	 * bestTour and bestCost as better tour are found
	 */
	private void findBest() {
		
		int city1, city2;
		
		
		String tourPerm = permutations.get(0);
		char[] tour = tourPerm.toCharArray();
		double cost;
		
		//stores initial tour and its cost as bestTour and bestCost to have comparison 
		for(int i=0; i<tour.length - 1; i++) {
			city1 = Character.getNumericValue(tour[i]);
			city2 = Character.getNumericValue(tour[i+1]);
			
			bestCost = costMatrix[(city1-1)][(city2-1)] + bestCost;
			bestTour = tourPerm;
		}
		
		//goes through each permutation finding cost and updating bestCost and bestTour when needed
		for(String perm : permutations) {
			tour = perm.toCharArray();
			cost = 0;
	
				for(int i=0; i<tour.length - 1; i++) {
					city1 = Character.getNumericValue(tour[i]);
					city2 = Character.getNumericValue(tour[i+1]);
				
					cost = costMatrix[(city1-1)][(city2-1)] + cost;
					
					}
				if(cost < bestCost) {
					bestCost = cost;
					bestTour = perm;
				}	
			
		}
		
		
	}
	
	//calculates euclidean distance between two cities
	private double pathCost(Point a, Point b) {
		double distance = (Math.pow((a.getX()-b.getX()), 2)
				+ Math.pow(a.getY()-b.getY(), 2));
		return Math.sqrt(distance);
	}
	
	/*
	 * reads in file creates city object for each
	 *  city and stores them all into a City array called cities
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
		cities = new City[n];
		

		for(int i=0; i<n; i++) {
			cityID = in.nextInt();	
			x = Float.parseFloat(in.next());
			y = Float.parseFloat(in.next());
			cities[i] = new City(cityID, x, y);
			
		}
		in.close();
		
	}
	
	//writes bestTour to output file
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
		writer.println("TYPE: TOUR" );
		writer.println("DIMENSION: " + dimension);
		writer.println("TOUR_SECTION");

		
		for(int i = 0; i<tour.length; i++) {
			if(i != dimension)
				writer.println(tour[i]);
			else
				writer.println("-" + tour[i]);
		}
			
		
		writer.close();
		
		return outfile;
	}
	
	/*
	 * gets the initial permutation by removing city 1
	 * example: 123456 => 23456
	 * this way we dont have to worry about the starting city getting mixed up
	 * when finding all the permutations
	 */
	private String getInitialPerm() {
		int n = cities.length;
		
		char[] perm = new char[n-1];
		
		for(int i=0; i<n-1; i++) 
			perm[i] = (char)(i+2 + '0');
		
		String initialPerm = new String(perm);
	
		return initialPerm;
	}
}

/*
 * city class is defined in BruteForceSolver.java
 * if BruteForceSolver.java is not in the package uncomment this city class
class City{
	
	private int cityID;
	private Point a;
	
	City(int cityID, float x, float y){
		this.cityID = cityID;
		this.a = new Point();
		a.setLocation(x, y);
	}
	
	public Point getPoint() {
		return a;
	}
	
	
	public String toString() {
		return (cityID + ": " + a.getX() + " " + a.getY());
	}
	
}
*/


