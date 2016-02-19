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

package net.sourceforge.openforecast.tests;

import junit.framework.Test;
import junit.framework.TestSuite;


public class OpenForecastTestSuite
{
    private OpenForecastTestSuite()
    {
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        try
            {
                // Test models and core classes
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.BasicTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.DataSetTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.SimpleExponentialSmoothingTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.DoubleExponentialSmoothingTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.TripleExponentialSmoothingTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.MovingAverageTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.MultipleLinearRegressionTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.PolynomialRegressionTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.WeightedMovingAverageTest" ) );

                // Test builder(s)
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.CSVBuilderTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.TimeSeriesBuilderTest" ) );
                
                // Test outputter(s)
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.DelimitedTextOutputterTest" ) );
                suite.addTestSuite( Class.forName( "net.sourceforge.openforecast.tests.TimeSeriesOutputterTest" ) );
            }
        catch ( ClassNotFoundException exCNF )
            {
                System.err.println( "Check setting of CLASSPATH environment variable" );
                System.err.println( "Unable to locate test class:" );
                System.err.println( "  "+exCNF.getMessage() );
            }
        
        return suite;
    }
}
// Local Variables:
// tab-width: 4
// End:
