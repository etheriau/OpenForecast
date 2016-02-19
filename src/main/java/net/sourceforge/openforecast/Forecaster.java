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


import java.util.ArrayList;
import java.util.Iterator;
import net.sourceforge.openforecast.models.MovingAverageModel;
import net.sourceforge.openforecast.models.MultipleLinearRegressionModel;
import net.sourceforge.openforecast.models.PolynomialRegressionModel;
import net.sourceforge.openforecast.models.RegressionModel;
import net.sourceforge.openforecast.models.SimpleExponentialSmoothingModel;
import net.sourceforge.openforecast.models.DoubleExponentialSmoothingModel;
import net.sourceforge.openforecast.models.TripleExponentialSmoothingModel;



/**
 * The Forecaster class is a factory class that obtains the best
 * ForecastingModel for the given data set. The interpretation of the "best"
 * forecasting model can be user selected (bias, MAD, MAPE, MSE, SAE or a
 * blend of these), or left up to the Forecaster. If the interpretation is left
 * up to the Forecaster class then it will evaluate a combination of these other
 * measures and comes up with somewhat of a concensus opinion as the to best
 * model. For more details on the different options available, see
 * {@link net.sourceforge.openforecast.EvaluationCriteria}.
 * @author Steven R. Gould
 */
public class Forecaster
{
    /**
     * Make constructor private to prevent this class from being instantiated
     * directly.
     */
    private Forecaster()
    {
    }
    
    /**
     * Obtains the best forecasting model for the given DataSet. There is
     * some intelligence built into this method to help it determine which
     * forecasting model is best suited to the data. In particular, it will
     * try applying various forecasting models, using different combinations
     * of independent variables and select the one with the least Sum of
     * Absolute Errors (SAE); i.e. the most accurate one based on historical
     * data.
     * @param dataSet a set of observations on which the given model should be
     *        based.
     * @return the best ForecastingModel for the given data set.
     */
    public static ForecastingModel getBestForecast( DataSet dataSet )
    {
        return getBestForecast( dataSet, EvaluationCriteria.BLEND );
    }
    
    /**
     * Obtains the best forecasting model for the given DataSet. To determine
     * which model is best the specified EvaluationCriteria is used - this
     * includes options to use bias, mean absolute deviation (MAD), mean
     * absolute percentage error (MAPE), mean squared error (MSE) and more. For
     * a complete list refer to the final static members defined in the
     * EvaluationCriteria class.
     * @param dataSet a set of observations on which the given model should be
     *        based.
     * @param evalMethod specifies how to determine the "best" model; using
     *        which EvaluationCriteria.
     * @return the best ForecastingModel for the given data set.
     * @see EvaluationCriteria
     * @since 0.5
     */
    public static ForecastingModel getBestForecast( DataSet dataSet, EvaluationCriteria evalMethod )
    {
        String independentVariable[] = dataSet.getIndependentVariables();
        ForecastingModel bestModel = null;
        
        // Try single variable models
        for ( int i=0; i<independentVariable.length; i++ )
            {
                ForecastingModel model;
                
                // Try the Regression Model
                model = new RegressionModel( independentVariable[i] );
                model.init( dataSet );
                if ( betterThan( model, bestModel, evalMethod ) )
                    bestModel = model;
                
                // Try the Polynomial Regression Model
                // Note: if order is about the same as dataSet.size() then
                //  we'll get a good/great fit, but highly variable - and
                //  unreliable - forecasts. We "guess" at a reasonable order
                //  for the polynomial curve based on the number of
                //  observations.
                int order = 10;
                if ( dataSet.size() < order*order )
                    order = (int)(Math.sqrt(dataSet.size()))-1;
                model = new PolynomialRegressionModel( independentVariable[i],
                                                       order );
                model.init( dataSet );
                if ( betterThan( model, bestModel, evalMethod ) )
                    bestModel = model;
            }
        
        
        // Try multiple variable models
        
        // Create a list of available variables
        ArrayList<String> availableVariables
            = new ArrayList<String>(independentVariable.length);
        for ( int i=0; i<independentVariable.length; i++ )
            availableVariables.add( independentVariable[i] );
        
        // Create a list of variables to use - initially empty
        ArrayList<String> bestVariables = new ArrayList<String>(independentVariable.length);
        
        // While some variables still available to consider
        while ( availableVariables.size() > 0 )
            {
                int count = bestVariables.size();
                String workingList[] = new String[count+1];
                if ( count > 0 )
                    for ( int i=0; i<count; i++ )
                        workingList[i] = (String)bestVariables.get(i);
                
                String bestAvailVariable = null;
                
                // For each available variable
                Iterator<String> it = availableVariables.iterator();
                while ( it.hasNext() )
                    {
                        // Get current variable
                        String currentVar = it.next();
                        
                        // Add variable to list to use for regression
                        workingList[count] = currentVar;
                        
                        // Do multiple variable linear regression
                        ForecastingModel model
                            = new MultipleLinearRegressionModel( workingList );
                        model.init( dataSet );
                        
                        //  If best so far, then save best variable
                        if ( betterThan( model, bestModel, evalMethod ) )
                            {
                                bestModel = model;
                                bestAvailVariable = currentVar;
                            }
                        
                        // Remove the current variable from the working list
                        workingList[count] = null;
                    }
                
                // If no better model could be found (by adding another
                //     variable), then we're done
                if ( bestAvailVariable == null )
                    break;
                
                // Remove best variable from list of available vars
                int bestVarIndex = availableVariables.indexOf( bestAvailVariable );
                availableVariables.remove( bestVarIndex );
                
                // Add best variable to list of vars. to use
                bestVariables.add( count, bestAvailVariable );
                
                count++;
            }
        
        
        // Try time-series models
        if ( dataSet.getTimeVariable() != null )
            {
                // Try moving average model
                ForecastingModel model = new MovingAverageModel();
                model.init( dataSet );
                if ( betterThan( model, bestModel, evalMethod ) )
                    bestModel = model;
                
                // Try moving average model using periods per year if avail.
                if ( dataSet.getPeriodsPerYear() > 0 )
                    {
                        model = new MovingAverageModel( dataSet.getPeriodsPerYear() );
                        model.init( dataSet );
                        if ( betterThan( model, bestModel, evalMethod ) )
                            bestModel = model;
                    }
                
                // TODO: Vary the period and try other MA models
                // TODO: Consider appropriate use of time period in this
                
                // Try the best fit simple exponential smoothing model
                model = SimpleExponentialSmoothingModel.getBestFitModel(dataSet);
                if ( betterThan( model, bestModel, evalMethod ) )
                    bestModel = model;
                
                // Try the best fit double exponential smoothing model
                model = DoubleExponentialSmoothingModel.getBestFitModel(dataSet);
                if ( betterThan( model, bestModel, evalMethod ) )
                    bestModel = model;
                
                // Try the best fit triple exponential smoothing model
                model = TripleExponentialSmoothingModel.getBestFitModel(dataSet);
                if ( betterThan( model, bestModel, evalMethod ) )
                    bestModel = model;
                
                
            }
        
        return bestModel;
    }
    
    /**
     * A helper method to determine, based on the existing evaluation criteria,
     * whether one model is "better than" a second model. This is done using
     * the evaluation criteria exposed by each model, as defined in the
     * ForecastingModel interface.
     *
     * <p>Generally, model2 should be the model that you expect to be worse. It
     * can also be <code>null</code> if no model2 has been selected. model1
     * cannot be <code>null</code>. If model2 is <code>null</code>, then
     * betterThan will return true on the assumption that some model, any
     * model, is better than no model.
     *
     * <p>The determination of which model is "best" is definitely subjective
     * when the two models are close. The approach implemented here is to
     * consider all current evaluation criteria (which admittedly are not
     * independent of each other), and if more of the criteria are in favor of
     * one model, then betterThan will return true.
     *
     * <p>It is expected that this implementation may change over time, so do
     * not depend on the approach described here. Rather just consider that
     * this method will implement a reasonable comparison of two models.
     * @param model1 the first model to compare.
     * @param model2 the second model to compare. If model1 is determined to
     *        be "better than" model2, then true is returned. model2 can be
     *        <code>null</code> representing the absence of a model.
     * @param evalMethod specifies how to determine the "best" model; using
     *        which EvaluationCriteria.
     * @return true if model1 is "better than" model2; otherwise false.
     */
    private static boolean betterThan( ForecastingModel model1,
                                       ForecastingModel model2,
                                       EvaluationCriteria evalMethod )
    {
        // Special case. Any model is better than no model!
        if ( model2 == null )
            return true;
        
        double tolerance = 0.00000001;
        
        // Use evaluation method as requested by user
        if ( evalMethod == EvaluationCriteria.BIAS )
            return ( model1.getBias() <= model2.getBias() );
        else if ( evalMethod == EvaluationCriteria.MAD )
            return ( model1.getMAD() <= model2.getMAD() );
        else if ( evalMethod == EvaluationCriteria.MAPE )
            return ( model1.getMAPE() <= model2.getMAPE() );
        else if ( evalMethod == EvaluationCriteria.MSE )
            return ( model1.getMSE() <= model2.getMSE() );
        else if ( evalMethod == EvaluationCriteria.SAE )
            return ( model1.getSAE() <= model2.getSAE() );
        else if ( evalMethod == EvaluationCriteria.AIC )
            return ( model1.getAIC() <= model2.getAIC() );
        
        // Default evaluation method is a combination
        int score = 0;
        if ( model1.getAIC()-model2.getAIC() <= tolerance )
            score++;
        else if ( model1.getAIC()-model2.getAIC() >= tolerance )
            score--;
        
        if ( model1.getBias()-model2.getBias() <= tolerance )
            score++;
        else if ( model1.getBias()-model2.getBias() >= tolerance )
            score--;
        
        if ( model1.getMAD()-model2.getMAD() <= tolerance )
            score++;
        else if ( model1.getMAD()-model2.getMAD() >= tolerance )
            score--;
        
        if ( model1.getMAPE()-model2.getMAPE() <= tolerance )
            score++;
        else if ( model1.getMAPE()-model2.getMAPE() >= tolerance )
            score--;
        
        if ( model1.getMSE()-model2.getMSE() <= tolerance )
            score++;
        else if ( model1.getMSE()-model2.getMSE() >= tolerance )
            score--;
        
        if ( model1.getSAE()-model2.getSAE() <= tolerance )
            score++;
        else if ( model1.getSAE()-model2.getSAE() >= tolerance )
            score--;
        
        if ( score == 0 )
            {
                // At this point, we're still unsure which one is best
                //  so we'll take another approach
                double diff = model1.getAIC() - model2.getAIC()
                    + model1.getBias() - model2.getBias()
                    + model1.getMAD()  - model2.getMAD()
                    + model1.getMAPE() - model2.getMAPE()
                    + model1.getMSE()  - model2.getMSE()
                    + model1.getSAE()  - model2.getSAE();
                return ( diff < 0 );
            }
        
        return ( score > 0 );
    }
}
// Local Variables:
// tab-width: 4
// End:
