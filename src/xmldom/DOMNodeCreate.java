package xmldom;

import java.util.ArrayList;
import java.util.Vector;

public class DOMNodeCreate {
	private static DOMNodeCreate instance = new DOMNodeCreate();
	int[] blocks;
	int blocksNum;
	Vector<String> elementNames;	//存储元素名称
	Vector<String> attributeNames;	//存储命名空间名称和属性名称
	Vector<String> textNames;	//存储文本节点的文本、属性文本及注释文本
	ArrayList<Label> wholeLableFlow;
	
	private DOMNodeCreate() {}
	//获取唯一可用的对象
	public static DOMNodeCreate getInstance(){
	      return instance;
	}
	void assignment(int[] blocks,int blocksNum,Vector<String> elementNames,Vector<String> attributeNames,Vector<String> textNames,ArrayList<Label> wholeLableFlow) {
		instance.blocks=blocks;
		instance.blocksNum=blocksNum;
		instance.elementNames=elementNames;
		instance.attributeNames=attributeNames;
		instance.textNames=textNames;
		instance.wholeLableFlow=wholeLableFlow;
	}
	int getAttrNum(int index) {
		int featureCode=(int) blocks[index];
		 int attrNum = (int) ((featureCode >> 16)& 0x1ff );
		 return attrNum;
	}
	int getNodeNamePos(int index) {
		int featureCode=(int) blocks[index];
		int textPos = (int) ((featureCode >> 4) & 0xfff);
		return textPos;
	}
	int getNodeKind(int index) {
		int featureCode=(int) blocks[index];
		int kind = (int) (featureCode  & 0x07);
		return kind;
	}
	int getAttrValuePos(int index) {
		//是属性节点
		return blocks[index+1];
	}
	String getNodeName(int index) {
		return elementNames.get(getNodeNamePos(index));
	}
	String getNameValue(int index) {
		//是文本节点
		return textNames.get(getNodeNamePos(index));
	}
	DOMNode createNode(int index) {
		DOMNode node=new DOMNode(blocks,index);
		return node;
	}
//	DOMNode createChildNode() {}

}
