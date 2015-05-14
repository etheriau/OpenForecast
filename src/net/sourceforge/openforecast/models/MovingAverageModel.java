//
//  OpenForecast - open source, general-purpose forecasting package.
//  Copyright (C) 2002-2004  Steven R. Gould
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


import net.sourceforge.openforecast.DataSet;


/**
 * A moving average forecast model is based on an artificially constructed
 * time series in which the value for a given time period is replaced by the
 * mean of that value and the values for some number of preceding and
 * succeeding time periods. As you may have guessed from the description, this
 * model is best suited to time-series data; i.e. data that changes over time.
 * For example, many charts of individual stocks on the stock market show 20,
 * 50, 100 or 200 day moving averages as a way to show trends.
 *
 * <p>Since the forecast value for any given period is an average of the
 * previous periods, then the forecast will always appear to "lag" behind
 * either increases or decreases in the observed (dependent) values. For
 * example, if a data series has a noticable upward trend then a moving average
 * forecast will generally provide an underestimate of the values of the
 * dependent variable.
 *
 * <p>The moving average method has an advantage over other forecasting models
 * in that it does smooth out peaks and troughs (or valleys) in a set of
 * observations. However, it also has several disadvantages. In particular this
 * model does not produce an actual equation. Therefore, it is not all that
 * useful as a medium-long range forecasting tool. It can only reliably be
 * used to forecast one or two periods into the future.
 *
 * <p>The moving average model is a special case of the more general weighted
 * moving average. In the simple moving average, all weights are equal.
 * @author Steven R. Gould
 * @since 0.3
 */
public class MovingAverageModel extends WeightedMovingAverageModel
{
    /**
     * Constructs a new moving average forecasting model. For a valid model to
     * be constructed, you should call init and pass in a data set containing
     * a series of data points with the time variable initialized to identify
     * the independent variable.
     */
    public MovingAverageModel()
    {
    }
    
    /**
     * Constructs a new moving average forecasting model, using the given name
     * as the independent variable.
     * @param independentVariable the name of the independent variable to use
     * in this model.
     * @deprecated As of 0.4, replaced by {@link #MovingAverageModel}.
     */
    public MovingAverageModel( String independentVariable )
    {
        super( independentVariable );
    }
    
    /**
     * Constructs a new moving average forecasting model, using the specified
     * period. For a valid model to be constructed, you should call init and
     * pass in a data set containing a series of data points with the time
     * variable initialized to identify the independent variable.
     *
     * <p>The period value is used to determine the number of observations to
     * be used to calculate the moving average. For example, for a 50-day
     * moving average where the data points are daily observations, then the
     * period should be set to 50.
     *
     * <p>The period is also used to determine the amount of future periods
     * that can effectively be forecast. With a 50 day moving average, then we
     * cannot reasonably - with any degree of accuracy - forecast more than
     * 50 days beyond the last period for which data is available. This may be
     * more beneficial than, say a 10 day period, where we could only
     * reasonably forecast 10 days beyond the last period.
     * @param period the number of observations to be used to calculate the
     * moving average.
     */
    public MovingAverageModel( int period )
    {
        double[] weights = new double[period];
        for ( int p=0; p<period; p++ )
            weights[p] = 1.0/period;
        
        setWeights( weights );
    }
    
    /**
     * Constructs a new moving average forecasting model, using the given name
     * as the independent variable and the specified period.
     * @param independentVariable the name of the independent variable to use
     * in this model.
     * @param period the number of observations to be used to calculate the
     * moving average.
     * @deprecated As of 0.4, replaced by {@link #MovingAverageModel(int)}.
     */
    public MovingAverageModel( String independentVariable, int period )
    {
        super( independentVariable );
        
        double[] weights = new double[period];
        for ( int p=0; p<period; p++ )
            weights[p] = 1.0/period;
        
        setWeights( weights );
    }
    
    /**
     * Used to initialize the moving average model. This method must be
     * called before any other method in the class. Since the moving
     * average model does not derive any equation for forecasting, this
     * method uses the input DataSet to calculate forecast values for all
     * valid values of the independent time variable.
     * @param dataSet a data set of observations that can be used to
     * initialize the forecasting parameters of the forecasting model.
     */
    public void init( DataSet dataSet )
    {
        if ( getNumberOfPeriods() <= 0 )
            {
                // Number of periods has not yet been defined
                //  - what's a reasonable number to use?
                
                // Use maximum number of periods as a default
                int period = getNumberOfPeriods();
                
                // Set weights for moving average model
                double[] weights = new double[period];
                for ( int p=0; p<period; p++ )
                    weights[p] = 1/period;
                
                setWeights( weights );
            }
        
        super.init( dataSet );
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
        return "Moving average";
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
        return "Moving average model, spanning " + getNumberOfPeriods()
            + " periods and using an independent variable of "
            + getIndependentVariable()+".";
    }
}
// Local Variables:
// tab-width: 4
// End:
