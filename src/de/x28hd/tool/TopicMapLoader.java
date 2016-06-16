package de.x28hd.tool;

import java.awt.Color;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TopicMapLoader {
	Hashtable<Integer, GraphNode> newNodes = new Hashtable<Integer, GraphNode>();
	Hashtable<Integer, GraphEdge> newEdges = new Hashtable<Integer, GraphEdge>();
	String dataString = "";
	int topicnum = 0;
	int assocnum = 0;
	Element root;
	boolean readyMap = false;
	int minX = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxY = Integer.MIN_VALUE;

	public TopicMapLoader(Document doc, GraphPanelControler controler) {

		if (doc.hasChildNodes()) {
			root = doc.getDocumentElement();
			
			if (root.getTagName() == "x28map") {
				System.out.println("TL Success: new" );
				readyMap = true;
			} else {
				System.out.println("TL Failure.");
				return;
			}
			
			NodeList topics = root.getElementsByTagName("topic");
			NodeList assocs = root.getElementsByTagName("assoc");
			for (int i = 0; i < topics.getLength(); i++) {
				importTopic((Element) topics.item(i));
			}
			for (int i = 0; i < assocs.getLength(); i++) {
				importAssoc((Element) assocs.item(i));
			}
			System.out.println("TL: " + topicnum + " new topics and " + assocnum + " new assocs loaded");
			
			System.out.println("TL recorded " + newNodes.size() + " nodes and " + newEdges.size() + " edges");
			return;
		}
	}

	//	Detail methods for current map XML format
	//	Nodes & edges are called "topics" and "assocs" to distinguish from xml nodes
	
	private void importTopic(Element topic) {
		GraphNode node;
		topicnum++;

		String label = topic.getFirstChild().getTextContent();
		String detail = topic.getLastChild().getTextContent();
		int x = Integer.parseInt(topic.getAttribute("x"));
		int y = Integer.parseInt(topic.getAttribute("y"));
		if (x < minX) minX = x;
		if (x > maxX) maxX = x;
		if (y < minY) minY = y;
		if (y > maxY) maxY = y;
		String color = topic.getAttribute("color");

		node = new GraphNode(topicnum, new Point(x,y), Color.decode(color), label, detail);
		newNodes.put(node.getID(), node);
	}

	private void importAssoc(Element assoc) {
		GraphEdge edge;
		assocnum++;

		String detail = assoc.getFirstChild().getTextContent();
		int n1 = Integer.parseInt(assoc.getAttribute("n1"));
		int n2 = Integer.parseInt(assoc.getAttribute("n2"));
		String color = assoc.getAttribute("color");

		edge = new GraphEdge(assocnum, newNodes.get(n1), newNodes.get(n2), Color.decode(color), detail);
		newEdges.put(assocnum, edge);
		newNodes.get(n1).addEdge(edge);
		newNodes.get(n2).addEdge(edge);
	}
}