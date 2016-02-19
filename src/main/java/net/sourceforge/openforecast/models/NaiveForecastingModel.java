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
 * A naive forecasting model is a special case of the moving average
 * forecasting model where the number of periods used for smoothing is 1.
 * Therefore, the forecast for a period, t, is simply the observed value
 * for the previous period, t-1.
 *
 * <p>Due to the simplistic nature of the naive forecasting model, it can only
 * be used to forecast up to one period in the future. It is not at all useful
 * as a medium-long range forecasting tool.
 *
 * <p>This model really is a simplistic model, and is included partly
 * for completeness and partly because of its simplicity. It is unlikely that
 * you'll want to use this model directly. Instead, consider using either the
 * moving average model, or the more general weighted moving average model
 * with a higher (i.e. greater than 1) number of periods, and possibly a
 * different set of weights.
 * @author Steven R. Gould
 * @since 0.3
 */
public class NaiveForecastingModel extends MovingAverageModel
{
    /**
     * Constructs a new naive forecasting model. For a valid model to be
     * constructed, you should call init and pass in a data set containing a
     * series of data points with the time variable initialized to identify
     * the independent variable.
     */
    public NaiveForecastingModel()
    {
        super( 1 );
    }
    
    /**
     * Constructs a new naive forecasting model, using the given name as the
     * independent variable.
     * @param independentVariable the name of the independent variable to use
     * in this model.
     * @deprecated As of 0.4, replaced by {@link #NaiveForecastingModel}.
     */
    public NaiveForecastingModel( String independentVariable )
    {
        super( independentVariable, 1 );
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
        return "Naive forecast";
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
        return
            "Naive forecasting model (i.e. moving average with a period of 1)"
            + ", using an independent variable of " + getIndependentVariable()
            + ".";
    }
}
// Local Variables:
// tab-width: 4
// End:
