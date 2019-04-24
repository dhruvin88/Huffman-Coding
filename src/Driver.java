import java.io.*;
import java.util.*;

public class Driver {
	
	public static void main(String[] args) {
		
		//store the char and the frequency
		ArrayList<String> input  = readFrequencyFile("huffman.dat");
		
		//builds the tree
		Tree huffman = bulidHuffmanTree(input);
		
		//append eof and eoln into the tree
		Tree EOF = new Tree("EOF",0);
		Tree EOLN = new Tree("EOLN",0);
		
		huffman = new Tree(huffman, EOF);
		huffman = new Tree(huffman, EOLN);
		
		//traverse the tree and sets the code for each node
		assignCode(huffman.root);
		System.out.println("");
		
		boolean repeat = true;
		while(repeat) {
			Scanner scan = new Scanner(System.in);
			System.out.println("Enter 1 to compress, 2 to decompress, or 3 to quit");
			int action = Integer.parseInt(scan.nextLine());
			
			switch(action) {
				case 1:
					compressFile(huffman);
					break;
				case 2:
					decompressFile(huffman);
					break;
				case 3:
					repeat = false;
					break;
				default:
					System.out.println("Incorrect input");
			}
			scan.close();
		}
	}

	private static void decompressFile(Tree huffman) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the filename to decompress: ");
		String fileName = scan.nextLine();
		
		FileReader fr;
		try {
			fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			
			String line ="";
			String bits = "";
			String decode = "";
			
			//read all the contain in file
			while((line = br.readLine())!= null) {
				char[] linebytes = line.toCharArray();
				for(int i = 0; i < linebytes.length;i++) {
					//for each char convert to 8 digit binary code
					String tmp = Integer.toString(linebytes[i], 2);
					while(tmp.length() != 8) {
						tmp = "0"+ tmp;
					}
					bits += tmp;
				}
			}
			
			//get the code of the node by checking if the current node is a leaf if not go left
			//or right depending on bit
			Node x = huffman.root;
			while(bits.length() != 0) {
				if(x.getLeft() == null && x.getRight() == null) {
					//check if code is EOLN
					if(x.getCh() =="EOLN") {
						decode += "\n";
					}
					//check if end of file.  If so break the for loop
					else if(x.getCh()=="EOF") {
						break;
					}else {
						decode += x.getCh();
					}
					
					//reset for next symbol
					x = huffman.root;
				}
				
				String next = bits.substring(0, 1);
				bits = bits.substring(1);
				
				if(Integer.parseInt(next) == 0) {
					x = x.getLeft();
				}
				else if(Integer.parseInt(next) == 1) {
					x = x.getRight();
				}
				
			}
			System.out.println("Decoded text: \n"+decode);
			br.close();
		}catch(FileNotFoundException e) {
			System.out.println(e);
			System.out.println("File does not exist");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void compressFile(Tree huffman) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the filename to compress: ");
		String fileName = scan.nextLine();
		
		FileReader fr;
		try {
			fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			
			//append .huff to compressed files
			PrintWriter writer = new PrintWriter(fileName+".huff");
			
			String line = "";
			String bits  = "";
			String encode = "";
			
			//read each line
			while( (line = br.readLine())!= null) {
				line = line.toUpperCase();
				char[] linebytes = line.toCharArray();
				
				//for each char get the binary code from the tree
				for(int i = 0; i < linebytes.length; i++) {
					Node tmp = traverseForCode(linebytes[i]+"",huffman.root);
					//if not in the tree ignore the character
					if(tmp != null) {
						bits += tmp.getCode();
					}
				}
				//add new line to each line
				bits += traverseForCode("EOLN",huffman.root).getCode();
				
			}
			
			//add eof symbol to the page
			bits += traverseForCode("EOF",huffman.root).getCode();
			
			//convert each 8 bits into an ascii symbol
			while(bits.length() > 8) {
				int x = Integer.parseInt(bits.substring(0, 8),2);
				encode += (char)(x);
				bits = bits.substring(8, bits.length());	
			}
			
			//append zero to be modulo by 8
			while(bits.length() % 8 != 0) {
				bits += "0";
			}
			
			int x = Integer.parseInt(bits,2);
			encode += (char)(x);
			
			System.out.println("Encode text: \n"+encode+"\n");
			writer.print(encode);
			
			//scan.close();
			br.close();
			writer.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not read file");
			e.printStackTrace();
		}	
		
	}

	private static Node traverseForCode(String c, Node node) {
		//gets the node's code based on the character
	    if(node != null){
	        if(node.getCh().equals(c)){
	           return node;
	        } else {
	            Node foundNode = traverseForCode(c, node.getLeft());
	            if(foundNode == null) {
	                foundNode = traverseForCode(c, node.getRight());
	            }
	            return foundNode;
	         }
	    } else {
	        return null;
	    }
	}

	private static void assignCode(Node root) {
		//recursive adds the code for each node based on its position on the tree
		if(root == null) {}
		if(root.getLeft() != null) {
			root.getLeft().setCode(root.getCode() + "0");
			assignCode(root.getLeft());
			
			root.getRight().setCode(root.getCode() + "1");
			assignCode(root.getRight());
		}
		else {
			//prints the leafs of the tree
			System.out.println(root.getCh()+ "\t\t" + root.getCode());
		}
	}
	

	private static Tree bulidHuffmanTree(ArrayList<String> input) {
		PriorityQueue<Tree> minHeap = new PriorityQueue<Tree>();
		
		for(int i = 0; i < input.size(); i++) {
			String next = input.get(i);
			String character = String.valueOf(next.charAt(0));
			double frequency = Double.parseDouble((next.substring(2)));
			
			//creates a new tree with one node
			minHeap.offer(new Tree(character, frequency));
		}
		
		//Adds all the trees to one tree using a min heap
		while(minHeap.size() > 1) {
			Tree t1 = minHeap.poll();
			Tree t2 = minHeap.poll();
			minHeap.add(new Tree(t1,t2));
		}
		//returns the root of the tree
		return minHeap.remove();
				
	}
	

	private static ArrayList<String> readFrequencyFile(String string) {
		ArrayList<String> input = new ArrayList<String>();
		
		Scanner scan = null;
		try {
			scan = new Scanner(new File("huffman.dat"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(scan.hasNext()){
			input.add(scan.nextLine());
		}
		return input;
	}

}
