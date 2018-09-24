package aihw1;

import java.util.Scanner;

public class HW1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ParsingSys ps = new ParsingSys();
		Scanner sc = new Scanner(System.in);
		String inp = "";
		while (!(inp = sc.nextLine()).equals("quit")) {
			ps.parseInput(inp);
		}
		sc.close();
	}

}
