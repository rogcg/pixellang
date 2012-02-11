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

/**
 * ASM Operators 
 *	op      |   asm           |   asm_comp
 *
 *	<	|	jl	  |		jge
 *	>	|	jg	  |		jle
 *	<=	|	jle	  |		jg
 *	>=	|	jge	  |		jl
 *	==	|	je	  |		jne
 *	!=	|	jne	  |		je
 *
 */

package px;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

	private Node root;
	private PrintWriter out;

	public CodeGenerator(Node root, String filename) throws IOException {

		this.root = root;
		out = new PrintWriter(new File(filename));

	}

	public Object generate() {

		generateHeader();
		generateDataSection();
		generateCodeSection(root);
		generateFooter();
		out.flush();
		out.close();

		return null;

	}

	private void generateHeader() {

		out.write(".486\n");
		out.write(".model flat, stdcall\n");
		out.write("option casemap nodene\n");
		out.write("include \\masm32\\include\\kernel32.inc\n");
		out.write("include \\masm32\\include\\masm32.inc\n");
		out.write("include \\masm32\\lib\\kernel32.lib\n");
		out.write("include lib\\masm32\\include\\masm32.lib\n");

	}

	private void generateDataSection() {

		out.write(".data\n");

		for (Symbol symb : SymbolsTable.symbols) {

			String image = symb.getToken().getImage();
			Token token = symb.getToken();

			if (token._getClass().equals("ID")) {

				Symbol symb2 = SymbolsTable.searchSymbol(token);

				if (symb2.getType().equals("int")) {

					out.write("\t " + image + " DD 0\n");

				} else if (symb2.getType().equals("real")) {

					out.write("\t " + image + " DD 0.0r\n");

				} else if (symb2.getType().equals("str")) {

					out.write("\t " + image + " DB 256 dup(0)\n");

				}

			}		

		}
		
		for (Symbol symb3 : SymbolsTable.literalStrings) {

			String img = symb3.getToken().getImage();
			String temp = symb3.getNick();

			out.write("\t " + temp + " DB " + img + "\n");
		}

		out.write(".code\n");
		out.write("start:\n");
	}

	private Object generateCodeSection(Node node) {

		if (node.getType().equals(NodeIdentifier.DEF)) {

			return def(node);

		} else if (node.getType().equals(NodeIdentifier.ARGLI)) {

			return argLi(node);

		} else if (node.getType().equals(NodeIdentifier.COMMANDLI)) {

			return commandLi(node);

		} else if (node.getType().equals(NodeIdentifier.ARG)) {

			return arg(node);

		} else if (node.getType().equals(NodeIdentifier.TYPE)) {

			return type(node);

		} else if (node.getType().equals(NodeIdentifier.COMMAND)) {

			return command(node);

		} else if (node.getType().equals(NodeIdentifier.INCOMMAND)) {

			return innerCommand(node);

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

	private void generateFooter() {

		out.write("\t push 0\n");
		out.write("\t call exit process\n");
		out.write("end start\n");
	}

	/*
	 * <Read> ::= 'read' Identifier
	 */

	private Object read(Node node) {

		Token id = node.getSon(1).getToken();

		out.write("\t push 256\n");
		out.write("\t push offset " + id.getImage() + "\n");
		out.write("\t call StdIn\n");

		return null;
	}

	/*
	 * <Print> ::= 'print' <Operan>
	 */
	private Object print(Node node) {

		Token operan = (Token) generateCodeSection(node.getSon(1));

		out.write("\t push offset " + operan.getImage() + "\n");
		out.write("\t call StdOut\n");

		return null;
	}

	/*
	 * <Atrib> ::= '=' Identifier <AritExpr>
	 */

	private Object atrib(Node node) {

		Token id = node.getSon(1).getToken();

		@SuppressWarnings("unchecked")
		List<Token> expr = (List<Token>) generateCodeSection(node.getSon(2));
		out.write("\t mov eax," + expr.get(0).getImage() + "\n");

		if (expr.size() > 1) {

			for (int i = 1; i < expr.size(); i++) {

				String operator = expr.get(i).getImage();
				i++;
				String operand = expr.get(i).getImage();
				
				if (operator.equals("+")) {

					out.write("\t add eax," + operand + "\n");

				} else if (operator.equals("-")) {

					out.write("\t sub eax," + operand + "\n");

				}

			}

		}
		out.write("\t mov " + id.getImage() + ", eax\n");
		return null;
	}

	/*
	 * <CondOp> ::= '>' |'<' | '<=' | '>=' | '==' | '!='
	 */

	private Object condOp(Node node) {

		return node.getSon(0).getToken();

	}

	/*
	 * <OpArit> ::= '+' | '-' | '*' | '/' | '&'
	 */

	private Object aritOp(Node node) {

		return node.getSon(0).getToken();

	}

	/*
	 * <Operan> ::= Identifier | StringLiteral | IntegerLiteral | RealLiteral
	 */

	private Object operan(Node node) {

		return node.getSon(0).getToken();

	}

	/*
	 * <AritExpr> ::= <Operan> | '(' <OpArit> <AritExpr> <AritExpr> ')'
	 */

	@SuppressWarnings({ "unchecked" })
	private Object aritExpr(Node node) {

		if (node.getSons().size() == 1) {

			List<Token> operandList = new ArrayList<Token>();
			operandList.add((Token) generateCodeSection(node.getSon(0)));
			return operandList;

		} else {

			List<Token> operand1 = (List<Token>) generateCodeSection(node.getSon(2));
			List<Token> operand2 = (List<Token>) generateCodeSection(node.getSon(3));
			Token operator = (Token) generateCodeSection(node.getSon(1));

			operand1.add(operator);
			operand1.addAll(operand2);
			return operand1;
		}
	}

	/*
	 * <IdLi2> ::= Identifier <IdLi2> |
	 */

	private Object idLi2(Node node) {

		if (node.getSons().size() > 0) {

			Token id = (Token) generateCodeSection(node.getSon(0));

			out.write("\t mov eax," + id.getImage() + "\n");

			generateCodeSection(node.getSon(1));

		}

		return null;
	}

	/*
	 * <IdLi> ::= Identifier <IdLi2>
	 */

	private Object idLi(Node node) {

		Token id = node.getSon(0).getToken();

		out.write("\t mov eax," + id.getImage() + "\n");
		generateCodeSection(node.getSon(1));

		return null;
	}

	/*
	 * <Decl> ::= <Type> <IdLi>
	 */

	private Object decl(Node node) {

		Token type = (Token) generateCodeSection(node.getSon(0));

		out.write("\t mov eax," + type.getImage() + "\n");
		generateCodeSection(node.getSon(1));

		return null;
	}

	/*
	 * <InnerCommand> ::= <Decl> | <Atrib> | <Read> | <Print> | <If> | <While> | <CommandLi>
	 */

	private Object innerCommand(Node node) {

		generateCodeSection(node.getSon(0));

		return null;
	}

	/**
	 * <Command> ::= '(' <InnerCommand> ')'
	 */

	private Object command(Node node) {

		generateCodeSection(node.getSon(1));

		return null;

	}

	/**
	 * <CommandLi> ::= <Command> <CommandLi> |
	 */
	private Object commandLi(Node node) {

		if (node.getSons().size() > 0) {

			generateCodeSection(node.getSon(0));
			generateCodeSection(node.getSon(1));

		}

		return null;
	}

	/**
	 * <ArgLi> ::= <Arg> <ArgLi> | 
	 */
	private Object argLi(Node node) {

		if (node.getSons().size() > 0) {

			Token arg = (Token) generateCodeSection(node.getSon(0));
			Token liArg = (Token) generateCodeSection(node.getSon(1));

			out.write("\t mov eax," + arg.getImage() + "\n");
			out.write("\t mov eax," + liArg.getImage() + "\n");

		}

		return null;
	}

	/**
	 * <Type> ::= 'int' | 'real' | 'str'
	 */
	private Object type(Node node) {

		return node.getSon(0).getToken().getImage();

	}

	/**
	 * <Arg> ::= '(' <Type> Identifier ')'
	 */
	private Object arg(Node node) {

		Token type = (Token) generateCodeSection(node.getSon(1));
		return node.getSon(2).getToken();

	}

	/**
	 * <CondExpr> ::= '(' <OpCond> <Operan> <Operan> ')'
	 */
	private Object condExpr(Node node) {

		Token opCond = (Token) generateCodeSection(node.getSon(1));
		Token operand1 = (Token) generateCodeSection(node.getSon(2));
		Token operand2 = (Token) generateCodeSection(node.getSon(3));

		String rotulo = LabelGenerator.getNextLabel();

		out.write("\t mov eax," + operand1.getImage() + "\n");
		out.write("\t mov ebx," + operand2.getImage() + "\n");
		out.write("\t cmp eax,ebx\n");
		out.write("\t " + condCommand(opCond) + " " + rotulo + "\n");

		return rotulo;

	}

	/*
	 * <While> ::= 'while' <CondExpr> <CommandLi>
	 */

	private Object while_(Node node) {

		String label = LabelGenerator.getNextLabel();

		out.write(label + ":\n");

		String labelExit = (String) generateCodeSection(node.getSon(1));
		generateCodeSection(node.getSon(2));
		out.write("\t jmp " + label + "\n");
		out.write(labelExit + ":\n");

		return null;

	}

	private String condCommand(Token op) {

		if (op.getImage().equals("<")) {

			return "jge";

		} else if (op.getImage().equals("<=")) {

			return "jg";

		} else if (op.getImage().equals(">")) {

			return "jle";

		} else if (op.getImage().equals(">=")) {

			return "jl";

		} else if (op.getImage().equals("==")) {

			return "jne";

		} else if (op.getImage().equals("!=")) {

			return "je";

		}

		return "";
	}

	/*
	 * 
<If> ::= 'if' <ExprCond> <Command> <Else>
	 */

	private Object if_(Node node) {

		String labelFalse = (String) generateCodeSection(node.getSon(1));
		generateCodeSection(node.getSon(2));
		String rotuloSai = LabelGenerator.getNextLabel();

		out.write("\t jmp " + rotuloSai + "\n");
		out.write(labelFalse + ":\n");

		generateCodeSection(node.getSon(3));

		out.write(rotuloSai + ":\n");

		return null;

	}

	/*
	 * <Else> ::= <Command> |
	 */

	private Object else_(Node node) {

		if (node.getSons().size() > 0) {

			generateCodeSection(node.getSon(0));

		}

		return null;

	}

	/*
	 * <Def> ::= '(' 'def' Identifier '[' <ArgLi> ']' <CommandLi> ')'
	 */

	private Object def(Node node) {

		generateCodeSection(node.getSon(4));
		generateCodeSection(node.getSon(6));

		return null;
	}

}
