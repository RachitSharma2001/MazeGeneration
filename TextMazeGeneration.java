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
	
	// Works
	public static Coordinate giveNeighbor(Coordinate curr_cord, int curr_wall){
		return new Coordinate(curr_cord.getX() + dx[curr_wall], curr_cord.getY() + dy[curr_wall]);
	}
	
	public static Coordinate findRandNeighbor(DisjointSet ds, Coordinate cord, int R, int C){
		Coordinate cord_head = ds.find(cord);
		Coordinate neighbor_head, neighbor;
		
		int num_neighbors = 0;
		for(int i = 0; i < 4; i++){
			Coordinate n = giveNeighbor(cord, i);
			if(n.inBounds(R, C) && !ds.find(n).isSame(cord_head)){
				num_neighbors++;
			}
		}
		if(num_neighbors == 0) return null;
		do{
			int current_wall = generateRandomInt(4);
			neighbor_head = ds.find(neighbor = giveNeighbor(cord, current_wall)); 
		}while(neighbor_head == null || cord_head.isSame(neighbor_head));
		
		return neighbor;
	}
	
	public static boolean[][][] createMaze(int R, int C){
		DisjointSet ds = new DisjointSet(R, C);
		Walls walls = new Walls(R, C, dx, dy);
		
		int num_sets = R*C;
		while(ds.numSets() > 1){
			Coordinate rand_cord = walls.giveRandCord();
			Coordinate rand_neighbor = findRandNeighbor(ds, rand_cord, R, C);
			if(rand_neighbor == null) continue;
			ds.union(rand_cord, rand_neighbor);
			walls.knockDown(rand_cord, rand_neighbor, dx, dy);
			num_sets--;
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
		
		// Fill Everything with walls
		for(int i = 0; i < newR; i++)
			for(int j = 0; j < newC; j++)
				grid[i][j] = WALL;

		// Fill in spaces 
		for(int maze_x = 0; maze_x < R; maze_x++){
			for(int maze_y = 0; maze_y < C; maze_y++){
				int array_x = 1 + maze_x*2;
				int array_y = 1 + maze_y*2;
				grid[array_x][array_y] = SPACE;
				if(!wall[maze_x][maze_y][right]){
					grid[array_x][array_y+1] = SPACE;
				}
				if(!wall[maze_x][maze_y][down]){
					grid[array_x+1][array_y] = SPACE;
				}
			}
		}
		
		grid[0][1] = SPACE;
		grid[newR-1][newC-2] = SPACE;
		
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

// Everything in Walls is confirmed to work
class Walls{
	boolean[][][] wall;
	int[][] num_neighbors;
	int num_rows, num_cols;
	
	boolean inBounds(int x, int y, int dx, int dy, int R, int C){
		int new_x = x + dx, new_y = y + dy;
		return !(new_x < 0 || new_x >= R || new_y < 0 || new_y >= C);
	}
	
	Walls(int R, int C, int[] dx, int[] dy){
		num_rows = R;
		num_cols = C;
		wall = new boolean[R][C][4];
		num_neighbors = new int[R][C];
		for(int i = 0; i < R; i++){
			for(int j = 0; j < C; j++){
				for(int k = 0; k < 4; k++){
					/*if(!inBounds(i, j, dx[k], dy[k], R, C)){ 
						wall[i][j][k] = false;
						continue;
					}*/
					wall[i][j][k] = true;
					num_neighbors[i][j]++;
				}
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
		
		num_neighbors[one_x][one_y] -= 1;
		num_neighbors[two_x][two_y] -= 1;
	}
	
	Coordinate giveRandCord(){
		int x, y;
		while(num_neighbors[x=randomInt(num_rows)][y=randomInt(num_cols)] == 0){}
		//num_neighbors[x][y]--;
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
	
	int findNumNeighbors(int x, int y){
		return num_neighbors[x][y];
	}
	
	int findNumNeighbors(Coordinate c){
		return num_neighbors[c.getX()][c.getY()];
	}
}

class DS_obj{
	private Coordinate parent;
	private int size;
	
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
		for(int i = 0; i < R; i++){
			for(int j = 0; j < C; j++){
				info[i][j] = new DS_obj(null, -1);
			}
		}
		num_sets = R*C;
	}
	
	int numSets(){
		return num_sets;
	}
	
	Coordinate find(Coordinate given){
		if(!given.inBounds(info.length, info[0].length)){
			//System.out.println("Returning null because " + given + " is out of bounds");
			return null;
		}
		
		while(info[given.getX()][given.getY()].getParent() != null){
			//System.out.println("Given: " + given);
			//System.out.println("Size: " + info[given.getX()][given.getY()].getSize());
			given = info[given.getX()][given.getY()].getParent();
		}
		return given;
	}
	
	void union(Coordinate one, Coordinate two){
		//System.out.println("Unioning " + one + " with " + two);
		Coordinate head1 = find(one);
		Coordinate head2 = find(two);
		
		if(head1.isSame(head2)){
			System.out.println("Error: Trying to union two coordinates that are in the same set");
			return;
		}
		
		DS_obj first = info[head1.getX()][head1.getY()];
		DS_obj second = info[head2.getX()][head2.getY()];
		int new_size = first.getSize() + second.getSize();
		
		if(first.getSize() < second.getSize()){
			info[head2.getX()][head2.getY()].setParent(head1);
			info[head1.getX()][head1.getY()].setSize(new_size);
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
	
    public boolean isSame(Coordinate c) {
		return c != null && c.getX() == x && c.getY() == y;
	}
    
    public boolean inBounds(int limit1, int limit2){
    	return !(x < 0 || x >= limit1 || y < 0 || y >= limit2);
    }
    
    public String toString(){
    	return "(" + x + ", " + y + ")";
    }
}

