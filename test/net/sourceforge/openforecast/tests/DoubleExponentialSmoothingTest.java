//
//  OpenForecast - open source, general-purpose forecasting package.
//  Copyright (C) 2004-2011  Steven R. Gould
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
import net.sourceforge.openforecast.models.DoubleExponentialSmoothingModel;


/**
 * Tests the implementation of the Double Exponential Smoothing forecasting
 * model.
 * @author Steven R. Gould
 * @since 0.4
 */
public class DoubleExponentialSmoothingTest extends OpenForecastTestCase
{
    /**
     * The amount of error in the forecast values where the forecast is
     * considered "correct" by the test.
     */
    private static double TOLERANCE = 0.005;
    
    /**
     * The amount of error/tolerance in the Mean Squared Error where the
     * forecast is still considered "correct" by the test.
     */
    private static double MSE_TOLERANCE = 0.1;
    
    /**
     * Constructor required by the JUnit framework.
     */
    public DoubleExponentialSmoothingTest( String name )
    {
        super(name);
    }
    
    /**
     * A simple exponential smoothing test where the observed data is
     * constant. This should result in the same constant forecast for
     * any value of the smoothing constant.
     */
    public void testConstantDoubleExponentialSmoothing()
    {
        final int NUMBER_OF_OBSERVATIONS = 25;
        
        // Set up some constant observed values
        DataSet observedData = new DataSet();
        DataPoint dp;
        
        for ( int t=0; t<NUMBER_OF_OBSERVATIONS; t++ )
            {
                dp = new Observation( 5.0 );
                dp.setIndependentValue( "t",  t+1 );
                observedData.add( dp );
            }
        
        // These are the expected results
        double expectedResult[] = { 5.0, 5.0 };
        
        // Try the forecasting model with smoothing constants ranging
        //  from 0.1 to 0.9 (in 0.1 increments)
        double gamma = 0.1;
        double smoothingConstant = 0.1;
        for ( ; smoothingConstant<0.95; smoothingConstant+=0.1 )
            {
                ForecastingModel model
                    = new DoubleExponentialSmoothingModel(smoothingConstant,
                                                          gamma);
                
                // Initialize the model
                model.init( observedData );
                
                // Create a data set for forecasting
                DataSet fcValues = new DataSet();
                
                dp = new Observation( 0.0 );
                dp.setIndependentValue( "t", NUMBER_OF_OBSERVATIONS-1.0 );
                fcValues.add( dp );
                
                dp = new Observation( 0.0 );
                dp.setIndependentValue( "t", NUMBER_OF_OBSERVATIONS );
                fcValues.add( dp );
                
                // Get forecast values
                DataSet results = model.forecast( fcValues );
                
                checkResults( results, expectedResult );
            }
    }
    
    /**
     * A somewhat more realistic test where the results are known (and were
     * calculated independently of the model). Validates that the
     * DoubleExponentialSmoothingModel returns the expected set of results
     * for the given inputs.
     */
    public void testDoubleExponentialSmoothing()
    {
        // NOTE: This is test data for simple exp. smoothing, so needs to
        // be changed/updated for double exp. smoothing
        
        
        DataSet observedData = new DataSet();
        DataPoint dp;
        
        double[] observations
            = { 5.6, 6.4, 7.8, 8.8, 11.0, 11.6, 16.7, 15.3, 21.6, 22.4 };
        
        for ( int t=0; t<observations.length; t++ )
            {
                dp = new Observation( observations[t] );
                dp.setIndependentValue( "t",  t+1 );
                observedData.add( dp );
            }
        
        ForecastingModel model
            = new DoubleExponentialSmoothingModel( 0.3623, 1.0 );
        
        // Initialize the model
        model.init( observedData );
        
        assertEquals( "Checking the accuracy of the model",
                      3.36563849, model.getMSE(), MSE_TOLERANCE );
        
        // Create a data set for forecasting
        DataSet fcValues = new DataSet();
        
        for ( int t=1; t<=10; t ++ )
            {
                dp = new Observation( 0.0 );
                dp.setIndependentValue( "t", t );
                fcValues.add( dp );
            }
        
        // Get forecast values
        DataSet results = model.forecast( fcValues );
        
        // These are the expected results
        double expectedResult[]
            = {  5.600000,  6.400000,  /* t =  1, 2 */
                 7.417380,  8.567086,  /* t =  3, 4 */
                 10.181699, 11.725188,  /* t =  5, 6 */
                 14.511845, 16.574445,  /* t =  7, 8 */
                 19.710524, 22.684798 };/* t =  9,10 */
        
        checkResults( results, expectedResult, TOLERANCE );
    }
}
// Local Variables:
// tab-width: 4
// End:
