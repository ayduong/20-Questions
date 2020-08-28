//Andrew Duong
//20 Questions Project


import java.io.PrintStream;
import java.util.*;

/**
 * Plays a game of 20 Questions. It can load and save its internal data
 * structures to a file.
 * 
 * @author STUDENT
 *
 */
public class QuestionTree {

	// will be used for the write method
	private String s;

	// the root of the tree
	private QuestionNode root;

	/**
	 * The one and only console object for getting input from user
	 */
	private static Scanner console = new Scanner(System.in);

	/**
	 * This method will construct a question tree with one leaf node representing
	 * the object “computer”.
	 */
	public QuestionTree() {
		root = new QuestionNode("computer");
	}

	/**
	 * This method will be called if the client wants to replace the current tree by
	 * reading another tree from a file. This method will be passed a Scanner that
	 * is linked to the file and should replace the current tree with a new tree
	 * using the information in the file. Here is a sample file format: Q: Is it an
	 * animal? Q: Does it hop? A: frog A: dog A: computer
	 * 
	 * Pre-Condition: The file input is legal and in the proper format.
	 * Post-Condition: QuestionTree is filled with new nodes.
	 * 
	 * @param input Scanner to a file to read with node contents.
	 */
	public void read(Scanner input) {
		// Student: Read entire lines of input using Scanner.nextLine.()
		root = read(input, root);
	}

	/**
	 * This method is the helper method for the public read method. It will take a
	 * Scanner to a file which it will use to fill the binary tree with
	 * QuestionNodes.
	 * 
	 * @param input Scanner to a file to read with node contents
	 * @param node  QuestionNode that will be modified
	 * @return returns null if the Scanner has no more lines, if not, returns the
	 *         resulting node
	 */
	private QuestionNode read(Scanner input, QuestionNode node) {
		if (!input.hasNext()) {
			return null;
		} else {
			String str = input.nextLine();
			if (str.contains("Q:")) {
				String data = input.nextLine();

				// for some reason, the bigquestion.txt that was provided to us had a question
				// that was split into two lines. the question was "Is it related to the bottle
				// nosed dolphin?" there was a line break between the l and p in dolphin. I'm
				// not sure if this was a mistake or not, but it created a bug in my code. this
				// was my way of working around it.
				if (data.contains("bottle nosed")) {
					data += "\n" + input.nextLine();
				}

				node = new QuestionNode(data);
				node.yes = read(input, node.yes);
				node.no = read(input, node.no);
			} else {
				return new QuestionNode(input.nextLine());
			}
		}
		return node;
	}

	/**
	 * This method will be called if the client wants to store the current tree to
	 * an output file. The given PrintStream will be valid and open for writing.
	 * 
	 * @param output The file to write to.
	 */
	public void write(PrintStream output) {
		// must reset s everytime
		s = "";
		write(root);
		output.print(s);
	}

	/**
	 * This helper method uses recursion to convert the QuestionNode tree into a
	 * single String.
	 * 
	 * @param node the QuestionNode that will be converted into a String
	 * @return returns null
	 */
	private String write(QuestionNode node) {

		// exit case
		if (node == null) {
			return null;
		}

		// if the node is an answer, we want to add "A:" if not, we add "Q:"
		if (node.isAnswer()) {
			s += "A:\n";
		} else {
			s += "Q:\n";
		}

		// add the node's contents
		s += node.data + "\n";

		// for preorder travesal, we traverse the left then right (yes -> no)
		write(node.yes);
		write(node.no);
		return null;

	}

	/**
	 * In this method we will use the current tree to ask the user a series of
	 * yes/no questions until we either guess their object correctly or until we
	 * fail, in which case we expand the tree to include their object and a new
	 * question to distinguish their object from the others.
	 */
	public void askQuestions() {
		boolean isGameOver = false;
		QuestionNode current = root;
		while (!isGameOver) {
			boolean answer;

			// if the node is an answer
			if (current.isAnswer()) {
				answerQuestions(current);

				// if the node was an answer, the game will be over no matter what. we either
				// guessed right or wrong
				isGameOver = true;

				// if the node is a question
			} else {
				answer = yesTo(current.data);

				// if the answer to the question was yes
				if (answer) {
					current = current.yes;
				} else {
					current = current.no;
				}
			}

		}
	}

	/**
	 * This method will handle what happens when a guess is made (when the program
	 * reaches a leaf/answer node). It will ask the user if the guess is right. If
	 * the guess was right, it will end the game. If the guess is wrong, it will ask
	 * what the user's object was and what a distinguishing question for the object
	 * is.
	 * 
	 * @param current the QuestionNode that the program is guessing as the user's object
	 */
	private void answerQuestions(QuestionNode current) {
		boolean input = yesTo("Would your object happen to be " + current.data);

		// if the program guessed correctly
		if (input) {
			System.out.println("Great, I got it right!");
			
		//if the program guessed wrong
		} else {
			
			//ask for the name of the user's object
			System.out.print("What is the name of your object? ");
			String object = console.nextLine();
			
			//ask for a question to add to the tree
			System.out
					.print("Please give me a yes/no question that \ndistinguishes between your object \nand mine--> ");
			String question = console.nextLine();
			System.out.print("And what is the answer for your object?");
			String answer = console.nextLine();
			
			//the new answer node
			QuestionNode newObj = new QuestionNode(object);
			
			//this will be the question node
			QuestionNode qNode;

			// this checks which side of the new question the new answer should go on
			if (answer.toLowerCase().equals("y")) {
				qNode = new QuestionNode(question, newObj, current);
			} else {
				qNode = new QuestionNode(question, current, newObj);
			}

			// if the current node is the root
			if (current.equals(root)) {
				root = qNode;
			} else {
				QuestionNode parent = findParent(current, root);

				// we must check which side the new question goes on
				if (parent.yes.equals(current)) {
					parent.yes = qNode;
				} else {
					parent.no = qNode;
				}
			}
		}
	}
	
	/**
	 * This method will traverse the tree and find the parent of the given node.
	 * @param node the node that we will find the parent of
	 * @param parent this is the potential parent that will be checked in the method
	 * @return will return null if parent isn't found, will return the node's parent if not
	 */
	private QuestionNode findParent(QuestionNode node, QuestionNode parent) {

		// if we have found the parent of the node
		if (parent.yes.equals(node) || parent.no.equals(node)) {
			return parent;
		}

		// if we've reached a leaf node
		if (parent.yes == null) {
			return null;
		}
		
		//checking if the parent can be found on the left side
		QuestionNode current = findParent(node, parent.yes);
		if (current != null) {
			return parent.yes;
		}
		
		//checking the right side
		current = findParent(node, parent.no);
		if (current != null) {
			return parent.no;
		}
		
		//if the parent isn't found, return null
		return null;
	}

	/**
	 * This method asks the given question until the user types “y” or “n.” The
	 * method forces an answer of "y" or "n";
	 * 
	 * @param prompt The question to ask the user
	 * @return true if the answer was "y", returns false if "n"
	 */
	public boolean yesTo(String prompt) {
		System.out.print(prompt + " (y/n)? ");
		String response = console.nextLine().trim().toLowerCase();
		while (!response.equals("y") && !response.equals("n")) {
			System.out.println("Please answer y or n.");
			System.out.print(prompt + " (y/n)? ");
			response = console.nextLine().trim().toLowerCase();
		}
		return response.equals("y");
	}
}