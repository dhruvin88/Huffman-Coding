
public class Node implements Comparable<Node>{
	private String ch;
	private double freq;
	private String code = "";
	private Node left,right;
	
	Node(String ch, double freq){
		this.ch = ch;
		this.freq = freq;
	}
	
	public Node() {
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	public double getFreq() {
		return freq;
	}

	public void setFreq(double freq) {
		this.freq = freq;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}
	
	//override Comparable for heap
	@Override
	public int compareTo(Node n) {
		if(getFreq() < n.getFreq()) {
			return -1;
		}
		else if(getFreq() > n.getFreq()){
			return 1;
		}
		else 
			return 0;
	}
	 
	 
}
