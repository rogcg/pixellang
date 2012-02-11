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

import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SemanticAnalyzer {

	private Node root;
	private List<String> errors = new ArrayList<String>();

	public SemanticAnalyzer(Node root) {

		this.root = root;

	}

	public void semanticAnalysis() {

		analyze(root);

	}

	private Object analyze(Node node) {

		if (node.getType().equals(NodeIdentifier.DEF)) {

			return def(node);

		} else if (node.getType().equals(NodeIdentifier.ARGLI)) {

			return liArg(node);

		} else if (node.getType().equals(NodeIdentifier.COMMANDLI)) {

			return commandLi(node);

		} else if (node.getType().equals(NodeIdentifier.ARG)) {

			return arg(node);

		} else if (node.getType().equals(NodeIdentifier.TYPE)) {

			return type(node);

		} else if (node.getType().equals(NodeIdentifier.COMMAND)) {

			return command(node);

		} else if (node.getType().equals(NodeIdentifier.INCOMMAND)) {

			return inCommand(node);

		} else if (node.getType().equals(NodeIdentifier.DECL)) {

			return decl(node);

		} else if (node.getType().equals(NodeIdentifier.ATRIB)) {

			return atrib(node);

		} else if (node.getType().equals(NodeIdentifier.READ)) {

			return read(node);

		} else if (node.getType().equals(NodeIdentifier.PRINT)) {

			return print(node);

		} else if (node.getType().equals(NodeIdentifier.IF)) {

			return if_(node);

		} else if (node.getType().equals(NodeIdentifier.WHILE)) {

			return while_(node);

		} else if (node.getType().equals(NodeIdentifier.IDLI)) {

			return idLi(node);

		} else if (node.getType().equals(NodeIdentifier.IDLI2)) {

			return idLi2(node);

		} else if (node.getType().equals(NodeIdentifier.ARITEXPR)) {

			return aritExpr(node);

		} else if (node.getType().equals(NodeIdentifier.OPERAN)) {

			return operan(node);

		} else if (node.getType().equals(NodeIdentifier.ARITOP)) {

			return aritOp(node);

		} else if (node.getType().equals(NodeIdentifier.CONDOP)) {

			return condOp(node);

		} else if (node.getType().equals(NodeIdentifier.CONDEXPR)) {

			return condExpr(node);

		} else if (node.getType().equals(NodeIdentifier.ELSE)) {

			return else_(node);

		}

		return null;

	}

	/*
	 * <Else> ::= <Command> |
	 */
	private Object else_(Node node) {

		if (node.getSons().size() == 1) {
			analyze(node.getSon(0));
		}
		return null;
	}

	/*
	 * <CondExpr> ::= '(' <CondOp> <Operan> <Operan> ')'
	 */
	private Object condExpr(Node node) {

		Token operan1 = (Token) analyze(node.getSon(2));
		Token operan2 = (Token) analyze(node.getSon(3));

		String typeOp1 = null;
		String typeOp2 = null;
		if (operan1._getClass().equals("ID")) {

			typeOp1 = SymbolsTable.getTokenType(operan1);

			if (typeOp1 == null) {

				errors.add("Semantic Error. Identifier "
						+ operan1.getImage() + " not declared. Line: "
						+ operan1.getLine());

			}

		} else {

			typeOp1 = SymbolsTable.constantTypeTable(operan1);

		}

		if (operan2._getClass().equals("ID")) {

			typeOp2 = SymbolsTable.getTokenType(operan2);

			if (typeOp2 == null) {

				errors.add("Semantic Error. Identifier "
						+ operan2.getImage() + " not declared. Line: "
						+ operan2.getLine());

			}

		} else {

			typeOp2 = SymbolsTable.constantTypeTable(operan2);

		}

		if (typeOp1 != null && typeOp2 != null && !typeOp1.equals(typeOp2)) {

			errors.add("Semantic Error. The type of the operands "
					+ operan1.getImage() + " and " + operan2.getImage()
					+ " are incompatible. Line: " + operan1.getLine());

		}

		return null;
	}

	/*
	 * <CondOp> ::= '>' |'<' | '<=' | '>=' | '==' | '!='
	 */
	private Object condOp(Node node) {

		analyze(node.getSon(0));
		return null;
	}

	/*
	 * <OpArit> ::= '+' | '-' | '*' | '/' | '&'
	 */
	private Object aritOp(Node node) {

		return node.getSon(0).getToken();
	}

	/*
	 * <Operan> ::= Identifier | LiteralString | LiteralInteger | LiteralReal
	 */
	private Object operan(Node node) {

		return node.getSon(0).getToken();

	}

	/*
	 * <AritExpr> ::= <Operan> | '(' <AritOp> <AritExpr> <AritExpr> ')'
	 */
	@SuppressWarnings("unchecked")
	private Object aritExpr(Node node) {

		List<Token> tokens = new ArrayList<Token>();

		if (node.getSons().size() == 1) {

			tokens.add((Token) analyze(node.getSon(0)));

		} else if (node.getSons().size() == 5) {

			tokens = (List<Token>) analyze(node.getSon(2));
			tokens.addAll((List<Token>) analyze(node.getSon(3)));

		}

		return tokens;
	}

	/*
	 * <IdLi2> ::= Identifier <IdLi2> |
	 */
	private Object idLi2(Node node) {

		if (node.getSons().size() == 0) {
			return new ArrayList<Token>();
		} else {
			Token id = node.getSon(0).getToken();
			@SuppressWarnings("unchecked")
			List<Token> liId2 = (List<Token>) analyze(node.getSon(1));
			liId2.add(id);
			return liId2;
		}

	}

	/*
	 * <IdLi> ::= Identifier <IdLi2>
	 */
	private Object idLi(Node node) {

		Token id = node.getSon(0).getToken();
		@SuppressWarnings("unchecked")
		List<Token> liId2 = (List<Token>) analyze(node.getSon(1));
		liId2.add(id);
		return liId2;

	}

	/*
	 * <While> ::= 'while' <CondExpr> <CommandLi>
	 */
	private Object while_(Node node) {

		analyze(node.getSon(1));
		analyze(node.getSon(2));

		return null;
	}

	/*
	 * <If> ::= 'if' <CondExpr> <Command> <Else>
	 */
	private Object if_(Node node) {

		analyze(node.getSon(1));
		analyze(node.getSon(2));
		analyze(node.getSon(3));

		return null;
	}

	/*
	 * <Print> ::= 'print' <Operan>
	 */
	private Object print(Node node) {

		Token operan = (Token) analyze(node.getSon(1));

		if (operan._getClass().equals("ID")) {
			String typeId = SymbolsTable.getTokenType(operan);
			if (typeId == null) {
				errors.add("Semantic Error. Identifier " + operan.getImage()
						+ " not declared. Line: " + operan.getLine());
			}

		}
		return null;
	}

	/*
	 * <Read> ::= 'read' Identifier
	 */
	private Object read(Node node) {

		Token id = node.getSon(1).getToken();

		String typeId = SymbolsTable.getTokenType(id);
		if (typeId == null) {
			errors.add("Semantic Error. Identifier " + id.getImage()
					+ " not declared. Line: " + id.getLine());
		}

		return null;
	}

	/*
	 * <Atrib> ::= '=' Identifier <AritExpr>
	 */
	private Object atrib(Node node) {

		Token token = node.getSon(1).getToken();
		String type = SymbolsTable.getTokenType(token);

		if (type == null) {

			errors.add("Semantic Error. Identifier " + token.getImage()
					+ " not declared. Line: " + token.getLine());

		} else {

			@SuppressWarnings("unchecked")
			List<Token> ids = (List<Token>) analyze(node.getSon(2));
			for (int i = ids.size() - 1; i >= 0; i--) {
				String typeId = null;
				Token id = ids.get(i);
				if (id._getClass().equals("ID")) {
					typeId = SymbolsTable.getTokenType(id);
				} else if (id._getClass().equals("ILC")
						|| id._getClass().equals("RLC")
						|| id._getClass().equals("LSC")) {
					typeId = SymbolsTable.constantTypeTable(id);
				}

				if (typeId == null) {
					errors.add("Semantic Error. Identifier " + id.getImage()
							+ " not declared. Line: " + id.getLine());
				} else if (type.equals(typeId)) {

					return ids;

				} else {

					errors.add("Semantic Error. The identifiers of the right side must "
							+ "must be of the same type of the identifiers on the left side. Image: "
							+ id.getImage() + " - Line: " + id.getLine());

				}
			}
			
		}
		return null;
	}

	/*
	 * <Decl> ::= <Type> <IdLi>
	 */
	private Object decl(Node node) {

		String type = (String) analyze(node.getSon(0));
		@SuppressWarnings("unchecked")
		List<Token> ids = (List<Token>) analyze(node.getSon(1));

		for (int i = ids.size() - 1; i >= 0; i--) {

			Token id = ids.get(i);
			String identifierType = SymbolsTable.getTokenType(id);
			if (identifierType != null) {
				errors.add("Semantic Error. Argument restatement."
						+ id.getImage() + " - Line: " + id.getLine());
			} else {
				SymbolsTable.setTokenType(id, type);
			}
		}
		return null;
	}

	/*
	 * <InnerCommand> ::= <Decl> | <Atrib> | <Read> | <Print> | <If> | <While> | <CommandLi>
	 */
	private Object inCommand(Node node) {

		return analyze(node.getSon(0));

	}

	/*
	 * <Coman> ::= '(' <InnerCommand> ')'
	 */
	private Object command(Node node) {

		return analyze(node.getSon(1));

	}

	/*
	 * <Type> ::= 'int' | 'real' | 'str'
	 */
	private Object type(Node node) {

		return node.getSon(0).getToken().getImage();

	}

	/*
	 * <Arg> ::= '(' <Type> Identifier ')'
	 */
	private Object arg(Node node) {

		String type = (String) analyze(node.getSon(1));
		Token id = (Token) analyze(node.getSon(2));
		String searchedType = SymbolsTable.getTokenType(id);
		if (searchedType != null) {
			errors.add("Semantic Error. Argument restatement "
					+ id.getImage() + " - Line: " + id.getLine());
		} else {

			SymbolsTable.setTokenType(id, type);

		}
		return null;
	}

	/*
	 * <CommandLi> ::= <Command> <CommandLi> |
	 */
	private Object commandLi(Node node) {

		if (node.getSons().size() > 0) {

			analyze(node.getSon(0));
			analyze(node.getSon(1));
			return null;
		} else {
			return null;
		}
	}

	/*
	 * <ArgLi> ::= <Arg> <ArgLi> |
	 */
	private Object liArg(Node node) {

		if (node.getSons().size() > 0) {

			analyze(node.getSon(0));
			analyze(node.getSon(1));

		}

		return null;

	}

	/*
	 * <Def> ::= '(' 'def' Identifier '[' <ArgLi> ']' <CommandLi> ')'
	 */
	private Object def(Node node) {

		Token id = node.getSon(2).getToken();
		String searchedId = SymbolsTable.getTokenImage(id);
		if (searchedId == null) {
			errors.add("Semantic Error. Use of reserved word as an identifier. Image: "
					+ id.getImage() + " - Line: " + id.getLine());
		}

		analyze(node.getSon(4));
		analyze(node.getSon(6));

		return null;

	}

	public boolean hasError() {
		return errors.size() > 0;
	}

	public void showErrors() {
		for (String error : errors) {
			JOptionPane.showMessageDialog(null, error);
		}
	}

}
