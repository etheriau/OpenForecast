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
 * This exception should be thrown when an attempt is made to use a method in
 * a model that has not been initialized by calling init.
 * @see net.sourceforge.openforecast.ForecastingModel#init
 * @author Steven R. Gould
 */
public class ModelNotInitializedException extends RuntimeException
{
    private static final long serialVersionUID = 500L;
    
    /**
     * Constructs a new ModelNotInitializedException with no detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to java.lang.Throwable.initCause(). 
     */
    public ModelNotInitializedException()
    {
    }
    
    /**
     * Constructs a new ModelNotInitializedException with the specified detail
     * message.
     * @param msg the detail message. The detail message is saved for later
     *        retrieval by the java.lang.Throwable.getMessage() method.
     */
    public ModelNotInitializedException( String msg )
    {
        super( msg );
    }
}
// Local variables:
// tab-width: 4
// End:
