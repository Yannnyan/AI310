import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Tests {

    @Test
    public void Generate_All_Assignments_Test()
    {
        XmlParse parser = new XmlParse();
        ArrayList<String> values = new ArrayList<String>();
        values.add("T");
        values.add("F");
        parser.currentQuery = new Variable("X", values);
        ArrayList<Variable> evidence = new ArrayList<Variable>();
        Variable V1, V2, V3;
        V1 = new Variable("V1", values);
        V2 = new Variable("V2", values);
        V3 = new Variable("V3", values);
        evidence.add(V1);
        evidence.add(V2);
        evidence.add(V3);
        parser.evidence_variables = evidence;
        // just to see the result, it supposed to fail
        assertEquals(null, parser.generate_all_assignments());
    }

    @Test
    public void Calculate_probability_bayes_net() throws SAXException {
        Bayesian_Network bayesian_network = Ex1.GetBayesNetFromXML("src\\alarm_net.xml");
        HashMap<String,Variable> name_to_var = Ex1.GetNameToVar(bayesian_network.var_to_cpt.keySet());
        InputParse inputParser = new InputParse("src\\input.txt",name_to_var);
        ArrayList<HashMap<Variable,Integer>> assignments = inputParser.GetAssignments();
        ArrayList<Variable> queries = inputParser.GetQueries();
        double p1 = bayesian_network.calculate_probability(assignments.get(0), queries.get(0));
        double p2 = bayesian_network.calculate_probability(assignments.get(3), queries.get(3));
        assertEquals(0.28417, p1, 0.0001);
        assertEquals(0.84902, p2, 0.0001);
    }



    @Test
    public void TestParseInput()
    {
        HashMap<String, Variable> name_to_var = new HashMap<>();
        Variable B = new Variable();
        B.variable_name = "B";
        B.value_names.add("T");
        B.value_names.add("F");
        Variable M = new Variable();
        M.variable_name = "M";
        M.value_names.add("T");
        M.value_names.add("F");
        Variable J = new Variable();
        J.variable_name = "J";
        J.value_names.add("T");
        J.value_names.add("F");
        Variable E = new Variable();
        E.variable_name = "E";
        E.value_names.add("T");
        E.value_names.add("F");
        Variable A = new Variable();
        A.variable_name = "A";
        A.value_names.add("T");
        A.value_names.add("F");
        name_to_var.put("B", B);
        name_to_var.put("M",M);
        name_to_var.put("A",A);
        name_to_var.put("J",J);
        name_to_var.put("E",E);
        InputParse parse = new InputParse("C:\\Users\\Yan\\IdeaProjects\\Ex1_BayesNetwork\\src\\Input\\input.txt", name_to_var);
        ArrayList<HashMap<Variable,Integer>> assignments = parse.GetAssignments();
        HashMap<Variable,Integer> first_assignment = assignments.get(0);
        assertEquals(first_assignment.get(B), Integer.valueOf(0));
        assertEquals(first_assignment.get(M), Integer.valueOf(0));
        assertEquals(first_assignment.get(J), Integer.valueOf(0));
        HashMap<Variable, Integer> forth_assignment = assignments.get(3);
        assertEquals(forth_assignment.get(B), Integer.valueOf(0));
        assertEquals(forth_assignment.get(J), Integer.valueOf(0));
        ArrayList<Variable> query_variables = parse.GetQueries();
        assertEquals(query_variables.get(0), B);
        assertEquals(query_variables.get(3), J);
    }

    @Test
    public void TestReduceCPT()
    {
        ArrayList<Variable> variables = new ArrayList<>();
        Variable B = new Variable();
        B.variable_name = "B";
        B.value_names.add("T");
        B.value_names.add("F");
        Variable M = new Variable();
        M.variable_name = "M";
        M.value_names.add("T");
        M.value_names.add("F");
        Variable J = new Variable();
        J.variable_name = "J";
        J.value_names.add("T");
        J.value_names.add("F");
        Variable E = new Variable();
        E.variable_name = "E";
        E.value_names.add("T");
        E.value_names.add("F");
        Variable A = new Variable();
        A.variable_name = "A";
        A.value_names.add("T");
        A.value_names.add("F");
        variables.add(A);
        variables.add(E);
        variables.add(B);
        try{
            CPT cpt = new CPT(variables);
            HashMap<Variable,Integer> assignment = new HashMap<>();
            assignment.put(A, 1);
            assignment.put(B, 1);
            assignment.put(E, 1);
            cpt.AddProbability(assignment, (float) 0.95);
            assignment = new HashMap<>();
            assignment.put(A,0);
            assignment.put(B, 1);
            assignment.put(E, 1);
            cpt.AddProbability(assignment,(float) 0.05);
            assignment=new HashMap<>();
            assignment.put(A,1);
            assignment.put(B, 0);
            assignment.put(E, 1);
            cpt.AddProbability(assignment,(float) 0.29);
            assignment=new HashMap<>();
            assignment.put(A,0);
            assignment.put(B, 0);
            assignment.put(E, 1);
            cpt.AddProbability(assignment,(float) 0.71);
            assignment=new HashMap<>();
            assignment.put(A,1);
            assignment.put(B, 1);
            assignment.put(E, 0);
            cpt.AddProbability(assignment,(float) 0.94);
            assignment=new HashMap<>();
            assignment.put(A,0);
            assignment.put(B, 1);
            assignment.put(E, 0);
            cpt.AddProbability(assignment,(float) 0.06);
            assignment=new HashMap<>();
            assignment.put(A,1);
            assignment.put(B, 0);
            assignment.put(E, 0);
            cpt.AddProbability(assignment,(float) 0.001);
            assignment=new HashMap<>();
            assignment.put(A,0);
            assignment.put(B, 0);
            assignment.put(E, 0);
            cpt.AddProbability(assignment,(float) 0.999);
            ArrayList<CPT> cptList = new ArrayList<>();
            VariableElimination VE = new VariableElimination(new Bayesian_Network(cptList));
//            Factor f = VE.ReduceCPT(cpt);
//            Factor f_check = new Factor(f.relevant_variables);
//            assignment=new HashMap<>();
//            assignment.put(B, 1);
//            assignment.put(E, 1);
//            f_check.AddProbability(assignment,(float) 0.05);
//            assignment=new HashMap<>();
//            assignment.put(B, 0);
//            assignment.put(E, 1);
//            f_check.AddProbability(assignment,(float) 0.71);
//            assignment=new HashMap<>();
//            assignment.put(B, 1);
//            assignment.put(E, 0);
//            f_check.AddProbability(assignment,(float) 0.06);
//            assignment=new HashMap<>();
//            assignment.put(B, 0);
//            assignment.put(E, 0);
//            f_check.AddProbability(assignment,(float) 0.999);
//            assertEquals(f.factor_table, f_check.factor_table);

        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    public void TestJoin() throws SAXException {
        ArrayList<Variable> variables1 = new ArrayList<>();
        ArrayList<Variable> variables2 = new ArrayList<>();

        Variable B = new Variable();
        B.variable_name = "B";
        B.value_names.add("T");
        B.value_names.add("F");
        Variable M = new Variable();
        M.variable_name = "M";
        M.value_names.add("T");
        M.value_names.add("F");
        Variable J = new Variable();
        J.variable_name = "J";
        J.value_names.add("T");
        J.value_names.add("F");
        Variable E = new Variable();
        E.variable_name = "E";
        E.value_names.add("T");
        E.value_names.add("F");
        Variable A = new Variable();
        A.variable_name = "A";
        A.value_names.add("T");
        A.value_names.add("F");
        variables1.add(J);
        variables1.add(A);
        variables2.add(M);
        variables2.add(A);
        CPT cpt1 = new CPT(variables1);
        CPT cpt2 = new CPT(variables2);
        HashMap<Variable,Integer> assignment1 = new HashMap<>();
        assignment1.put(J, 0);
        assignment1.put(A, 0);
        cpt1.AddProbability(assignment1, (float)0.9);
        assignment1 = new HashMap<>();
        assignment1.put(J, 1);
        assignment1.put(A, 0);
        cpt1.AddProbability(assignment1, (float) 0.1);
        assignment1 = new HashMap<>();
        assignment1.put(J, 0);
        assignment1.put(A,1);
        cpt1.AddProbability(assignment1, (float)0.05);
        assignment1 = new HashMap<>();
        assignment1.put(J,1);
        assignment1.put(A,1);
        cpt1.AddProbability(assignment1, (float)0.95);
        assignment1 = new HashMap<>();
        assignment1.put(M,0);
        assignment1.put(A,0);
        cpt2.AddProbability(assignment1, (float)0.7);
        assignment1 = new HashMap<>();
        assignment1.put(M,1);
        assignment1.put(A,0);
        cpt2.AddProbability(assignment1, (float) 0.3);
        assignment1 = new HashMap<>();
        assignment1.put(M,0);
        assignment1.put(A,1);
        cpt2.AddProbability(assignment1, (float)0.01);
        assignment1 = new HashMap<>();
        assignment1.put(M,1);
        assignment1.put(A,1);
        cpt2.AddProbability(assignment1, (float)0.99);
        ArrayList<CPT> cptlist = new ArrayList<>();
        cptlist.add(cpt1);
        cptlist.add(cpt2);
        VariableElimination VE = new VariableElimination(new Bayesian_Network(cptlist));
//        Factor f = VE.Join(VE.ReduceCPT(cpt1), VE.ReduceCPT(cpt2));
        ArrayList<Variable> test_variables = new ArrayList<>();
        test_variables.add(A);
        assignment1 = new HashMap<>();
        assignment1.put(A, 0);
        Factor f_test = new Factor(test_variables);
        f_test.AddProbability(assignment1, (float)0.63);
        assignment1 = new HashMap<>();
        assignment1.put(A,1);
        f_test.AddProbability(assignment1, (float)0.0005);
        //assertEquals(f.factor_table, f_test.factor_table);

        // P(J|a) * P(M|a)
        Bayesian_Network bayesian_network = Ex1.GetBayesNetFromXML("src\\" + InputParse.GetXmlPath("src\\input.txt"));
        assert bayesian_network != null;
        VariableElimination VE1 = new VariableElimination(bayesian_network);
        HashMap<String ,Variable> name_to_var = Ex1.GetNameToVar(bayesian_network.var_to_cpt.keySet());
        CPT JCpt = bayesian_network.var_to_cpt.get(name_to_var.get("J"));
        CPT MCpt = bayesian_network.var_to_cpt.get(name_to_var.get("M"));
//        Factor facJ = VE1.ReduceCPT(JCpt);
        Factor facM = new Factor(MCpt);
//        Factor fres = VE1.Join(facJ, facM);

    }

    @Test
    public void TestEliminate() throws SAXException {
        Variable B = new Variable();
        B.variable_name = "B";
        B.value_names.add("T");
        B.value_names.add("F");
        Variable M = new Variable();
        M.variable_name = "M";
        M.value_names.add("T");
        M.value_names.add("F");
        Variable J = new Variable();
        J.variable_name = "J";
        J.value_names.add("T");
        J.value_names.add("F");
        Variable E = new Variable();
        E.variable_name = "E";
        E.value_names.add("T");
        E.value_names.add("F");
        Variable A = new Variable();
        A.variable_name = "A";
        A.value_names.add("T");
        A.value_names.add("F");
        ArrayList<Variable> variables = new ArrayList<>();
        variables.add(E);
        variables.add(B);
        variables.add(A);
        CPT f = new CPT(variables);
        HashMap<Variable, Integer> assignment = new HashMap<>();
        assignment.put(A, 0);
        assignment.put(B, 0);
        assignment.put(E, 0);
        f.AddProbability(assignment, (float)0.95);
        assignment = new HashMap<>();
        assignment.put(A, 0);
        assignment.put(B, 1);
        assignment.put(E, 0);
        f.AddProbability(assignment, (float)0.29);
        assignment = new HashMap<>();
        assignment.put(A, 0);
        assignment.put(B, 0);
        assignment.put(E, 1);
        f.AddProbability(assignment, (float)0.94);
        assignment = new HashMap<>();
        assignment.put(A, 0);
        assignment.put(B, 1);
        assignment.put(E, 1);
        f.AddProbability(assignment, (float)0.001);
        assignment = new HashMap<>();
        assignment.put(A, 1);
        assignment.put(B, 0);
        assignment.put(E, 0);
        f.AddProbability(assignment, (float)0.05);
        assignment = new HashMap<>();
        assignment.put(A, 1);
        assignment.put(B, 1);
        assignment.put(E, 0);
        f.AddProbability(assignment, (float)0.71);
        assignment = new HashMap<>();
        assignment.put(A, 1);
        assignment.put(B, 0);
        assignment.put(E, 1);
        f.AddProbability(assignment, (float)0.06);
        assignment = new HashMap<>();
        assignment.put(A, 1);
        assignment.put(B, 1);
        assignment.put(E, 1);
        f.AddProbability(assignment, (float)0.999);
        ArrayList<CPT> cptList = new ArrayList<>();
        cptList.add(f);
        VariableElimination VE = new VariableElimination(new Bayesian_Network(cptList));
        Factor f_new = VE.Eliminate(new Factor(f), A);
        for(HashMap<Variable, Integer> assign : f_new.factor_table.keySet())
        {
            assertEquals(f_new.factor_table.get(assign), Float.valueOf((float) 1));
        }

        // sigma (P(J|a)*P(m|a)*P(a|B,e))
        Bayesian_Network bayesian_network = Ex1.GetBayesNetFromXML(InputParse.GetXmlPath("input.txt"));
        assert bayesian_network != null;

    }

    @Test
    public void TestOrderVariableList()
    {
        ArrayList<Variable> variables = new ArrayList<>();
        Variable B = new Variable();
        B.variable_name = "B";
        B.value_names.add("T");
        B.value_names.add("F");
        Variable M = new Variable();
        M.variable_name = "M";
        M.value_names.add("T");
        M.value_names.add("F");
        Variable J = new Variable();
        J.variable_name = "J";
        J.value_names.add("T");
        J.value_names.add("F");
        Variable E = new Variable();
        E.variable_name = "E";
        E.value_names.add("T");
        E.value_names.add("F");
        Variable A = new Variable();
        A.variable_name = "A";
        A.value_names.add("T");
        A.value_names.add("F");
        variables.add(B);
        variables.add(M);
        variables.add(J);
        variables.add(E);
        variables.add(A);
        VariableElimination VE = new VariableElimination(new Bayesian_Network(new ArrayList<>()));
//        ArrayList<Variable> variables_in_order = VE.OrderVariableList(variables);
        ArrayList<Variable> variables_in_order_test = new ArrayList<>();
        variables_in_order_test.add(A);
        variables_in_order_test.add(B);
        variables_in_order_test.add(E);
        variables_in_order_test.add(J);
        variables_in_order_test.add(M);
//        assertEquals(variables_in_order, variables_in_order_test);
    }

    @Test
    public void TestAlgorithm()
    {
        XmlParse parser = new XmlParse();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(Ex1.FILENAME, parser);
            for (int i = 0; i < parser.cptList.size(); i++) {
                assert parser.cptList.get(i).CheckCPTComplete();
            }
            Bayesian_Network bayesian_network = new Bayesian_Network(parser.cptList);
            String FILE_PATH = "src\\input.txt";
            InputParse parse = new InputParse(FILE_PATH, parser.name_to_var);
            ArrayList<HashMap<Variable,Integer>> assignments = parse.GetAssignments();
            ArrayList<Variable> query_variables = parse.GetQueries();
            VariableElimination VE = new VariableElimination(bayesian_network);
            GlobalCounter.ZeroCounters();
            Factor f = VE.Variable_Elimination_Algorithm(assignments.get(0),query_variables.get(0), 1);
            ArrayList<Variable> v_test = new ArrayList<>();
            Variable B = query_variables.get(0);
            v_test.add(B);
            Factor f_test = new Factor(v_test);
            HashMap<Variable,Integer> assignment = new HashMap<>();
            assignment.put(B, 0);
            f_test.AddProbability(assignment, 0.2841718348510418);
            assignment = new HashMap<>();
            assignment.put(B, 1);
            f_test.AddProbability(assignment, 0.7158281651489581);
            assertEquals(f.factor_table, f_test.factor_table);
            assertEquals(7, GlobalCounter.AdditionCounter);
            assertEquals(16, GlobalCounter.MultiplicationCounter); // works
            Factor f1 = VE.Variable_Elimination_Algorithm(assignments.get(4), query_variables.get(4), 1);
            assignment = new HashMap<>();
            Variable Q = f1.relevant_variables.get(0);
            assignment.put(Q, assignments.get(4).get(Q));
            assertEquals(f1.factor_table.get(assignment), 0.84902, 0.0001);
        }
        catch (SAXException | ParserConfigurationException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
