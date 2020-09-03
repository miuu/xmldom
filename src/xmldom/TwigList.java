package xmldom;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

class Pointer {
	Element start;
	Element end;

	public void setStart(Element start) {
		this.start = start;
	}

	public Element getStart() {
		return start;
	}

	public void setEnd(Element end) {
		this.end = end;
	}

	public Element getEnd() {
		return end;
	}
}

class Element {
	Label label;
	HashMap<String, Pointer> pointerMap;

	public Element(Label label) {
		this.label = label;
		pointerMap = new HashMap<String, Pointer>();
	}

	public Label getLabel() {
		return label;
	}

	public HashMap<String, Pointer> getPointerMap() {
		return pointerMap;
	}
}

public class TwigList {
	Map<String, ArrayList<Element>> LMap;
	Stack<Integer> S;
	int[] outTree;

	public void match(DOMNodeCreate DNC, String stringXPath) {
		// ArrayList<Label> wholeLableFlow=wholeLableFlow;
		// int[] qTree=new int[32]; //左子右兄存储查询树
		// 只考虑 一个根节点及其以下元素的查询树 即类似/book/title | /book/price即只有/ | 英文字母且按顺序输入
		// Vector<String> qElementNames=new Vector<String>();
		XPathTree xpt = new XPathTree();
		xpt.parse(stringXPath);
		ArrayList<NodePair> nodePairs = xpt.getNodePairs();
		Vector<String> qEleNames = xpt.getQEleNames();

		Stack<Element> S = new Stack<Element>();

		// 为每个查询节点q分配一个列表Lq,Lq中每个元素为Eq
		LMap = new HashMap<String, ArrayList<Element>>();
		for (Iterator<String> itQEle = qEleNames.iterator(); itQEle.hasNext();) {
			ArrayList<Element> pointerList = new ArrayList<Element>();
			LMap.put(itQEle.next(), pointerList);
		}
		int labelFlowNum = 0;

		Iterator<Label> it = DNC.wholeLableFlow.iterator();
		DOMNode q = new DOMNode(DNC.blocks, 0);
		Element eq;
		DOMNode m = new DOMNode(DNC.blocks, 0);
		Element em;
		DOMNode domNodeTemp = new DOMNode(DNC.blocks, 0);
		// 顺序扫描标签流中的元素
		while (it.hasNext()) {
			Label labelTemp = it.next();
			q.setIndex(labelTemp.index);
			eq = new Element(labelTemp);
			if (qEleNames.contains(q.getNodeName(DNC.elementNames))) {
				int pushEq = 0;
				// if(eq是栈S中栈顶元素em的后代结点||栈S为空) eq进栈
				if (S.empty()) {
					S.push(eq);
					pushEq = 1;
				} else {
					em = S.peek();
					m.setIndex(em.getLabel().getIndex());
					if (m.isAncestorOf(q.getIndex())) {
						S.push(eq);
						pushEq = 1;
					}
				}
				// 否则将栈顶元素em弹出，直到栈顶元素是eq的祖先
				if (pushEq != 1) {
					em = S.peek();
					m.setIndex(em.getLabel().getIndex());
					// while(栈顶元素不是eq祖先)
					while (!m.isAncestorOf(q.getIndex())) {
						// 弹出栈顶元素em
						em = S.pop();
						m.setIndex(em.getLabel().getIndex());
						// if(m是叶子节点)
						
 						if (xpt.isLeaf(m.getNodeName(DNC.elementNames))) {
							// em进入相应列表
							LMap.get(m.getNodeName(DNC.elementNames)).add(em);
						}
						// 或者m的孩子节点列表中都有相应元素点与em满足结构关系
						else {
							int emAllMatch = 0;
							NodePair npTemp;
							Iterator<NodePair> itNP = xpt.getNodePairs().iterator();
							// 遍历nodePairs
							while (itNP.hasNext()) {
								npTemp = itNP.next();
								// if nodePair的左边是<M>
								if (npTemp.getAncestor().equals(m.getNodeName(DNC.elementNames))) {
									ArrayList<Element> desEleList = LMap.get(npTemp.getDesendant());
									Iterator<Element> itEle = desEleList.iterator();
									Pointer emPointer = new Pointer();
									Element eleTemp = new Element(new Label());
									// 遍历npTemp.des的列表
									int emNpMatch = 0, firstMeet = 0;
									while (itEle.hasNext()) {
										eleTemp = itEle.next();
										domNodeTemp.setIndex(eleTemp.getLabel().getIndex());
										// if npTemp.des的列表中存在元素节点与em满足结构关系
										if (m.isAncestorOf(domNodeTemp.getIndex())) {
											// 设置emPointer的start
											if (firstMeet == 0) {
												emPointer.setStart(eleTemp);
												firstMeet = 1;
											}
											emAllMatch = 1;
											emNpMatch = 1;
											emPointer.setEnd(eleTemp);
										}
									}
									// 如果npTemp.des的列表中存在元素节点与em满足结构关系
									if (emNpMatch == 1) {
										if (em.pointerMap.containsKey(npTemp.getDesendant()))
											em.pointerMap.remove(npTemp.getDesendant());
										em.pointerMap.put(npTemp.getDesendant(), emPointer);
									} else {
										emAllMatch = 0;
										break;
									}

								}
								// if nodePair的右边是<M>
//								if (npTemp.getDesendant().equals(m.getNodeName(DNC.elementNames))) {
//									ArrayList<Element> ancEleList = LMap.get(npTemp.getAncestor());
//									Iterator<Element> itEle = ancEleList.iterator();
//									Pointer emPointer = new Pointer();
//									Element eleTemp = new Element(new Label());
//									// 遍历npTemp.anc的列表
//									int emNpMatch = 0;
//									while (itEle.hasNext()) {
//										eleTemp = itEle.next();
//										domNodeTemp.setIndex(eleTemp.getLabel().getIndex());
//										// if npTemp.des的列表中存在元素节点与em满足结构关系
//										if (domNodeTemp.isAncestorOf(m.getIndex())) {
//											emAllMatch = 1;
//											emNpMatch = 1;
//											emPointer.setEnd(eleTemp);
//										}
//									}
//									// 如果npTemp.des的列表中存在元素节点与em满足结构关系
//									if (emNpMatch != 1) {
//										emAllMatch = 0;
//										break;
//									}
//								}
							}
							// m的孩子节点列表中都有相应元素点与em满足结构关系
							if (emAllMatch == 1) {
								// em=new Element(labelTemp);
								LMap.get(m.getNodeName(DNC.elementNames)).add(em);
							}

						}
						if (S.empty()) {
							S.push(eq);
							pushEq = 1;
							break;
						}
						em = S.peek();
						m.setIndex(em.getLabel().getIndex());
					}
					// 直到栈顶元素是eq的祖先，然后eq进栈
					if (pushEq != 1 && m.isAncestorOf(q.getIndex())) {
						S.push(eq);
					}
				}
			}
			labelFlowNum++;
		}

		// 将栈中剩余元素加入相应列表中
		while (!S.isEmpty()) {
			em = S.pop();
			m.setIndex(em.getLabel().getIndex());
			LMap.get(m.getNodeName(DNC.elementNames)).add(em);
		}
		// 设置元素对其它列表元素的指针
		// 遍历LMAP
		Set<Map.Entry<String, ArrayList<Element>>> entrySet = LMap.entrySet();
		Iterator<Map.Entry<String, ArrayList<Element>>> itLMap = entrySet.iterator();
		while (itLMap.hasNext()) {
			// 得到每一对对应关系
			Map.Entry<String, ArrayList<Element>> entry = itLMap.next();
			// 通过每一对对应关系获取对应的key
			String key = entry.getKey();
			// 通过每一对对应关系获取对应的value
			ArrayList<Element> L = entry.getValue();
			for (int j = 0; j < L.size(); j++) {
				int ljAllMatch = 0;
				NodePair npTemp;
				Iterator<NodePair> itNP = xpt.getNodePairs().iterator();
				// 遍历nodePairs
				while (itNP.hasNext()) {
					npTemp = itNP.next();
					DOMNode domNodeLj = new DOMNode(DNC.blocks, L.get(j).getLabel().getIndex());
					// nodePair的左边是<M>
					if (npTemp.getAncestor().equals(domNodeLj.getNodeName(DNC.elementNames))) {
						ArrayList<Element> desEleList = LMap.get(npTemp.getDesendant());
						Iterator<Element> itEle = desEleList.iterator();
						Pointer ljPointer = new Pointer();
						Element eleTemp = new Element(new Label());
						// 遍历npTemp.des的列表
						int ljNpMatch = 0, firstMeet = 0;
						while (itEle.hasNext()) {
							eleTemp = itEle.next();
							domNodeTemp.setIndex(eleTemp.getLabel().getIndex());
							// if npTemp.des的列表中存在元素节点与em满足结构关系
							if (domNodeLj.isAncestorOf(domNodeTemp.getIndex())) {
								// 设置emPointer的start
								if (firstMeet == 0) {
									ljPointer.setStart(eleTemp);
									firstMeet = 1;
								}
								ljAllMatch = 1;
								ljNpMatch = 1;
								ljPointer.setEnd(eleTemp);
							}
						}
						// 如果npTemp.des的列表中存在元素节点与em满足结构关系
						if (ljNpMatch == 1) {
							if (L.get(j).pointerMap.containsKey(npTemp.getDesendant()))
								L.get(j).pointerMap.remove(npTemp.getDesendant());
							L.get(j).pointerMap.put(npTemp.getDesendant(), ljPointer);
						} else {
							ljAllMatch = 0;
							break;
						}

					}
				}
			}
		}

	}

	public void printResult() {
		Set<Map.Entry<String, ArrayList<Element>>> entrySet = LMap.entrySet();
		Iterator<Map.Entry<String, ArrayList<Element>>> it = entrySet.iterator();
		int flag = 0;
		while (it.hasNext()) {
			// 得到每一对对应关系
			Map.Entry<String, ArrayList<Element>> entry = it.next();
			// 通过每一对对应关系获取对应的key
			String key = entry.getKey();
			System.out.println(key + "的列表:");
			// 通过每一对对应关系获取对应的value
			ArrayList<Element> elements = entry.getValue();
			for (int j = 0; j < elements.size(); j++) {
				Label l = elements.get(j).getLabel();
				System.out.println("第" + j + "个元素" + key + "(" + l.index + "):");
				HashMap<String, Pointer> p = elements.get(j).getPointerMap();
				Set<HashMap.Entry<String, Pointer>> eS = p.entrySet();
				Iterator<HashMap.Entry<String, Pointer>> it2 = eS.iterator();
				while (it2.hasNext()) {
					String s;
					Pointer pt;
					Map.Entry<String, Pointer> entrySP = it2.next();
					s = entrySP.getKey();
					pt = entrySP.getValue();
					Element start = pt.getStart();
					Element end = pt.getEnd();
					System.out.println(s+" start:" + start.getLabel().getIndex() + ",end:" + end.getLabel().getIndex());
				}
			}

		}
	}

	public void printMatchGroups() {
		Set<Map.Entry<String, ArrayList<Element>>> entrySet = LMap.entrySet();
		Iterator<Map.Entry<String, ArrayList<Element>>> it = entrySet.iterator();
		int flag = 0;
		// 根元素it.next()
		ArrayList<Element> L1 = it.next().getValue();
		for (int i = 0; i < L1.size(); i++) {
			Element e = L1.get(i);
			ArrayList<Integer> ansList=new ArrayList<Integer>();
			ansList.add(e.getLabel().getNum());
			HashMap<String, Pointer> SPMap = e.getPointerMap();

			while (!SPMap.isEmpty()) {
				Set<HashMap.Entry<String, Pointer>> eS = SPMap.entrySet();
				Iterator<HashMap.Entry<String, Pointer>> it2 = eS.iterator();
				while (it2.hasNext()) {
					Map.Entry<String, Pointer> entrySP = it2.next();
					String nameSP = entrySP.getKey();
					Pointer pt = entrySP.getValue();
					Element eStart = pt.getStart();
					Element eEnd = pt.getEnd();
					ArrayList<Element> Lse =new ArrayList<Element>();
					ArrayList<Element> L=LMap.get(nameSP);
					for (int j = 0; j < L1.size(); j++) {
						if(L.get(j).getLabel().getIndex()>=eStart.getLabel().getIndex()&&eEnd.getLabel().getIndex()>=L.get(j).getLabel().getIndex()) {
							Lse.add(L.get(j));
							
						}
					}
					
				}
			}

		}

		while (it.hasNext()) {
			// 得到每一对对应关系
			Map.Entry<String, ArrayList<Element>> entry = it.next();
			// 通过每一对对应关系获取对应的key
			String key = entry.getKey();
			System.out.println(key + "的列表:");
			// 通过每一对对应关系获取对应的value
			ArrayList<Element> elements = entry.getValue();
			for (int j = 0; j < elements.size(); j++) {
				Label l = elements.get(j).getLabel();
				System.out.println("第" + j + "个元素" + key + "(" + l.index + "):");
				HashMap<String, Pointer> p = elements.get(j).getPointerMap();
				Set<HashMap.Entry<String, Pointer>> eS = p.entrySet();
				Iterator<HashMap.Entry<String, Pointer>> it2 = eS.iterator();
				while (it2.hasNext()) {
					String s;
					Pointer pt;
					// s = it2.next().getKey();
					pt = it2.next().getValue();
					Element start = pt.getStart();
					Element end = pt.getEnd();
					System.out.println("start:" + start.getLabel().getIndex() + ",end:" + end.getLabel().getIndex());
				}
			}

		}
	}

	public static void main(String[] args) {

	}
}
