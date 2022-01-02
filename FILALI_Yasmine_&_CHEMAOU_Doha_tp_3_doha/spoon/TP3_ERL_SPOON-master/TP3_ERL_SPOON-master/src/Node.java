import java.util.ArrayList;
import java.util.List;

public class Node<T> {

	private T data = null;

	private List<Node<T>> children = new ArrayList<>();

	private Node<T> parent = null;

	private int nodeMetric = 0;

	public Node(T data, int nodeMetric) {
		this.data = data;
		this.nodeMetric = nodeMetric;
	}

	public Node<T> addChild(Node<T> child) {
		child.setParent(this);
		this.children.add(child);
		return child;
	}

	public void addChildren(List<Node<T>> children) {
		children.forEach(each -> each.setParent(this));
		this.children.addAll(children);
	}

	public List<Node<T>> getChildren() {
		return children;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	private void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public Node<T> getParent() {
		return parent;
	}

	public Node<T> getNode(T data) {
		Node<T> node = null;
		
		if (this.data.equals(data)) {
			node = this;

		} else { 
			
			for (Node nodeElement : this.getChildren()) {
				
				if (nodeElement.getData().equals(data)) {
					return nodeElement;
				}else{
					node = this.getNode((T) nodeElement.getData());
					if(node.getChildren().size()==0) break;
				}
			}
		}
		return node;
	}
	public Node find (T data){
	    Node result = null;

	    //check for value first
	    if (this.getData().equals(data)) return this;

	    //check if you can go any further from the current node
	    if(this.getChildren().size()==0 || (this.getChildren().get(0)==null && this.getChildren().get(1)==null)) return null;

	    //now go right
	    result = this.getChildren().get(0).find(data);

	    //check the node
	    if(result != null && result.getData().equals(data)) return result;

	    //if not found return whatever is returned by searching left
	    return this.getChildren().get(1).find(data);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	public int getNodeMetric() {
		return nodeMetric;
	}

	public void setNodeMetric(int nodeMetric) {
		this.nodeMetric = nodeMetric;
	}

}