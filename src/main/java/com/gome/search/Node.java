package com.gome.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @desc ac节点
* @date 2012-08-04
* 
*/
public class Node {
private Map<String, Node> sons = new HashMap<String, Node>();
// 指向失败链
private Node fail;
private Node parent;
// 输出标志
private boolean out;
// 字符
private String val;

public void addSon(String val, Node son) {
sons.put(val, son);
}

public Map<String, Node> getSons() {
return sons;
}

public Node getFail() {
return fail;
}

public void setFail(Node fail) {
this.fail = fail;
}

public Node getParent() {
return parent;
}

public void setParent(Node parent) {
this.parent = parent;
}

public boolean isOut() {
return out;
}

public void setOut(boolean out) {
this.out = out;
}

public String getVal() {
return val;
}

public void setVal(String val) {
this.val = val;
}

public String toString() {
	List<String> result = new ArrayList<String>();
	result.add(this.getVal());
	Node parent = this.getParent();
	while (parent != null) {
		result.add(parent.getVal());
		parent = parent.getParent();
	}
	StringBuffer res = new StringBuffer();
	for (int i = result.size() - 2; i >=0; i--) {
		res.append(result.get(i));
	}
	return res.toString();
}

}
