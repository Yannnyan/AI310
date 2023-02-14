import java.util.ArrayList;
import java.util.HashMap;

public class Conditional_probability {


    /**
     * Get all the hidden variables which are not in the assignemnt
     * @param assignment Assingment
     * @param all_variables all variables
     * @return
     */
    public static ArrayList<Variable> GetHiddenVariables(HashMap<Variable, Integer> assignment,
                                                         ArrayList<Variable> all_variables)
    {
        ArrayList<Variable> hidden = new ArrayList<>();
        for(Variable var : all_variables)
            if(!assignment.containsKey(var))
                hidden.add(var);
        return hidden;
    }

    /**
     *
     * @param variables All the variables available in the bayes net
     * @param assignment the query with the assigned values, such as P(J = 1 | M = 1)
     * @return the whole probability of the assignment, such as
     * [{{J, 1}, {M, 1}, {E, 0}, {B, 0}, {A, 0}},
     * {{J, 1}, {M, 1}, {E, 0}, {B, 0}, {A, 1}}, {{J, 1}, {M, 1}, {E, 0}, {B, 1}, {A, 0}},
     * {{J, 1}, {M, 1}, {E, 0}, {B, 1}, {A, 1}}, {{J, 1}, {M, 1}, {E, 1}, {B, 0}, {A, 0}},
     * {{J, 1}, {M, 1}, {E, 1}, {B, 0}, {A, 1}}, {{J, 1}, {M, 1}, {E, 1}, {B, 1}, {A, 0}},
     * {{J, 1}, {M, 1}, {E, 1}, {B, 1}, {A, 1}}]
     */
    public static ArrayList<HashMap<Variable,Integer>> WholeProbability(ArrayList<Variable> variables,
                                                                        HashMap<Variable, Integer> assignment)
    {
        ArrayList<Variable> hidden = new ArrayList<>();
        for(Variable var : variables)
            if(!assignment.containsKey(var))
                hidden.add(var);
        ArrayList<HashMap<Variable, Integer>> assignments = new ArrayList<>();
        assignments.add(assignment);
        assignments = XmlParse.generate_all_assignments_helper(0, assignments, hidden);
        return assignments;
    }

    /**
     * This function calculates complex queries such as P(J=1 | M=1, E=1)
     * @param bayesian_network Bayes net
     * @param assignment Assignment we want to calculate, such as {{J=1}, {M=1}, {E=1}}
     * @param query_variable The query of the assignment, such as J
     * @return the probability of the query to happen
     */
    public static double CalcQuery(Bayesian_Network bayesian_network, HashMap<Variable, Integer> assignment, Variable query_variable)
    {
        double nominator;
        double denominator = 0;
        ArrayList<HashMap<Variable, Integer>> all_assignments = WholeProbability(new ArrayList<>(bayesian_network.var_to_cpt.keySet()),
                                                assignment);
        nominator = SumAssignmentsProbabilities(bayesian_network, all_assignments);
        /** Calculates the rest of the assignments*/
        ArrayList<HashMap<Variable, Integer>> restAssignments = Joint_Probability.MakeRestProbabilities(query_variable, assignment);
        /** Calculates the sum of all the assignments to get the denominator*/
        for(HashMap<Variable, Integer> current_assignment : restAssignments)
        {
            ArrayList<HashMap<Variable, Integer>> all_assignments1 = WholeProbability(new ArrayList<>(bayesian_network.var_to_cpt.keySet()),
                    current_assignment);
            double curr = SumAssignmentsProbabilities(bayesian_network, all_assignments1);
            if(denominator == 0)
                denominator = curr;
            else
            {
                GlobalCounter.Add();
                denominator += curr;
            }
        }
        if(denominator == 0 )
            denominator = nominator;
        else {
            GlobalCounter.Add();
            // adds the value calculated to the last assignment we already calculated
            denominator += nominator;
        }
        System.out.print("Nominator: " + nominator + " Denominator: " + denominator);
        System.out.println();
        return (nominator / denominator);
    }

    /**
     * This function is called when we want to calculate the sum of the joint probabilities,
     * of a list of assignments.
     * @param bayesian_network Bayesian network
     * @param all_assignments all the assignments to sum
     * @return
     */
    private static double SumAssignmentsProbabilities(Bayesian_Network bayesian_network, ArrayList<HashMap<Variable, Integer>> all_assignments)
    {
        double total = 0;
        for(HashMap<Variable, Integer> assignment : all_assignments)
        {
            double cur = Joint_Probability.CalcProb(bayesian_network, assignment);
            if(total == 0)
                total = cur;
            else{
                GlobalCounter.Add();
                total += cur;
            }
        }
        return total;
    }


}
