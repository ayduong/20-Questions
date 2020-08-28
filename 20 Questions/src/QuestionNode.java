//Andrew Duong
//20 Questions Project


public class QuestionNode {
	public String data;
	public QuestionNode yes;
	public QuestionNode no;
	
	
	public QuestionNode(String data, QuestionNode yes, QuestionNode no) {
		this.data = data;
		this.yes = yes;
		this.no = no;
	}
	
	public QuestionNode(String data) {
		this.data = data;
	}
	
	
	//returns whether or not this object is an object
	public boolean isAnswer() {
		return (yes == null || no == null);
	}
}
