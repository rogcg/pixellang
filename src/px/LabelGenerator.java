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

public class LabelGenerator {

	private static int n;
	private static int i;
	
	public static String getNextLabel(){
		String label = "label"+n;
		n++;
		return label;
	}
	
	public static String getNextTemp(){
		String temp = "temp"+i;
		i++;
		return temp;
	}
	
}
