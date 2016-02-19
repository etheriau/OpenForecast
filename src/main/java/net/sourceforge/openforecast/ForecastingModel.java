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

package net.sourceforge.openforecast;


/**
 * Defines a consistent interface that must be implemented by all Forecasting
 * Models. Note that any forecasting model should first be initialized by
 * calling init. Once init has been called, any of the other methods can be
 * expected to return reasonable results.
 * @author Steven R. Gould
 */
public interface ForecastingModel
{
    /**
     * Used to initialize the model-specific parameters and customize them
     * to the given data set. This method must be called before any other
     * method in the class.
     * @param dataSet a data set of observations that can be used to initialize
     *        the forecasting parameters of the forecasting model.
     */
    public void init( DataSet dataSet );

    /**
     * Returns the Akaike Information Criteria obtained from applying the current
     * forecasting model to the initial data set to try and predict each data
     * point. The result is an indication of the accuracy of the model when
     * applied to your initial data set - the smaller the Akaike Information
     * Criteria (AIC), the more accurate the model.
     * @return the Akaike Information Criteria (AIC) when the current model was
     *         applied to the initial data set.
     * @since 0.5
     */
    public double getAIC();

    /**
     * Returns the bias - the arithmetic mean of the errors - obtained from
     * applying the current forecasting model to the initial data set to try
     * and predict each data point. The result is an indication of the accuracy
     * of the model when applied to your initial data set - the closer the bias
     * is to zero, the more accurate the model.
     * @return the bias - mean of the errors - when the current model was
     *         applied to the initial data set.
     */
    public double getBias();

    /**
     * Returns the mean absolute deviation obtained from applying the current
     * forecasting model to the initial data set to try and predict each data
     * point. The result is an indication of the accuracy of the model when
     * applied to your initial data set - the smaller the Mean Absolute
     * Deviation (MAD), the more accurate the model.
     * @return the mean absolute deviation (MAD) when the current model was
     *         applied to the initial data set.
     */
    public double getMAD();

    /**
     * Returns the mean absolute percentage error obtained from applying the
     * current forecasting model to the initial data set to try and predict
     * each data point. The result is an indication of the accuracy of the
     * model when applied to the initial data set - the smaller the Mean
     * Absolute Percentage Error (MAPE), the more accurate the model.
     * @return the mean absolute percentage error (MAPE) when the current model
     *         was applied to the initial data set.
     */
    public double getMAPE();

    /**
     * Returns the mean square of the errors (MSE) obtained from applying the
     * current forecasting model to the initial data set to try and predict
     * each data point. The result is an indication of the accuracy of the
     * model when applied to your initial data set - the smaller the Mean
     * Square of the Errors, the more accurate the model.
     * @return the mean square of the errors (MSE) when the current model was
     *         applied to the initial data set.
     */
    public double getMSE();

    /**
     * Returns the sum of the absolute errors (SAE) obtained from applying the
     * current forecasting model to the initial data set to try and predict
     * each data point. The result is an indication of the accuracy of the
     * model when applied to your initial data set - the smaller the Sum of
     * Absolute Errors, the more accurate the model.
     * @return the sum of the absolute errors (SAE) when the current model was
     *         applied to the initial data set.
     */
    public double getSAE();

    /**
     * Returns the number of predictors used by the underlying model.
     * @return the number of predictors used by the underlying model.
     * @since 0.5
     */
    public int getNumberOfPredictors();

    /**
     * Using the current model parameters (initialized in init), apply the
     * forecast model to the given data point. The data point must have valid
     * values for the independent variables. Upon return, the value of the
     * dependent variable will be updated with the forecast value computed for
     * that data point.
     * @param dataPoint the data point for which a forecast value (for the
     *        dependent variable) is required.
     * @return the same data point passed in but with the dependent value
     *         updated to contain the new forecast value.
     */
    public double forecast( DataPoint dataPoint );

    /**
     * Using the current model parameters (initialized in init), apply the
     * forecast model to the given data set. Each data point in the data set
     * must have valid values for the independent variables. Upon return, the
     * value of the dependent variable will be updated with the forecast
     * values computed.
     * @param dataSet the set of data points for which forecast values (for
     *        the dependent variable) are required.
     * @return the same data set passed in but with the dependent values
     *         updated to contain the new forecast values.
     */
    public DataSet forecast( DataSet dataSet );

    /**
     * Returns a one or two word name of this type of forecasting model. Keep
     * this short. A longer description should be implemented in the toString
     * method.
     * @return a string representation of the type of forecasting model
     *         implemented.
     */
    public String getForecastType();

    /**
     * This should be overridden to provide a textual description of the
     * current forecasting model including, where possible, any derived
     * parameters used.
     * @return a string representation of the current forecast model, and its
     *         parameters.
     */
    public String toString();
}
// Local Variables:
// tab-width: 4
// End:
