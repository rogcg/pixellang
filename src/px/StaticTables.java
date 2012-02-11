/**
 * Copyright 2011 Roger Câmara
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package px;

import java.util.ArrayList;
import java.util.List;

public class StaticTables {

	private static List<String> reservatedWords = initiateReservatedWords();
	private static List<String> operators = initiateOperators();
	private static List<String> delimiters = initiateDelimiters();
	private List<Token> tokens = new ArrayList<Token>();
	
	private static List<String> initiateReservatedWords() {
		
		List<String> localReservatedWords = new ArrayList<String>();
		localReservatedWords.add("def");
		localReservatedWords.add("main");
		localReservatedWords.add("int");
		localReservatedWords.add("str");
		localReservatedWords.add("ret");
		localReservatedWords.add("if");
		localReservatedWords.add("while");
		localReservatedWords.add("read");
		localReservatedWords.add("print");
		localReservatedWords.add("real");
		
		
		return localReservatedWords;
	}
	
	private static List<String> initiateDelimiters() {
		
		List<String> localDelimiters = new ArrayList<String>();
		localDelimiters.add("(");
		localDelimiters.add(")");
		localDelimiters.add("[");
		localDelimiters.add("]");
		
		return localDelimiters;
	}	
	
	private static List<String> initiateOperators(){
		List<String> localOperators = new ArrayList<String>();
		localOperators.add("=");
		localOperators.add("+");
		localOperators.add("-");
		localOperators.add(">");
		localOperators.add("<");
		localOperators.add("<=");
		localOperators.add(">=");
		localOperators.add("==");
		localOperators.add("!=");
		
		return localOperators;
	}
	
	public static boolean isReservatedWord(String image) {
		return reservatedWords.contains(image);
	}
	
	public static boolean isDelimiter(String image) {
		return delimiters.contains(image);
	}
	
	public static boolean isOperator(String image){
		return operators.contains(image);
		
	}
}
