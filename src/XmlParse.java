import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class XmlParse extends DefaultHandler{

    public XmlParse()
    {

    }
    boolean isVariableName, isOutcome, isCpt, isCurrentQuery, isCurrentEvidence, isCurrentAssignments;
    public Variable currentVariable;
    public Variable currentQuery;
    public Variable currentEvidence;
    public CPT currentCPT;
    public ArrayList<HashMap<Variable,Integer>> all_current_assignments;
    public ArrayList<Variable> evidence_variables;

    public ArrayList<Variable> variables = new ArrayList<>();
    public HashMap<String, Variable> name_to_var = new HashMap<>();
    public ArrayList<CPT> cptList = new ArrayList<>();



    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {

        if(qName.equals("VARIABLE")) {currentVariable = new Variable();}
        if(qName.equals("NAME")) { isVariableName = true; }
        if(qName.equals("OUTCOME"))  { isOutcome = true;  }
        if(qName.equals("DEFINITION"))
        {
            evidence_variables = new ArrayList<>();
            isCpt = true;

        }
        if(qName.equals("FOR")){isCurrentQuery = true;}
        if(qName.equals("GIVEN")){isCurrentEvidence = true;}
        if(qName.equals("TABLE"))
        {
            isCurrentAssignments = true;
            ArrayList<Variable> variables_in_order = new ArrayList<>();
            variables_in_order.add(currentQuery);
            variables_in_order.addAll(evidence_variables);
            currentCPT = new CPT(variables_in_order);
            all_current_assignments = generate_all_assignments();
        }
    }
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if(qName.equals("VARIABLE")) {
            variables.add(currentVariable);
            name_to_var.put(currentVariable.variable_name, currentVariable);
            currentVariable = null;
        }
        if(qName.equals("NAME")) { isVariableName = false; }
        if(qName.equals("OUTCOME"))  { isOutcome = false;  }
        if(qName.equals("DEFINITION"))
        {
            cptList.add(currentCPT);
            isCpt = false;
            currentEvidence = null;
            currentQuery = null;
            currentCPT = null;
        }
        if(qName.equals("FOR")){isCurrentQuery = false;}
        if(qName.equals("GIVEN")){
            evidence_variables.add(0,currentEvidence);
            isCurrentEvidence = false;
            currentEvidence = null;
        }
        if(qName.equals("TABLE"))
        {
            isCurrentAssignments = false;

        }
    }
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isVariableName) {
            currentVariable.variable_name = new String(ch, start, length);
        }
        if (isOutcome) {
            currentVariable.value_names.add(new String(ch, start, length));
        }
        if (isCurrentEvidence)
        {
            currentEvidence = name_to_var.get(new String(ch, start, length));
        }
        if (isCurrentQuery){
            currentQuery = name_to_var.get(new String(ch, start, length));
        }
        if (isCurrentAssignments)
        {
            String str = new String(ch, start, length);
            String[] values = str.split(" ");
            int assign_index = 0;
            for (int i = 0; i < values.length; i++) {
                HashMap<Variable, Integer> v = all_current_assignments.get(i);
                currentCPT.AddProbability(all_current_assignments.get(i) ,Float.parseFloat(values[i]));
            }
        }
    }
    public ArrayList<HashMap<Variable, Integer>> generate_all_assignments()
    {
        ArrayList<HashMap<Variable, Integer>> assignments = new ArrayList<>();
        // add all the query's assignments
        for (int i = 0; i < currentQuery.value_names.size(); i++) {
            HashMap<Variable, Integer> assignment = new HashMap<>();
            assignment.put(currentQuery, i);
            assignments.add(assignment);
        }
        // add all the remaining evidence assignments
        return generate_all_assignments_helper(0, assignments, evidence_variables);
    }
    /**
     * For each assignment array so far, duplicate it, and add this variable's  assignment
     * recursivly for each variable in the list
     *
     */
    public static ArrayList<HashMap<Variable,Integer>> generate_all_assignments_helper(int current_variable_index,
                                                                                ArrayList<HashMap<Variable, Integer>> assignments,
                                                                                ArrayList<Variable> variable_list)
    {
        if (current_variable_index >= variable_list.size())
        {
            return assignments;
        }
        Variable current = variable_list.get(current_variable_index);
        ArrayList<HashMap<Variable, Integer>> new_assignments = new ArrayList<>();
        for (int j = 0; j < current.value_names.size(); j++)
            for (int i = 0; i < assignments.size(); i++)
            {
                HashMap<Variable,Integer> arr = assignments.get(i);
                HashMap<Variable,Integer> copy = generate_deep_copy(arr);
                copy.put(current, j);
                new_assignments.add(copy);
            }
        return generate_all_assignments_helper(current_variable_index + 1, new_assignments, variable_list);

    }
    public static HashMap<Variable,Integer> generate_deep_copy(HashMap<Variable,Integer> hash)
    {
        HashMap<Variable, Integer> copy = new HashMap<>();
        for (Variable var : hash.keySet())
            copy.put(var, hash.get(var));
        return copy;
    }





}
