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

public class Node {
	
	private Node parent;
	private List<Node> sons = new ArrayList<Node>();
	private Token token;
	private NodeIdentifier type;
	
	public Node(){}
	
	public Node(NodeIdentifier type){
		
		this.type = type;
		
	}
	
	public Node(NodeIdentifier type, Token token){

		this.type = type;
		this.token = token;
		
		
	}
	
	public void addSon(Node no){
		
		sons.add(no);
		if(no != null){
			
			no.setParent(this);
			
		}		
	}
	
	public Node getSon(int i){
		
		return sons.get(i);
		
	}
	
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public List<Node> getSons() {
		return sons;
	}
	public void setSons(List<Node> sons) {
		this.sons = sons;
	}
	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}
	public NodeIdentifier getType() {
		return type;
	}
	public void setType(NodeIdentifier type) {
		this.type = type;
	}

}
