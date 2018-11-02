package tsp;

public class TSPSolution {

	public static void main(String[] args) {
		
		String fileLocation = "sample.tsp";
		TSPSolver solution = new BruteForceSolver(fileLocation);
		String outputFile = solution.solve();
		
		System.out.println("TSP Solution placed in " + outputFile);
	}
}
