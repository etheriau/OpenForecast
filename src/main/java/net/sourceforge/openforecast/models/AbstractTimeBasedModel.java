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


import java.util.Iterator;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;


/**
 * A time based forecasting model is the base class that implements much of
 * the common code for models based on a time series. In particular, it was
 * designed to support the needs of the Weighted Moving Average, as well as
 * the Single, Double and Triple Exponential Smoothing models.
 *
 * <p>These models have an advantage over other forecasting models in that
 * they smooth out peaks and troughs (or valleys) in a set of observations.
 * However, they also have several disadvantages. In particular these models
 * do not produce an actual equation. Therefore, they often are not all that
 * useful as medium-long range forecasting tools. They can only reliably be
 * used to forecast a few periods into the future.
 * @author Steven R. Gould
 * @since 0.4
 */
public abstract class AbstractTimeBasedModel extends AbstractForecastingModel
{
    /**
     * The name of the independent (time) variable used in this model.
     */
    private String timeVariable = null;
    
    /**
     * Initialized to the time difference (in whatever units time is reported
     * in) between two consecutive data points. You could also think of this
     * as the "delta time" between data points.
     */
    private double timeDiff = 0.0;
    
    /**
     * Stores the minimum number of prior periods of data required to produce a
     * forecast. Since this varies depending on the details of the model, any
     * subclass must call setMinimumNumberOfPeriods - usually from the
     * constructor - before init is invoked to provide the correct information.
     */
    private int minPeriods = 0;
    
    /**
     * The observed values are stored for future reference. In this model,
     * unlike most others, we store all observed values. This is because these
     * models don't derive any formula from the data, so the values may be
     * needed later in order to derive future forecasts.
     */
    private DataSet observedValues;
    
    /**
     * The forecast values are stored to save recalculation. In this model,
     * unlike most others, we store all forecast values. This is because these
     * models don't derive any formula from the data.
     */
    private DataSet forecastValues;
    
    /**
     * The minimum value of the independent variable supported by this
     * forecasting model. This is dependent on the data set used to
     * initialize the model.
     * @see #maxIndependentValue
     */
    private double minTimeValue;
    
    /**
     * The maximum value of the independent variable supported by this
     * forecasting model. This is dependent on the data set used to
     * initialize the model.
     * @see #minTimeValue
     */
    private double maxTimeValue;
    
    /**
     * Constructs a new time based forecasting model. For a valid model to be
     * constructed, you should call init and pass in a data set containing a
     * series of data points. The data set should also have the time variable
     * initialized to the independent time variable name.
     */
    public AbstractTimeBasedModel()
    {
    }
    
    /**
     * Constructs a new time based forecasting model, using the named variable
     * as the independent (time) variable.
     * @param timeVariable the name of the independent variable to use as the
     * time variable in this model.
     * @deprecated As of 0.4, replaced by {@link #AbstractTimeBasedModel}.
     */
    public AbstractTimeBasedModel( String timeVariable )
    {
        this.timeVariable = timeVariable;
    }
    
    /**
     * Returns the current number of periods used in this model. This is also
     * the minimum number of periods required in order to produce a valid
     * forecast. Since this varies depending on the details of the model, any
     * subclass must override this to provide the correct information.
     * @return the minimum number of periods used in this model.
     */
    protected abstract int getNumberOfPeriods();
    
    /**
     * Used to initialize the time based model. This method must be called
     * before any other method in the class. Since the time based model does
     * not derive any equation for forecasting, this method uses the input
     * DataSet to calculate forecast values for all values of the independent
     * time variable within the initial data set.
     * @param dataSet a data set of observations that can be used to initialize
     *        the forecasting parameters of the forecasting model.
     */
    public void init( DataSet dataSet )
    {
        initTimeVariable( dataSet );
        
        if ( dataSet == null  || dataSet.size() == 0 )
            throw new IllegalArgumentException("Data set cannot be empty in call to init.");
        
        int minPeriods = getNumberOfPeriods();
        
        if ( dataSet.size() < minPeriods )
            throw new IllegalArgumentException("Data set too small. Need "
                                               +minPeriods
                                               +" data points, but only "
                                               +dataSet.size()
                                               +" passed to init.");
        
        observedValues = new DataSet( dataSet );
        observedValues.sort( timeVariable );
        
        // Check that intervals between data points are consistent
        //  i.e. check for complete data set
        Iterator<DataPoint> it = observedValues.iterator();
        
        DataPoint dp = it.next();  // first data point
        double lastValue = dp.getIndependentValue(timeVariable);
        
        dp = it.next();  // second data point
        double currentValue = dp.getIndependentValue(timeVariable);
        
        // Create data set in which to save new forecast values
        forecastValues = new DataSet();
        
        // Determine "standard"/expected time difference between observations
        timeDiff = currentValue - lastValue;
        
        // Min. time value is first observation time
        minTimeValue = lastValue;
        
        while ( it.hasNext() )
            {
                lastValue = currentValue;
                
                // Get next data point
                dp = it.next();
                currentValue = dp.getIndependentValue(timeVariable);
                
                double diff = currentValue - lastValue;
                if ( Math.abs(timeDiff - diff) > TOLERANCE )
                    throw new IllegalArgumentException( "Inconsistent intervals found in time series, using variable '"+timeVariable+"'" );
                
                try
                    {
                        initForecastValue( currentValue );
                    }
                catch (IllegalArgumentException ex)
                    {
                        // We can ignore these during initialization
                    }
            }
        
        // Create test data set for determining accuracy indicators
        //  - same as input data set, but without the first n data points
        DataSet testDataSet = new DataSet( observedValues );
        int count = 0;
        while ( count++ < minPeriods )
            testDataSet.remove( (testDataSet.iterator()).next() );
        
        // Calculate accuracy
        calculateAccuracyIndicators( testDataSet );
    }
    
    /**
     * Initializes the time variable from the given data set. If the data set
     * does not have a time variable explicitly defined, then provided there
     * is only one independent variable defined for the data set that is used
     * as the time variable. If more than one independent variable is defined
     * for the data set, then it is not possible to take an educated guess at
     * which one is the time variable. In this case, an
     * IllegalArgumentException will be thrown.
     * @param dataSet the data set to use to initialize the time variable.
     * @throws IllegalArgumentException If more than one independent variable
     * is defined for the data set and no time variable has been specified. To
     * correct this, be sure to explicitly specify the time variable in the
     * data set passed to {@link #init}.
     */
    protected void initTimeVariable( DataSet dataSet )
        throws IllegalArgumentException
    {
        if ( timeVariable == null )
            {
                // Time variable not set, so look at independent variables
                timeVariable = dataSet.getTimeVariable();
                if ( timeVariable == null )
                    {
                        String[] independentVars
                            = dataSet.getIndependentVariables();
                        
                        if ( independentVars.length != 1 )
                            throw new IllegalArgumentException("Unable to determine the independent time variable for the data set passed to init for "+toString()+". Please use DataSet.setTimeVariable before invoking model.init.");
                        
                        timeVariable = independentVars[0];
                    }
            }
    }
    
    /**
     * A helper method that calculates a complete set of forecast values
     * derived from the given DataSet. These are calculated in advance due
     * to the way in which forecast values are so dependent on the original
     * data set. The resulting forecast values are stored in the private
     * DataSet forecastValues. Additionally, this method initializes the
     * private member variables minTimeValue and maxTimeValue.
     * @param dataSet the set of data points on which to base the forecast.
     * @return a data set containing all "possible" forecast DataPoints
     * that can reasonably be supported by this forecasting model. "Possible"
     * forecast DataPoints generally are those which are even partially
     * based on an observed value, since forecasting purely off of forecast
     * values tends to be unreliable at best.
     */
    private double initForecastValue( double timeValue )
        throws IllegalArgumentException
    {
        // Temporary store for current forecast value
        double forecast = forecast(timeValue);
        
        // Create new forecast data point
        DataPoint dpForecast = new Observation( forecast );
        dpForecast.setIndependentValue( timeVariable, timeValue );
        
        // Add new data point to forecast set
        forecastValues.add( dpForecast );
        
        // Update maximum time value, if necessary
        if ( timeValue > maxTimeValue )
            maxTimeValue = timeValue;
        
        return forecast;
    }
    
    /**
     * Using the current model parameters (initialized in init), apply the
     * forecast model to the given data point. The data point must have a valid
     * value for the independent variable. Upon return, the value of the
     * dependent variable will be updated with the forecast value computed for
     * that data point.
     * @param dataPoint the data point for which a forecast value (for the
     *        dependent variable) is required.
     * @return the same data point passed in but with the dependent value
     *         updated to contain the new forecast value.
     * @throws ModelNotInitializedException if forecast is called before the
     *         model has been initialized with a call to init.
     * @throws IllegalArgumentException if the forecast period specified by
     *         the dataPoint is invalid with respect to the historical data
     *         provided.
     */
    public double forecast( DataPoint dataPoint )
        throws IllegalArgumentException
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        // Get value of independent variable (the time variable)
        double t = dataPoint.getIndependentValue( timeVariable );
        
        return getForecastValue( t );
    }
    
    /**
     * Returns the forecast value of the dependent variable for the given
     * value of the independent time variable. Subclasses must implement
     * this method in such a manner consistent with the forecasting model
     * they implement. Subclasses can make use of the getForecastValue and
     * getObservedValue methods to obtain "earlier" forecasts and
     * observations respectively.
     * @param timeValue the value of the time variable for which a forecast
     * value is required.
     * @return the forecast value of the dependent variable for the given
     * time.
     * @throws IllegalArgumentException if there is insufficient historical
     * data - observations passed to init - to generate a forecast for the
     * given time value.
     */
    protected abstract double forecast( double timeValue )
        throws IllegalArgumentException;
    
    /**
     * Returns the forecast value for the dependent variable for the given
     * value of the independent time variable. This method is only intended
     * for use by models that base future forecasts, in part, on past
     * forecasts.
     * @param timeValue the value of the independent time variable for which
     * the forecast value is required. This value must be greater than the
     * minimum time value defined by the observations passed into the init
     * method.
     * @return the forecast value of the dependent variable for the given
     * value of the independent time variable.
     * @throws IllegalArgumentException if the given value of the time
     * variable was not a valid value for forecasts.
     */
    protected double getForecastValue( double timeValue )
        throws IllegalArgumentException
    {
        if ( timeValue>=minTimeValue-TOLERANCE
             && timeValue<=maxTimeValue+TOLERANCE )
            {
                // Find required forecast value in set of
                //  pre-computed forecasts
                Iterator<DataPoint> it = forecastValues.iterator();
                while ( it.hasNext() )
                    {
                        DataPoint dp = it.next();
                        double currentTime
                            = dp.getIndependentValue( timeVariable );
                        
                        // If required data point found,
                        //  return pre-computed forecast
                        if ( Math.abs(currentTime-timeValue) < TOLERANCE )
                            return dp.getDependentValue();
                    }
            }
        
        try
            {
                return initForecastValue( timeValue );
            }
        catch ( IllegalArgumentException idex )
            {
                throw new IllegalArgumentException(
                                                   "Time value (" + timeValue
                                                   + ") invalid for Time Based forecasting model. Valid values are in the range "
                                                   + minTimeValue + "-" + maxTimeValue
                                                   + " in increments of " + timeDiff + "." );
            }
    }
    
    /**
     * Returns the observed value of the dependent variable for the given
     * value of the independent time variable.
     * @param timeValue the value of the independent time variable for which
     * the observed value is required.
     * @return the observed value of the dependent variable for the given
     * value of the independent time variable.
     * @throws IllegalArgumentException if the given value of the time
     * variable was not found in the observations originally passed to init.
     */
    protected double getObservedValue( double timeValue )
        throws IllegalArgumentException
    {
        // Find required forecast value in set of
        //  pre-computed forecasts
        Iterator<DataPoint> it = observedValues.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                double currentTime
                    = dp.getIndependentValue( timeVariable );
                
                // If required data point found,
                //  return pre-computed forecast
                if ( Math.abs(currentTime-timeValue) < TOLERANCE )
                    return dp.getDependentValue();
            }
        
        throw new
            IllegalArgumentException("No observation found for time value, "
                                     +timeVariable+"="+timeValue);
    }
    
    /**
     * Returns the name of the independent variable representing the time
     * value used by this model.
     * @return the name of the independent variable representing the time
     * value.
     */
    public String getTimeVariable()
    {
        return timeVariable;
    }
    
    /**
     * Returns the minimum value of the independent time variable currently
     * forecast by this model.
     * @return the minimum value of the independent time variable.
     */
    public double getMinimumTimeValue()
    {
        return minTimeValue;
    }
    
    /**
     * Returns the maximum value of the independent time variable currently
     * forecast by this model.
     * @return the maximum value of the independent time variable.
     */
    public double getMaximumTimeValue()
    {
        return maxTimeValue;
    }
    
    /**
     * Returns the independent variable - or the time variable - used in this
     * model.
     * @return the independent variable in this model.
     */
    public String getIndependentVariable()
    {
        return timeVariable;
    }
    
    /**
     * Returns the current time interval between observations.
     * @return the current time interval between observations.
     */
    protected double getTimeInterval()
    {
        return timeDiff;
    }
    
    /**
     * Returns a one or two word name of this type of forecasting model. Keep
     * this short. A longer description should be implemented in the toString
     * method.
     * @return a string representation of the type of forecasting model
     *         implemented.
     */
    public String getForecastType()
    {
        return "Time Based Model";
    }
    
    /**
     * This should be overridden to provide a textual description of the
     * current forecasting model including, where possible, any derived
     * parameters used.
     * @return a string representation of the current forecast model, and its
     *         parameters.
     */
    public String toString()
    {
        return "time based model, spanning " + getNumberOfPeriods()
            + " periods and using a time variable of "
            + timeVariable+".";
    }
}
// Local Variables:
// tab-width: 4
// End:
