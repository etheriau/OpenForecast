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


/**
 * A weighted moving average forecast model is based on an artificially
 * constructed time series in which the value for a given time period is
 * replaced by the weighted mean of that value and the values for some number
 * of preceding time periods. As you may have guessed from the description,
 * this model is best suited to time-series data; i.e. data that changes over
 * time.
 *
 * <p>Since the forecast value for any given period is a weighted average of
 * the previous periods, then the forecast will always appear to "lag" behind
 * either increases or decreases in the observed (dependent) values. For
 * example, if a data series has a noticable upward trend then a weighted
 * moving average forecast will generally provide an underestimate of the
 * values of the dependent variable.
 *
 * <p>The weighted moving average model, like the moving average model, has
 * an advantage over other forecasting models in that it does smooth out
 * peaks and troughs (or valleys) in a set of observations. However, like the
 * moving average model, it also has several disadvantages. In particular this
 * model does not produce an actual equation. Therefore, it is not all that
 * useful as a medium-long range forecasting tool. It can only reliably be
 * used to forecast a few periods into the future.
 * @author Steven R. Gould
 * @since 0.4
 */
public class WeightedMovingAverageModel extends AbstractTimeBasedModel
{
    /**
     * The weights to assign to each period. These weights must add up to 1.0.
     */
    private double[] weights;
    
    /**
     * Constructs a new weighted moving average forecasting model, using the
     * specified weights. For a valid model to be constructed, you should call
     * init and pass in a data set containing a series of data points with the
     * time variable initialized to identify the independent variable.
     *
     * <p>The size of the weights array is used to determine the number of
     * observations to be used to calculate the weighted moving average.
     * Additionally, the most recent period will be given the weight defined
     * by the first element of the array; i.e. <code>weights[0]</code>.
     *
     * <p>The size of the weights array is also used to determine the amount
     * of future periods that can effectively be forecast. With a 50 day
     * weighted moving average, then we cannot reasonably - with any degree
     * of accuracy - forecast more than 50 days beyond the last period for
     * which data is available. Even forecasting near the end of this range
     * is likely be be unreliable.
     *
     * <h3>Note on weights</h3>
     * <p>In general, the weights passed to this constructor should add up
     * to 1.0. However, as a convenience, if the sum of the weights does not
     * add up to 1.0, this implementation scales all weights proportionally
     * so that they do sum to 1.0.
     * @param weights an array of weights to assign to the historical
     * observations when calculating the weighted moving average.
     */
    public WeightedMovingAverageModel( double[] weights )
    {
        setWeights( weights );
    }
    
    /**
     * Constructs a new weighted moving average forecasting model, using the
     * named variable as the independent variable and the specified weights.
     * @param independentVariable the name of the independent variable to use
     * in this model.
     * @param weights an array of weights to assign to the historical
     * observations when calculating the weighted moving average.
     * @deprecated As of 0.4, replaced by {@link #WeightedMovingAverageModel(double[])}.

     */
    public WeightedMovingAverageModel( String independentVariable,
                       double[] weights )
    {
        super( independentVariable );
        setWeights( weights );
    }

    /**
     * Constructs a new weighted moving average forecasting model. This
     * constructor is intended to be used only by subclasses (hence it is
     * protected). Any subclass using this constructor must subsequently
     * invoke the (protected) setWeights method to initialize the weights to
     * be used by this model.
     */
    protected WeightedMovingAverageModel()
    {
    }
    
    /**
     * Constructs a new weighted moving average forecasting model using
     * the given independent variable.
     * @param independentVariable the name of the independent variable to use
     * in this model.
     * @deprecated As of 0.4, replaced by {@link #WeightedMovingAverageModel}.
     */
    protected WeightedMovingAverageModel( String independentVariable )
    {
        super( independentVariable );
    }
    
    /**
     * Sets the weights used by this weighted moving average forecasting model
     * to the given weights. This method is intended to be used only by
     * subclasses (hence it is protected), and only in conjunction with the
     * (protected) one-argument constructor.
     *
     * <p>Any subclass using the one-argument constructor must subsequently
     * call setWeights <em>before</em> invoking the {@link #init} method to
     * initialize the model.
     *
     * <h3>Note on weights</h3>
     * <p>In general, the weights passed to this method should add up to 1.0.
     * However, as a convenience, if the sum of the weights does not add up
     * to 1.0, this implementation scales all weights proportionally so that
     * they do sum to 1.0.
     * @param weights an array of weights to assign to the historical
     * observations when calculating the weighted moving average.
     */
    protected void setWeights( double[] weights )
    {
        int periods = weights.length;
        
        // Check sum of weights adds up to 1.0
        double sum = 0.0;
        for ( int w=0; w<periods; w++ )
            sum += weights[w];
        
        // If sum of weights does not add up to 1.0,
        //  then an adjustment is needed
        boolean adjust = false;
        if ( Math.abs( sum - 1.0 ) > TOLERANCE )
            adjust = true;
        
        // Save weights or adjusted weights
        this.weights = new double[ periods ];
        for ( int w=0; w<periods; w++ )
            this.weights[w] = (adjust ? weights[w]/sum : weights[w]);
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
    protected double forecast( double timeValue )
        throws IllegalArgumentException
    {
        int periods = getNumberOfPeriods();
        double t = timeValue;
        double timeDiff = getTimeInterval();
        
        if ( timeValue - timeDiff*periods < getMinimumTimeValue() )
            return getObservedValue( t );
        
        double forecast = 0.0;
        for ( int p=periods-1; p>=0; p-- )
            {
                t -= timeDiff;
                try
                    {
                        forecast += weights[p]*getObservedValue( t );
                    }
                catch ( IllegalArgumentException iaex )
                    {
                        forecast += weights[p]*getForecastValue( t );
                    }
            }
        
        return forecast;
    }
    
    /**
     * Returns the number of predictors used by the underlying model.
     * @return the number of predictors used by the underlying model.
     */
    public int getNumberOfPredictors()
    {
        return 1;
    }
    
    /**
     * Returns the current number of periods used in this model.
     * @return the current number of periods used in this model.
     */
    protected int getNumberOfPeriods()
    {
        return weights.length;
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
        return "Weighted Moving Average";
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
        return "weighted moving average model, spanning "
            + getNumberOfPeriods()
            + " periods and using an independent variable of "
            + getIndependentVariable() + ".";
    }
}
// Local Variables:
// tab-width: 4
// End:
