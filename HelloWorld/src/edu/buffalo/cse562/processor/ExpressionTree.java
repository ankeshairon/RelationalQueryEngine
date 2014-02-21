/*
 * @Author: Dev Bharadwaj
 * Expression tree is needed for evaluating the WHERE CLAUSE
 * We will calculate the maxDepth of the tree and collapse the tree till there are
 * only Logical operator like AND and OR are left
 * 
 * 									Root (AND/OR some logical operator)
 * 									 |
 * 							 --------------------
 * 							 <                  =
 * 						     |                  |
 * 				    ----------------     -----------------
 * 				  Column           -   Column            String
 * 								   |
 * 						   -------------------
 * 						   |			     |
 * 						  Long	           Long
 */

package edu.buffalo.cse562.processor;


import net.sf.jsqlparser.expression.Expression;

public class ExpressionTree {
	
	Node root;
	Node currentNode;
	static int count = 0;
	
	/*
	 * Inner class to represent the Node of the Expression Tree
	 */
	class Node {
		Expression expression;
		Node leftChild;
		Node rightChild;
		
		Node () {
			expression = null;
			leftChild = null;
			rightChild = null;
		}
		Node(Expression expr) {
			expression = expr;
			leftChild = null;
			rightChild = null;
		}
	}
	
	public ExpressionTree(Expression expr) {
		root = new Node(expr);
		currentNode = root;
	}
	public ExpressionTree() {
		root = new Node();
		currentNode = root;
	}
	/*
	 * Insert call
	 */
	public void insert(Expression expr) {
		currentNode.expression = expr;
		count++;
	}
	
	public void moveLeft() {
		currentNode.leftChild = new Node();
		currentNode = currentNode.leftChild;
	}
	
	public void moveRight() {
		currentNode.rightChild = new Node();
		currentNode = currentNode.rightChild;
	}
	
	/* Traversal Begin */
	
	private void traverse(Node node) {
		System.out.println(node.expression);
		if (node.leftChild != null)
			traverse(node.leftChild);
		if (node.rightChild != null)
			traverse(node.rightChild);
			
	}
	
	public void traverse() {
		traverse(root);
	}
	/* Traversal End */
	
}
