package shmarovfedor.areaplanning.background;

import javax.swing.SwingWorker;

import shmarovfedor.areaplanning.model.BuildingManager;
import shmarovfedor.areaplanning.model.SolutionManager;
import shmarovfedor.areaplanning.solver.OptimizationManager;

// TODO: Auto-generated Javadoc
/**
 * The Class BackgroundWorker.
 */
public class BackgroundWorker  extends SwingWorker<String, Object>{

	private static boolean binarySearch = true;
	
	public static boolean isBinarySearch() {
		return binarySearch;
	}

	public static void setBinarySearch(boolean binarySearch) {
		BackgroundWorker.binarySearch = binarySearch;
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected String doInBackground() throws Exception {
		OptimizationManager.setModel();
		if (binarySearch) {
			OptimizationManager.optimize();
			double lowerBound = SolutionManager.getLowerBound();
			double upperBound = SolutionManager.getObjectiveUpperBound();
			OptimizationManager.dispose();
			while((upperBound - lowerBound) >= BuildingManager.getPrecision())
			{
				double bound = (upperBound + lowerBound) / 2;
				SolutionManager.setLowerBound(lowerBound);
				SolutionManager.setUpperBound(upperBound);
				SolutionManager.setCurrentBound(bound);
				
				OptimizationManager.setModel();
				OptimizationManager.setLowerBound(bound);
				OptimizationManager.optimize();
				if (OptimizationManager.isCorrectTermination()) lowerBound = bound; else upperBound = bound;
				if (OptimizationManager.isExecutionTermination()) {
					OptimizationManager.terminateExecution();
					OptimizationManager.dispose();
					break;
				}
			}
		} else {
			OptimizationManager.removeLowerBound();
			OptimizationManager.setLowerBound(SolutionManager.getLowerBound());
			OptimizationManager.optimize();
			if (!OptimizationManager.isExecutionTermination()) OptimizationManager.terminateExecution();
			OptimizationManager.dispose();
		}
		
		if (!OptimizationManager.isExecutionTermination()) {
			OptimizationManager.terminateExecution();
			OptimizationManager.dispose();
		}

		return null;
	}

}
