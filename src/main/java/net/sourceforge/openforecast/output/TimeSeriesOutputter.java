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

package net.sourceforge.openforecast.output;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;

/**
 * Defines an Outputter that can be used to add the DataPoints from a DataSet
 * to a (JFreeChart) TimeSeries. This class provides for a quick and easy
 * "export" of forecast data points to a JFreeChart TimeSeries that can be
 * displayed in a JFreeChart.
 * @see <a href="http://www.jfree.org/jfreechart/" target="_blank">JFreeChart</a>
 * @author Steven R. Gould
 * @since 0.4
 */
public class TimeSeriesOutputter implements Outputter
{
    /**
     * Stores the result set from which this builder is to read its data.
     */
    private TimeSeries timeSeries;
    
    /**
     * The Constructor object representing the constructor to use for the
     * TimePeriod components of the TimeSeriesDataItems within the output
     * TimeSeries.
     */
    private Constructor<?> timePeriodConstructor;
    
    /**
     * Constructs a new TimeSeriesOutputter that adds its output to the named
     * TimeSeries.
     * @param timeSeries the TimeSeries to add output to.
     */
    public TimeSeriesOutputter( TimeSeries timeSeries,
                                Class<?> timePeriodClass )
        throws ClassNotFoundException, NoSuchMethodException
    {
        this.timeSeries = timeSeries;
        
        Class<?>[] args = new Class[1];
        args[0] = Class.forName("java.util.Date");
        timePeriodConstructor = timePeriodClass.getConstructor(args);
    }
    
    /**
     * Adds a DataSet - a collection of DataPoints - to the current TimeSeries.
     * The DataSet should contain all DataPoints to be output.
     * @param dataSet the DataSet to be output to the current TimeSeries.
     */
    public void output( DataSet dataSet )
        throws InstantiationException, IllegalAccessException,
        InvocationTargetException, InstantiationException
    {
        String timeVariable = dataSet.getTimeVariable();
        
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dataPoint = it.next();
                output( dataPoint, timeVariable );
            }
    }
    
    /**
     * Outputs the given DataPoint to the current TimeSeries.
     * @param dataPoint the DataPoint to output to the current TimeSeries.
     */
    private void output( DataPoint dataPoint, String timeVariable )
        throws InstantiationException, IllegalAccessException,
        InvocationTargetException, InstantiationException
    {
        long timeValue = (long)dataPoint.getIndependentValue(timeVariable);
        
        Object[] args = new Object[1];
        args[0] = new Date( timeValue );
        RegularTimePeriod period
            = (RegularTimePeriod)timePeriodConstructor.newInstance(args);
        
        double value = dataPoint.getDependentValue();
        
        timeSeries.add( new TimeSeriesDataItem(period,value) );
    }
}
// Local variables:
// tab-width: 4
// End:
