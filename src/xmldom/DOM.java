package xmldom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
class Label{
	int num;
	int start;
	int end;
	int index;
	
	Label(){}
	Label(int num,int start,int end,int index){
		this.num=num;
		this.start=start;
		this.end=end;
		this.index=index;
	}
	
	public void setStart(int start) {
		this.start=start;
	}
	public int getStart() {
		return start;
	}
	public void setEnd(int end) {
		this.end=end;
	}
	public int getEnd() {
		return end;
	}
	public void setNum(int num) {
		this.num=num;
	}
	public int getNum() {
		return num;
	}
	public void setIndex(int index) {
		this.index=index;
	}
	public int getIndex() {
		return index;
	}
	public String toString() {
		return num+"<"+start+","+end+">"+index;
	}
}
public class DOM {
	int[] blocks;
	int blocksNum;
	Vector<String> elementNames;	//存储元素名称
	Vector<String> attributeNames;	//存储命名空间名称和属性名称
	Vector<String> textNames;	//存储文本节点的文本、属性文本及注释文本
	Map<String,List<Label>> labelFlows;
	ArrayList<Label> wholeLableFlow;
	
	public DOM() {
		blocks=new int[32];
		blocksNum=0;
		elementNames=new Vector<String>();
		attributeNames=new Vector<String>();
		textNames=new Vector<String>();
		labelFlows=new HashMap<String,List<Label>>();
		wholeLableFlow=new ArrayList<Label>();
	}
	
	
	//将XML信息转化为节点特征码
	//命名空间数目，属性数目，名字存储位置，有无孩子，类型
	public int encoding(int namespaceNum,int attrsNum,int namePos,int child, int kind) {
		int featureCode=namespaceNum*0x2000000+attrsNum*0x10000+namePos*0x10+child*8+kind;
		return featureCode;
	}
	
	//blocks初始化大小为32，当存储空间不够时这个函数负责进行扩展
	public void extendBlocks() {
		int[] newblocks= new int[blocks.length+32];
		for(int i=0;i<blocks.length;i++) {
			newblocks[i]=blocks[i];
		}
		blocks=newblocks;
	}
	
	//在节点树指定位置设置指定的值
	public Boolean setBlocks(int pos, int value) {
		blocks[pos]=value;
		return true;
	}

		public int getBlocks(int pos) {
			return blocks[pos];
		}
		

}
