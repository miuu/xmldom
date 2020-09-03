package xmldom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;


class NodePair{
	String ancestor;
	String desendant;
	NodePair(){}
	NodePair(String ancestor,String desendant){
		this.desendant=desendant;
		this.ancestor=ancestor;
	}
	public void setPair(String ancestor,String desendant){
		this.desendant=desendant;
		this.ancestor=ancestor;
	}
	public String getAncestor() {
		return ancestor;
	}
	public String getDesendant() {
		return desendant;
	}
	public String toString() {
		return "<"+ancestor+","+desendant+">";
	}
}
public class XPathTree {
	private ArrayList<NodePair> nodePairs;
	private Vector<String> qEleNames;

	
	public void parse(String XPathString){
		this.nodePairs=new ArrayList<NodePair>();
		this.qEleNames=new Vector<String>();
		NodePair nPair=new NodePair();
		String now="";
		String last="";
		String[] pathSentences=XPathString.split("\\|") ; 
		for(String s: pathSentences) {
			last="";
			now="";
			String[] elements=s.split("//") ; 
			for(String ele: elements) {
				if(!ele.isEmpty()) {
					if(!qEleNames.contains(ele)) {
						qEleNames.add(ele);
					}
					last=now;
					now=ele;
					if(!last.isEmpty()) {
						nPair=new NodePair(last, now);
						nodePairs.add(nPair);
					}
				}
				
			}
		}
	}
	
	public ArrayList<NodePair> getNodePairs(){
		return nodePairs;
	}
	
	public Vector<String> getQEleNames(){
		return qEleNames;
	}
	public boolean isLeaf(String a) {
		Iterator<NodePair> itNP = nodePairs.iterator();
		NodePair npTemp;
		// 遍历nodePairs
		while (itNP.hasNext()) {
			npTemp = itNP.next();
			if (npTemp.getAncestor().equals(a)) {
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args) {
		XPathTree xpt=new XPathTree();
		xpt.parse("//a//b|//a//d//f");
		System.out.println("NodePairs:"+xpt.getNodePairs().toString());
		System.out.println("qEleNames"+xpt.getQEleNames().toString());
	}
}
