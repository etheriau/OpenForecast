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
 * A simple Java Bean that gathers together in one class all current "accuracy
 * indicators" for a given forecasting model. Currently, the supported
 * measurements of accuracy of a forecasting model supported by this class
 * include:
 * <ul>
 *  <li>AIC - or the Akaike Information Criteria value</li>
 *  <li>bias - or the mean error</li>
 *  <li>MAD - or the Mean Absolute Deviation</li>
 *  <li>MAPE - or the Mean Absolute Percentage Error</li>
 *  <li>MSE - or the Mean Square of the Error</li>
 *  <li>SAE - or the Sum of the Absolute Errors</li>
 * </ul>
 * @since 0.3
 * @author Steven R. Gould
 */
class AccuracyIndicators
{
    /** Akaike Information Criteria measure. */
    private double aic;

    /** Arithmetic mean of the errors. */
    private double bias;
    
    /** Mean Absolute Deviation. */
    private double mad;

    /** Mean Absolute Percentage Error. */
    private double mape;

    /** Mean Square of the Error. */
    private double mse;

    /** Sum of the Absolute Errors. */
    private double sae;

    /**
     * Default constructor. Initializes all accuracy indicators to their
     * "worst" possible values - generally this means some large number,
     * indicating "very" inaccurate.
     */
    public AccuracyIndicators()
    {
        aic = bias = mad = mape = mse = sae = Double.MAX_VALUE;
    }

    /**
     * Returns the AIC for the associated forecasting model.
     * @return the AIC.
     */
    public double getAIC()
    {
        return aic;
    }

    /**
     * Sets the AIC for the associated forecasting model to the given value.
     * @param aic the new value for the AIC.
     */
    public void setAIC(double aic)
    {
        this.aic = aic;
    }

    /**
     * Returns the bias for the associated forecasting model.
     * @return the bias.
     */
    public double getBias()
    {
        return bias;
    }

    /**
     * Sets the bias for the associated forecasting model to the given value.
     * @param bias the new value for the bias.
     */
    public void setBias(double bias)
    {
        this.bias = bias;
    }

    /**
     * Returns the Mean Absolute Deviation (MAD) for the associated
     * forecasting model to the given value.
     * @return the Mean Absolute Deviation.
     */
    public double getMAD()
    {
        return mad;
    }

    /**
     * Sets the Mean Absolute Deviation (MAD) for the associated forecasting
     * model. That is, the <code>SUM( abs(actual-forecast) ) / n</code> for
     * the initial data set.
     * @param mad the new value for the Mean Absolute Deviation.
     */
    public void setMAD(double mad)
    {
        this.mad = mad;
    }

    /**
     * Returns the Mean Absolute Percentage Error (MAPE) for the associated
     * forecasting model. That is, the
     * <code>SUM( 100% . abs(actual-forecast)/actual ) / n</code> for the
     * initial data set.
     * @return the Mean Absolute Percentage Error.
     */
    public double getMAPE()
    {
        return mape;
    }

    /**
     * Sets the Mean Absolute Percentage Error (MAPE) for the associated
     * forecasting model to the given value.
     * @param mape the new value for the Mean Absolute Percentage Error.
     */
    public void setMAPE(double mape)
    {
        this.mape = mape;
    }

    /**
     * Returns the Mean Square of the Errors (MSE) for the associated
     * forecasting model. That is, the
     * <code>SUM( (actual-forecast)^2 ) / n</code> for the initial data set.
     * @return the Mean Square of the Errors.
     */
    public double getMSE()
    {
        return mse;
    }

    /**
     * Sets the Mean Square of the Errors (MSE) for the associated
     * forecasting model to the new value.
     * @param mse the new value for the Mean Square of the Errors.
     */
    public void setMSE(double mse)
    {
        this.mse = mse;
    }

    /**
     * Returns the Sum of the Absolute Errors (SAE) for the associated
     * forecasting model. That is, the
     * <code>SUM( abs(actual-forecast) )</code> for the initial data set.
     * @return the Mean Absolute Deviation.
     */
    public double getSAE()
    {
        return sae;
    }

    /**
     * Sets the Sum of the Absolute Errors (SAE) for the associated
     * forecasting model to the new value.
     * @return the new value for the Mean Absolute Deviation.
     */
    public void setSAE(double sae)
    {
        this.sae = sae;
    }

    /**
     * Returns a string containing the accuracy indicators and their values.
     * Overridden to provide some useful output for debugging.
     * @return a string containing the accuracy indicators and their values.
     */
    public String toString()
    {
        return "AIC=" + aic
            + ", bias=" + bias
            + ", MAD=" + mad
            + ", MAPE=" + mape
            + ", MSE=" + mse
            + ", SAE=" + sae;
    }
}
// Local Variables:
// tab-width: 4
// End:
