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

package net.sourceforge.openforecast.models;


import java.util.Iterator;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;


/**
 * Triple exponential smoothing - also known as the Winters method - is a
 * refinement of the popular double exponential smoothing model but adds
 * another component which takes into account any seasonality - or periodicity
 * - in the data.
 *
 * <p>Simple exponential smoothing models work best with data where there are
 * no trend or seasonality components to the data. When the data exhibits
 * either an increasing or decreasing trend over time, simple exponential
 * smoothing forecasts tend to lag behind observations. Double exponential
 * smoothing is designed to address this type of data series by taking into
 * account any trend in the data. However, neither of these exponential
 * smoothing models address any seasonality in the data.
 *
 * <p>For better exponentially smoothed forecasts of data where there is
 * expected or known to be seasonal variation in the data, use triple
 * exponential smoothing.
 *
 * <p>As with simple exponential smoothing, in triple exponential smoothing
 * models past observations are given exponentially smaller weights as the
 * observations get older. In other words, recent observations are given
 * relatively more weight in forecasting than the older observations. This is
 * true for all terms involved - namely, the base level
 * <code>L<sub>t</sub></code>, the trend <code>T<sub>t</sub></code> as well as
 * the seasonality index <code>s<sub>t</sub></code>.
 *
 * <p>There are four equations associated with Triple Exponential Smoothing.
 *
 * <ul>
 *  <li><code>L<sub>t</sub> = a.(x<sub>t</sub>/s<sub>t-c</sub>)+(1-a).(L<sub>t-1</sub>+T<sub>t-1</sub>)</code></li>
 *  <li><code>T<sub>t</sub> = b.(L<sub>t</sub>-L<sub>t-1</sub>)+(1-b).T<sub>t-1</sub></code></li>
 *  <li><code>s<sub>t</sub> = g.(x<sub>t</sub>/L<sub>t</sub>)+(1-g).s<sub>t-c</sub></code></li>
 *  <li><code>f<sub>t,k</sub> = (L<sub>t</sub>+k.T<sub>t</sub>).s<sub>t+k-c</sub></code></li>
 * </ul>
 *
 * <p>where:
 * <ul>
 *  <li><code>L<sub>t</sub></code> is the estimate of the base value at time
 *      <code>t</code>. That is, the estimate for time <code>t</code> after
 *      eliminating the effects of seasonality and trend.</li>
 *  <li><code>a</code> - representing alpha - is the first smoothing
 *      constant, used to smooth <code>L<sub>t</sub></code>.</li>
 *  <li><code>x<sub>t</sub></code> is the observed value at time t.</li>
 *  <li><code>s<sub>t</sub></code> is the seasonal index at time t.</li>
 *  <li><code>c</code> is the number of periods in the seasonal pattern. For
 *      example, c=4 for quarterly data, or c=12 for monthly data.</li>
 *  <li><code>T<sub>t</sub></code> is the estimated trend at time t.</li>
 *  <li><code>b</code> - representing beta - is the second smoothing
 *      constant, used to smooth the trend estimates.</li>
 *  <li><code>g</code> - representing gamma - is the third smoothing constant,
 *      used to smooth the seasonality estimates.</li>
 *  <li><code>f<sub>t,k</sub></code> is the forecast at time the end of period
 *      <code>t</code> for the period <code>t+k</code>.</li>
 * </ul>
 *
 * <p>There are a variety of different ways to come up with initial values for
 * the triple exponential smoothing model. The approach implemented here uses
 * the first two "years" (or complete cycles) of data to come up with initial
 * values for <code>L<sub>t</sub></code>, <code>T<sub>t</sub></code> and
 * <code>s<sub>t</sub></code>. Therefore, at least two complete cycles of data
 * are required to initialize the model. For best results, more data is
 * recommended - ideally a minimum of 4 or 5 complete cycles. This gives the
 * model chance to better adapt to the data, instead of relying on getting
 * - guessing - good estimates for the initial conditions.
 *
 * <h2>Choosing values for the smoothing constants</h2>
 * <p>The smoothing constants <code>a</code>, <code>b</code>, and
 * <code>g</code> each must be a value in the range 0.0-1.0. But, what are the
 * "best" values to use for the smoothing constants? This depends on the data
 * series being modeled.
 *
 * <p>In general, the speed at which the older responses are dampened
 * (smoothed) is a function of the value of the smoothing constant. When this
 * smoothing constant is close to 1.0, dampening is quick - more weight is
 * given to recent observations - and when it is close to 0.0, dampening is
 * slow - and relatively less weight is given to recent observations.
 *
 * <p>The best value for the smoothing constant is the one that results in the
 * smallest mean of the squared errors (or other similar accuracy indicator).
 * The  {@link #getBestFitModel} static methods can help with the selection of
 * the best values for the smoothing constants, though the results obtained
 * from these methods should always be validated. If any of the "best fit"
 * smoothing constants turns out to be 1.0, you may want to be a little
 * suspicious. This may be an indication that you really need to use more data
 * to initialize the model.
 * @author Steven R. Gould
 * @since 0.4
 * @see <a href="http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc435.htm">Engineering Statistics Handbook, 6.4.3.5 Triple Exponential Smoothing</a>
 */
public class TripleExponentialSmoothingModel extends AbstractTimeBasedModel
{
    /**
     * The default value of the tolerance permitted in the estimates of the
     * smoothing constants in the {@link #getBestFitModel} methods.
     */
    private static double DEFAULT_SMOOTHING_CONSTANT_TOLERANCE = 0.001;

    /**
     * Minimum number of years of data required.
     */
    private static int NUMBER_OF_YEARS = 2;

    /**
     * The overall smoothing constant, alpha, used in this exponential
     * smoothing model.
     */
    private double alpha;
    
    /**
     * The second smoothing constant, beta, used in this exponential
     * smoothing model for trend smoothing.
     */
    private double beta;
    
    /**
     * The third smoothing constant (gamma) used in this exponential smoothing
     * model for the seasonal smoothing.
     */
    private double gamma;

    /**
     * Stores the number of periods per year in this exponential smoothing
     * model. Note that, in spite of the name, this does not limit the
     * functionality of this model to seasonality within a year. It is quite
     * possible that the "seasonality" - or periodicity - of interest is the
     * variability by day within a week, or any other period.
     */
    private int periodsPerYear = 0;

    /**
     * Stores the maximum observed time. Initialized in {@link #init}.
     */
    private double maxObservedTime;

    /**
     * Provides a cache of calculated baseValues. The "base" represents an
     * estimate of the underlying value of the data after accounting for
     * - i.e. removing - the effects of trend and seasonality. Since these
     * values are used very frequently when calculating forecast values, it
     * is more efficient to cache the previously calculated base values for
     * future use.
     */
    private DataSet baseValues;

    /**
     * Provides a cache of calculated trendValues. Since these values are
     * used very frequently when calculating forecast values, it is more
     * efficient to cache the previously calculated trend values for future
     * use.
     */
    private DataSet trendValues;

    /**
     * Provides a cache of calculated seasonal indexes. Since these values
     * are used very frequently when calculating forecast values, it is more
     * efficient to cache the previously calculated seasonal index values
     * for future use than have to recalculate them each time.
     */
    private DataSet seasonalIndex;

    /**
     * Factory method that returns a "best fit" triple exponential smoothing
     * model for the given data set. This, like the overloaded
     * {@link #getBestFitModel(DataSet,double,double)}, attempts to derive
     * "good" - hopefully near optimal - values for the alpha and beta
     * smoothing constants.
     * @param dataSet the observations for which a "best fit" triple
     * exponential smoothing model is required.
     * @return a best fit triple exponential smoothing model for the given
     * data set.
     * @see #getBestFitModel(DataSet,double,double)
     */
    public static TripleExponentialSmoothingModel
        getBestFitModel( DataSet dataSet )
    {
        return getBestFitModel( dataSet,
                                DEFAULT_SMOOTHING_CONSTANT_TOLERANCE,
                                DEFAULT_SMOOTHING_CONSTANT_TOLERANCE );
    }

    /**
     * Factory method that returns a best fit triple exponential smoothing
     * model for the given data set. This, like the overloaded
     * {@link #getBestFitModel(DataSet)}, attempts to derive "good" -
     * hopefully near optimal - values for the alpha and beta smoothing
     * constants.
     *
     * <p>To determine which model is "best", this method currently uses only
     * the Mean Squared Error (MSE). Future versions may use other measures in
     * addition to the MSE. However, the resulting "best fit" model - and the
     * associated values of alpha and beta - is expected to be very similar
     * either way.
     *
     * <p>Note that the approach used to calculate the best smoothing
     * constants - alpha and beta - <em>may</em> end up choosing values near
     * a local optimum. In other words, there <em>may</em> be other values for
     * alpha and beta that result in an even better model.
     * @param dataSet the observations for which a "best fit" triple
     * exponential smoothing model is required.
     * @param alphaTolerance the required precision/accuracy - or tolerance
     * of error - required in the estimate of the alpha smoothing constant.
     * @param betaTolerance the required precision/accuracy - or tolerance
     * of error - required in the estimate of the beta smoothing constant.
     * @return a best fit triple exponential smoothing model for the given
     * data set.
     */
    public static TripleExponentialSmoothingModel
        getBestFitModel( DataSet dataSet,
                         double alphaTolerance, double betaTolerance )
    {
        // Check we have the minimum amount of data points
        if ( dataSet.size() < NUMBER_OF_YEARS*dataSet.getPeriodsPerYear() )
            throw new IllegalArgumentException("TripleExponentialSmoothing models require a minimum of a full two years of data in the data set.");

        // Check alphaTolerance is in the expected range
        if ( alphaTolerance < 0.0  || alphaTolerance > 0.5 )
            throw new IllegalArgumentException("The value of alphaTolerance must be significantly less than 1.0, and no less than 0.0. Suggested value: "+DEFAULT_SMOOTHING_CONSTANT_TOLERANCE);

        // Check betaTolerance is in the expected range
        if ( betaTolerance < 0.0  || betaTolerance > 0.5 )
            throw new IllegalArgumentException("The value of betaTolerance must be significantly less than 1.0, and no less than 0.0. Suggested value: "+DEFAULT_SMOOTHING_CONSTANT_TOLERANCE);

        TripleExponentialSmoothingModel model1
            = findBestBeta( dataSet, 0.0, 0.0, 1.0, betaTolerance );
        TripleExponentialSmoothingModel model2
            = findBestBeta( dataSet, 0.5, 0.0, 1.0, betaTolerance );
        TripleExponentialSmoothingModel model3
            = findBestBeta( dataSet, 1.0, 0.0, 1.0, betaTolerance );

        // First rough estimate of alpha and beta to the nearest 0.1
        TripleExponentialSmoothingModel bestModel
            = findBest( dataSet, model1, model2, model3,
                        alphaTolerance, betaTolerance );

        return bestModel;
    }

    /**
     * Performs a non-linear - yet somewhat intelligent - search for the best
     * values for the smoothing coefficients alpha and beta for the given
     * data set.
     *
     * <p>For the given data set, and models with a small, medium and large
     * value of the alpha smoothing constant, returns the best fit model where
     * the value of the alpha and beta (trend) smoothing constants are within
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
     * @param betaTolerance the tolerance within which the beta value is
     * required. Must be considerably less than 1.0. However, note that the
     * smaller this value the longer it will take to diverge on a best fit
     * model. This value can be the same as, greater than or less than the
     * value of the alphaTolerance parameter. It makes no difference - at
     * least to this code.
     */
    private static TripleExponentialSmoothingModel findBest(
                        DataSet dataSet,
                        TripleExponentialSmoothingModel modelMin,
                        TripleExponentialSmoothingModel modelMid,
                        TripleExponentialSmoothingModel modelMax,
                        double alphaTolerance,
                        double betaTolerance)
    {
        double alphaMin = modelMin.getAlpha();
        double alphaMid = modelMid.getAlpha();
        double alphaMax = modelMax.getAlpha();

        // If we're not making much ground, then we're done
        if (Math.abs(alphaMid-alphaMin)<alphaTolerance
            && Math.abs(alphaMax-alphaMid)<alphaTolerance )
            return modelMid;

        TripleExponentialSmoothingModel model[]
            = new TripleExponentialSmoothingModel[5];
        model[0] = modelMin;
        model[1] = findBestBeta( dataSet, (alphaMin+alphaMid)/2.0,
                                  0.0, 1.0, betaTolerance );
        model[2] = modelMid;
        model[3] = findBestBeta( dataSet, (alphaMid+alphaMax)/2.0,
                                  0.0, 1.0, betaTolerance );
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
                                 alphaTolerance, betaTolerance );

            case 2:
                // Can discard models 0 and 4
                model[0] = null;
                model[4] = null;
                return findBest( dataSet, model[1], model[2], model[3],
                                 alphaTolerance, betaTolerance );
                
            case 3:
                // Reduce minimums
                // Can discard models 0 and 1
                model[0] = null;
                model[1] = null;
                return findBest( dataSet, model[2], model[3], model[4],
                                 alphaTolerance, betaTolerance );

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
     * fit model where the value of the beta (trend) smoothing constant is
     * between betaMin and betaMax. This method will continually try to
     * refine the estimate of beta until a tolerance of less than
     * betaTolerance is achieved.
     *
     * <p>Note that the descriptions of the parameters below include a
     * discussion of valid values. However, since this is a private method and
     * to help improve performance, we don't provide any validation of these
     * parameters. Using invalid values may lead to unexpected results.
     * @param dataSet the data set for which a best fit model is required.
     * @param alpha the (fixed) value of the alpha smoothing constant to use
     * for the best fit model.
     * @param betaMin the minimum value of the beta (trend) smoothing
     * constant accepted in the resulting best fit model. Must be greater than
     * (or equal to) 0.0 and less than betaMax.
     * @param betaMax the maximum value of the beta (trend) smoothing
     * constant accepted in the resulting best fit model. Must be greater than
     * betaMin and less than (or equal to) 1.0.
     * @param betaTolerance the tolerance within which the beta value is
     * required. Must be considerably less than 1.0. However, note that the
     * smaller this value the longer it will take to diverge on a best fit
     * model.
     */
    private static TripleExponentialSmoothingModel findBestBeta(
                        DataSet dataSet, double alpha,
                        double betaMin, double betaMax,
                        double betaTolerance )
    {
        int stepsPerIteration = 10;

        if ( betaMin < 0.0 )
            betaMin = 0.0;
        if ( betaMax > 1.0 )
            betaMax = 1.0;

        TripleExponentialSmoothingModel bestModel
            = new TripleExponentialSmoothingModel( alpha, betaMin, 0.0 );
        bestModel.init(dataSet);

        double initialMSE = bestModel.getMSE();

        boolean betaImproving = true;
        double betaStep = (betaMax-betaMin)/stepsPerIteration;
        double beta = betaMin + betaStep;
        for ( ; beta<=betaMax || betaImproving; )
            {
                TripleExponentialSmoothingModel model
                    = new TripleExponentialSmoothingModel( alpha, beta, 0.0 );
                model.init( dataSet );
                
                if ( model.getMSE() < bestModel.getMSE() )
                    bestModel = model;
                else
                    betaImproving = false;
                
                beta += betaStep;
                if ( beta > 1.0 )
                    betaImproving = false;
            }

        // If we're making progress, then try to refine the beta estimate
        if ( bestModel.getMSE() < initialMSE
             && betaStep > betaTolerance )
            {
                // Can this be further refined - improving efficiency - by
                //  only searching in the range beta-betaStep/2 to
                //  beta+betaStep/2 ?
                return findBestBeta( dataSet, bestModel.getAlpha(),
                                      bestModel.getBeta()-betaStep,
                                      bestModel.getBeta()+betaStep,
                                      betaTolerance );
            }
        
        return bestModel;
    }

    /**
     * Constructs a new triple exponential smoothing forecasting model, using
     * the given smoothing constants - alpha, beta and gamma. For a valid
     * model to be constructed, you should call init and pass in a data set
     * containing a series of data points with the time variable initialized
     * to identify the independent variable.
     * @param alpha the smoothing constant to use for this exponential
     * smoothing model. Must be a value in the range 0.0-1.0. Values above 0.5
     * are uncommon - though they are still valid and are supported by this
     * implementation.
     * @param beta the second smoothing constant, beta to use in this model
     * to smooth the trend. Must be a value in the range 0.0-1.0. Values above
     * 0.5 are uncommon - though they are still valid and are supported by this
     * implementation.
     * @param gamma the third smoothing constant, gamma to use in this model
     * to smooth the seasonality. Must be a value in the range 0.0-1.0.
     * @throws IllegalArgumentException if the value of any of the smoothing
     * constants are invalid - outside the range 0.0-1.0.
     */
    public TripleExponentialSmoothingModel( double alpha,
                                            double beta,
                                            double gamma )
    {
        if ( alpha < 0.0  ||  alpha > 1.0 )
            throw new IllegalArgumentException("TripleExponentialSmoothingModel: Invalid smoothing constant, " + alpha + " - must be in the range 0.0-1.0.");
        
        if ( beta < 0.0  ||  beta > 1.0 )
            throw new IllegalArgumentException("TripleExponentialSmoothingModel: Invalid smoothing constant, beta=" + beta + " - must be in the range 0.0-1.0.");
        
        if ( gamma < 0.0  ||  gamma > 1.0 )
            throw new IllegalArgumentException("TripleExponentialSmoothingModel: Invalid smoothing constant, gamma=" + gamma + " - must be in the range 0.0-1.0.");
        
        baseValues = new DataSet();
        trendValues = new DataSet();
        seasonalIndex = new DataSet();
        
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }
    
    /**
     * Used to initialize the time based model. This method must be called
     * before any other method in the class. Since the time based model does
     * not derive any equation for forecasting, this method uses the input
     * DataSet to calculate forecast values for all values of the independent
     * time variable within the initial data set.
     * @param dataSet a data set of observations that can be used to
     * initialize the forecasting parameters of the forecasting model.
     */
    public void init( DataSet dataSet )
    {
        initTimeVariable( dataSet );
        String timeVariable = getTimeVariable();

        if ( dataSet.getPeriodsPerYear() <= 1 )
            throw new IllegalArgumentException("Data set passed to init in the triple exponential smoothing model does not contain seasonal data. Don't forget to call setPeriodsPerYear on the data set to set this.");

        periodsPerYear = dataSet.getPeriodsPerYear();

        // Check we have the minimum amount of data points
        if ( dataSet.size() < NUMBER_OF_YEARS*periodsPerYear )
            throw new IllegalArgumentException("TripleExponentialSmoothing models require a minimum of a full two years of data to initialize the model.");

        // Calculate initial values for base and trend
        initBaseAndTrendValues( dataSet );

        // Initialize seasonal indices using data for all complete years
        initSeasonalIndices( dataSet );

        Iterator<DataPoint> it = dataSet.iterator();
        maxObservedTime = Double.NEGATIVE_INFINITY;
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                if ( dp.getIndependentValue(timeVariable) > maxObservedTime )
                    maxObservedTime = dp.getIndependentValue(timeVariable);
            }

        super.init( dataSet );
    }

    /**
     * Calculates (and caches) the initial base and trend values for the given
     * DataSet. Note that there are a variety of ways to estimate initial
     * values for both the base and the trend. The approach here averages the
     * trend calculated from the first two complete "years" - or cycles - of
     * data in the DataSet.
     * @param dataSet the set of data points - observations - to use to
     * initialize the base and trend values.
     */
    private void initBaseAndTrendValues( DataSet dataSet )
    {
        String timeVariable = getTimeVariable();
        double trend = 0.0;
        Iterator<DataPoint> it = dataSet.iterator();
        for ( int p=0; p<periodsPerYear; p++ )
            {
                DataPoint dp = it.next();
                trend -= dp.getDependentValue();
            }

        double year2Average = 0.0;
        for ( int p=0; p<periodsPerYear; p++ )
            {
                DataPoint dp = it.next();
                trend += dp.getDependentValue();

                year2Average += dp.getDependentValue();
            }
        trend /= periodsPerYear;
        trend /= periodsPerYear;
        year2Average /= periodsPerYear;

        it = dataSet.iterator();
        for ( int p=0; p<periodsPerYear*NUMBER_OF_YEARS; p++ )
            {
                DataPoint obs = it.next();
                double time = obs.getIndependentValue( timeVariable );

                DataPoint dp = new Observation( trend );
                dp.setIndependentValue( timeVariable, time );
                trendValues.add( dp );

                // Initialize base values for second year only
                if ( p >= periodsPerYear )
                    {
                        // This formula gets a little convoluted partly due to
                        // the fact that p is zero-based, and partly because
                        // of the generalized nature of the formula
                        dp.setDependentValue(year2Average
                                             + (p+1-periodsPerYear
                                                -(periodsPerYear+1)/2.0)*trend);
                        //dp.setIndependentValue( timeVariable, time );
                        baseValues.add( dp );
                    }
            }
    }

    /**
     * Calculates (and caches) the initial seasonal indices for the given
     * DataSet. Note that there are a variety of ways to estimate initial
     * values for the seasonal indices. The approach here averages seasonal
     * indices calculated for the first two complete "years" - or cycles - in
     * the DataSet.
     * @param dataSet the set of data points - observations - to use to
     * initialize the seasonal indices.
     */
    private void initSeasonalIndices( DataSet dataSet )
    {
        String timeVariable = getTimeVariable();

        double yearlyAverage[] = new double[NUMBER_OF_YEARS];
        Iterator<DataPoint> it = dataSet.iterator();
        for ( int year=0; year<NUMBER_OF_YEARS; year++ )
            {
                double sum = 0.0;
                for ( int p=0; p<periodsPerYear; p++ )
                    {
                        DataPoint dp = it.next();
                        sum += dp.getDependentValue();
                    }

                yearlyAverage[year] = sum / (double)periodsPerYear;
            }

        it = dataSet.iterator();
        double index[] = new double[periodsPerYear];
        for ( int year=0; year<NUMBER_OF_YEARS; year++ )
            for ( int p=0; p<periodsPerYear; p++ )
                {
                    DataPoint dp = it.next();
                    index[p]
                        += (dp.getDependentValue()/yearlyAverage[year])
                        / NUMBER_OF_YEARS;
                }

        it = dataSet.iterator();

        // Skip over first n-1 years
        for ( int year=0; year<NUMBER_OF_YEARS-1; year++ )
            for ( int p=0; p<periodsPerYear; p++ )
                it.next();

        for ( int p=0; p<periodsPerYear; p++ )
            {
                DataPoint dp = (DataPoint)it.next();
                double time = dp.getIndependentValue( timeVariable );

                Observation obs = new Observation( index[p] );
                obs.setIndependentValue( timeVariable, time );

                seasonalIndex.add( obs );
            }
    }
    
    /**
     * Returns the forecast value of the dependent variable for the given
     * value of the (independent) time variable using a single exponential
     * smoothing model. See the class documentation for details on the
     * formulation used.
     * @param time the value of the time variable for which a forecast
     * value is required.
     * @return the forecast value of the dependent variable at time, t.
     * @throws IllegalArgumentException if there is insufficient historical
     * data - observations passed to init - to generate a forecast for the
     * given time value.
     */
    protected double forecast( double time )
        throws IllegalArgumentException
    {
        double previousTime = time - getTimeInterval();
        double previousYear = time - getTimeInterval()*periodsPerYear;
        
        
        // As a starting point, we set the first forecast value to be
        //  the same as the observed value
        if ( previousTime < getMinimumTimeValue()-TOLERANCE )
            return getObservedValue( time );

        try
            {
                double base = getBase( previousTime );
                double trend = getTrend( previousTime );
                double si = getSeasonalIndex( previousYear );

                double forecast = (base+trend)*si;
                return forecast;
            }
        catch ( IllegalArgumentException idex )
            {
                double base = getBase( maxObservedTime );
                double trend = getTrend( maxObservedTime-getTimeInterval() );
                double si = getSeasonalIndex( previousYear );
                
                double forecast = (base+(time-maxObservedTime)*trend) * si;
                return forecast;
            }
    }
    
    /**
     * Calculates and returns the base value for the given time period. Except
     * for the first "year" - where base values are not available - the base
     * is calculated using a smoothed value of the previous base. See the
     * class documentation for details on the formulation used.
     * @param time the time value for which the trend is required.
     * @return the estimated base value at the given period of time.
     * @param IllegalArgumentException if the base cannot be determined for
     * the given time period.
     */
    private double getBase( double time )
        throws IllegalArgumentException
    {
        // TODO: Optimize this search by having data set sorted by time
        
        // Search for previously calculated - and saved - base value
        String timeVariable = getTimeVariable();
        Iterator<DataPoint> it = baseValues.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                double dpTimeValue = dp.getIndependentValue( timeVariable );
                if ( Math.abs(time-dpTimeValue) < TOLERANCE )
                    return dp.getDependentValue();
            }
        
        if ( time <
             getMinimumTimeValue()
             +periodsPerYear*getTimeInterval()
             +TOLERANCE )
            throw new IllegalArgumentException(
                         "Attempt to forecast for an invalid time "
                         +time
                         +" - before sufficient observations were made ("
                         +getMinimumTimeValue()
                         +periodsPerYear*getTimeInterval()+").");

        // Saved base value not found, so calculate it
        //  (and save it for future reference)
        double previousTime = time - getTimeInterval();
        double previousYear = time - periodsPerYear*getTimeInterval();

        double base
            = alpha*(getObservedValue(time)/getSeasonalIndex(previousYear))
            + (1-alpha)*(getBase(previousTime)+getTrend(previousTime));

        DataPoint dp = new Observation( base );
        dp.setIndependentValue( timeVariable, time );
        baseValues.add( dp );
        
        return base;
    }

    
    /**
     * Calculates and returns the trend for the given time period. Except
     * for the initial periods - where forecasts are not available - the
     * trend is calculated using forecast values, and not observed values.
     * See the class documentation for details on the formulation used.
     * @param time the time value for which the trend is required.
     * @return the trend of the data at the given period of time.
     * @param IllegalArgumentException if the trend cannot be determined for
     * the given time period.
     */
    private double getTrend( double time )
        throws IllegalArgumentException
    {
        // TODO: Optimize this search by having data set sorted by time

        // Search for previously calculated - and saved - trend value
        String timeVariable = getTimeVariable();
        Iterator<DataPoint> it = trendValues.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                double dpTimeValue = dp.getIndependentValue( timeVariable );
                if ( Math.abs(time-dpTimeValue) < TOLERANCE )
                    return dp.getDependentValue();
            }
        
        if ( time < getMinimumTimeValue()+TOLERANCE )
            throw new IllegalArgumentException("Attempt to forecast for an invalid time - before the observations began ("+getMinimumTimeValue()+").");

        // Saved trend not found, so calculate it
        //  (and save it for future reference)
        double previousTime = time - getTimeInterval();
        double trend
            = beta*(getBase(time)-getBase(previousTime))
            + (1-beta)*getTrend(previousTime);
        
        DataPoint dp = new Observation( trend );
        dp.setIndependentValue( timeVariable, time );
        trendValues.add( dp );
        
        return trend;
    }

    /**
     * Returns the seasonal index for the given time period.
     */    
    private double getSeasonalIndex( double time )
        throws IllegalArgumentException
    {
        // TODO: Optimize this search by having data set sorted by time

        // Handle initial conditions for seasonal index
        if ( time
             < getMinimumTimeValue()
             +(NUMBER_OF_YEARS-1)*periodsPerYear-TOLERANCE )
            return getSeasonalIndex( time
                                     + periodsPerYear*getTimeInterval() );
        
        // Search for previously calculated - and saved - seasonal index
        String timeVariable = getTimeVariable();
        Iterator<DataPoint> it = seasonalIndex.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                double dpTimeValue = dp.getIndependentValue( timeVariable );
                if ( Math.abs(time-dpTimeValue) < TOLERANCE )
                    return dp.getDependentValue();
            }
        
        // Saved seasonal index not found, so calculate it
        //  (and save it for future reference)
        double previousYear = time - getTimeInterval()*periodsPerYear;
        double index = gamma*(getObservedValue(time)/getForecastValue(time))
                       + (1-gamma)*getSeasonalIndex(previousYear);
        
        DataPoint dp = new Observation( index );
        dp.setIndependentValue( timeVariable, time );
        seasonalIndex.add( dp );
        
        return index;
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
     * Returns the current number of periods used in this model. This is also
     * the minimum number of periods required in order to produce a valid
     * forecast. Strictly speaking, for triple exponential smoothing only two
     * previous periods are needed - though such a model would be of relatively
     * little use. At least ten to fifteen prior observations would be
     * preferred.
     * @return the minimum number of periods used in this model.
     */
    protected int getNumberOfPeriods()
    {
        return 2*periodsPerYear;
    }

    /**
     * Since this version of triple exponential smoothing uses the current
     * observation to calculate a smoothed value, we must override the
     * calculation of the accuracy indicators.
     * @param dataSet the DataSet to use to evaluate this model, and to
     *        calculate the accuracy indicators against.
     */
    protected void calculateAccuracyIndicators( DataSet dataSet )
    {
        // WARNING: THIS STILL NEEDS TO BE VALIDATED

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
                    + getTrend( previousTime );

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
     * @see #getBeta
     * @see #getGamma
     */
    public double getAlpha()
    {
        return alpha;
    }

    /**
     * Returns the value of the trend smoothing constant, beta, used in this
     * model.
     * @return the value of the trend smoothing constant, beta.
     * @see #getAlpha
     * @see #getGamma
     */
    public double getBeta()
    {
        return beta;
    }

    /**
     * Returns the value of the seasonal smoothing constant, gamma, used in
     * this model.
     * @return the value of the seasonal smoothing constant, gamma.
     * @see #getAlpha
     * @see #getBeta
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
        return "triple exponential smoothing";
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
        return "Triple exponential smoothing model, with smoothing constants of alpha="
            + alpha + ", beta="
            + beta  + ", gamma="
            + gamma + ", and using an independent variable of "
            + getIndependentVariable();
    }
}
// Local Variables:
// tab-width: 4
// End:
