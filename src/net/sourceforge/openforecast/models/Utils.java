//
//  OpenForecast - open source, general-purpose forecasting package.
//  Copyright (C) 2002-2011  Steven R. Gould
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package net.sourceforge.openforecast.models;

import java.util.ArrayList;


/**
 * This class implements a variety of helper methods that typically help out
 * with the computational components of the models. These static methods are
 * not really suitable for general purpose use, and are therefore not made
 * public.
 * @author Steven R. Gould
 */
final class Utils
{
    /**
     * A private default constructor to prevent instantiation of this class.
     */
    private Utils()
    {
    }

    /**
     * Implements a Gaussian elimination on the given matrix. The last column
     * being the Right Hand Side values. All rows in the matrix are used.
     * @param a the matrix to be solved.
     */
    static double[] GaussElimination( double a[][] )
    {
        int n = a.length;

        return GaussElimination( n, a );
    }

    /**
     * Implements a Gaussian elimination on the given matrix. The matrix
     * <code>a</code> should be n rows by n+1 columns. Column <code>n+1</code>
     * being the Right Hand Side values.
     * @param n the number of rows in the matrix.
     * @param a the matrix to be solved.
     */
    static double[] GaussElimination( int n, double a[][] )
    {
        // Forward elimination
        for ( int k=0; k<n-1; k++ )
            {
                for ( int i=k+1; i<n; i++ )
                    {
                        double qt = a[i][k] / a[k][k];
                        for ( int j=k+1; j<n+1; j++ )
                            a[i][j] -= qt * a[k][j];

                        a[i][k] = 0.0;
                    }
            }

        /*
        // DEBUG
        for ( int i=0; i<n; i++ )
            for ( int j=0; j<n+1; j++ )
                System.out.println( "After forward elimination, a["+i+"]["+j+"]="+a[i][j] );
        */

        double x[] = new double[n];

        // Back-substitution
        x[n-1] = a[n-1][n] / a[n-1][n-1];
        for ( int k=n-2; k>=0; k-- )
            {
                double sum = 0.0;
                for ( int j=k+1; j<n; j++ )
                    sum += a[k][j]*x[j];
                
                x[k] = ( a[k][n] - sum ) / a[k][k];
            }

        /*
        // DEBUG
        for ( int k=0; k<n; k++ )
            System.out.println( "After back-substitution, x["+k+"]="+x[k] );
        */

        return x;
    }

    /**
     * Calculates and returns the seasonal indices for the given set of
     * observations and the given seasonal cycle. To determine any seasonality
     * at least 2 full seasons of data are required. If the seasonal cycle is
     * say, 12 months, then at least 2 years of data is required - more is
     * preferred and recommended if available. If the "seasonal cycle" is say,
     * 7 days then at least 2 weeks of data is required. Again, more is both
     * recommended and preferred for better - more reliable - results.
     *
     * For more information on the detailed approach implemented here, refer to
     * "Business Statistics" (4th Ed.) by Daniel and Terrell
     * (ISBN 0-395-35651-2), section 13.7 "Measuring Seasonal Variation" (pp.
     * 615-621).
     * @param observations the observed values of the dependent variable over
     * past seasons.
     * @param seasonalCycle the number of observations in a "season". Note that
     * this could be 12 months in a year, 4 quarters in a year, but also
     * something like 7 days in a week (where the "seasonality" of interest is
     * weekly by day).
     * @throws IllegalArgumentException if there are insufficient observations
     * available to determine seasonal indices. Basically the number of
     * observations should be at least twice the number of observations in a
     * seasonal cycle. For example, if the observations are monthly and the
     * seasonal cycle is 12 months, then there should be at least 24
     * observations in order to determine the seasonal indices. More is
     * recommended. In this case, 4-5 years or more of observations (depending
     * on the data) would be a good starting point.
     */
    static double[] calculateSeasonalIndices( double observation[],
                                              int seasonalCycle )
    {
        int numberOfCycles = observation.length / seasonalCycle;
        if ( numberOfCycles < 2 )
            throw new IllegalArgumentException("Too few observations. Need at least "+seasonalCycle*2+" observations - preferably more - to calculate the seasonal indices");

        if ( seasonalCycle % 2 != 0 )
            throw new IllegalArgumentException("seasonalCycle must be even - for now at least");

        if ( observation.length % seasonalCycle > 0 )
            numberOfCycles++;

        double seasonalIndex[] = new double[ seasonalCycle ];


        // Calculate "Ratio to Moving Average" for each period and cycle
        ArrayList<Double> ratioToMovingAverage = new ArrayList<Double>( observation.length );

        double movingTotal = 0.0;
        for ( int t=0; t < seasonalCycle; t++ )
            {
                movingTotal += observation[t];
                ratioToMovingAverage.add(t,null);
            }
                
        int numberOfObservations = observation.length;
        for ( int t=seasonalCycle; t < numberOfObservations; t++ )
            {
                double movingAverage = movingTotal / (seasonalCycle*2);

                // For efficiency (to avoid recalculating sums), we
                // drop the oldest observation, and add the current one
                movingTotal -= observation[t-seasonalCycle];
                movingTotal += observation[t];

                movingAverage += movingTotal / (seasonalCycle*2);

                // The "+1" below is to correctly handle an odd number of
                // seasons within a cycle - e.g. 7 days in a week.
                int period = t - (seasonalCycle+1)/2;
                ratioToMovingAverage.add(period,
                            new Double(observation[period]/movingAverage));
            }

        // if more than 4 cycles, then drop min/max outliers
        // else we'll just average what we have
        boolean dropOutliers = (numberOfCycles > 4);

        // Calculate mean indices
        double sumIndices = 0.0;
        for ( int season=0; season<seasonalCycle; season++ )
            {
                int count = 0;
                double sum = 0.0;
                double minIndex = Double.POSITIVE_INFINITY;
                double maxIndex = Double.NEGATIVE_INFINITY;
                for ( int cycle=0; cycle<numberOfCycles; cycle++ )
                    {
                        int t = season + cycle*seasonalCycle;
                        if ( ratioToMovingAverage.get(t) != null )
                            {
                                double currentIndex
                                    = ((Double)ratioToMovingAverage.get(t)).doubleValue();

                                if ( dropOutliers )
                                    {
                                        if ( currentIndex < minIndex )
                                            minIndex = currentIndex;

                                        if ( currentIndex > maxIndex )
                                            maxIndex = currentIndex;
                                    }

                                sum += currentIndex;
                                count++;
                            }
                    }
                
                if ( dropOutliers )
                    {
                        sum -= minIndex;
                        count--;
                        
                        sum -= maxIndex;
                        count--;
                    }

                seasonalIndex[season] = sum / count;
                
                sumIndices += seasonalIndex[season];
            }
          
        // Scale indices to sum to seasonalCycle
        for ( int season=0; season<seasonalCycle; season++ )
            seasonalIndex[season] *= (seasonalCycle/sumIndices);

        return seasonalIndex;
    }
}
// Local Variables:
// tab-width: 4
// End:
