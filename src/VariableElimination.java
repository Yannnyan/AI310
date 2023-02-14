import java.lang.reflect.Array;
import java.util.*;

public class VariableElimination {

    ArrayList<CPT> initialCPTList;
    ArrayList<CPT> factors;
    Bayesian_Network bayesian_network;
    public VariableElimination(Bayesian_Network bayesian_network)
    {
        initialCPTList = new ArrayList<>(bayesian_network.var_to_cpt.values());
        factors = new ArrayList<>(bayesian_network.var_to_cpt.values());
        this.bayesian_network = bayesian_network;
    }

    private ArrayList<Variable> GetCommonVariables(ArrayList<Variable> f1_Variables, ArrayList<Variable> f2_Variables)
    {
        ArrayList<Variable> common_variables = new ArrayList<>();
        for(Variable var : f1_Variables)
            if(f2_Variables.contains(var))
                common_variables.add(var);
        return common_variables;
    }

    /**
     * Unions two sets of variables. (refer to set as ArrayList)
     * @param listf1 first set
     * @param listf2 second set
     * @return returns the unioned set
     */
    private ArrayList<Variable> UnionVariableList(ArrayList<Variable> listf1, ArrayList<Variable> listf2)
    {
        ArrayList<Variable> unioned_variable_list = new ArrayList<>();
        unioned_variable_list.addAll(listf1);
        for(Variable var : listf2)
        {
            if(!unioned_variable_list.contains(var))
                unioned_variable_list.add(var);
        }
        return unioned_variable_list;
    }

    /**
     * This function checks supposed to be called before we reduce the cpt
     * this because sometimes we don't want to reduce the cpt because of common variables
     * @param evidence The evidence of the inital query
     * @param var The variable to check reduce on
     * @return if the cpt has the current join variable in it's query variables
     */
    public boolean CheckToReduce(ArrayList<Variable> evidence, Variable var)
    {
        return evidence.contains(var);
    }

    /**
     * This function takes a CPT and reduces it's query values by one, therefore the factor's table
     * shrinks the column of the variable to reduce
     * we need to use this before we can Join or Eliminate factors with variable elimination only on CPTs
     * @param cpt The original CPT table
     * @param vars_to_reduce_with_value The variable to reduce the cpt with the value of the var_to_reduce in the initial query
     * @return the shrinked factor
     */
    public Factor ReduceCPT(CPT cpt, HashMap<Variable, Integer> vars_to_reduce_with_value)
    {
        Factor f_ret = new Factor(cpt);
        for(Variable var_to_reduce : vars_to_reduce_with_value.keySet()) {
            Factor f = new Factor(f_ret.relevant_variables);
            for (HashMap<Variable, Integer> assignment : f_ret.factor_table.keySet()) {
                if (Objects.equals(assignment.get(var_to_reduce), vars_to_reduce_with_value.get(var_to_reduce))) {
                    HashMap<Variable, Integer> new_assignment = XmlParse.generate_deep_copy(assignment);
                    new_assignment.remove(var_to_reduce);
                    f.AddProbability(new_assignment, f_ret.GetProbability(assignment));
                }
            }
            f.relevant_variables.remove(var_to_reduce);
            f_ret = f;
        }
        return f_ret;
    }

    /**
     * This function checks if assignment1, and assignment2 share the same
     * values for the commonVariables.
     * This is used when we want to join two factors
     * @param assignment1 assignment
     * @param assignment2 assignment
     * @param commonVariables must be common variables of assignment1 and assignment2, please use GetCommonVariables
     * @return if assignment1, and assignment2 share the same values for the commonVariables
     */
    private boolean CheckContainsSameValues(HashMap<Variable, Integer> assignment1,
                                            HashMap<Variable,Integer> assignment2,
                                            ArrayList<Variable> commonVariables)
    {
        for(Variable var : commonVariables)
            if(!Objects.equals(assignment1.get(var), assignment2.get(var)))
                return false;
        return true;
    }

    /**
     * This function joins two assignments, and returns a new assignment,
     * where the values who are common stay and the values which are not common
     * are added to the assignment.
     * @param assignment1 first assignment
     * @param assignment2 second assignment
     * @return as specified
     */
    private HashMap<Variable, Integer> JoinAssignment(HashMap<Variable,Integer> assignment1,
                                                      HashMap<Variable,Integer> assignment2,
                                                      ArrayList<Variable> commonVariables)
    {
        HashMap<Variable, Integer> assignment = new HashMap<>();
        for(Variable var : assignment1.keySet())
            assignment.put(var, assignment1.get(var));
        for(Variable var : assignment2.keySet())
            if(!commonVariables.contains(var))
                assignment.put(var, assignment2.get(var));
        return assignment;
    }
    /**
     * This function joins the two factors f1, f2 as described in the presentation
     *
     * @param f1 the first Factor
     * @param f2 the second Factor
     * @return the new and joined factor
     */
    public Factor Join(Factor f1, Factor f2)
    {
        ArrayList<HashMap<Variable, Integer>> assignments_f1 = new ArrayList<>(f1.factor_table.keySet());
        ArrayList<HashMap<Variable, Integer>> assignments_f2 = new ArrayList<>(f2.factor_table.keySet());
        ArrayList<Variable> commonVars = GetCommonVariables(f1.relevant_variables, f2.relevant_variables);
        ArrayList<Variable> unioned_variables = UnionVariableList(f1.relevant_variables, f2.relevant_variables);
        Factor f = new Factor(unioned_variables);
        for(HashMap<Variable, Integer> assignment1 : assignments_f1)
        {
            for(HashMap<Variable, Integer> assignment2 : assignments_f2)
            {
                // for each common variable, please create a new probability and assignment,
                // only for the assignments where the values of the common variables are the same
                if(CheckContainsSameValues(assignment1,assignment2,commonVars))
                {
                    double p1 = f1.GetProbability(assignment1);
                    double p2 = f2.GetProbability(assignment2);
                    GlobalCounter.Multiply();
                    f.AddProbability(JoinAssignment(assignment1,assignment2,commonVars), p1 * p2);
                }
            }
        }
        return f;
    }

    /**
     * This function takes a factor and a variable, and then sums out all the values possible of V
     * for an assignment without it.
     * this is used in the variable elimination algorithm
     * @param f Factor
     * @param V Elimination variable
     * @return the new Factor with summed values for the Eliminated variable
     */
    public Factor Eliminate(Factor f, Variable V)
    {
        if(f.relevant_variables.size() == 1)
            return new Factor(new ArrayList<>());
        ArrayList<Variable> new_relevant_vars = f.relevant_variables;
        new_relevant_vars.remove(V);
        Factor Eliminated = new Factor(new_relevant_vars);
        ArrayList<HashMap<Variable,Integer>> all_assignments = new ArrayList<>();
        Variable v1 = new_relevant_vars.get(0);
        for(int i =0; i< v1.value_names.size(); i++)
        {
            HashMap<Variable,Integer> assign = new HashMap<>();
            assign.put(v1, i);
            all_assignments.add(assign);
        }
        new_relevant_vars.remove(v1);
        all_assignments = XmlParse.generate_all_assignments_helper(0, all_assignments, new_relevant_vars);
        new_relevant_vars.add(v1);
        for(HashMap<Variable,Integer> assignment : all_assignments)
        {
            double p = 0;
            for (int value = 0; value < V.value_names.size(); value++) {
                HashMap<Variable, Integer> copy = XmlParse.generate_deep_copy(assignment);
                copy.put(V, value);
                double p1 = f.GetProbability(copy);
                if(p == 0)
                {
                    p = p1;
                }
                else
                {
                    GlobalCounter.Add();
                    p += p1;
                }

            }
            Eliminated.AddProbability(assignment, p);
        }
        return Eliminated;
    }
    public static class compareVariables implements Comparator<Variable>{

        @Override
        public int compare(Variable o1, Variable o2) {
            return o1.variable_name.compareTo(o2.variable_name);
        }
    }
    public static class compareVariables2 implements Comparator<Variable>{
        ArrayList<Factor> factors;
        public compareVariables2(ArrayList<Factor> factors)
        {
            this.factors = factors;
        }
        private int find_average_factor_size(Variable v)
        {
            int sum = 0;
            int num = 0;
            for(Factor f : factors)
            {
                if(f.relevant_variables.contains(v))
                {
                    sum += f.factor_table.size();
                    num += 1;
                }
            }
            if(num == 0)
                return 0;
            return sum / num;
        }
        @Override
        public int compare(Variable v1, Variable v2)
        {
            int average_v1 = find_average_factor_size(v1);
            int average_v2 = find_average_factor_size(v2);
            return Integer.compare(average_v1, average_v2);
        }
    }
    public ArrayList<Variable> OrderVariableList(ArrayList<Variable> variables,
                                                 ArrayList<Factor> factors,
                                                 int algor_num)
    {
        if(algor_num == 1)
        {
            Variable[] variables_in_order = new Variable[variables.size()];
            for(int i = 0; i< variables.size(); i++)
                variables_in_order[i] = variables.get(i);
            Arrays.sort(variables_in_order, new compareVariables());
            return new ArrayList<>(Arrays.asList(variables_in_order));
        }
        else if(algor_num == 2)
        {

            compareVariables2 compareVariables = new compareVariables2(factors);
            Variable[] variables_in_order = new Variable[variables.size()];
            for(int i = 0; i< variables.size(); i++)
                variables_in_order[i] = variables.get(i);
            Arrays.sort(variables_in_order, compareVariables);
            return new ArrayList<>(Arrays.asList(variables_in_order));
        }
        else{
            assert (false);
            return null;
        }
    }

    private ArrayList<Factor> GetAllFactorsByVariable(ArrayList<Factor> factors, Variable var)
    {
        ArrayList<Factor> factors1 = new ArrayList<>();
        for(Factor factor : factors)
            if(factor.relevant_variables.contains(var)) {
                factors1.add(factor);
//                factors.remove(factor);
            }
        return factors1;
    }
    private ArrayList<CPT> GetAllCPTsByVariable(ArrayList<CPT> cptList, Variable var)
    {
        ArrayList<CPT> cptList1 = new ArrayList<>();
        for(CPT factor : cptList)
            if(factor.variables.contains(var))
                cptList1.add(factor);
        return cptList1;
    }

    /**
     *
     * @param cpts the cpts tables
     * @param evidence the evidence variables of the inital query
     * @param assignment the initial assignment of the inital query
     * @return the reduced factors derived from the cpts and from the query
     */
    private ArrayList<Factor> ConvertCPTsToFactors(ArrayList<CPT> cpts, ArrayList<Variable> evidence,
                                                   HashMap<Variable, Integer> assignment)
    {
        ArrayList<Factor> factorList = new ArrayList<>();
        for(CPT cpt : cpts)
        {
            boolean to_reduce = false;
            HashMap<Variable, Integer> var_to_reduce_with_value = new HashMap<>();
            for(Variable var : cpt.variables) {
                if (CheckToReduce(evidence, var))
                {
                    var_to_reduce_with_value.put(var, assignment.get(var));
                    to_reduce = true;
                }
            }
            if(to_reduce)
            {
                Factor f = ReduceCPT(cpt, var_to_reduce_with_value);
                if(f.factor_table.size() <= 1)
                    continue;
                factorList.add(f);
            }
            else
                factorList.add(new Factor(cpt));
        }
        return factorList;
    }

    /**
     * basically run dfs on the bayes net, and delete all the nodes from all_variables
     * which are not ancestors of the query nor the evidence variables
     * @param all_variables all the variables that we want to remove irrelevant variables from them
     * @param query_evidence_variables the query and the evidence variables
     */
    public void removeAllTrivialNodes(ArrayList<Variable> all_variables,
                                      ArrayList<Variable> query_evidence_variables,
                                      ArrayList<CPT> cptList)
    {
        HashSet<Variable> ancestor_variables = new HashSet<>(); // black color
        HashSet<Variable> queued_variables = new HashSet<>(); // grey color
        for(Variable var : query_evidence_variables)
        {
            ArrayList<Variable> ancestors_queue = new ArrayList<>(bayesian_network.var_to_cpt.get(var).variables);
            queued_variables.addAll(ancestors_queue);
            ancestor_variables.add(var);
            ancestors_queue.remove(var);
            while(ancestors_queue.size() > 0) // every variable is a parent of itself
            {
                Variable ancestor  = ancestors_queue.remove(0);
                ancestor_variables.add(ancestor);
                for(Variable parent : bayesian_network.var_to_cpt.get(ancestor).variables)
                {
                    if (!queued_variables.contains(parent)) {
                        ancestors_queue.add(parent);
                        queued_variables.add(parent);
                    }
                }
            }
        }
        ArrayList<Variable> variables = new ArrayList<>(all_variables);
        // remove all the variables which are not ancestors to the query nor the evidence
        variables.forEach(var ->
        {
            if(!ancestor_variables.contains(var))
            {
                all_variables.remove(var);
                cptList.remove(bayesian_network.var_to_cpt.get(var));
            }
        });
    }
    public void removeAllTrivialLeafNodes(ArrayList<Variable> all_variables,
                                          ArrayList<Variable> hidden,
                                          Variable query,
                                          ArrayList<CPT> cptList)
    {
        ArrayList<Variable> leafVariables = new ArrayList<>();
        for(Variable var : all_variables)
        {
            boolean isLeaf = true;
            for(Variable v : all_variables)
            {
                if(v == var)
                    continue;
                if(bayesian_network.var_to_cpt.get(v).variables.contains(var)) {
                    isLeaf = false;
                    break;
                }
            }
            if(isLeaf)
                leafVariables.add(var);
        }
        for(Variable var : leafVariables)
        {
            if(hidden.contains(var) && !query.equals(var))
            {
                all_variables.remove(var);
                cptList.remove(bayesian_network.var_to_cpt.get(var));
            }
        }
    }
    public static class compareFactors implements Comparator<Factor>
    {

        @Override
        public int compare(Factor f1, Factor f2) {
            return Integer.compare(f1.factor_table.size(), f2.factor_table.size());
        }
    }
    /**
     * This function runs the elimination algorithm by eliminating variables in ABC order
     * @param assignemnt the assignment
     * @param query_variable the query variable
     * @param algor_num can mbe number 1 and sorts the cpts vby the abc order
     *                  or can be number 2 and then sorts the cpts by thier size
     * @return the factor with the answer
     */
    public Factor Variable_Elimination_Algorithm(HashMap<Variable,Integer> assignemnt,
                                                 Variable query_variable, int algor_num)
    {
        ArrayList<CPT> cptList = new ArrayList<>(initialCPTList);
        ArrayList<Variable> evidence = new ArrayList<>();
        assignemnt.keySet().forEach(v ->
        {if(!query_variable.equals(v)) evidence.add(v);});
        ArrayList<Variable> all_variables = new ArrayList<>(bayesian_network.var_to_cpt.keySet());
        ArrayList<Variable> hidden = Conditional_probability.GetHiddenVariables(assignemnt, new ArrayList<>(all_variables));
//        removeAllTrivialLeafNodes(all_variables, hidden, query_variable, cptList);
        ArrayList<Variable> evidence_and_query = new ArrayList<>(evidence);
        evidence_and_query.add(query_variable);
        removeAllTrivialNodes(all_variables, evidence_and_query, cptList);
        ArrayList<Factor> factorList = new ArrayList<>();
        factorList.addAll(ConvertCPTsToFactors(cptList , evidence , assignemnt));
        ArrayList<Variable> variablesInOrder = new ArrayList<>();
        if(algor_num == 1)
             variablesInOrder = OrderVariableList(all_variables, factorList, 1 );
        else if(algor_num == 2)
        {
            variablesInOrder = OrderVariableList(all_variables, factorList, 2);
        }
        else
            assert(false);
        for(Variable v : variablesInOrder)
        {
            if(hidden.contains(v))
            {
                ArrayList<Factor> factors_by_var = GetAllFactorsByVariable(factorList, v);
                factors_by_var.forEach(factor -> factorList.remove(factor));
                Factor[] f_sorted_by_size = new Factor[factors_by_var.size()];
                for (int i = 0; i < factors_by_var.size(); i++)
                    f_sorted_by_size[i] = factors_by_var.get(i);
                Arrays.sort(f_sorted_by_size, new compareFactors());
                factors_by_var = new ArrayList<>(Arrays.asList(f_sorted_by_size));
                if(factors_by_var.size() == 0)
                    continue;
                while(factors_by_var.size() > 1)
                {
                    Factor f1 = factors_by_var.get(0);
                    Factor f2 = factors_by_var.get(1);
                    factors_by_var.remove(f1);
                    factors_by_var.remove(f2);
                    Factor f = Join(f1, f2);
                    factors_by_var.add(0, f);
                }
                Factor f = Eliminate(factors_by_var.get(0), v);
                if(f.factor_table.size() <= 1) // discard one valued factors
                    continue;
                factorList.add(0, f);
            }
        }
        while(factorList.size() > 1)
        {
            Factor f1 = factorList.get(0);
            Factor f2 = factorList.get(1);
            factorList.remove(f1);
            factorList.remove(f2);
            Factor f = Join(f1, f2);
            factorList.add(f);
        }
        return NormalizeFinalFactor(factorList.get(0));
    }

    public Factor NormalizeFinalFactor(Factor f)
    {
        assert(f.relevant_variables.size() == 1);
        double denominator = 0;
        for(HashMap<Variable, Integer> assignment : f.factor_table.keySet()) {
            double cur = f.GetProbability(assignment);
            if(denominator == 0)
            {
                denominator = cur;
            }
            else
            {
                GlobalCounter.Add();
                denominator += f.GetProbability(assignment);
            }
        }
        Factor f_new = new Factor(f.relevant_variables);
        for(HashMap<Variable, Integer> assignment : f.factor_table.keySet()) {
            double normalized_assignment_probability = f.GetProbability(assignment) / denominator;
            f_new.AddProbability(assignment, normalized_assignment_probability);
        }
        return f_new;
    }

}
