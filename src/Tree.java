
public class Tree implements Comparable<Tree>{
	Node root;
	
	public Tree(Tree t1, Tree t2) {
		root = new Node();
		root.setCh(t1.root.getCh()+t2.root.getCh());
		root.setFreq(t1.root.getFreq()+t2.root.getFreq());
		root.setLeft(t1.root);
		root.setRight(t2.root);
	}
	
	public Tree(String ch, double freq) {
		root = new Node(ch, freq);
	}

	//override Comparable for heap
	@Override
	public int compareTo(Tree T) {
		if(root.getFreq() > T.root.getFreq()) {
			return 1;
		}
		else if(root.getFreq() < T.root.getFreq()){
			return -1;
		}
		else 
			return 0;
	}
}
