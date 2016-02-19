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

import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;



public class BasicTest extends OpenForecastTestCase
{
    /**
     * Tests that two DataPoint objects initialized differently but with the
     * same data are equal.
     */
    public void testDataPoint()
    {
        DataPoint dp1 = new Observation( 1.0 );
        dp1.setIndependentValue( "x", 2.0 );
        dp1.setIndependentValue( "t", 3.0 );
        
        DataPoint dp2 = new Observation( 1.0 );
        dp2.setIndependentValue( "t", 3.0 );
        dp2.setIndependentValue( "x", 2.0 );
        
        assertTrue( dp1.equals( dp2 ) );
    }
    
    /**
     * Tests that an IllegalArgumentException is thrown if an attempt is made
     * to retrieve the value of an unknown independent variable.
     */
    public void testUnknownVariableInObservation()
    {
        Observation observation = new Observation( 5.0 );
        try
            {
                // Independent variable should not exist
                double value = observation.getIndependentValue( "y" );
                
                fail("Attempt to obtain the value of an unknown variable from an Observation returned a value ("+value+") - it should have thrown an exception");
            }
        catch ( IllegalArgumentException e )
            {
                // Expected result!
            }
    }
    
    /**
     * Creates a simple data set where the dependent value is exactly the same
     * as the independent value, then tests the chosen forecast model on five
     * further values in the series. A regression model should give accurate
     * results for this series, whereas a moving average would always lag the
     * value (so not be as appropriate).
     */
    public void testForecast()
    {
        DataSet observedData = new DataSet();
        for ( int count=0; count<10; count++ )
            {
                DataPoint dp = new Observation( (double)count );
                dp.setIndependentValue( "x", count );
                
                observedData.add( dp );
            }
        
        // Obtain a good forecasting model given this data set
        ForecastingModel forecaster
            = Forecaster.getBestForecast( observedData );
        
        // Create additional data points for which forecast values are required
        DataSet requiredDataPoints = new DataSet();
        for ( int count=11; count<15; count++ )
            {
                DataPoint dp = new Observation( 0.0 );
                dp.setIndependentValue( "x", count );
                
                requiredDataPoints.add( dp );
            }
        
        // Use the given forecasting model to forecast values for the
        //  required (future) data points
        forecaster.forecast( requiredDataPoints );
        
        double expectedResult[] = { 11.0, 12.0, 13.0, 14.0 };
        
        checkResults( requiredDataPoints, expectedResult );
    }
    
    public BasicTest( String name )
    {
        super(name);
    }
}
// Local variables:
// tab-width: 4
// End:
