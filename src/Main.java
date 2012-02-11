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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import px.LexicalAnalyzer;
import px.SemanticAnalyzer;
import px.CodeGenerator;
import px.NodeIdentifier;
import px.SintaticAnalyzerTreeGenerator;
import px.Symbol;
import px.SymbolsTable;
import px.SintaticAnalyzerTreeGenerator;
import px.Token;
import px.Node;

public class Main {

	public static void main(String[] args) throws IOException {

		// reads the source code
		BufferedReader br = new BufferedReader(new FileReader("Prog1.px"));

		// Lexical Analyzer instance receiving the reader (br)
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(br);
		lexicalAnalyzer.analyze();

		if (lexicalAnalyzer.hasError()) {
			lexicalAnalyzer.showErrors();
			System.exit(0);
		}
		System.out
				.println("Lexical Analysis successfully performed! \n\n->Tokens List\n");
		lexicalAnalyzer.showTokens();
		SymbolsTable.showSymbolsTable();
		System.out.println("\n\nCLS\n");
		SymbolsTable.showSymbolsTableLSC();

		SintaticAnalyzerTreeGenerator sintaticAnalyzerTreeGenerator = new SintaticAnalyzerTreeGenerator(
				lexicalAnalyzer.tokens);
		sintaticAnalyzerTreeGenerator.analyze();

		if (sintaticAnalyzerTreeGenerator.hasError()) {
			sintaticAnalyzerTreeGenerator.showErrors();
			System.exit(0);
		} else {
			System.out.println("\nSintatic Analysis performed successfully!");
		}
		System.out.println("\nGenerated Tree!\n\n");
		sintaticAnalyzerTreeGenerator.showTree(
				sintaticAnalyzerTreeGenerator.getRoot(), "   ");

		Node node = new Node();
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(
				sintaticAnalyzerTreeGenerator.getRoot());
		semanticAnalyzer.semanticAnalysis();
		if (semanticAnalyzer.hasError()) {
			semanticAnalyzer.showErrors();
		} else {
			System.out.println("\nSemantic Analysis performed succesfully!");
		}

		CodeGenerator generator = new CodeGenerator(
				sintaticAnalyzerTreeGenerator.getRoot(), "GeneratedFile.asm");
		generator.generate();

		System.out.println("Build Done!");

	}

}
