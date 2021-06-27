import java.util.ArrayList;
import java.util.Scanner;


public class TextMazeGeneration {
	static int[] dx = {1, 0, -1, 0};
	static int[] dy = {0, 1, 0, -1};
	static int down = 0, up = 2, right = 1, left = 3;
	static char WALL = '#', SPACE = ' ';
	
	public static boolean isEven(int n){
		return n%2 == 0;
	}
	
	public static int generateRandomInt(int maximum){
		return (int) (Math.random()*maximum);
	}
	
	public static ArrayList<Point> generateInitialPoints(int R, int C){
		ArrayList<Point> list = new ArrayList<Point>();
		
		for(int i = 0; i < R; i++){
			for(int j = 0; j < C; j++){
				int random_perm = generateRandomInt(16);
				list.add(new Point(0, 0, random_perm, 0));
			}
		}
		
		return list;
	}
	
	public static int getAcrossWallInd(int wall_ind){
		return (wall_ind + 2) % 4;
	}
	
	public static int[] createIntArray(int[] arr){
		int[] arr2 = new int[4];
		arr2[0] = arr[0];
		arr2[1] = arr[1];
		arr2[2] = arr[2];
		arr2[3] = arr[3];
		return arr2;
	}
	
	public static int[][] generatePermutations(){
		int[][] p = new int[16][4];
		
		// just for now
		for(int i = 0; i < 16; i++){
			for(int j = 0; j < 4; j++){
				p[i][j] = j;
			}
		}
		
		return p;
	}
	
	public static Coordinate getNeighbor(Coordinate cord, int wall_ind){
		return new Coordinate(cord.getX() + dx[wall_ind], cord.getY() + dy[wall_ind]);
	}
	
	public static Coordinate find(Ds[][] disjoint_set, Coordinate cord){
		int x = cord.getX(), y = cord.getY();
		Coordinate parent = disjoint_set[x][y].parent;
		
		if(parent == null){
			return new Coordinate(x, y);
		}
		
		return find(disjoint_set, parent);
	}
	
	public static boolean isLarger(Coordinate head1, Coordinate head2, Ds[][] disjoint_set){
		return disjoint_set[head1.getX()][head1.getY()].size < disjoint_set[head2.getX()][head2.getY()].size;
	}
	
	public static int findNewSize(Coordinate head1, Coordinate head2, Ds[][] disjoint_set){
		return disjoint_set[head1.getX()][head1.getY()].size + disjoint_set[head2.getX()][head2.getY()].size;
	}
	
	public static Ds[][] union(Coordinate head1, Coordinate head2, Ds[][] disjoint_set){
		int head1_x = head1.getX(), head1_y = head1.getY(), head2_x = head2.getX(), head2_y = head2.getY();
		int new_size = findNewSize(head1, head2, disjoint_set);
		
		if(isLarger(head1, head2, disjoint_set)){
			disjoint_set[head1_x][head1_y].size = new_size;
			disjoint_set[head2_x][head2_y].parent = head1;
		}else{
			disjoint_set[head2_x][head2_y].size = new_size;
			disjoint_set[head1_x][head1_y].parent = head2;
		}
		
		return disjoint_set;
	}
	
	public static boolean[][][] createMaze(int R, int C){
		boolean[][][] wall = new boolean[R][C][4];
		
		int[][] permutation = generatePermutations();
		
		ArrayList<Point> initial_list = generateInitialPoints(R, C);
		Ds[][] disjoint_set = new Ds[R][C];
		int num_sets = R*C;
		
		while(num_sets > 1 && initial_list.size() > 0){
			// Get current point
			int point_ind = generateRandomInt(initial_list.size());
			Point curr_point = initial_list.get(point_ind);
			initial_list.remove(point_ind);
			

			Coordinate cord = curr_point.getCord();
			int ind = curr_point.getInd();
			int perm_ind = curr_point.getPermInd();
			
			Coordinate head1, head2, neighbor;
			do{
				int wall_ind = permutation[ind][perm_ind];
				neighbor = getNeighbor(cord, wall_ind);
				head1 = find(disjoint_set, cord);
				head2 = find(disjoint_set, neighbor);
				perm_ind += 1;
			}while(head1.equals(head2) && perm_ind < 4);
			
			if(perm_ind < 4){
				disjoint_set = union(head1, head2, disjoint_set);
				
				int wall_ind = permutation[ind][perm_ind];
				int other_wall_ind = getAcrossWallInd(wall_ind);
				
				wall[cord.getX()][cord.getY()][wall_ind] = true;
				wall[neighbor.getX()][neighbor.getY()][other_wall_ind] = true;
				
				if(ind != 3){
					curr_point.setInd(ind+1);
					initial_list.add(curr_point);
				}
				
				num_sets--;
			}
		}
		
		return wall;
	}
	
	public static boolean inRange(int start_x, int start_y, char[][] grid){
		return !((start_x < 0) || (start_x >= grid.length) || (start_y < 0) || (start_y >= grid[0].length));
	}
	
	public static char[][] makeAllChar(int start_x, int start_y, int d_ind, char given_character, char[][] grid){
		while(inRange(start_x, start_y, grid)){
			grid[start_x][start_y] = given_character;
			start_x += dx[d_ind];
			start_y += dy[d_ind];
		}
		
		return grid;
	}
	
	public static char[][] buildBorders(char[][] grid){
		int R = grid.length;
		int C = grid[0].length;
		
		// Make first column all '|'
		grid = makeAllChar(0, 0, down, WALL, grid);
		
		// Make last column all '|'
		grid = makeAllChar(0, C-1, down, WALL, grid);
		
		// Make first row all '-'
		grid = makeAllChar(0, 0, right, WALL, grid);
		
		// Make last row all '-'
		grid = makeAllChar(R-1, 0, right, WALL, grid);
		
		return grid;
	}
	
	public static char[][] buildMaze(int R, int C, boolean[][][] wall){
		char[][] grid = new char[2*R + 1][2*C + 1];
		int newR = 2*R+1;
		int newC = 2*C+1;
		
		grid = buildBorders(grid);
		for(int i = 1; i < newR-1; i++){
			int d_ind = (isEven(i) ? down : right);
			for(int j = 1; j < newC-1; j++){
				// Do later: grid[i][j] = decideChar()
				if(isEven(i) || isEven(j)){
					int x = i/2;
					int y = (j-1)/2;
					
					if(wall[x][y][d_ind]) { grid[i][j] = WALL; }
					else { grid[i][j] = SPACE; }
				}else{
					grid[i][j] = SPACE;
				}
			}
		}
		
		grid[0][1] = SPACE;
		
		return grid;
 	}
	
	public static void printMaze(char[][] grid){
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[i].length; j++){
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		int R = in.nextInt();
		int C = in.nextInt();
		
		boolean[][][] wall = createMaze(R, C);
		char[][] grid = buildMaze(R, C, wall);
		printMaze(grid);
	}
	
}

class Ds{
	static Coordinate parent;
	static int size;
	
	Ds(){
		parent = null;
		size = -1;
	}
}

class Coordinate{
	private static int x, y;
	
	Coordinate(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public static int getX(){
		return x;
	}
	
	public static int getY(){
		return y;
	}
}

class Point{
	private static Coordinate cord;
	private static int ind, perm_index;
	
	Point(int x, int y, int ind, int permIndex){
		this.cord = new Coordinate(x, y);
		this.ind = ind;
		this.perm_index = permIndex;
	}
	
	void setInd(int new_ind){ ind = new_ind; }
	
	int getPermInd(){ return perm_index; }
	
	int getInd(){ return ind; }
	
	Coordinate getCord(){ return cord; }
	
	int getX(){ return cord.getX(); }
	
	int getY(){ return cord.getY(); }
};

