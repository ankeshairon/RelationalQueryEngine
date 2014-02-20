/*
 * @author: Dev Bharadwaj
 * 										         Operator
 * 								       				 |
 * 				--------------------------------------------------------------------------------
 * 				|								     |									       |
 * 			   Leaf 						       Unary                                     Binary				
 * 		-------------------			-------------------------------------	         --------------------
 * 		|                 |	        |       |         |        |        |            |                  |
 *    Source            Nested*   Select  Project  Aggregate  GroupBy  OrderBy     Join               Union
 *    					  | 	                      |
 *    			    -----------       -----------------------------------
 *    			    |         |       |      |      |     |      |      |
 *    			   In       Exists   Sum   Count   Avg   Mean   Mean   Limit 
 */

package edu.buffalo.cse562.model.operatorabstract;

import edu.buffalo.cse562.model.data.ResultSet;

public interface Operator {

    public void dataIn(ResultSet data);

    public ResultSet dataOut();

}
