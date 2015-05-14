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

package net.sourceforge.openforecast.tests;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.models.PolynomialRegressionModel;


/**
 * Tests the methods used by the Polynomial Regression model. This test
 * case needs to be part of the models package to enable it to access
 * the package-level Utils.GaussElimination method for testing.
 */
public class PolynomialRegressionTest extends OpenForecastTestCase
{
    public void testPolynomialRegression()
    {
        DataSet observedData = new DataSet();
        
        for ( int x=0; x<10; x++ )
            {
                double y = 10.6 + 3.5*x + 0.5*Math.pow(x,3);
                
                DataPoint dp = new Observation( y );
                dp.setIndependentValue( "x1", x );
                
                // Fill x2 with random data - it should be ignored
                dp.setIndependentValue( "x2", (Math.random()-0.5)*100 );
                observedData.add( dp );
            }
        
        ForecastingModel model = new PolynomialRegressionModel( "x1", 5 );
        model.init( observedData );
        
        DataSet fcValues = new DataSet();
        double expectedResult[] = new double[10];
        for ( int x=10; x<20; x++ )
            {
                double y = 10.6 + 3.5*x + 0.5*Math.pow(x,3);
                
                DataPoint dp = new Observation( 0.0 );
                dp.setIndependentValue( "x1", x );
                
                // Fill x2 with random data - it should be ignored
                dp.setIndependentValue( "x2", (Math.random()-0.5)*100 );
                fcValues.add( dp );
                
                // Save expected value
                expectedResult[x-10] = y;
            }
        
        DataSet results = model.forecast( fcValues );
        
        checkResults( results, expectedResult );
    }
    
    public PolynomialRegressionTest( String name )
    {
        super(name);
    }
}
// Local variables:
// tab-width: 4
// End:
