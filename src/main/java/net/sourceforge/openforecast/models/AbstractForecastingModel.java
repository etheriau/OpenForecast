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

import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;


/**
 * This class implements a variety of methods that are common across all
 * forecasting models. In particular, the calculation of the accuracy
 * indicators can be generalized, and is therefore implemented in
 * the method, calculateAccuracyIndicators.
 * @since 0.3
 * @author Steven R. Gould
 * @see #calculateAccuracyIndicators
 */
public abstract class AbstractForecastingModel implements ForecastingModel
{
    /**
     * The maximum tolerance permitted when comparing two double precision
     * numbers for equality. In other words, if two numbers are different
     * by less than the value of TOLERANCE, then they are considered equal.
     */
    static double TOLERANCE = 0.00000001;
    
    /**
     * The accuracy indicators, or measures of accuracy, obtained by applying
     * this forecasting model to the initial data set. Initialized following a
     * call to init.
     */
    protected AccuracyIndicators accuracyIndicators = new AccuracyIndicators();
    
    /**
     * Remembers whether this model has been properly initialized.
     */
    protected boolean initialized = false;
    
    /**
     * Default constructor.
     */
    protected AbstractForecastingModel()
    {
    }
    
    /**
     * Returns the Akaike Information Criteria obtained from applying the current
     * forecasting model to the initial data set to try and predict each data
     * point. The result is an indication of the accuracy of the model when
     * applied to your initial data set - the smaller the Akaike Information
     * Criteria (AIC), the more accurate the model.
     * @return the Akaike Information Criteria (AIC) when the current model was
     *         applied to the initial data set.
     * @throws ModelNotInitializedException if getAIC is called before the
     *         model has been initialized with a call to init.
     * @since 0.5
     */
    public double getAIC()
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        return accuracyIndicators.getAIC();
    }
    
    /**
     * Returns the bias - the arithmetic mean of the errors - obtained from
     * applying the current forecasting model to the initial data set to try
     * and predict each data point. The result is an indication of the accuracy
     * of the model when applied to your initial data set - the smaller the
     * bias, the more accurate the model.
     * @return the bias - mean of the errors - when the current model was
     *         applied to the initial data set.
     * @throws ModelNotInitializedException if getBias is called before the
     *         model has been initialized with a call to init.
     */
    public double getBias()
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        return accuracyIndicators.getBias();
    }
    
    /**
     * Returns the mean absolute deviation obtained from applying the current
     * forecasting model to the initial data set to try and predict each data
     * point. The result is an indication of the accuracy of the model when
     * applied to your initial data set - the smaller the Mean Absolute
     * Deviation (MAD), the more accurate the model.
     * @return the mean absolute deviation (MAD) when the current model was
     *         applied to the initial data set.
     * @throws ModelNotInitializedException if getMAD is called before the
     *         model has been initialized with a call to init.
     */
    public double getMAD()
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        return accuracyIndicators.getMAD();
    }
    
    /**
     * Returns the mean absolute percentage error obtained from applying the
     * current forecasting model to the initial data set to try and predict
     * each data point. The result is an indication of the accuracy of the
     * model when applied to the initial data set - the smaller the Mean
     * Absolute Percentage Error (MAPE), the more accurate the model.
     * @return the mean absolute percentage error (MAPE) when the current model
     *         was applied to the initial data set.
     * @throws ModelNotInitializedException if getMAPE is called before the
     *         model has been initialized with a call to init.
     */
    public double getMAPE()
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        return accuracyIndicators.getMAPE();
    }
    
    /**
     * Returns the mean square of the errors (MSE) obtained from applying the
     * current forecasting model to the initial data set to try and predict
     * each data point. The result is an indication of the accuracy of the
     * model when applied to your initial data set - the smaller the Mean
     * Square of the Errors, the more accurate the model.
     * @return the mean square of the errors (MSE) when the current model was
     *         applied to the initial data set.
     * @throws ModelNotInitializedException if getMSE is called before the
     *         model has been initialized with a call to init.
     */
    public double getMSE()
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        return accuracyIndicators.getMSE();
    }
    
    /**
     * Returns the Sum of Absolute Errors (SAE) obtained by applying the
     * current forecasting model to the initial data set. Initialized following
     * a call to init.
     * @return the sum of absolute errors (SAE) obtained by applying this
     *         forecasting model to the initial data set.
     * @throws ModelNotInitializedException if getSAE is called before the
     *         model has been initialized with a call to init.
     */
    public double getSAE()
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        return accuracyIndicators.getSAE();
    }
    
    /**
     * Using the current model parameters (initialized in init), apply the
     * forecast model to the given data set. Each data point in the data set
     * must have valid values for the independent variables. Upon return, the
     * value of the dependent variable will be updated with the forecast
     * values computed.
     *
     * This method is provided as a convenience method, and iterates through the
     * data set invoking forecast(DataPoint) to do the actual forecast for each
     * data point. In general, it is not necessary to override this method.
     * However, if a subclass can provide a more efficient approach then it is
     * recommended that the subclass provide its own implementation.
     * @param dataSet the set of data points for which forecast values (for
     *        the dependent variable) are required.
     * @return the same data set passed in but with the dependent values
     *         updated to contain the new forecast values.
     * @throws ModelNotInitializedException if getMSE is called before the
     *         model has been initialized with a call to init.
     */
    public DataSet forecast( DataSet dataSet )
    {
        if ( !initialized )
            throw new ModelNotInitializedException();
        
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                dp.setDependentValue( forecast(dp) );
            }
        
        return dataSet;        
    }
    
    /**
     * A helper method to calculate the various accuracy indicators when
     * applying the given DataSet to the current forecasting model.
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
        
        // Obtain the forecast values for this model
        DataSet forecastValues = new DataSet( dataSet );
        forecast( forecastValues );
        
        // Calculate the Sum of the Absolute Errors
        Iterator<DataPoint> it = dataSet.iterator();
        Iterator<DataPoint> itForecast = forecastValues.iterator();
        while ( it.hasNext() )
            {
                // Get next data point
                DataPoint dp = it.next();
                double x = dp.getDependentValue();
                
                // Get next forecast value
                DataPoint dpForecast = itForecast.next();
                double forecastValue = dpForecast.getDependentValue();
                
                // Calculate error in forecast, and update sums appropriately
                double error = forecastValue - x;
                sumErr += error;
                sumAbsErr += Math.abs( error );
                sumAbsPercentErr += Math.abs( error / x );
                sumErrSquared += error*error;
            }
        
        // Initialize the accuracy indicators
        int n = dataSet.size();
        int p = getNumberOfPredictors();

        accuracyIndicators.setAIC( n*Math.log(2*Math.PI)
                   + Math.log(sumErrSquared/n)
                   + 2 * ( p+2 ) );
        accuracyIndicators.setBias( sumErr / n );
        accuracyIndicators.setMAD( sumAbsErr / n );
        accuracyIndicators.setMAPE( sumAbsPercentErr / n );
        accuracyIndicators.setMSE( sumErrSquared / n );
        accuracyIndicators.setSAE( sumAbsErr );
    }
}
// Local Variables:
// tab-width: 4
// End:
