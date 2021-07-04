import java.util.ArrayList;
import java.util.Scanner;


public class TextMazeGeneration_BetterOOP {
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		int R = in.nextInt();
		int C = in.nextInt();
		
		Maze maze = new Maze(R, C);
		maze.generateMaze();
		maze.print();
		
		in.close();
	}
}

class Maze{
	private Walls walls;
	private Grid grid;
	private DisjointSet ds;
	
	Maze(int R, int C){
		walls = new Walls(R, C);
		grid = new Grid(R, C);
		ds = new DisjointSet(R, C);
	}
	
	void print(){
		grid.print();
	}
	
	void generateMaze(){
		while(!walls.noneLeft()){
			WallObj curr_wall = walls.giveRandomWall();
			int first_id = curr_wall.getFirstId();
			int second_id = curr_wall.getSecondId();
			if(!ds.sameSet(first_id, second_id)){
				ds.union(first_id, second_id);
				grid.knockDownWall(first_id, second_id);
			}
		}
	}
}

// Eventually I want to remove the array list and have just the priority queues
class Walls{
	private ArrayList<WallObj> walls_list;
	
	Walls(int R, int C){
		int id_count = 0;
		walls_list = new ArrayList<WallObj>();
		for(int row = 0; row < R; row++){
			for(int col = 0; col < C; col++){
				if(col != 0) walls_list.add(new WallObj(id_count-1, id_count));
				if(row != 0) walls_list.add(new WallObj(id_count-C, id_count));
				id_count++;
			}
		}
	}
	
	WallObj giveRandomWall(){
		int rand_ind = Random.giveRandInt(0, walls_list.size() - 1);
		WallObj deleted_wall = walls_list.get(rand_ind);
		walls_list.remove(rand_ind);
		return deleted_wall;
	}
	
	boolean noneLeft(){
		return walls_list.size() == 0;
	}
}

class WallObj{
	int first_id, second_id;
	
	WallObj(int f_id, int s_id){
		first_id = f_id;
		second_id = s_id;
	}
	
	int getFirstId(){
		return first_id;
	}
	
	int getSecondId(){
		return second_id;
	}
}

class Grid{
	char[][] grid;
	char SPACE = ' ', WALL = '#';
	int R, C;
	Grid(int given_R, int given_C){
		R = given_R;
		C = given_C;
		
		initializeGridArray(2*R+1, 2*C+1);
	}
	
	void initializeGridArray(int R, int C){
		grid = new char[R][C];
		for(int i = 0; i < R; i++){
			for(int j = 0; j < C; j++){
				if(isEven(i*j)){
					grid[i][j] = WALL;
				}else{
					grid[i][j] = SPACE;
				}
			}
		}
		grid[0][1] = SPACE;
		grid[R-1][C-2] = SPACE;
	}
	
	void knockDownWall(int first_id, int second_id){
		int x = getRow(first_id);
		int y = getCol(first_id);
		if(isRight(first_id, second_id)){
			grid[x][y+1] = SPACE;
		}else{
			grid[x+1][y] = SPACE;
		}
	}
	
	int getRow(int id){
		return 1 + 2*(id/C);
	}
	
	int getCol(int id){
		return 1 + 2*(id%C);
	}
	
	boolean isRight(int first_id, int second_id){
		return first_id + 1 == second_id;
	}
	
	boolean isEven(int num){ 
		return num % 2 == 0;
	}
	
	void print(){
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}
}

class DisjointSet{
	int[] disjoint_set;
	
	DisjointSet(int R, int C){
		disjoint_set = new int[R*C];
		for(int i = 0; i < disjoint_set.length; i++){
			disjoint_set[i] = -1;
		}
	}
	
	void union(int first, int second){
		int head_first = find(first);
		int head_second = find(second);
		int new_size = disjoint_set[head_first] + disjoint_set[head_second];
		
		if(isSmaller(head_first, head_second)){
			disjoint_set[head_first] = head_second;
			disjoint_set[head_second] = new_size;
		}else{
			disjoint_set[head_second] = head_first;
			disjoint_set[head_first] = new_size;
		}
	}
	
	int find(int id){
		while(disjoint_set[id] >= 0){
			id = disjoint_set[id];
		}
		
		return id;
	}
	
	boolean sameSet(int first_id, int second_id){
		return find(first_id) == find(second_id);
	}
	
	boolean isSmaller(int left_head, int right_head){
		return disjoint_set[left_head] > disjoint_set[right_head];
	}
}

class Random{
	static int giveRandInt(int low, int high){
		return low + (int) (Math.random() * (high - low));
	}
}
