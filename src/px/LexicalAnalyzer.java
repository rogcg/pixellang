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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import px.SymbolsTable;
import px.Token;

public class LexicalAnalyzer {
	
	public List<Token> tokens = new ArrayList<Token>();
	private BufferedReader br;
	private List<String> errors = new ArrayList<String>();			
	
	public LexicalAnalyzer(BufferedReader br) {
		this.br = br;
	}

	public void analyze() throws IOException {
		String currentLine = br.readLine();
		Integer lineNumber = 0;
		
		while (currentLine != null) {
			lineNumber++;
			StringTokenizer tokenizator = new StringTokenizer(currentLine);
			while(tokenizator.hasMoreTokens()) {
				String image = tokenizator.nextToken();
				if(StaticTables.isReservatedWord(image)) {
					Token token = new Token(image, "RW", -1, lineNumber, 0);
					tokens.add(token);
					SymbolsTable.addToken(token);
				} else if(StaticTables.isDelimiter(image)) {
					Token token = new Token(image, "DE", -1, lineNumber, 0);
					tokens.add(token);
					SymbolsTable.addToken(token);
				}else if(image.matches("[a-z][a-z0-9]*")){
					Token token = new Token(image, "ID", -1, lineNumber, 0);
					tokens.add(token);
					SymbolsTable.addToken(token);
				} 
				else if(image.matches("\"[0-9a-zA-z.\" ]*")) {
					Token token = new Token(image, "LSC", -1, lineNumber, 0);
					tokens.add(token);
					SymbolsTable.addToken(token);
					SymbolsTable.addLiteralStringToken(token);					
				}else if(image.matches(("[0-9]+.[0-9]*[0-9]*.[0-9]+"))) {
					Token token = new Token(image, "RLC", -1, lineNumber, 0);
					tokens.add(token);
					SymbolsTable.addToken(token);
				}else if(image.matches("[0-9]+")) {
					Token token = new Token(image, "ILC", -1, lineNumber, 0);
					tokens.add(token);
					SymbolsTable.addToken(token);
				}  else if(StaticTables.isOperator(image)){
					Token token = new Token(image, "OP", -1, lineNumber, 0);
					tokens.add(token);
					SymbolsTable.addToken(token);
				}				
				else {
					errors.add("\nLexical Error: unknown symbol: " + image + ", line: " + lineNumber + ", column: ");
				} 
			}
			
			currentLine = br.readLine();
		}
		
		tokens.add(new Token("$", "EOF", -1, -1, -1));
		
	}

	public boolean hasError() {
		return errors.size() > 0;
	}
	
	public void showErrors() {
		for(String error: errors) {
			System.out.println(error);
		}
	}
	
	public void showTokens() {
		for(Token token: tokens) {
			System.out.println(token);
		}
	}
}
