package xmldom;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class DOMNode {
	private int[] blocks;	//节点索引树
	private int index;
	
	public DOMNode(int[] blocks,int index) {
		this.blocks=blocks;
		this.index=index;
	}
	
	public void setIndex(int index) {
		this.index= index;
	}
	
	public int getIndex() {
		return index;
	}
	//获取节点的名字
	public String getNodeName(Vector<String> elementNames) {
		int featureCode=(int) blocks[index];
		int textPos = (int) ((featureCode >> 4) & 0xfff);
		return  elementNames.get(textPos).toString();
	}
	//获取节点的值
		public String getNodeValue(Vector<String> textNames) {
			int featureCode=(int) blocks[index];
			int attrNum = (int) ((featureCode >> 16)& 0x1ff );
			featureCode=(int) blocks[index+3+attrNum*2];
			
			int textPos = (int) ((featureCode >> 4) & 0xfff);
			return  textNames.get(textPos).toString();
		}

	//获取节点的类型
	public NodeType getNodeType() {
		int featureCode=(int) blocks[index];
		int kind = (int) (featureCode  & 0x07);
		NodeType nodeType=NodeType.ELEMENT;
		switch(kind)
	      {
	         case 1 :
	            nodeType=NodeType.DOCUMENT;
	            break;
	         case 2 :
	        	nodeType=NodeType.ELEMENT;
		        break;
	         case 3 :
	        	nodeType=NodeType.ATTRIBUTE;
		        break;
	         case 4 :
	        	nodeType=NodeType.TEXT;
			    break;
	         default :
	        	 
	            	      
	   }
		return nodeType;
	}
	
	
	//获取父节点
	public DOMNode getParentNode() {
		DOMNode domNode=new DOMNode(blocks,index+1);
		return domNode;
	}
	
	//获取第一个孩子
	public DOMNode getFirstChild() {
		//获取nsNum
		//不考虑，为0
		//获取attrNum
		int featureCode=(int) blocks[index];
		 int attrNum = (int) ((featureCode >> 16)& 0x1ff );
		DOMNode firstChild=new DOMNode(blocks,index+3+attrNum*2);
		return firstChild;
	}
	
	
	//获取所有的孩子
	public ArrayList<DOMNode> getChildNodes() {
		//DOMNode[] ListDOMNode;
		ArrayList<DOMNode> ListDOMNode=new ArrayList<DOMNode>();
		DOMNode firstChild=getFirstChild();
		ListDOMNode.add(firstChild);
		int p=firstChild.index;
		while(blocks[p+2]!=-1) {
			DOMNode newChild=new DOMNode(blocks,blocks[p+2]);
			ListDOMNode.add(newChild);
			p=blocks[p+2];
		}
		return ListDOMNode;
	}
	
	
	//获取后一个兄弟
	public DOMNode getNextSibling() {
		DOMNode domNode=new DOMNode(blocks,index+2);
		return domNode;
	}
	public boolean hasChild() {
		int featureCode=(int) blocks[index];
		int child=(int) ((featureCode >> 3) & 0x1);
		if(child==1) return true;
		else return false;
	}
	
	public boolean hasEleChild() {
		if(hasChild()) {
			DOMNode firstChild=getFirstChild();
			if(firstChild.getNodeType()==NodeType.ELEMENT)
				return true;
			else return false;
		}
		else return false;
	}
	
	public boolean isAncestorOf(int index2) {
		//DOMNode node=new DOMNode(blocks,index2);
		DOMNode nodeTemp=new DOMNode(blocks,index);
		int flag=0;
		if(nodeTemp.index==index2) {
			return true;
		}
		while(nodeTemp.hasChild()) {
			ArrayList<DOMNode> ListDOMNode=nodeTemp.getChildNodes();
			Iterator<DOMNode> it=ListDOMNode.iterator();
			while(it.hasNext()) {
				nodeTemp=it.next();
				if(nodeTemp.isAncestorOf(index2)) {
					flag=1;
					break;
				}
			}
			if(flag==1)return true;
		}
		return false;
	}
	public static void main(String[] args) {
		int[] blocks= {10,-1,-1,26,0,9,34,3,-1,58,0,15,66,9,-1,82,0,-1};
		DOMNode n=new DOMNode(blocks,0);
		if(n.isAncestorOf(9))System.out.println(1);
		else System.out.println(0);
	}

}



class AttributeNode extends DOMNode {

	public AttributeNode(int[] blocks, int index) {
		super(blocks, index);
		// TODO Auto-generated constructor stub
	}
	public NodeType getNodeType() {
		return NodeType.ATTRIBUTE;
	}
}

class ElementNode extends DOMNode{

	public ElementNode(int[] blocks, int index) {
		super(blocks, index);
		// TODO Auto-generated constructor stub
		
	}
	public NodeType getNodeType() {
		return NodeType.ELEMENT;
	}
	
}

class TextNode extends DOMNode{

	public TextNode(int[] blocks, int index) {
		super(blocks, index);
		// TODO Auto-generated constructor stub
	}
	public NodeType getNodeType() {
		return NodeType.TEXT;
	}
//	public String getNodeName() {
//		
//	}

}


