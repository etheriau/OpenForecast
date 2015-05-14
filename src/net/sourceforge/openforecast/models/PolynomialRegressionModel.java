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


/**
 * Implements a single variable polynomial regression model using the variable
 * named in the constructor as the independent variable. The cofficients of
 * the regression as well as the accuracy indicators are determined from the
 * data set passed to init.
 *
 * <p>Once initialized, this model can be applied to another data set using
 * the forecast method to forecast values of the dependent variable based on
 * values of the dependent variable (the one named in the constructor).
 *
 * <p>A single variable polynomial regression model essentially attempts to
 * put a polynomial line - a curve if you prefer - through the data points.
 * Mathematically, assuming the independent variable is x and the dependent
 * variable is y, then this line can be represented as:
 *
 * <pre>y = a<sub>0</sub> + a<sub>1</sub>*x + a<sub>2</sub>*x<sup>2</sup> + a<sub>3</sub>*x<sup>3</sup> + ... + a<sub>m</sub>*x<sup>m</sup></pre>
 *
 * You can specify the order of the polynomial fit (the value of
 * <code>m</code> in the above equation) in the constructor.
 * @author Steven R. Gould
 */
public class PolynomialRegressionModel extends AbstractForecastingModel
{
    /**
     * The name of the independent variable used in this regression model.
     */
    private String independentVariable;

    /**
     * The order of the polynomial to fit in this regression model.
     */
    private int order = 0;

    /**
     * An array of coefficients for this polynomial regression model. These are
     * initialized following a call to init.
     */
    private double coefficient[];

    /**
     * Constructs a new polynomial regression model, using the given name as
     * the independent variable. For a valid model to be constructed, you
     * should call init and pass in a data set containing a series of data
     * points involving the given independent variable.
     *
     * <p>Using this constructor the order of the polynomial fit is not
     * specified. The effect is that the model will try to determine an
     * appropriate order for the given data. It will do this by calculating
     * up to 10 coefficients and once the coefficients become numerically
     * insignificant they will be excluded from the model.
     * @param independentVariable the name of the independent variable to use
     * in this model.
     */
    public PolynomialRegressionModel( String independentVariable )
    {
        this( independentVariable, 10 );
    }

    /**
     * Constructs a new linear regression model, using the given name as the
     * independent variable. For a valid model to be constructed, you should
     * call init and pass in a data set containing a series of data points
     * involving the given independent variable.
     * @param independentVariable the name of the independent variable to use
     * in this model.
     * @param order the required order of the polynomial to fit.
     */
    public PolynomialRegressionModel( String independentVariable, int order )
    {
        this.independentVariable = independentVariable;
        this.order = order;
    }

    /**
     * Initializes the coefficients to use for this regression model. The
     * intercept and slope are derived so as to give the best fit line for the
     * given data set.
     *
     * <p>Additionally, the accuracy indicators are calculated based on this
     * data set.
     * @param dataSet the set of observations to use to derive the regression
     * coefficients for this model.
     */
    public void init( DataSet dataSet )
    {
        double a[][] = new double[order][order+1];

        for ( int i=0; i<order; i++ )
            {
                for ( int j=0; j<order; j++ )
                    {
                        int k = i + j;

                        Iterator<DataPoint> it = dataSet.iterator();
                        while ( it.hasNext() )
                            {
                            DataPoint dp = it.next();
                            
                            double x = dp.getIndependentValue( independentVariable );
                            
                            a[i][j] = a[i][j] + Math.pow(x,k);
                            }
                    }

                Iterator<DataPoint> it = dataSet.iterator();
                while ( it.hasNext() )
                    {
                        DataPoint dp = it.next();
                        
                        double x = dp.getIndependentValue( independentVariable );
                        double y = dp.getDependentValue();
                        
                        a[i][order] += y*Math.pow(x,i);
                    }
            }

        coefficient = Utils.GaussElimination( order, a );

        // Calculate the accuracy indicators
        calculateAccuracyIndicators( dataSet );
    }

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
     * @throws ModelNotInitializedException if forecast is called before the
     *         model has been initialized with a call to init.
     */
    public double forecast( DataPoint dataPoint )
    {
        if ( !initialized )
            throw new ModelNotInitializedException();

        double x = dataPoint.getIndependentValue( independentVariable );
        double forecastValue = 0.0;
        for ( int i=0; i<order; i++ )
            forecastValue += coefficient[i] * Math.pow(x,i);

        dataPoint.setDependentValue( forecastValue );

        return forecastValue;
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
     * Returns a short name for this type of forecasting model. A more detailed
     * explanation is provided by the toString method.
     * @return a short string describing this type of forecasting model.
     */
    public String getForecastType()
    {
        return "Single variable polynomial regression";
    }

    /**
     * Returns a detailed description of this forcasting model, including the
     * intercept and slope. A shortened version of this is provided by the
     * getForecastType method.
     * @return a description of this forecasting model.
     */
    public String toString()
    {
        String description = "Single variable polynomial regression model";
        if ( !initialized )
            return description + " (uninitialized)";

        description += " with an equation of: y = "+coefficient[0];
        for ( int i=1; i<coefficient.length; i++ )
             if ( Math.abs(coefficient[i]) > 0.001 )
                  description += (coefficient[i]<0 ? "" : "+")
                        + coefficient[i] + "*"
                        + independentVariable + (i>1 ? "^"+i : "" );

        return description;
    }
}
// Local variables:
// tab-width: 4
// End:
