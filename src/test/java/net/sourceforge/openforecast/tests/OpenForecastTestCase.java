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


import java.util.Iterator;

import junit.framework.TestCase;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;


/**
 * Defines a base test case class that all Open Forecast test cases can
 * extend (1) for consistency; and (2) in order to inherit some common
 * functionality used in validating test results.
 * @author Steven R. Gould
 */
public abstract class OpenForecastTestCase extends TestCase
{
    /**
     * The amount of error in the forecast values where the forecast is
     * considered "correct" by the test.
     */
    private static double TOLERANCE = 0.00001;
    
    /**
     * Constructs a new test case using the given name. Required by the
     * JUnit framework.
     * @param name the name of this test case. 
     */
    public OpenForecastTestCase( String name )
    {
        super(name);
    }
    
    /**
     * A helper function that validates the actual results obtaining for
     * a DataSet match the expected results.
     * @param actualResults the DataSet returned from the forecast method
     *        that contains the data points for which forecasts were done.
     * @param expectedResults an array of expected values for the forecast
     *        data points. The order should match the order of the results
     *        as defined by the DataSet iterator.
     */
    protected void checkResults( DataSet actualResults,
                                 double[] expectedResults )
    {
        checkResults( actualResults, expectedResults, TOLERANCE );
    }
    
    /**
     * A helper function that validates the actual results obtaining for
     * a DataSet match the expected results. This is the same as the other
     * checkResults method except that with this method, the caller can
     * specify an acceptance tolerance when comparing actual with expected
     * results.
     * @param actualResults the DataSet returned from the forecast method
     *        that contains the data points for which forecasts were done.
     * @param expectedResults an array of expected values for the forecast
     *        data points. The order should match the order of the results
     *        as defined by the DataSet iterator.
     * @param tolerance the tolerance to accept when comparing the actual
     *        results (obtained from a forecasting model) with the expected
     *        results.
     */
    protected void checkResults( DataSet actualResults,
                                 double[] expectedResults,
                                 double tolerance )
    {
        // This is just to safeguard against a bug in the test case!  :-)
        assertNotNull( "Checking expected results is not null",
                       expectedResults );
        assertTrue( "Checking there are some expected results",
                    expectedResults.length > 0 );
        
        assertEquals( "Checking the correct number of results returned",
                      expectedResults.length, actualResults.size() );
        
        // Iterate through the results, checking each one in turn
        Iterator<DataPoint> it = actualResults.iterator();
        int i=0;
        while ( it.hasNext() )
            {
                // Check that the results are within specified tolerance
                //  of the expected values
                DataPoint fc = (DataPoint)it.next();
                double fcValue = fc.getDependentValue();
                
                assertEquals( "Checking result",
                              expectedResults[i], fcValue, tolerance );
                i++;
            }
    }
}
// Local variables:
// tab-width: 4
// End:
