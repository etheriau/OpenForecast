//
//  OpenForecast - open source, general-purpose forecasting package.
//  Copyright (C) 2004  Steven R. Gould
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

package net.sourceforge.openforecast.input;


import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;

/**
 * Defines a Builder that can be used to construct a DataSet from a TimeSeries
 * object as used/defined by JFreeChart. This class makes for a quick and easy
 * "import" of data from a JFreeChart TimeSeries.
 *
 * Note that since a TimeSeries does not give a name to the independent, time
 * variable, this builder defaults the name to be the class name of the
 * RegularTimePeriod used in the TimeSeries. For example, if a series of
 * org.jfree.data.time.Day objects are used, then the name of the independent
 * variable will default to "Day" (without the quotes).
 * @author Steven R. Gould
 * @since 0.4
 */
public class TimeSeriesBuilder extends AbstractBuilder
{
    /**
     * Stores the result set from which this builder is to read its data.
     */
    private TimeSeries timeSeries;
    
    /**
     * Constructs a new TimeSeriesBuilder that reads its input from the given
     * TimeSeries object. This builder defaults the of the independent, time
     * variable to be the class name of the RegularTimePeriod used in the
     * TimeSeries. For example, if a series of org.jfree.data.time.Day objects
     * are used, then the name of the independent variable will default to
     * "Day" (without the quotes).
     *
     * See the class description for more information.
     * @param timeSeries the TimeSeries object containing data to be used to
     * build the DataSet.
     * @throws IllegalArgumentException if the TimeSeries is empty.
     */
    public TimeSeriesBuilder( TimeSeries timeSeries )
    {
        if ( timeSeries.getItemCount() <= 0 )
            throw new IllegalArgumentException("TimeSeries cannot be empty.");
        
        this.timeSeries = timeSeries;
        
        // Use base name of TimePeriod class, as name of time variable
        RegularTimePeriod timePeriod = timeSeries.getTimePeriod(0);
        String name = timePeriod.getClass().getName();
        name = name.substring( name.lastIndexOf(".")+1 );
        addVariable( name );
    }
    
    /**
     * Constructs a new TimeSeriesBuilder that reads its input from the given
     * TimeSeries object. This builder uses the given name for the independent,
     * time variable in the DataPoints that are created.
     * @param timeSeries the TimeSeries object containing data to be used to
     * build the DataSet.
     * @param timeVariableName the name to use for the time variable.
     * @throws IllegalArgumentException if the TimeSeries is empty.
     */
    public TimeSeriesBuilder( TimeSeries timeSeries, String timeVariableName )
    {
        if ( timeSeries.getItemCount() <= 0 )
            throw new IllegalArgumentException("TimeSeries cannot be empty.");
        
        this.timeSeries = timeSeries;
        setTimeVariable( timeVariableName );
    }
    
    /**
     * Returns the name of the currently defined time variable.
     * @return the name currently defined for the time variable.
     */
    public String getTimeVariable()
    {
        return getVariableName( 0 );
    }
    
    /**
     * Used to change the time variable name.
     * @param name the new name for the time variable.
     */
    public void setTimeVariable( String name )
    {
        setVariableName( 0, name );
    }
    
    /**
     * Retrieves a DataSet - a collection of DataPoints - from the current
     * (JFreeChart) TimeSeries. The DataSet should contain all DataPoints
     * defined by the TimeSeries.
     *
     * <p>In general, build will attempt to convert all values in the
     * TimeSeries to data points.
     * @return a DataSet built from the current TimeSeries.
     */
    public DataSet build()
    {
        DataSet dataSet = new DataSet();
        
        dataSet.setTimeVariable( getTimeVariable() );
        
        // Iterate through TimeSeries,
        //  creating new DataPoint instance for each row
        int numberOfPeriods = timeSeries.getItemCount();
        for ( int t=0; t<numberOfPeriods; t++ )
            dataSet.add( build(timeSeries.getDataItem(t)) );
        
        return dataSet;
    }
    
    /**
     * Builds a DataPoint from the given TimeSeriesDataItem. The name used for
     * the independent, time variable can be changed using the setTimeVariable
     * method.
     * @param dataItem the TimeSeriesDataItem from which the values are to be
     * read and used to construct a new DataPoint.
     * @return a DataPoint object with values as specified by the given
     * TimeSeriesDataItem.
     */
    private DataPoint build( TimeSeriesDataItem dataItem )
    {
        DataPoint dataPoint
            = new Observation( dataItem.getValue().doubleValue() );
        
        // Get time value (at middle of time period)
        double timeValue = dataItem.getPeriod().getMiddleMillisecond();
        
        // Store time value as independent variable
        dataPoint.setIndependentValue( getVariableName(0),
                                       timeValue );
        
        return dataPoint;
    }
}
// Local variables:
// tab-width: 4
// End:
