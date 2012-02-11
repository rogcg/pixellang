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

public class SymbolsTable {

	public static ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	public static ArrayList<Symbol> literalStrings = new ArrayList<Symbol>();

	public SymbolsTable() {
		symbols = new ArrayList<Symbol>();
	}

	public static void addToken(Token token) {

		int index = getSymbolIndex(token);

		if (index == -1) {
			Symbol symb = new Symbol(token, null);
			symbols.add(symb);			
			token.setIndex(symbols.indexOf(symb));
		} else {
			token.setIndex(index);
		}

	}

	public static void addLiteralStringToken(Token token) {
		
			Symbol symb = new Symbol(token, null);
			symb.setNick(LabelGenerator.getNextTemp());
			literalStrings.add(symb);
			token.setIndex(literalStrings.indexOf(symb));		 			
		
	}

	public static void setTokenType(Token token, String type) {

		Symbol symb = searchSymbol(token);

		if (symb != null) {

			symb.setType(type);

		} else {
			throw new RuntimeException("Nonexistent symbol.");
		}

	}

	public static String getTokenImage(Token token) {

		Symbol symb = searchSymbol(token);

		if (symb.getToken() == null) {

			throw new RuntimeException("Nonexistent symbol on the table");

		} else {

			return symb.getToken().getImage();

		}

	}

	public static String getTokenType(Token token) {

		Symbol symb = searchSymbol(token);

		if (symb == null) {

			throw new RuntimeException("Nonexistent symbol.");

		} else {

			return symb.getType();

		}

	}

	public static Symbol searchSymbol(Token token) {

		return symbols.get(getSymbolIndex(token));

	}

	public static int getSymbolIndex(Token token) {

		for (Symbol symb : symbols) {

			if (symb.getToken().getImage().equals(token.getImage())) {
				return symbols.indexOf(symb);
			}
		}
		return -1;
	}

	public static String constantTypeTable(Token token) {

		if (token._getClass().equals("ILC")) {

			return "int";

		} else if (token._getClass().equals("LSC")) {

			return "str";

		} else if (token._getClass().equals("RLC")) {

			return "real";

		}
		
		return null;
		
	}

	public static void showSymbolsTable() {

		System.out.println("\n-> Symbols list\n");
		for (Symbol symb : symbols) {
			System.out.println(symb);
		}
	}

	public static void showSymbolsTableLSC() {

		System.out.println("\n-> Symbols list LSC\n");
		for (Symbol symb : literalStrings) {
			System.out.println(symb);
		}
	}

}
