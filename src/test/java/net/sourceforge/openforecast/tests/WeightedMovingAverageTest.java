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

package net.sourceforge.openforecast.tests;


import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.models.WeightedMovingAverageModel;


/**
 * Implements a series of tests to test the WeightedMovingAverageModel class.
 * @author Steven R. Gould
 * @since 0.4
 */
public class WeightedMovingAverageTest extends OpenForecastTestCase
{
    public WeightedMovingAverageTest( String name )
    {
        super(name);
    }
    
    /**
     * A simple weighted moving average test where the observed data is
     * constant. This should result in the same constant forecast.
     */
    public void testConstantWeightedMovingAverage()
    {
        DataSet observedData = new DataSet();
        DataPoint dp;
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 1.0 );
        observedData.add( dp );
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 2.0 );
        observedData.add( dp );
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 3.0 );
        observedData.add( dp );
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 4.0 );
        observedData.add( dp );
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 5.0 );
        observedData.add( dp );
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 6.0 );
        observedData.add( dp );
        
        double weights[] = { 0.4, 0.3, 0.2, 0.1 };
        ForecastingModel model = new WeightedMovingAverageModel( weights );
        
        // Initialize the model
        model.init( observedData );
        
        // Create a data set for forecasting
        DataSet fcValues = new DataSet();
        
        dp = new Observation( 0.0 );
        dp.setIndependentValue( "t", 7.0 );
        fcValues.add( dp );
        
        dp = new Observation( 0.0 );
        dp.setIndependentValue( "t", 8.0 );
        fcValues.add( dp );
        
        dp = new Observation( 0.0 );
        dp.setIndependentValue( "t", 9.0 );
        fcValues.add( dp );
        
        // Get forecast values
        DataSet results = model.forecast( fcValues );
        
        // These are the expected results
        double expectedResult[] = { 5.0, 5.0, 5.0 };
        
        checkResults( results, expectedResult );
    }
    
    /**
     * A somewhat more realistic test where the results are known (and were
     * calculated independently of the model). Validates that the
     * WeightedMovingAverageModel returns the expected set of results for the
     * given inputs. This is still not a great test, since the weights are
     * all equal - at 0.25 - so it is equivalent to a four period moving
     * average. Could do with some better test data here.
     */
    public void testWeightedMovingAverage()
    {
        DataSet observedData = new DataSet();
        DataPoint dp;
        
        dp = new Observation( 4.0 );
        dp.setIndependentValue( "t", 101.0 );
        observedData.add( dp );
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 102.0 );
        observedData.add( dp );
        
        dp = new Observation( 6.0 );
        dp.setIndependentValue( "t", 103.0 );
        observedData.add( dp );
        
        dp = new Observation( 9.0 );
        dp.setIndependentValue( "t", 104.0 );
        observedData.add( dp );
        
        dp = new Observation( 3.0 );
        dp.setIndependentValue( "t", 105.0 );
        observedData.add( dp );
        
        dp = new Observation( 7.0 );
        dp.setIndependentValue( "t", 106.0 );
        observedData.add( dp );
        
        dp = new Observation( 5.0 );
        dp.setIndependentValue( "t", 107.0 );
        observedData.add( dp );
        
        dp = new Observation( 6.0 );
        dp.setIndependentValue( "t", 108.0 );
        observedData.add( dp );
        
        dp = new Observation( 7.0 );
        dp.setIndependentValue( "t", 109.0 );
        observedData.add( dp );
        
        dp = new Observation( 9.0 );
        dp.setIndependentValue( "t", 110.0 );
        observedData.add( dp );
        
        dp = new Observation( 3.0 );
        dp.setIndependentValue( "t", 111.0 );
        observedData.add( dp );
        
        dp = new Observation( 4.0 );
        dp.setIndependentValue( "t", 112.0 );
        observedData.add( dp );
        
        dp = new Observation( 6.0 );
        dp.setIndependentValue( "t", 113.0 );
        observedData.add( dp );
        
        dp = new Observation( 7.0 );
        dp.setIndependentValue( "t", 114.0 );
        observedData.add( dp );
        
        dp = new Observation( 7.0 );
        dp.setIndependentValue( "t", 115.0 );
        observedData.add( dp );
        
        dp = new Observation( 4.0 );
        dp.setIndependentValue( "t", 116.0 );
        observedData.add( dp );
        
        double weights[] = { 0.25, 0.25, 0.25, 0.25 };
        
        ForecastingModel model = new WeightedMovingAverageModel( weights );
        
        // Initialize the model
        model.init( observedData );
        
        // Create a data set for forecasting
        DataSet fcValues = new DataSet();
        
        for ( int t=105; t<=120; t ++ )
            {
                dp = new Observation( 0.0 );
                dp.setIndependentValue( "t", t );
                fcValues.add( dp );
            }
        
        // Get forecast values
        DataSet results = model.forecast( fcValues );
        
        // These are the expected results
        double expectedResult[] = { 6.00, 5.75, 6.25, 6.00, 5.25,
                                    6.25, 6.75, 6.25, 5.75, 5.50,
                                    5.00, 6.00, 6.00, 6.00, 5.75,
                                    5.4375 };
        
        checkResults( results, expectedResult );
    }
    
    // TODO: Implement a testForecaster (need suitable data set)
}
// Local Variables:
// tab-width: 4
// End:
