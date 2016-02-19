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


import java.lang.reflect.InvocationTargetException;

import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.output.TimeSeriesOutputter;


/**
 * Implements tests of the TimeSeriesOutputter class.
 */
public class TimeSeriesOutputterTest extends OpenForecastTestCase
{
    // Constants used to determine size of test
    private double TOLERANCE = 0.000001;
    
    /**
     * Tests the correct output of a DataSet to a TimeSeries by outputting it,
     * then iterating through TimeSeries object checking the correct values
     * were stored/output.
     */
    public void testOutput()
        throws ClassNotFoundException, NoSuchMethodException,
        InstantiationException, IllegalAccessException,
        InvocationTargetException, InstantiationException
    {
        // Constants used to determine size of test
        int NUMBER_OF_TIME_PERIODS = 10;
        String TIME_VARIABLE = "t";
        
        // Set up array for expected results
        double expectedValue[] = new double[ NUMBER_OF_TIME_PERIODS ];

        // We'll set up periods starting from today
        RegularTimePeriod period = new Day();

        // Create a test DataSet for output
        //  - note that only one independent variable (the time variable)
        //    will be output. This is expected.
        DataSet dataSet = new DataSet();
        dataSet.setTimeVariable( TIME_VARIABLE );
        for ( int d=0; d<NUMBER_OF_TIME_PERIODS; d++ )
            {
                double value = (double)d;
                DataPoint obs = new Observation( value );
                obs.setIndependentValue( TIME_VARIABLE,
                                         period.getMiddleMillisecond() );
                dataSet.add( obs );

                period = period.next();
                expectedValue[d] = value;
            }

        assertEquals("Checking only one independent variable exists in dataSet",
                     1,dataSet.getIndependentVariables().length);


        assertEquals("Checking dataSet has correct number of entries",
                     NUMBER_OF_TIME_PERIODS,
                     dataSet.size());

        // Create TimeSeriesOutputter and use it to output dataSet
        TimeSeries timeSeries = new TimeSeries("test");
        TimeSeriesOutputter outputter
            = new TimeSeriesOutputter( timeSeries,
                                       period.getClass() );
        outputter.output( dataSet );

        assertEquals("Checking number of items in time series",
                     NUMBER_OF_TIME_PERIODS,
                     timeSeries.getItemCount());

        // Reset period to start checking from today onwards
        period = new Day();
        for ( int d=0; d<NUMBER_OF_TIME_PERIODS; d++ )
            {
                TimeSeriesDataItem dataItem = timeSeries.getDataItem(d);

                period = dataItem.getPeriod();
                assertNotNull("Checking time period",period);
                
                long timeValue = period.getMiddleMillisecond();
                assertTrue("Checking time periods match",
                           (double)timeValue>=period.getFirstMillisecond()
                           && (double)timeValue<=period.getLastMillisecond());

                assertEquals("Checking values for period "+dataItem.getPeriod()
                             +" match",
                             expectedValue[d],
                             dataItem.getValue().doubleValue(),
                             TOLERANCE);

                period = period.next();
            }
    }
    
    public TimeSeriesOutputterTest( String name )
    {
        super(name);
    }
}
// Local variables:
// tab-width: 4
// End:
