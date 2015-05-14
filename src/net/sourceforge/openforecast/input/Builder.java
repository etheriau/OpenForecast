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

package net.sourceforge.openforecast.input;

import net.sourceforge.openforecast.DataSet;

/**
 * An interface that defines the minimal requirements for a class to be
 * considered a "builder". This serves to bring some consistency between
 * the potentially very different builder classes.
 *
 * <p>A class that implements the Builder interface generally provides some
 * means of obtaining data and using that data to build a DataSet - a
 * collection of DataPoints - or, in some cases, a single DataPoint from that
 * data.
 * @author Steven R. Gould
 * @since 0.4
 */
public interface Builder
{
    /**
     * Retrieves a DataSet - a collection of DataPoints - from the current
     * input source. The DataSet should contain all DataPoints defined by
     * the input source.
     * @return a DataSet built from the current input source.
     * @throws Exception if an error occurred reading from the input source.
     */
    public DataSet build()
        throws Exception;
}
// Local Variables:
// tab-width: 4
// End:
