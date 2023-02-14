import java.util.ArrayList;
import java.util.HashMap;

public class Joint_Probability {



    /**
     * for example, this function calculates the joint probability for an assignment like, P(M=1, J=0, E=0, A=0, B=0)
     * we need this to calculate the whole probability
     * @param bayesian_network
     * @param assignment
     * @return
     */
    public static double CalcProb(Bayesian_Network bayesian_network, HashMap<Variable, Integer> assignment)
    {
        double prob = 1;
        for(Variable var : assignment.keySet())
        {
            CPT var_cpt = bayesian_network.var_to_cpt.get(var);
            HashMap<Variable, Integer> current_assignment = new HashMap<>();
            // the cpt itself knows which variable is the query, we just need to supply values
            for(Variable v : var_cpt.variables)
                current_assignment.put(v, assignment.get(v));
            double current_prob = var_cpt.GetProbability(current_assignment);
            if(prob == 1)
                prob = current_prob;
            else
            {
                GlobalCounter.Multiply();
                prob *= current_prob;
            }
        }
        return prob;
    }
    /**
     * This function takes an assignment, and generates all the assignments that complete it to the probability of the
     * denominator. example with boolean variables: P(J=1 | M=1, E=1) --->
     * MakeRestProbabilities(J, {{J=1},{M=1}, {E=1}}) = [{{J=0},{M=1}, {E=1}}]
     * @param query_variable J
     * @param assignment {{J=1},{M=1}, {E=1}}
     * @return [{{J=0},{M=1}, {E=1}}]
     */
    public static ArrayList<HashMap<Variable, Integer>> MakeRestProbabilities(Variable query_variable, HashMap<Variable,Integer> assignment)
    {
        ArrayList<HashMap<Variable,Integer>> restAssignments = new ArrayList<>();
        for (int i = 0; i < query_variable.value_names.size(); i++)
            if(i != assignment.get(query_variable))
            {
                HashMap<Variable, Integer> new_assignment = XmlParse.generate_deep_copy(assignment);
                new_assignment.replace(query_variable, i);
                restAssignments.add(new_assignment);
            }
        return restAssignments;
    }
}
