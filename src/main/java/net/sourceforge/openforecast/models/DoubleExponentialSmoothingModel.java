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
 * Double exponential smoothing - also known as Holt exponential smoothing
 * - is a refinement of the popular simple exponential smoothing model but
 * adds another component which takes into account any trend in the data.
 * Simple exponential smoothing models work best with data where there are no
 * trend or seasonality components to the data. When the data exhibits either
 * an increasing or decreasing trend over time, simple exponential smoothing
 * forecasts tend to lag behind observations. Double exponential smoothing is
 * designed to address this type of data series by taking into account any
 * trend in the data.
 *
 * <p>Note that double exponential smoothing still does not address
 * seasonality. For better exponentially smoothed forecasts using data where
 * there is expected or known to be seasonal variation in the data, use triple
 * exponential smoothing.
 *
 * <p>As with simple exponential smoothing, in double exponential smoothing
 * models past observations are given exponentially smaller weights as the
 * observations get older. In other words, recent observations are given
 * relatively more weight in forecasting than the older observations.
 *
 * <p>There are two equations associated with Double Exponential Smoothing.
 *
 * <ul>
 *  <li><code>f<sub>t</sub> = a.Y<sub>t</sub>+(1-a)(f<sub>t-1</sub>+b<sub>t-1</sub>)</code></li>
 *  <li><code>b<sub>t</sub> = g.(f<sub>t</sub>-f<sub>t-1</sub>)+(1-g).b<sub>t-1</sub></code></li>
 * </ul>
 *
 * <p>where:
 * <ul>
 *  <li><code>Y<sub>t</sub></code> is the observed value at time t.</li>
 *  <li><code>f<sub>t</sub></code> is the forecast at time t.</li>
 *  <li><code>b<sub>t</sub></code> is the estimated slope at time t.</li>
 *  <li><code>a</code> - representing alpha - is the first smoothing constant, used to smooth the observations.</li>
 *  <li><code>g</code> - representing gamma - is the second smoothing constant, used to smooth the trend.</li>
 * </ul>
 *
 * <p>To initialize the double exponential smoothing model,
 * <code>f<sub>1</sub></code> is set to <code>Y<sub>1</sub></code>, and the
 * initial slope <code>b<sub>1</sub></code> is set to the difference between
 * the first two observations; i.e. <code>Y<sub>2</sub>-Y<sub>1</sub></code>.
 * Although there are other ways to initialize the model, as of the time of
 * writing, these alternatives are not available in this implementation.
 * Future implementations of this model <em>may</em> offer these options.
 *
 * <h2>Choosing values for the smoothing constants</h2>
 * <p>The smoothing constants must be a values in the range 0.0-1.0. But, what
 * are the "best" values to use for the smoothing constants? This depends on
 * the data series being modeled.
 *
 * <p>In general, the speed at which the older responses are dampened
 * (smoothed) is a function of the value of the smoothing constant. When this
 * smoothing constant is close to 1.0, dampening is quick - more weight is
 * given to recent observations - and when it is close to 0.0, dampening is
 * slow - and relatively less weight is given to recent observations.
 *
 * <p>The best value for the smoothing constant is the one that results in the
 * smallest mean of the squared errors (or other similar accuracy indicator).
 * The  {@link net.sourceforge.openforecast.Forecaster} class can help with
 * selection of the best values for the smoothing constants.
 * @author Steven R. Gould
 * @since 0.4
 * @see <a href="http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc433.htm">Engineering Statistics Handbook, 6.4.3.3 Double Exponential Smoothing</a>
 */
public class DoubleExponentialSmoothingModel extends AbstractTimeBasedModel
{
    /**
     * The default value of the tolerance permitted in the estimates of the
     * smoothing constants in the {@link #getBestFitModel} methods.
     */
    private static double DEFAULT_SMOOTHING_CONSTANT_TOLERANCE = 0.001;
    
    /**
     * The smoothing constant used in this exponential smoothing model.
     */
    private double alpha;
    
    /**
     * The second smoothing constant (gamma) used in this exponential
     * smoothing model. This is used to smooth the trend.
     */
    private double gamma;
    
    /**
     * Provides a cache of calculated slopeValues. Since these values are
     * used very frequently when calculating forecast values, it is more
     * efficient to cache the previously calculated slope values for future
     * use.
     */
    private DataSet slopeValues;
    
    /**
     * Factory method that returns a "best fit" double exponential smoothing
     * model for the given data set. This, like the overloaded
     * {@link #getBestFitModel(DataSet,double,double)}, attempts to derive
     * "good" - hopefully near optimal - values for the alpha and gamma
     * smoothing constants.
     * @param dataSet the observations for which a "best fit" double
     * exponential smoothing model is required.
     * @return a best fit double exponential smoothing model for the given
     * data set.
     * @see #getBestFitModel(DataSet,double,double)
     */
    public static DoubleExponentialSmoothingModel
        getBestFitModel( DataSet dataSet )
    {
        return getBestFitModel( dataSet,
                                DEFAULT_SMOOTHING_CONSTANT_TOLERANCE,
                                DEFAULT_SMOOTHING_CONSTANT_TOLERANCE );
    }
    
    /**
     * Factory method that returns a best fit double exponential smoothing
     * model for the given data set. This, like the overloaded
     * {@link #getBestFitModel(DataSet)}, attempts to derive "good" -
     * hopefully near optimal - values for the alpha and gamma smoothing
     * constants.
     *
     * <p>To determine which model is "best", this method currently uses only
     * the Mean Squared Error (MSE). Future versions may use other measures in
     * addition to the MSE. However, the resulting "best fit" model - and the
     * associated values of alpha and gamma - is expected to be very similar
     * either way.
     *
     * <p>Note that the approach used to calculate the best smoothing
     * constants - alpha and gamma - <em>may</em> end up choosing values near
     * a local optimum. In other words, there <em>may</em> be other values for
     * alpha and gamma that result in an even better model.
     * @param dataSet the observations for which a "best fit" double
     * exponential smoothing model is required.
     * @param alphaTolerance the required precision/accuracy - or tolerance
     * of error - required in the estimate of the alpha smoothing constant.
     * @param gammaTolerance the required precision/accuracy - or tolerance
     * of error - required in the estimate of the gamma smoothing constant.
     * @return a best fit double exponential smoothing model for the given
     * data set.
     */
    public static DoubleExponentialSmoothingModel
        getBestFitModel( DataSet dataSet,
                         double alphaTolerance, double gammaTolerance )
    {
        DoubleExponentialSmoothingModel model1
            = findBestGamma( dataSet, 0.0, 0.0, 1.0, gammaTolerance );
        DoubleExponentialSmoothingModel model2
            = findBestGamma( dataSet, 0.5, 0.0, 1.0, gammaTolerance );
        DoubleExponentialSmoothingModel model3
            = findBestGamma( dataSet, 1.0, 0.0, 1.0, gammaTolerance );
        
        // First rough estimate of alpha and gamma to the nearest 0.1
        DoubleExponentialSmoothingModel bestModel
            = findBest( dataSet, model1, model2, model3,
                        alphaTolerance, gammaTolerance );
        
        return bestModel;
    }
    
    /**
     * Performs a non-linear - yet somewhat intelligent - search for the best
     * values for the smoothing coefficients alpha and gamma for the given
     * data set.
     *
     * <p>For the given data set, and models with a small, medium and large
     * value of the alpha smoothing constant, returns the best fit model where
     * the value of the alpha and gamma (trend) smoothing constants are within
     * the given tolerances.
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
     * @param gammaTolerance the tolerance within which the gamma value is
     * required. Must be considerably less than 1.0. However, note that the
     * smaller this value the longer it will take to diverge on a best fit
     * model. This value can be the same as, greater than or less than the
     * value of the alphaTolerance parameter. It makes no difference - at
     * least to this code.
     */
    private static DoubleExponentialSmoothingModel findBest(
        DataSet dataSet,
        DoubleExponentialSmoothingModel modelMin,
        DoubleExponentialSmoothingModel modelMid,
        DoubleExponentialSmoothingModel modelMax,
        double alphaTolerance,
        double gammaTolerance )
    {
        double alphaMin = modelMin.getAlpha();
        double alphaMid = modelMid.getAlpha();
        double alphaMax = modelMax.getAlpha();
        
        // If we're not making much ground, then we're done
        if (Math.abs(alphaMid-alphaMin)<alphaTolerance
            && Math.abs(alphaMax-alphaMid)<alphaTolerance )
            return modelMid;
        
        DoubleExponentialSmoothingModel model[]
            = new DoubleExponentialSmoothingModel[5];
        model[0] = modelMin;
        model[1] = findBestGamma( dataSet, (alphaMin+alphaMid)/2.0,
                                  0.0, 1.0, gammaTolerance );
        model[2] = modelMid;
        model[3] = findBestGamma( dataSet, (alphaMid+alphaMax)/2.0,
                                  0.0, 1.0, gammaTolerance );
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
                return findBest( dataSet, model[0], model[1], model[2],
                                 alphaTolerance, gammaTolerance );
                
            case 2:
                // Can discard models 0 and 4
                model[0] = null;
                model[4] = null;
                return findBest( dataSet, model[1], model[2], model[3],
                                 alphaTolerance, gammaTolerance );
                
            case 3:
                // Reduce minimums
                // Can discard models 0 and 1
                model[0] = null;
                model[1] = null;
                return findBest( dataSet, model[2], model[3], model[4],
                                 alphaTolerance, gammaTolerance );
                
            case 0:
            case 4:
                // We're done???
                break;
            }
        
        // Release all but the best model constructed so far
        for ( int m=0; m<5; m++ )
            if ( m != bestModelIndex )
                model[m] = null;
        
        return model[bestModelIndex];
    }
    
    /**
     * For the given value of the alpha smoothing constant, returns the best
     * fit model where the value of the gamma (trend) smoothing constant is
     * between gammaMin and gammaMax. This method will continually try to
     * refine the estimate of gamma until a tolerance of less than
     * gammaTolerance is achieved.
     *
     * <p>Note that the descriptions of the parameters below include a
     * discussion of valid values. However, since this is a private method and
     * to help improve performance, we don't provide any validation of these
     * parameters. Using invalid values may lead to unexpected results.
     * @param dataSet the data set for which a best fit model is required.
     * @param alpha the (fixed) value of the alpha smoothing constant to use
     * for the best fit model.
     * @param gammaMin the minimum value of the gamma (trend) smoothing
     * constant accepted in the resulting best fit model. Must be greater than
     * (or equal to) 0.0 and less than gammaMax.
     * @param gammaMax the maximum value of the gamma (trend) smoothing
     * constant accepted in the resulting best fit model. Must be greater than
     * gammaMin and less than (or equal to) 1.0.
     * @param gammaTolerance the tolerance within which the gamma value is
     * required. Must be considerably less than 1.0. However, note that the
     * smaller this value the longer it will take to diverge on a best fit
     * model.
     */
    private static DoubleExponentialSmoothingModel findBestGamma(
                                                                 DataSet dataSet, double alpha,
                                                                 double gammaMin, double gammaMax,
                                                                 double gammaTolerance )
    {
        int stepsPerIteration = 10;
        
        if ( gammaMin < 0.0 )
            gammaMin = 0.0;
        if ( gammaMax > 1.0 )
            gammaMax = 1.0;
        
        DoubleExponentialSmoothingModel bestModel
            = new DoubleExponentialSmoothingModel( alpha, gammaMin );
        bestModel.init(dataSet);
        
        double initialMSE = bestModel.getMSE();
        
        boolean gammaImproving = true;
        double gammaStep = (gammaMax-gammaMin)/stepsPerIteration;
        double gamma = gammaMin + gammaStep;
        for ( ; gamma<=gammaMax || gammaImproving; )
            {
                DoubleExponentialSmoothingModel model
                    = new DoubleExponentialSmoothingModel( alpha, gamma );
                model.init( dataSet );
                
                if ( model.getMSE() < bestModel.getMSE() )
                    bestModel = model;
                else
                    gammaImproving = false;
                
                gamma += gammaStep;
                if ( gamma > 1.0 )
                    gammaImproving = false;
            }
        
        // If we're making progress, then try to refine the gamma estimate
        if ( bestModel.getMSE() < initialMSE
             && gammaStep > gammaTolerance )
            {
                // Can this be further refined - improving efficiency - by
                //  only searching in the range gamma-gammaStep/2 to
                //  gamma+gammaStep/2 ?
                return findBestGamma( dataSet, bestModel.getAlpha(),
                                      bestModel.getGamma()-gammaStep,
                                      bestModel.getGamma()+gammaStep,
                                      gammaTolerance );
            }
        
        return bestModel;
    }
    
    /**
     * Constructs a new double exponential smoothing forecasting model, using
     * the given smoothing constants - alpha and gamma. For a valid model to
     * be constructed, you should call init and pass in a data set containing
     * a series of data points with the time variable initialized to identify
     * the independent variable.
     * @param alpha the smoothing constant to use for this exponential
     * smoothing model. Must be a value in the range 0.0-1.0.
     * @param gamma the second smoothing constant, gamma to use in this model
     * to smooth the trend. Must be a value in the range 0.0-1.0.
     * @throws IllegalArgumentException if the value of either smoothing
     * constant is invalid - outside the range 0.0-1.0.
     */
    public DoubleExponentialSmoothingModel( double alpha,
                                            double gamma )
    {
        if ( alpha < 0.0  ||  alpha > 1.0 )
            throw new IllegalArgumentException("DoubleExponentialSmoothingModel: Invalid smoothing constant, " + alpha + " - must be in the range 0.0-1.0.");
        
        if ( gamma < 0.0  ||  gamma > 1.0 )
            throw new IllegalArgumentException("DoubleExponentialSmoothingModel: Invalid smoothing constant, gamma=" + gamma + " - must be in the range 0.0-1.0.");
        
        slopeValues = new DataSet();
        
        this.alpha = alpha;
        this.gamma = gamma;
    }
    
    /**
     * Returns the forecast value of the dependent variable for the given
     * value of the (independent) time variable using a single exponential
     * smoothing model. See the class documentation for details on the
     * formulation used.
     * @param t the value of the time variable for which a forecast
     * value is required.
     * @return the forecast value of the dependent variable at time, t.
     * @throws IllegalArgumentException if there is insufficient historical
     * data - observations passed to init - to generate a forecast for the
     * given time value.
     */
    protected double forecast( double t )
        throws IllegalArgumentException
    {
        double previousTime = t - getTimeInterval();
        
        // As a starting point, we set the first forecast value to be
        //  the same as the observed value
        if ( previousTime < getMinimumTimeValue()+TOLERANCE )
            return getObservedValue( t );
        
        try
            {
                double b = getSlope( previousTime );
                
                double forecast
                    = alpha*getObservedValue(t)
                    + (1.0-alpha)*(getForecastValue(previousTime)+b);
                
                return forecast;
            }
        catch ( IllegalArgumentException iaex )
            {
                double maxTimeValue = getMaximumTimeValue();
                
                double b = getSlope( maxTimeValue-getTimeInterval() );
                double forecast
                    = getForecastValue(maxTimeValue)
                    + (t-maxTimeValue)*b;
                
                return forecast;
            }
    }
    
    /**
     * Calculates and returns the slope for the given time period. Except
     * for the initial periods - where forecasts are not available - the
     * slope is calculated using forecast values, and not observed values.
     * See the class documentation for details on the formulation used.
     * @param time the time value for which the slope is required.
     * @return the slope of the data at the given period of time.
     * @param IllegalArgumentException if the slope cannot be determined for
     * the given time period.
     */
    private double getSlope( double time )
        throws IllegalArgumentException
    {
        // TODO: Optimize this search by having data set sorted by time
        
        // Search for previously calculated - and saved - slope value
        String timeVariable = getTimeVariable();
        Iterator<DataPoint> it = slopeValues.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                double dpTimeValue = dp.getIndependentValue( timeVariable );
                if ( Math.abs(time-dpTimeValue) < TOLERANCE )
                    return dp.getDependentValue();
            }
        
        // Saved slope not found, so calculate it
        //  (and save it for future reference)
        double previousTime = time - getTimeInterval();
        double slope = 0.0;
        
        // Initial condition for first periods
        if ( previousTime < getMinimumTimeValue()+TOLERANCE )
            slope = getObservedValue(time)-getObservedValue(previousTime);
        else
            slope
                = gamma*(forecast(time)-forecast(previousTime))
                + (1-gamma)*getSlope(previousTime);
        
        DataPoint dp = new Observation( slope );
        dp.setIndependentValue( timeVariable, time );
        slopeValues.add( dp );
        
        return slope;
    }
    
    /**
     * Returns the current number of periods used in this model. This is also
     * the minimum number of periods required in order to produce a valid
     * forecast. Strictly speaking, for double exponential smoothing only two
     * previous periods are needed - though such a model would be of relatively
     * little use. At least ten to fifteen prior observations would be
     * preferred.
     * @return the minimum number of periods used in this model.
     */
    protected int getNumberOfPeriods()
    {
        return 2;
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
     * Since this version of double exponential smoothing uses the current
     * observation to calculate a smoothed value, we must override the
     * calculation of the accuracy indicators.
     * @param dataSet the DataSet to use to evaluate this model, and to
     *        calculate the accuracy indicators against.
     */
    protected void calculateAccuracyIndicators( DataSet dataSet )
    {
        // Note that the model has been initialized
        initialized = true;
        
        // Reset various helper summations
        double sumErr = 0.0;
        double sumAbsErr = 0.0;
        double sumAbsPercentErr = 0.0;
        double sumErrSquared = 0.0;
        
        String timeVariable = getTimeVariable();
        double timeDiff = getTimeInterval();
        
        // Calculate the Sum of the Absolute Errors
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                // Get next data point
                DataPoint dp = it.next();
                double x = dp.getDependentValue();
                double time = dp.getIndependentValue( timeVariable );
                double previousTime = time - timeDiff;
                
                // Get next forecast value, using one-period-ahead forecast
                double forecastValue
                    = getForecastValue( previousTime )
                    + getSlope( previousTime );
                
                // Calculate error in forecast, and update sums appropriately
                double error = forecastValue - x;
                sumErr += error;
                sumAbsErr += Math.abs( error );
                sumAbsPercentErr += Math.abs( error / x );
                sumErrSquared += error*error;
            }
        
        // Initialize the accuracy indicators
        int n = dataSet.size();
        
        accuracyIndicators.setBias( sumErr / n );
        accuracyIndicators.setMAD( sumAbsErr / n );
        accuracyIndicators.setMAPE( sumAbsPercentErr / n );
        accuracyIndicators.setMSE( sumErrSquared / n );
        accuracyIndicators.setSAE( sumAbsErr );
    }
    
    /**
     * Returns the value of the smoothing constant, alpha, used in this model.
     * @return the value of the smoothing constant, alpha.
     * @see #getGamma
     */
    public double getAlpha()
    {
        return alpha;
    }
    
    /**
     * Returns the value of the trend smoothing constant, gamma, used in this
     * model.
     * @return the value of the trend smoothing constant, gamma.
     * @see #getAlpha
     */
    public double getGamma()
    {
        return gamma;
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
        return "double exponential smoothing";
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
        return "Double exponential smoothing model, with smoothing constants of alpha="
            + alpha + ", gamma="
            + gamma + ", and using an independent variable of "
            + getIndependentVariable();
    }
}
// Local Variables:
// tab-width: 4
// End:
