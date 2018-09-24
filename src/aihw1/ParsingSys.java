package aihw1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ParsingSys {
	
	ArrayList<String> rootList, rules, learnedList;
	HashMap<String, Boolean> facts;

	public ParsingSys() {
		facts = new HashMap<String, Boolean>();
		rootList = new ArrayList<String>();
		rules = new ArrayList<String>();
		learnedList = new ArrayList<String>();
	}

	public void parseInput(String input) {
		// Teach 1
		if (input.matches("Teach (-R|-L) [a-zA-Z_]+ = \"(\\w| )+\"")) {
			if (varNotExist(input.split(" ")[2])) {
				if(input.split(" ")[1].equals("-R")){
					rootList.add(input.substring(9));
				} else {
					learnedList.add(input.substring(9));
				}
				facts.put(input.split(" ")[2], false);
			} else {
				System.out.println("Variable already defined");
			}
		// Teach 2
		} else if (input.matches("Teach [a-zA-Z_]+ = (true|false)")) {
			if (checkRoot(input.split(" ")[1])){
				for(String s: facts.keySet()){
					if(s.split(" ")[0].equals(input.split(" ")[1])){
						if(input.split(" ")[3].equals("true"))
							facts.put(s, true);
						else
							facts.put(s, false);
					}
				}
			} else {
				if(!varNotExist(input.split(" " )[1])){
					System.out.println("Cannot set a learned variable");
				} else {
					System.out.println("Variable does not exist");
				}
			}
		// Teach 3
		} else if (input.matches("Teach [a-zA-Z_&|!()]+ -> [a-zA-Z_]")){
			if(checkRules(input.split(" ")[1])){
				String temp = input.substring(6);
				String temp2 = temp.replaceAll("[&|!(->)]", " ").trim();
				String[] tempArray = temp2.split(" +");
				if(!checkLearn(tempArray[tempArray.length-1]))
					System.out.println("Consquence variable must be a learned variable");
				else{
					Set<String> varSet = new HashSet<String>();
					for(int i = 0; i < tempArray.length; i++){
						varSet.add(tempArray[i]);
					}
					boolean definedVarsFlag = true;
					for(String var: varSet){
						if(varNotExist(var)){
							System.out.println("One or more of the variables does not exist");
							definedVarsFlag = false;
							break;
						}
					}
					if(definedVarsFlag){
						rules.add(temp);
					}
				}
			} else {
				System.out.println("Rules already exists");
			}
		}
		// List
		else if (input.matches("List")){
			System.out.println("Root Variables:");
			for(String s: rootList){
				System.out.println("\t" + s);
			}
			System.out.println("\n" + "Learned Variables:");
			for(String s: learnedList){
				System.out.println("\t" + s);
			}
			System.out.println("\n" + "Facts:");
			for(String s: facts.keySet()){
				if (facts.get(s))
					System.out.println("\t" + s);
			}
			System.out.println("\n" + "Rules:");
			for(String s: rules){
				System.out.println("\t" + s);
			}
		}
		// Learn
		else if (input.matches("Learn")){
			int factsSize = -1;
			while(factsSize != trueFactsSize(facts)){
				factsSize = trueFactsSize(facts);
				for(String s: rules){
					if(checkExpression(s.split(" ")[0], facts)){
						facts.put(s.split(" ")[2], true);
					}	
				}
				
			}
		// Query
		} else if (input.matches("Query [a-zA-Z_&|!()]+")){
			HashMap<String, Boolean> facts2 = new HashMap<String, Boolean>(facts);
			int factsSize = -1;
			while(factsSize != trueFactsSize(facts2)){
				factsSize = trueFactsSize(facts2);
				for(String s: rules){
					if(checkExpression(s.split(" ")[0], facts2)){
						facts2.put(s.split(" ")[2], true);
					}	
				}
			}
			System.out.println(checkExpression(input.split(" ")[1], facts2));
		// Why
		} else if (input.matches("Why [a-zA-Z_&!|()]+")){
			HashMap<String, Boolean> facts2 = new HashMap<String, Boolean>(facts);
			int factsSize = -1;
			while(factsSize != trueFactsSize(facts2)){
				factsSize = trueFactsSize(facts2);
				for(String s: rules){
					if(checkExpression(s.split(" ")[0], facts2)){
						facts2.put(s.split(" ")[2], true);
					}	
				}
			}
			for(String var: facts2.keySet()){
				if(facts2.get(var)){
					if (rootList.contains(var)){
						System.out.println("WE KNOW " + var + " IS TRUE");
					} else if (learnedList.contains(var)) {
						
					}
				}
			}
		}else{
			System.out.println("Unrecognized command.");
		}
	}

	public boolean checkRules(String rule) {
		for (String s : rules) {
			if (s.equals(rule)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkRoot(String root) {
		for (String s : rootList) {
			if (s.split(" ")[0].equals(root)) {
				return true;
			}
		}
		return false;
	}

	public boolean varNotExist(String var) {
		for (String s : rootList) {
			if (s.split(" ")[0].equals(var)) {
				return false;
			}
		}
		for (String s: learnedList) {
			if (s.split(" ")[0].equals(var)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean checkLearn(String learn){
		for (String s : learnedList) {
			if (s.split(" ")[0].equals(learn)) {
				return true;
			}
		}
		return false;
	}
	
	public int trueFactsSize(HashMap<String, Boolean> f){
		int i = 0;
		for(String s: f.keySet()){
			if(f.get(s))
				i++;
		}
		return i;
	}
	
	public boolean checkExpression(String exp, HashMap<String, Boolean> f){
		while(exp.contains("(")){
			int par_depth = 0;
			int priority_par_depth = 0;
			int priority_par_index = -1;
			for(int i = 0; i < exp.length(); i++){
				if(exp.charAt(i) == '('){
					par_depth++;
					if(par_depth > priority_par_depth){
						priority_par_index = i;
						priority_par_depth = par_depth;
					}
				}
				else if(exp.charAt(i) == ')')
					par_depth--;
			}
			String priority_expression = "";
			int i = priority_par_index;
			while(exp.charAt(i) != ')'){
				priority_expression += exp.charAt(i);
				i++;
			}
			priority_expression += exp.charAt(i);
			//System.out.println(priority_expression);
			if(evaluate(priority_expression, f))
				exp = exp.replace(priority_expression, "true");
			else
				exp = exp.replace(priority_expression, "false");
			//System.out.println(exp);
		}
		if(!(exp.equals("true") || exp.equals("false"))){
			//System.out.println(exp);
			if(evaluate(exp, f))
				exp = "true";
			else
				exp = "false";
		}
		if(exp.equals("true"))
			return true;
		else
			return false;
	}
	
	public boolean evaluate(String exp, HashMap<String, Boolean> f){
		if(exp.charAt(0) == '(')
			exp = exp.substring(1, exp.length() - 1);
		//System.out.println(exp);
		while(exp.contains("!") || exp.contains("&") || exp.contains("|")){
			for(int i = 0; i < exp.length(); i++){
				if(exp.charAt(i) == '!'){
					String temp = "";
					int j = i + 1;
					while(j < exp.length() && exp.charAt(j) != '!' && exp.charAt(j) != '&' && exp.charAt(j) != '|'){
						temp += exp.charAt(j);
						j++;
					}
					if(temp.equals("true"))
						exp = exp.replace("!true", "false");
					else if(temp.equals("false"))
						exp = exp.replace("!false", "true");
					else{
						if(f.get(temp))
							exp = exp.replace("!" + temp, "false");
						else
							exp = exp.replace("!" + temp, "true");
					}
				}
			}
			for(int i = 0; i < exp.length(); i++){
				if(exp.charAt(i) == '&'){
					String temp2 = "";
					int j = i + 1;
					while(j < exp.length() && exp.charAt(j) != '&' && exp.charAt(j) != '|'){
						temp2 += exp.charAt(j);
						j++;
					}
					String temp1 = exp.substring(0, i);
					if(temp1.equals("true")){
						if(temp2.equals("true"))
							exp = exp.replace(temp1 + "&" + temp2, "true");
						else if(temp2.equals("false"))
							exp = exp.replace(temp1 + "&" + temp2, "false");
						else{
							if(f.get(temp2))
								exp = exp.replace(temp1 + "&" + temp2, "true");
							else
								exp = exp.replace(temp1 + "&" + temp2, "false");
						}
					}
					else if(temp1.equals("false")){
						exp = exp.replace(temp1 + "&" + temp2, "false");
					}
					else{
						if(f.get(temp1)){
							if(temp2.equals("true"))
								exp = exp.replace(temp1 + "&" + temp2, "true");
							else if(temp2.equals("false"))
								exp = exp.replace(temp1 + "&" + temp2,  "false");
							else{
								if(f.get(temp2)){
									exp = exp.replace(temp1 + "&" + temp2, "true");
								}
								else
									exp = exp.replace(temp1 + "&" + temp2, "false");
							}
						}
						else
							exp = exp.replace(temp1 + "&" + temp2, "false");
					}
				}
			}
			for(int i = 0; i < exp.length(); i++){
				if(exp.charAt(i) == '|'){
					String temp2 = "";
					int j = i + 1;
					while(j < exp.length() && exp.charAt(j) != '&' && exp.charAt(j) != '|'){
						temp2 += exp.charAt(j);
						j++;
					}
					String temp1 = exp.substring(0, i);
					if(temp1.equals("true"))
						exp = exp.replace(temp1 + "|" + temp2, "true");
					else if(temp1.equals("false")){
						if(temp2.equals("true"))
							exp = exp.replace(temp1 + "|" + temp2, "true");
						else if(temp2.equals("false"))
							exp = exp.replace(temp1 + "|" + temp2, "false");
						else{
							if(f.get(temp2))
								exp = exp.replace(temp1 + "|" + temp2, "true");
							else
								exp = exp.replace(temp1 + "|" + temp2, "false");
						}
					}
					else{
						if(f.get(temp1))
							exp = exp.replace(temp1 + "|" + temp2, "true");
						else{
							if(temp2.equals("true"))
								exp = exp.replace(temp1 + "|" + temp2, "true");
							else if(temp2.equals("false"))
								exp = exp.replace(temp1 + "|" + temp2, "false");
							else{
								if(f.get(temp2))
									exp = exp.replace(temp1 + "|" + temp2, "true");
								else
									exp = exp.replace(temp1 + "|" + temp2, "false");
							}
						}
					}
				}
			}
		}
		if(exp.equals("true"))
			return true;
		else
			return false;
	}
	
	/*public void explain(String exp){
		HashMap<String, Boolean> facts2 = new HashMap<String, Boolean>(facts);
		int factsSize = -1;
		while(factsSize != trueFactsSize(facts2)){
			factsSize = trueFactsSize(facts2);
			for(String s: rules){
				if(checkExpression(s.split(" ")[0], facts2)){
					facts2.put(s.split(" ")[2], true);
				}	
			}
		}
		if (checkExpression(exp.split(" ")[1], facts2)){
			// start = 0
			// for length of string
				// char at i == !
					// char at i == ( take inner expression, call explain()
					// if var -> explain if true
				// char at i == & or |
					// char at i == ( take inner expression, call explain()
					// if var -> explain if true
		}*/
	//}

}