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

import px.NodeIdentifier;

public class SintaticAnalyzerTreeGenerator {

	private List<Token> tokens;
	private Integer pToken;
	private Token token;
	private List<String> errors = new ArrayList<String>();
	private Node root;

	public SintaticAnalyzerTreeGenerator(List<Token> tokens) {
		this.tokens = tokens;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public boolean analyze() {
		pToken = 0;
		readToken();
		root = Def();
		if (!token.getImage().equalsIgnoreCase("$")) {
			errors.add("Error: EOF expected. line: "
					+ token.getLine());
			return false;
		}
		return true;
	}

	public Token readToken() {

		token = tokens.get(pToken);
		pToken++;
		return token;

	}

	/**
	 * <Def> ::= '(' 'def' Identifier '[' <ArgLi> ']' <CommandLi> ')'
	 */
	public Node Def() {

		Node node = new Node(NodeIdentifier.DEF);
		if (token.getImage().equals("(")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			if (token.getImage().equals("def")) {
				node.addSon(new Node(NodeIdentifier.TOKEN, token));
				token = readToken();
				if (token._getClass().equals("ID")
						|| token.getImage().equals("main")) {
					node.addSon(new Node(NodeIdentifier.TOKEN, token));
					token = readToken();
					if (token.getImage().equals("[")) {
						node.addSon(new Node(NodeIdentifier.TOKEN, token));
						token = readToken();
						node.addSon(argLi());
						if (token.getImage().equals("]")) {
							node.addSon(new Node(NodeIdentifier.TOKEN, token));
							token = readToken();
							node.addSon(commandLi());
							if (token.getImage().equals(")")) {
								node.addSon(new Node(NodeIdentifier.TOKEN,
										token));
								token = readToken();
							} else {
								errors.add("Error: token ')' not recognized. line: "
										+ token.getLine());
							}
						} else {
							errors.add("Error: token ']' not recognized. line: "
									+ token.getLine());
						}
					} else {
						errors.add("Error: token '[' not recognized. line: "
								+ token.getLine());
					}
				} else {
					errors.add("Error: token 'ID' not recognized. line: "
							+ token.getLine());
				}
			} else {
				errors.add("Error: token 'def' not recognized. line: "
						+ token.getLine());
			}
		} else {
			errors.add("Error: token '(' not recognized. line: "
					+ token.getLine());
		}
		return node;
	}

	/**
	 * <ArgLi> ::= <Arg> <ArgLi> |
	 */
	public Node argLi() {

		Node node = new Node(NodeIdentifier.ARGLI);
		if (token.getImage().equals("(")) {
			node.addSon(arg());
			node.addSon(argLi());
		}
		return node;
	}

	/**
	 * <Arg> ::= '(' <Type> Identifier ')'
	 */
	public Node arg() {
		Node node = new Node(NodeIdentifier.ARG);
		if (token.getImage().equals("(")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(type());
			if (token._getClass().equals("ID")) {
				node.addSon(new Node(NodeIdentifier.TOKEN, token));
				token = readToken();
				if (token.getImage().equals(")")) {
					node.addSon(new Node(NodeIdentifier.TOKEN, token));
					token = readToken();
				} else {
					errors.add("Error: token ')' not recognized. line: "
							+ token.getLine());
				}
			} else {
				errors.add("Error: token 'ID' not recognized. line: "
						+ token.getLine());
			}
		} else {
			errors.add("Error: token '(' not recognized. line: "
					+ token.getLine());
		}

		return node;
	}

	/**
	 * <Type> ::= 'int' | 'real' | 'str'
	 */
	public Node type() {
		Node node = new Node(NodeIdentifier.TYPE);
		if (token.getImage().equals("int")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
		} else if (token.getImage().equals("str")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
		} else if (token.getImage().equals("real")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
		} else {
			errors.add("Error: Waiting end of file. line: "
					+ token.getLine());
		}

		return node;

	}

	/**
	 * <CommandLi> ::= <Command> <CommandLi> |
	 */
	public Node commandLi() {

		Node node = new Node(NodeIdentifier.COMMANDLI);
		if (token.getImage().equals("(")) {
			node.addSon(command());
			node.addSon(commandLi());
		}
		return node;
	}

	/**
	 * <Command> ::= '(' <InnerCommand> ')'
	 */
	public Node command() {

		Node node = new Node(NodeIdentifier.COMMAND);
		if (token.getImage().equals("(")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();			
			node.addSon(innerCommand());
			if ((token.getImage().equals(")"))) {
				node.addSon(new Node(NodeIdentifier.TOKEN, token));
				token = readToken();				
			} else {
				errors.add("Error: token ')' not recognized. line: "
						+ token.getLine());
			}
		} else {
			errors.add("Error: token '(' not recognized. line: "
					+ token.getLine());
		}		
		return node;
	}

	/**
	 * <InnerCommand> ::= <Decl> | <Atrib> | <Read> | <Print> | <If> | <While> | <CommandLi>
	 */
	public Node innerCommand() {

		Node node = new Node(NodeIdentifier.INCOMMAND);

		if ((token.getImage().equals("int") || token.getImage().equals("real") || token
				.getImage().equals("str"))) {
			node.addSon(decl());
		} else if (token.getImage().equals("=")) {
			node.addSon(atrib());
		} else if (token.getImage().equals("read")) {
			node.addSon(read());
		} else if (token.getImage().equals("print")) {
			node.addSon(print());
		} else if (token.getImage().equals("if")) {
			node.addSon(if_());
		} else if (token.getImage().equals("while")) {
			node.addSon(while_());
		} else if (token.getImage().equals("(")) {
			node.addSon(commandLi());
		} else {
			errors.add("Error: waiting anything. line: " + token.getLine());
		}

		return node;
	}

	/**
	 * <Decl> ::= <Type> <IdLi>
	 */
	public Node decl() {
		Node node = new Node(NodeIdentifier.DECL);
		node.addSon(type());
		node.addSon(idLi());
		return node;
	}

	/**
	 * <IdLi> ::= Identifier <IdLi2>
	 */
	public Node idLi() {

		Node node = new Node(NodeIdentifier.IDLI);

		if (token._getClass().equals("ID")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(idLi2());

		} else {
			errors.add("Error: token 'ID' not recognized. line: "
					+ token.getLine());
		}

		return node;
	}

	/**
	 * <LiId2> ::= Identifier <LiId2> |
	 */
	public Node idLi2() {

		Node node = new Node(NodeIdentifier.IDLI2);

		if (token._getClass().equals("ID")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(idLi2());

		}
		return node;
	}

	/**
	 * <Atrib> ::= '=' Identifier <AritExpr>
	 */
	public Node atrib() {

		Node node = new Node(NodeIdentifier.ATRIB);

		if (token.getImage().equals("=")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			if (token._getClass().equals("ID")) {
				node.addSon(new Node(NodeIdentifier.TOKEN, token));
				token = readToken();
				node.addSon(aritExpr());
			} else {
				errors.add("Error: token '=' not recognized. line: "
						+ token.getLine());
			}
		} else {
			errors.add("Error: token 'ID' not recognized. line: "
					+ token.getLine());
		}
		return node;
	}

	/**
	 * <AritExpr> ::= <Operan> | '(' <AritOp> <AritExpr> <AritExpr> ')'
	 */
	public Node aritExpr() {
			
		Node node = new Node(NodeIdentifier.ARITEXPR);

		if (token._getClass().equals("ID") || token._getClass().equals("LSC")
				|| token._getClass().equals("ILC")
				|| token._getClass().equals("RLC")) {
			node.addSon(operan());
		} else if (token.getImage().equals("(")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(opArit());
			node.addSon(aritExpr());
			node.addSon(aritExpr());
			if (token.getImage().equals(")")) {
				node.addSon(new Node(NodeIdentifier.TOKEN, token));
				token = readToken();
			} else {
				errors.add("Error: token ')' not recognized. line: "
						+ token.getLine());
			}
		} else {
			errors.add("Error: token '(' not recognized. line: " + token.getLine());
		}

		return node;

	}

	/**
	 * <Operan> ::= Identifier | LiteralString | LiteralInteger | LiteralReal
	 */
	public Node operan() {

		Node node = new Node(NodeIdentifier.OPERAN);

		if (token._getClass().equals("ID") || token._getClass().equals("LSC")
				|| token._getClass().equals("ILC")
				|| token._getClass().equals("RLC")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
		} else {
			errors.add("Error: token 'ID' ou 'LSC' ou 'ILC' ou 'RLC' not recognizeds. line: "
					+ token.getLine());
		}

		return node;

	}

	/**
	 * <AritOp> ::= '+' | '-' | '*' | '/' | '&'
	 */
	public Node opArit() {

		Node node = new Node(NodeIdentifier.ARITOP);

		if (token.getImage().equals("+") || token.getImage().equals("-")
				|| token.getImage().equals("-") || token.getImage().equals("*")
				|| token.getImage().equals("/")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();

		} else {
			errors.add("Error: token '+' or '-' or '*' or '/' not recognized. line: "
					+ token.getLine());
		}

		return node;
	}

	/**
	 * <Read> ::= 'read' Identifier
	 */
	public Node read() {

		Node node = new Node(NodeIdentifier.READ);

		if (token.getImage().equals("read")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			if (token._getClass().equals("ID")) {
				node.addSon(new Node(NodeIdentifier.TOKEN, token));
				token = readToken();
			} else {
				errors.add("Error: token 'ID' not recognized. line: "
						+ token.getLine());
			}
		} else {
			errors.add("Error: token 'ler'  not recognized. line: "
					+ token.getLine());
		}

		return node;
	}

	/**
	 * <Print> ::= 'print' <Operan>
	 */
	public Node print() {

		Node node = new Node(NodeIdentifier.PRINT);

		if (token.getImage().equals("print")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(operan());
		} else {
			errors.add("Error: token 'print'  not recognized. line: "
					+ token.getLine());
		}

		return node;

	}

	/**
	 * <If> ::= 'if' <CondExpr> <Command> <Else>
	 */
	public Node if_() {

		Node node = new Node(NodeIdentifier.IF);

		if (token.getImage().equals("if")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(condExpr());
			node.addSon(command());
			node.addSon(else_());
		} else {
			errors.add("Error: token 'if'  not recognized. linha: "
					+ token.getLine());
		}

		return node;
	}

	/**
	 * <Else> ::= <Command> |
	 */
	public Node else_() {

		Node node = new Node(NodeIdentifier.ELSE);
		if (token.getImage().equals("(")) {
			node.addSon(command());
		}

		return node;
	}

	/**
	 * <ExprCond> ::= '(' <CondOp> <Operan> <Operan> ')'
	 */
	public Node condExpr() {

		Node node = new Node(NodeIdentifier.CONDEXPR);

		if (token.getImage().equals("(")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(condOp());
			node.addSon(operan());
			node.addSon(operan());
			if (token.getImage().equals(")")) {
				node.addSon(new Node(NodeIdentifier.TOKEN, token));
				token = readToken();
			} else {
				errors.add("Error: token ')'  not recognized. line: "
						+ token.getLine());
			}
		} else {
			errors.add("Error: token '('  not recognized. line: "
					+ token.getLine());
		}

		return node;
	}

/**
	 * <CondOp> ::= '>' |'<' | '<=' | '>=' | '==' | '!='
	 */
	public Node condOp() {

		Node node = new Node(NodeIdentifier.CONDOP);

		if (token.getImage().equals(">=") || token.getImage().equals("<=")
				|| token.getImage().equals("==")
				|| token.getImage().equals("=")
				|| token.getImage().equals("!=")
				|| token.getImage().equals("<") || token.getImage().equals(">")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
		} else {
			errors.add("Error: token '>' or '<' or '<=' or '>=' or '==' or '!=' not recognized. line: "
					+ token.getLine());
		}

		return node;
	}

	/**
	 * <While> ::= 'while' <CondExpr> <CommandLi>
	 */
	public Node while_() {

		Node node = new Node(NodeIdentifier.WHILE);

		if (token.getImage().equals("while")) {
			node.addSon(new Node(NodeIdentifier.TOKEN, token));
			token = readToken();
			node.addSon(condExpr());
			node.addSon(commandLi());
		} else {
			errors.add("Error: token 'while' not recognized. line: "
					+ token.getLine());
		}
		return node;
	}

	public boolean hasError() {
		return errors.size() > 0;
	}

	public void showErrors() {
		for (String error : errors) {
			System.out.println(error);
		}
	}

	public void showTree(Node node, String space) {

		if (node.getToken() != null) {
			System.out.println(space + node.getToken()._toString());

		} else {

			System.out.println(space + node.getType());

		}

		if (node != null) {

			for (Node nodeSon : node.getSons()) {
				showTree(nodeSon, space + "|   ");

			}

		}

	}
}
