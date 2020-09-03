package xmldom;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Main {
	public static void main(String[] args) throws Exception {
		InputStream input = SAXMain.class.getResourceAsStream("/text.xml");
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		MyHandler mh=new MyHandler();
		saxParser.parse(input, mh);
		DOMNodeCreate DNC=mh.getDNC();
		mh.printResult();
//		DOMNode node=new DOMNode(DNC.blocks,3);
////		System.out.println(node.getNodeName(DNC.elementNames));
////		System.out.println(node.getNodeType().toString());
////		
////		System.out.println(node.getFirstChild().getNodeName(DNC.elementNames));
////		System.out.println(node.getFirstChild().getNodeValue(DNC.textNames));
//		
//		DOMNode node2=new DOMNode(DNC.blocks,0);
//		ArrayList<DOMNode> allChilds=node.getChildNodes();
//		Iterator<DOMNode> it=allChilds.iterator();
//		while(it.hasNext()) {
//			node2=it.next();
//			System.out.println(node2.getNodeName(DNC.elementNames));
//			System.out.println(node2.getNodeType().toString());
//			System.out.println(node2.getNodeValue(DNC.textNames));
//			
//		}
		TwigList TL=new TwigList();
		String stringXPath="//a//b|//a//f";
		TL.match(DNC, stringXPath);
		TL.printResult();
	}
}
