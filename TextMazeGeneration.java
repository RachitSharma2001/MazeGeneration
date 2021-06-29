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
	
	public static Coordinate giveNeighbor(Coordinate curr_cord, int curr_wall){
		return new Coordinate(curr_cord.getX() + dx[curr_wall], curr_cord.getY() + dy[curr_wall]);
	}
	
	public static Coordinate findRandNeighbor(DisjointSet ds, Coordinate cord){
		int current_wall = generateRandomInt(4);
		Coordinate cord_head = ds.find(cord);
		Coordinate neighbor_head, neighbor;
		while(cord_head.equals(neighbor_head = ds.find(neighbor = giveNeighbor(cord, current_wall)))){
			current_wall = (current_wall + 1) % 4;
		}
		return neighbor;
	}
	
	public static boolean[][][] createMaze(int R, int C){
		Walls walls = new Walls(R, C);
		DisjointSet ds = new DisjointSet(R, C);
		
		while(ds.numSets() > 1){
			Coordinate rand_cord = walls.giveRandCord();
			Coordinate rand_neighbor = findRandNeighbor(ds, rand_cord);
			ds.union(rand_cord, rand_neighbor);
			walls.knockDown(rand_cord, rand_neighbor, dx, dy);
		}
		
		return walls.getArray();
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
		
		DisjointSet ds = new DisjointSet(R, C);
		Coordinate first = new Coordinate(1, 2);
		Coordinate second = new Coordinate(3, 1);
		Coordinate head = ds.find(first);
		System.out.println(head);
		/*boolean[][][] wall = createMaze(R, C);
		char[][] grid = buildMaze(R, C, wall);
		printMaze(grid);*/
	}
	
}

// Everything in Walls is confirmed to work
class Walls{
	boolean[][][] wall;
	int[][] num_neighbors;
	int num_rows, num_cols;
	
	Walls(int R, int C){
		num_rows = R;
		num_cols = C;
		wall = new boolean[R][C][4];
		num_neighbors = new int[R][C];
		for(int i = 0; i < R; i++){
			for(int j = 0; j < C; j++){
				num_neighbors[i][j] = 4;
				for(int k = 0; k < 4; k++) wall[i][j][k] = true;
			}
		}
	}
	
	int randomInt(int maximum){
		return (int) (Math.random()*maximum);
	}
	
	int findWallIndex(Coordinate one, Coordinate two, int[] dx, int[] dy){
		int curr_dx = two.getX() - one.getX();
		int curr_dy = two.getY() - one.getY();
		
		for(int i = 0; i < 4; i++){
			if(dx[i] == curr_dx && dy[i] == curr_dy) return i;
		}
		
		return -1;
	}
	
	void knockDown(Coordinate one, Coordinate two, int[] dx, int[] dy){
		int one_x = one.getX(), one_y = one.getY();
		int two_x = two.getX(), two_y = two.getY();
				
		int ones_wall_ind = findWallIndex(one, two, dx, dy);
		int twos_wall_ind = findWallIndex(two, one, dx, dy);
		
		wall[one_x][one_y][ones_wall_ind] = false;
		wall[two_x][two_y][twos_wall_ind] = false;
	}
	
	Coordinate giveRandCord(){
		int x, y;
		while(num_neighbors[x=randomInt(num_rows)][y=randomInt(num_cols)] == 0){}
		num_neighbors[x][y]--;
		return new Coordinate(x, y);
	}
	
	boolean[][][] getArray(){
		return wall;
	}
	
	void print(){
		for(int i = 0; i < num_rows; i++){
			for(int j = 0; j < num_cols; j++){
				System.out.println("At: (" +  i + ", " + j + "): " + wall[i][j][0] + " " + wall[i][j][1] + " " + wall[i][j][2] + " " + wall[i][j][3]);
			}
		}
	}
}

class DS_obj{
	private Coordinate parent = null;
	private int size = -1;
	
	DS_obj(Coordinate p, int s){
		p = parent;
		size = s;
	}
	
	Coordinate getParent(){
		return parent;
	}
	
	void setParent(Coordinate p){
		parent = p;
	}
	
	void setSize(int new_size){
		size = new_size;
	}
	
	int getSize(){
		return size;
	}
}

class DisjointSet{
	private DS_obj[][] info;
	private int num_sets;
	
	DisjointSet(int R, int C){
		info = new DS_obj[R][C];
		num_sets = R*C;
	}
	
	int numSets(){
		return num_sets;
	}
	
	Coordinate find(Coordinate given){
		while(info[given.getX()][given.getY()].getParent() != null){
			given = info[given.getX()][given.getY()].getParent();
		}
		return given;
	}
	
	void union(Coordinate one, Coordinate two){
		Coordinate head1 = find(one);
		Coordinate head2 = find(two);
		
		DS_obj first = info[head1.getX()][head1.getY()];
		DS_obj second = info[head2.getX()][head2.getY()];
		int new_size = first.getSize() + second.getSize();
		
		if(first.getSize() < second.getSize()){
			info[head2.getX()][head2.getY()].setParent(head1);
			info[head2.getX()][head2.getY()].setSize(new_size);
		}else{
			info[head1.getX()][head1.getY()].setParent(head2);
			info[head2.getX()][head2.getY()].setSize(new_size);
		}
		
		num_sets--;
	}
}

class Coordinate{
	private int x, y;
	
	Coordinate(int given_x, int given_y){
		x = given_x;
		y = given_y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
    public boolean equals(Coordinate c) {
		return c.getX() == x && c.getY() == y;
	}
    
    public String toString(){
    	return "(" + x + ", " + y + ")";
    }
}

