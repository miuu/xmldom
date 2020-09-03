package xmldom;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class SAXMain {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		//要测试的程序或方法
		InputStream input = SAXMain.class.getResourceAsStream("/9complex.xml");
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		MyHandler mh=new MyHandler();
		saxParser.parse(input, mh);
		mh.printResult();
		long end = System.currentTimeMillis();
		System.out.println("程序运行时间："+(end-start)+"ms");

		
	}
}


class MyHandler extends DefaultHandler {

	DOM dom;
	DOMNodeCreate DNC;
	Stack<Integer> positionStack;
	Stack<Boolean> judgeStack;
	int namePos;
	int start;
	int end;
	
	void print(Object... objs) {
		for (Object obj : objs) {
			System.out.print(obj);
			System.out.print(" ");
		}
		System.out.println();
	}
	public DOMNodeCreate getDNC() {
		return DNC;
	}
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
        	str=str.trim();
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
	}


	@Override
	public void startDocument() throws SAXException {
		print("start document");
		dom=new DOM();
		positionStack=new Stack<Integer>();
		judgeStack=new Stack<Boolean>();
		DNC=DOMNodeCreate.getInstance();
		namePos=0;
		start=0;
		end=0;
	}

	@Override
	public void endDocument() throws SAXException {
		print("end document");
		DNC.assignment(dom.blocks,dom.blocksNum,dom.elementNames,dom.attributeNames,dom.textNames,dom.wholeLableFlow);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//print("start element:", localName, qName);
		//检查存储空间
		int length=dom.blocks.length;
		if(dom.blocks[length-4]!=0) {
			dom.extendBlocks();
		}
		//命名空间名字
		
		//属性数量
		int attrsNum= attributes.getLength();
		
		//pos为目前要存入的位置的上一个位置
		
	    //元素存入索引树
		//把元素名字存入elementNames
		int pos;
		List<Label> aLabelFlow;
		if(dom.elementNames.contains(qName)) {
			pos=dom.elementNames.indexOf(qName);
			aLabelFlow=dom.labelFlows.get(qName);
		}
		else {
			dom.elementNames.add(qName);
			aLabelFlow=new ArrayList<Label>();
			dom.labelFlows.put(qName, aLabelFlow);
			
			pos=dom.elementNames.indexOf(qName);
		}
		
		
		//DOMNode domNode=new DOMNode(dom.blocks,dom.blocksNum);
	    //将编码后的节点特征码存入
		int featureCode=dom.encoding(0, attrsNum, pos, 0, 2);
		//System.out.println(Integer.toBinaryString(featureCode));
		dom.blocks[namePos]=featureCode;
		dom.blocksNum++;
		
		//标签流相关操作
		start=end+1;
		end=start;
		Label label=new Label(aLabelFlow.size(),start,end,namePos);
	    dom.labelFlows.get(qName).add(label);
	    dom.wholeLableFlow.add(new Label(dom.wholeLableFlow.size(),start,end,namePos));
	    int a=dom.labelFlows.size();
	    //位置栈入栈
		positionStack.push(namePos);
		//判断栈入栈
		judgeStack.push(false);
		dom.blocks[namePos+1]=-1;
		dom.blocks[namePos+2]=-1;
	    namePos+=3;  //在索引树中保留出父子节点位置的空位，之后回填
	    
	    
	    //属性存入索引树
	    for(int i=0;i<attrsNum;i++) {
	    	//存入属性节点特征码
	    	String attrName=attributes.getQName(i);
	    	
	    	//System.out.println(attrName+","+attrText+","+attrText2);
	    	int attrPos;
			if(dom.attributeNames.contains(attrName)) {
				attrPos=dom.attributeNames.indexOf(attrName);
			}
			else {
				dom.attributeNames.add(attrName);
				attrPos=dom.attributeNames.indexOf(attrName);
			}
			featureCode=dom.encoding(0, 0, attrPos, 0,3);
			dom.blocks[namePos++]=featureCode;
			//存入属性值位置
			String attrText=attributes.getValue(i);
	    	dom.textNames.add(attrText);
	    	dom.blocks[namePos++]=dom.textNames.size()-1;
	    }
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		//print("end element:", localName, qName);
		Stack<Integer> tempStack=new Stack();
		int i=0;
		end=start+1;
		start=end;
		while(judgeStack.peek()==true) {
			//设置第一个出栈元素没有右兄弟
			if(i==0) {
				int pos=positionStack.peek();
				dom.blocks[pos+2]=-1;
				i++;
			}
			tempStack.push(positionStack.peek());
			positionStack.pop();
			judgeStack.pop();
			if(judgeStack.peek()==false) break;
		    //设置当前元素的右兄弟位置为前一个出栈元素位置
			dom.blocks[positionStack.peek()+2]=tempStack.peek();
		}
		
		
		Set<Map.Entry<String,List<Label>>> entrySet = dom.labelFlows.entrySet();
		Iterator<Map.Entry<String,List<Label>>> it =entrySet.iterator();
		int flag=0;
		while(it.hasNext()){
            //得到每一对对应关系
            Map.Entry<String,List<Label>> entry = it.next();
            //通过每一对对应关系获取对应的key
            String key = entry.getKey();
            //通过每一对对应关系获取对应的value
            List<Label> labels = entry.getValue();
            for(int j=labels.size()-1;j>=0;j--) {
            	if(labels.get(j).index==positionStack.peek()) {
            		labels.get(j).end=end;
            		flag=1;
            		break;
            	}
            }
            if(flag==1)break;
            
        }
		Iterator<Label> itWholeLabel=dom.wholeLableFlow.iterator();
		while(itWholeLabel.hasNext()) {
			Label l=itWholeLabel.next();
			if(l.index==positionStack.peek()) {
				l.end=end;
				break;
			}
		}
		//设置父节点已经被访问过
		judgeStack.pop();
		judgeStack.push(true);
		if(tempStack.isEmpty()!=true) {
			int temp=dom.blocks[positionStack.peek()] | 0x8;
			dom.blocks[positionStack.peek()]=temp;
			
			}
		while(tempStack.isEmpty()!=true) {
			//子节点元素的父节点位置设置为当前栈顶元素
			dom.blocks[tempStack.peek()+1]=positionStack.peek();
			tempStack.pop();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		//检测存储空间是否足够
		String charText=new String(ch, start, length);
		if(dom.blocks[dom.blocks.length-4]!=0) {
			dom.extendBlocks();
		}
		//去除文本前后的空格以及串内\n\t
		charText=replaceBlank(charText);
		
		if(charText.length()!=0) {
			//print("characters:"+charText);
			dom.textNames.add(charText);
			int featureCode=dom.encoding(0, 0, dom.textNames.size()-1, 0,4);
			dom.blocks[namePos]=featureCode;
			dom.blocks[namePos+1]=positionStack.peek();
			dom.blocks[namePos+2]=-1;
			positionStack.push(namePos);
			judgeStack.push(true);
			namePos+=3;
		}
	}


	@Override
	public void error(SAXParseException e) throws SAXException {
		print("error:", e);
	}
	
	public void printResult() {
		for(int i=0;i<dom.blocks.length-1;i++) {
			
			System.out.println(dom.blocks[i]);
		}
		 
		 System.out.println(DNC.elementNames.toString());
		 System.out.println(DNC.attributeNames.toString());
		 System.out.println(DNC.textNames.toString());
		 //System.out.println(DNC.labelFlows.toString());
		 System.out.println(DNC.wholeLableFlow.toString());
	}
}

