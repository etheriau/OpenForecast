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


import net.sourceforge.openforecast.DataSet;


/**
 * A simple exponential smoothing forecast model is a very popular model
 * used to produce a smoothed Time Series. Whereas in simple Moving Average
 * models the past observations are weighted equally, Exponential Smoothing
 * assigns exponentially decreasing weights as the observations get older.
 *
 * <p>In other words, recent observations are given relatively more weight
 * in forecasting than the older observations.
 *
 * <p>In the case of moving averages, the weights assigned to the
 * observations are the same and are equal to <sup>1</sup>/<sub>N</sub>. In
 * simple exponential smoothing, however, a "smoothing parameter" - or
 * "smoothing constant" - is used to determine the weights assigned to the
 * observations.
 *
 * <p>This simple exponential smoothing model begins by setting the forecast
 * for the second period equal to the observation of the first period. Note
 * that there are ways of initializing the model. As of the time of writing,
 * these alternatives are not available in this implementation. Future
 * implementations of this model may offer these options.
 *
 * <h2>Choosing a smoothing constant</h2>
 * <p>The smoothing constant must be a value in the range 0.0-1.0. But, what
 * is the "best" value to use for the smoothing constant? This depends on the
 * data series being modeled. The speed at which the older responses are
 * dampened (smoothed) is a function of the value of the smoothing constant.
 * When this smoothing constant is close to 1.0, dampening is quick - more
 * weight is given to recent observations - and when it is close to 0.0,
 * dampening is slow - and relatively less weight is given to recent
 * observations.
 *
 * <p>The best value for the smoothing constant is the one that results in the
 * smallest mean of the squared errors (or other similar accuracy indicator).
 *
 * <h2>Note on alternate formulations</h2>
 * <p>This class supports two approaches to forecasting using Simple
 * Exponential Smoothing. The first approach - and the default approach - is
 * to use the formulation according to Hunter. Hunter's formulation uses the
 * observed and forecast values from the previous period to come up with a
 * forecast for the current period.
 *
 * <p>An alternative formulation is also supported - that proposed by Roberts.
 * The formulation according to Roberts uses the observed value from the
 * current period and the forecast value from the previous period to come up
 * with a forecast for the current period.
 *
 * <p>By default, the formulation according to Hunter is used. To override
 * this, use the {@link #SimpleExponentialSmoothingModel three argument
 * constructor} and specify {@link #ROBERTS} as the third argument.
 * @author Steven R. Gould
 * @since 0.4
 * @see <a href="http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc431.htm">Engineering Statistics Handbook, 6.4.3.1 Simple Expnential Smoothing</a>
 */
public class SimpleExponentialSmoothingModel extends AbstractTimeBasedModel
{
    /**
     * The default value of the tolerance permitted in the estimates of the
     * smoothing constants in the {@link #getBestFitModel} methods.
     */
    private static double DEFAULT_SMOOTHING_CONSTANT_TOLERANCE = 0.001;

    /**
     * Used in the {@link #SimpleExponentialSmoothingModel three argument
     * constructor} to specify that Hunter's formula is to be used for
     * calculating forecast values. The formulation according to Hunter uses
     * the observed and forecast values from the previous period to come up
     * with a forecast for the current period.
     */
    public static final int HUNTER = 1;
    
    /**
     * Used in the {@link #SimpleExponentialSmoothingModel three argument
     * constructor} to specify that Robert's formula is to be used for
     * calculating forecast values. The formulation according to Roberts uses
     * the observed value from the current period and the forecast value from
     * the previous period to come up with a forecast for the current period.
     */
    public static final int ROBERTS = 2;
    
    /**
     * The smoothing constant, alpha, used in this exponential smoothing model.
     */
    private double alpha;
    
    /**
     * Stores the approach to forecasting to be used in this instance of the
     * Simple Exponential Smoothing model.
     */
    private int approach;

    /**
     * Factory method that returns a "best fit" simple exponential smoothing
     * model for the given data set. This, like the overloaded
     * {@link #getBestFitModel(DataSet,double)}, attempts to derive a
     * "good" - hopefully near optimal - value for the alpha smoothing
     * constant.
     * @param dataSet the observations for which a "best fit" simple
     * exponential smoothing model is required.
     * @return a best fit simple exponential smoothing model for the given
     * data set.
     * @see #getBestFitModel(DataSet,double)
     */
    public static SimpleExponentialSmoothingModel
        getBestFitModel( DataSet dataSet )
    {
        return getBestFitModel( dataSet,
                                DEFAULT_SMOOTHING_CONSTANT_TOLERANCE );
    }

    /**
     * Factory method that returns a best fit simple exponential smoothing
     * model for the given data set. This, like the overloaded
     * {@link #getBestFitModel(DataSet)}, attempts to derive a "good" -
     * hopefully near optimal - value for the alpha smoothing constant.
     *
     * <p>To determine which model is "best", this method currently uses only
     * the Mean Squared Error (MSE). Future versions may use other measures in
     * addition to the MSE. However, the resulting "best fit" model - and the
     * associated value of alpha - is expected to be very similar either way.
     *
     * <p>Note that the approach used to calculate the best smoothing
     * constant, alpha, <em>may</em> end up choosing values near a local
     * optimum. In other words, there <em>may</em> be other values for alpha
     * and that result in a model with the same, or even better MSE.
     * @param dataSet the observations for which a "best fit" simple
     * exponential smoothing model is required.
     * @param alphaTolerance the required precision/accuracy - or tolerance
     * of error - required in the estimate of the alpha smoothing constant.
     * @return a best fit simple exponential smoothing model for the given
     * data set.
     */
    public static SimpleExponentialSmoothingModel
        getBestFitModel( DataSet dataSet, double alphaTolerance )
    {
        SimpleExponentialSmoothingModel model1
            = new SimpleExponentialSmoothingModel( 0.0 );
        SimpleExponentialSmoothingModel model2
            = new SimpleExponentialSmoothingModel( 0.5 );
        SimpleExponentialSmoothingModel model3
            = new SimpleExponentialSmoothingModel( 1.0 );

        return findBestFit( dataSet, model1, model2, model3, TOLERANCE );
    }

    /**
     * Performs a somewhat intelligent search for the best values for the
     * smoothing constant, alpha, for the given data set. For the given data
     * set and models with a small, medium and large value of the alpha
     * smoothing constant, returns the best fit model where the value of the
     * alpha smoothing constant is within the given tolerances.
     *
     * <p>Note that the descriptions of the parameters below include a
     * discussion of valid values. However, since this is a private method and
     * to help improve performance, we don't provide any validation of these
     * parameters. Using invalid values may lead to unexpected results.
     * @param dataSet the data set for which a best fit model is required.
     * @param modelMin the pre-initialized best fit model with the smallest
     * value of the alpha smoothing constant found so far.
     * @param modelMid the pre-initialized best fit model with the value of
     * the alpha smoothing constant between that of modelMin and modelMax.
     * @param modelMax the pre-initialized best fit model with the largest
     * value of the alpha smoothing constant found so far.
     * @param alphaTolerance the tolerance within which the alpha value is
     * required. Must be considerably less than 1.0. However, note that the
     * smaller this value the longer it will take to diverge on a best fit
     * model.
     */
    private static SimpleExponentialSmoothingModel findBestFit(
                        DataSet dataSet,
                        SimpleExponentialSmoothingModel modelMin,
                        SimpleExponentialSmoothingModel modelMid,
                        SimpleExponentialSmoothingModel modelMax,
                        double alphaTolerance)
    {
        double alphaMin = modelMin.getAlpha();
        double alphaMid = modelMid.getAlpha();
        double alphaMax = modelMax.getAlpha();

        // If we're not making much ground, then we're done
        if (Math.abs(alphaMid-alphaMin)<alphaTolerance
            && Math.abs(alphaMax-alphaMid)<alphaTolerance )
            return modelMid;

        SimpleExponentialSmoothingModel model[]
            = new SimpleExponentialSmoothingModel[5];
        model[0] = modelMin;
        model[1]
            = new SimpleExponentialSmoothingModel((alphaMin+alphaMid)/2.0);
        model[2] = modelMid;
        model[3]
            = new SimpleExponentialSmoothingModel((alphaMid+alphaMax)/2.0);
        model[4] = modelMax;

        for ( int m=0; m<5; m++ )
            model[m].init(dataSet);

        int bestModelIndex = 0;
        for ( int m=1; m<5; m++ )
            if ( model[m].getMSE() < model[bestModelIndex].getMSE() )
                bestModelIndex = m;

        switch ( bestModelIndex )
            {
            case 1:
                // Reduce maximums
                // Can discard models 3 and 4
                model[3] = null;
                model[4] = null;
                return findBestFit( dataSet, model[0], model[1], model[2],
                                    alphaTolerance );

            case 2:
                // Can discard models 0 and 4
                model[0] = null;
                model[4] = null;
                return findBestFit( dataSet, model[1], model[2], model[3],
                                    alphaTolerance );
                
            case 3:
                // Reduce minimums
                // Can discard models 0 and 1
                model[0] = null;
                model[1] = null;
                return findBestFit( dataSet, model[2], model[3], model[4],
                                    alphaTolerance );

            case 0:
            case 4:
                // We're done???
                //  - these cases should not occur, unless the MSE for model[0]
                //    and model[1], or model[3] and model[4] are equal
                break;
            }

        // Release all but the best model constructed so far
        for ( int m=0; m<5; m++ )
            if ( m != bestModelIndex )
                model[m] = null;
        
        return model[bestModelIndex];
    }

    /**
     * Constructs a new simple exponential smoothing forecasting model, using
     * the specified smoothing constant. For a valid model to be constructed,
     * you should call init and pass in a data set containing a series of data
     * points with the time variable initialized to identify the independent
     * variable.
     * @param alpha the smoothing constant to use for this exponential
     * smoothing model. Must be a value in the range 0.0-1.0.
     * @throws IllegalArgumentException if the value of the smoothing constant
     * is invalid - outside the range 0.0-1.0.
     */
    public SimpleExponentialSmoothingModel( double alpha )
    {
        this(alpha,HUNTER);
    }
    
    /**
     * Constructs a new exponential smoothing forecasting model, using the
     * given name as the independent variable and the specified smoothing
     * constant.
     * @param independentVariable the name of the independent variable - or
     * time variable - to use in this model.
     * @param alpha the smoothing constant to use for this exponential
     * smoothing model. Must be a value in the range 0.0-1.0.
     * @throws IllegalArgumentException if the value of the smoothing constant
     * is invalid - outside the range 0.0-1.0.
     * @deprecated As of 0.4, replaced by {@link #SimpleExponentialSmoothingModel(double)}.
     */
    public SimpleExponentialSmoothingModel( String independentVariable,
                                            double alpha )
    {
        this(independentVariable,alpha,HUNTER);
    }
    
    /**
     * Constructs a new exponential smoothing forecasting model, using the
     * given name as the independent variable and the specified smoothing
     * constant. For a valid model to be constructed, you should call init and
     * pass in a data set containing a series of data points with the time
     * variable initialized to identify the independent variable.
     * @param alpha the smoothing constant to use for this exponential
     * smoothing model. Must be a value in the range 0.0-1.0.
     * @param approach determines which approach to use for the forecasting.
     * This must be either {@link #HUNTER} - the default - or {@link #ROBERTS}.
     * @throws IllegalArgumentException if the value of the smoothing constant
     * is invalid - outside the range 0.0-1.0.
     */
    public SimpleExponentialSmoothingModel( double alpha,
                                            int approach )
    {
        if ( alpha < 0.0 || alpha > 1.0 )
            throw new IllegalArgumentException("SimpleExponentialSmoothingModel: Invalid smoothing constant, " + alpha + " - must be in the range 0.0-1.0.");
        
        this.alpha = alpha;
        this.approach = approach;
    }
    
    /**
     * Constructs a new exponential smoothing forecasting model, using the
     * given name as the independent variable and the specified smoothing
     * constant.
     * @param independentVariable the name of the independent variable - or
     * time variable - to use in this model.
     * @param alpha the smoothing constant to use for this exponential
     * smoothing model. Must be a value in the range 0.0-1.0.
     * @param approach determines which approach to use for the forecasting.
     * This must be either {@link #HUNTER} - the default - or {@link #ROBERTS}.
     * @throws IllegalArgumentException if the value of the smoothing constant
     * is invalid - outside the range 0.0-1.0.
     * @deprecated As of 0.4, replaced by {@link #SimpleExponentialSmoothingModel(double,int)}.
     */
    public SimpleExponentialSmoothingModel( String independentVariable,
                                            double alpha,
                                            int approach )
    {
        super(independentVariable);
        
        if ( alpha < 0.0 || alpha > 1.0 )
            throw new IllegalArgumentException("SimpleExponentialSmoothingModel: Invalid smoothing constant, " + alpha + " - must be in the range 0.0-1.0.");
        
        this.alpha = alpha;
        this.approach = approach;
    }
    
    /**
     * Returns the forecast value of the dependent variable for the given
     * value of the independent time variable using a single exponential
     * smoothing model. The model used here follows the formulation of
     * Hunter that combines the observation and forecast values from the
     * previous period.
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
        // As a starting point, we set the first forecast value to be the
        //  same as the first observed value
        if ( timeValue-getMinimumTimeValue() < TOLERANCE )
            return getObservedValue( timeValue );

        double previousTime = timeValue - getTimeInterval();
        
        double forecast;
        try
            {
                if ( approach == ROBERTS )
                    forecast
                        = alpha*getObservedValue(timeValue)
                        + (1.0-alpha)*getForecastValue(previousTime);
                else // Default - Hunter's formula
                    forecast
                        = alpha*getObservedValue(previousTime)
                        + (1.0-alpha)*getForecastValue(previousTime);
            }
        catch ( IllegalArgumentException iaex )
            {
                // For "future" forecasts, all we can do is use the forecast
                //  from the last period
                if ( timeValue > getMaximumTimeValue()-TOLERANCE )
                    return getForecastValue( getMaximumTimeValue() );

                throw iaex;
            }
        
        
        return forecast;
    }
    
    /**
     * Returns the current number of periods used in this model. This is also
     * the minimum number of periods required in order to produce a valid
     * forecast. Strictly speaking, for simple exponential smoothing only one
     * previous period is needed - though such a model would be of relatively
     * little use. At least five to ten prior observations would be preferred.
     * @return the minimum number of periods used in this model.
     */
    protected int getNumberOfPeriods()
    {
        return 1;
    }
    
    /**
     * Returns the number of predictors used by the underlying model.
     * @return the number of predictors used by the underlying model.
     * @since 0.5
     */
    public int getNumberOfPredictors()
    {
        return 1;
    }
    
    /**
     * Returns the value of the smoothing constant, alpha, used in this model.
     * @return the value of the smoothing constant, alpha.
     */
    public double getAlpha()
    {
        return alpha;
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
        return "simple exponential smoothing";
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
        return "Simple exponential smoothing model (using "
            + (approach==ROBERTS?"Roberts":"Hunters")
            + " formula), with a smoothing constant of "
            + alpha + " and using an independent variable of "
            + getIndependentVariable();
    }
}
// Local Variables:
// tab-width: 4
// End:
