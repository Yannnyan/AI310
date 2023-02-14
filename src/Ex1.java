import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Ex1 {
    public static String FILENAME = "src\\alarm_net.xml";
    public static String INPUTPATH = "src\\input2.txt"; // remember to change to input.txt
    public static String OUTPUTPATH = "src\\output.txt";
    public static void main(String[] args)
    {
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String XMLPATH = "src\\" + InputParse.GetXmlPath(INPUTPATH);
        Bayesian_Network bayesian_network = GetBayesNetFromXML( XMLPATH);
        assert bayesian_network != null;
        InputParse inputParser = new InputParse(INPUTPATH, GetNameToVar(bayesian_network.var_to_cpt.keySet()));
        GenerateOutputfile(inputParser, bayesian_network);
    }
    public static void GenerateOutputfile(InputParse inputParser,
                                          Bayesian_Network bayesian_network)
    {
        WipeOutputFile();
        ArrayList<HashMap<Variable, Integer>> assignments = inputParser.GetAssignments();
        ArrayList<Integer> algorithmIndexes = inputParser.GetAlgorithmIndex();
        ArrayList<Variable> query_variables = inputParser.GetQueries();
        for (int i = 0; i < assignments.size(); i++) {
            // check if the query already exists
            boolean existsAlready = false;
            for(CPT cpt : bayesian_network.var_to_cpt.values())
            {
                Double existsInCPT = cpt.GetProbability(assignments.get(i));
                if(existsInCPT != null)
                {
                    existsAlready = true;
                    BigDecimal bd = BigDecimal.valueOf(existsInCPT);
                    bd = bd.setScale(5, RoundingMode.HALF_UP);
                    double result = bd.doubleValue();
                    String line = result + ",0,0";
                    WriteLineToOutput(line);
                    break;
                }
            }
            if(existsAlready)
                continue;
            if(algorithmIndexes.get(i) == 1)
            {
                GlobalCounter.ZeroCounters();
                double res = bayesian_network.calculate_probability(assignments.get(i), query_variables.get(i));
                BigDecimal bd = BigDecimal.valueOf(res);
                bd = bd.setScale(5, RoundingMode.HALF_UP);
                double result = bd.doubleValue();
                int addition_count = GlobalCounter.AdditionCounter;
                int multiply_count = GlobalCounter.MultiplicationCounter;
                String line = result + "," + addition_count + "," + multiply_count;
                WriteLineToOutput(line);
            }
            else if(algorithmIndexes.get(i) == 2)
            {
                GlobalCounter.ZeroCounters();
                HashMap<Variable, Integer> assignment = assignments.get(i);
                Variable query_var = query_variables.get(i);
                VariableElimination VE = new VariableElimination(bayesian_network);
                Factor f = VE.Variable_Elimination_Algorithm(assignment, query_var, 1);
                HashMap<Variable, Integer> wanted_assignment_for_query = new HashMap<>();
                Variable Q = f.relevant_variables.get(0);
                wanted_assignment_for_query.put(Q, assignment.get(Q));
                double res = f.factor_table.get(wanted_assignment_for_query);
                BigDecimal bd = BigDecimal.valueOf(res);
                bd = bd.setScale(5, RoundingMode.HALF_UP);
                double result = bd.doubleValue();
                int addition_count = GlobalCounter.AdditionCounter;
                int multiply_count = GlobalCounter.MultiplicationCounter;
                String line = result + "," + addition_count + "," + multiply_count;
                WriteLineToOutput(line);
            }
            else
            {
                GlobalCounter.ZeroCounters();
                HashMap<Variable, Integer> assignment = assignments.get(i);
                Variable query_var = query_variables.get(i);
                VariableElimination VE = new VariableElimination(bayesian_network);
                Factor f = VE.Variable_Elimination_Algorithm(assignment, query_var, 2);
                HashMap<Variable, Integer> wanted_assignment_for_query = new HashMap<>();
                Variable Q = f.relevant_variables.get(0);
                wanted_assignment_for_query.put(Q, assignment.get(Q));
                double res = f.factor_table.get(wanted_assignment_for_query);
                BigDecimal bd = BigDecimal.valueOf(res);
                bd = bd.setScale(5, RoundingMode.HALF_UP);
                double result = bd.doubleValue();
                int addition_count = GlobalCounter.AdditionCounter;
                int multiply_count = GlobalCounter.MultiplicationCounter;
                String line = result + "," + addition_count + "," + multiply_count;
                WriteLineToOutput(line);
            }
        }
    }
    public static void WipeOutputFile()
    {
        File outputFile = new File(OUTPUTPATH);
        try{
            FileWriter fw = new FileWriter(outputFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.write("");
            pw.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
    public static void WriteLineToOutput(String line)
    {
        FileWriter fw;
        BufferedWriter bw;
        PrintWriter pw;
        File outputFile = new File(OUTPUTPATH);
        try {
            fw = new FileWriter(outputFile, true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            pw.println(line);
            pw.close();
            bw.close();
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static HashMap<String, Variable> GetNameToVar(Set<Variable> variables)
    {
        HashMap<String, Variable> nameToVar = new HashMap<>();
        for(Variable var : variables)
            nameToVar.put(var.variable_name, var);

        return nameToVar;
    }

    public static Bayesian_Network GetBayesNetFromXML(String PATH) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            XmlParse parser = new XmlParse();
            saxParser.parse(PATH, parser);
            for (int i = 0; i < parser.cptList.size(); i++) {
                assert parser.cptList.get(i).CheckCPTComplete();
            }
            return new Bayesian_Network(parser.cptList);
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
