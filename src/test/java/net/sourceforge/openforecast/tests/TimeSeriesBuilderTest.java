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


import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.input.TimeSeriesBuilder;


/**
 * Implements tests of the TimeSeriesBuilder class.
 */
public class TimeSeriesBuilderTest extends OpenForecastTestCase
{
    /**
     * Tests the correct input of a DataSet from a TimeSeries by creating a
     * simple TimeSeries object then inputting it using a TimeSeriesBuilder
     * instance.
     */
    public void testBuilder()
    {
        // Constants used to determine size of test
        int NUMBER_OF_TIME_PERIODS = 100;
        
        // Set up array for expected results
        double expectedValue[] = new double[ NUMBER_OF_TIME_PERIODS ];
        
        // Create test TimeSeries
        TimeSeries timeSeries = new TimeSeries("Simple time series");
        RegularTimePeriod period = new Day();
        
        for ( int d=0; d<NUMBER_OF_TIME_PERIODS; d++ )
            {
                expectedValue[d] = d;
                timeSeries.add(period,d);
                period = period.next();
            }
        
        // Create TimeSeriesBuilder and use it to create the DataSet
        String TIME_VARIABLE = "t";
        TimeSeriesBuilder builder
            = new TimeSeriesBuilder( timeSeries, TIME_VARIABLE );
        DataSet dataSet = builder.build();
        
        // Verify data set contains the correct number of entries
        assertEquals( "DataSet created is of the wrong size",
                      NUMBER_OF_TIME_PERIODS, dataSet.size() );
        
        // Vefify that only two independent variable names are reported
        String[] independentVariables = dataSet.getIndependentVariables();
        assertEquals( "Checking the correct number of independent variables",
                      1, independentVariables.length );
        assertEquals( "Independent variable not set as expected",
                      TIME_VARIABLE, independentVariables[0] );
        
        // Check the data points in the data set. This may not be a good
        //  test since it is dependent on the order of the data points in
        //  the 2-d array
        checkResults( dataSet, expectedValue );
    }
    
    public TimeSeriesBuilderTest( String name )
    {
        super(name);
    }
}
// Local variables:
// tab-width: 4
// End:
