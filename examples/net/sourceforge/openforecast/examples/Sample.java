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

package net.sourceforge.openforecast.examples;

import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.Observation;


public class Sample
{
    public static void main( String args[] )
    {
        // Create some sample observed data values
        DataSet observedData = new DataSet();
        DataPoint dp;
        
        dp = new Observation( 2.1 );
        dp.setIndependentValue( "x", 0 );
        observedData.add( dp );
        
        dp = new Observation( 7.7 );
        dp.setIndependentValue( "x", 1 );
        observedData.add( dp );
        
        dp = new Observation( 13.6 );
        dp.setIndependentValue( "x", 2 );
        observedData.add( dp );
        
        dp = new Observation( 27.2 );
        dp.setIndependentValue( "x", 3 );
        observedData.add( dp );
        
        dp = new Observation( 40.9 );
        dp.setIndependentValue( "x", 4 );
        observedData.add( dp );
        
        dp = new Observation( 61.1 );
        dp.setIndependentValue( "x", 5 );
        observedData.add( dp );
        
        dp = new Observation( 59.2 );
        dp.setIndependentValue( "x", 6 );
        observedData.add( dp );
        
        
        System.out.println("Input data, observed values");
        System.out.println( observedData );
        
        // Obtain a good forecasting model given this data set
        ForecastingModel forecaster
            = Forecaster.getBestForecast( observedData );
        System.out.println("Forecast model type selected: "+forecaster.getForecastType());
        System.out.println( forecaster.toString() );
        
        
        // Create additional data points for which forecast values are required
        DataSet requiredDataPoints = new DataSet();
        for ( int count=7; count<15; count++ )
            {
                dp = new Observation( 0.0 );
                dp.setIndependentValue( "x", count );
                
                requiredDataPoints.add( dp );
            }
        
        // Dump data set before forecast
        System.out.println("Required data set before forecast");
        System.out.println( requiredDataPoints );
        
        // Use the given forecasting model to forecast values for
        //  the required (future) data points
        forecaster.forecast( requiredDataPoints );
        
        // Output the results
        System.out.println("Output data, forecast values");
        System.out.println( requiredDataPoints );
    }
}
// Local variables:
// tab-width: 4
// End:
