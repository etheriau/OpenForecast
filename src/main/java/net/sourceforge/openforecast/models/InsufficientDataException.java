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
 * Represents the case when there is insufficient data available to return a
 * valid forecast value.
 * @since 0.4
 */
public class InsufficientDataException extends Exception
{
    private static final long serialVersionUID = 500L;
    
    /**
     * Default constructor. If possible, use the one argument constructor and
     * give more information about the lack of data.
     */
    public InsufficientDataException()
    {
        super();
    }
    
    /**
     * Constructs a new InsufficientDataException with the given reason.
     * This is the preferred constructor.
     * @param reason the reason for, or details about, the
     * InsufficientDataException.
     */
    public InsufficientDataException( String reason )
    {
        super( reason );
    }
}
// Local Variables:
// tab-width: 4
// End:
