/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import bot.BotState;
import field.Field;
import java.awt.Point;
import java.util.ArrayList;
import move.Move;
import move.MoveType;

/**
 *
 * @author User
 */
public class MinMax {
    BotState bot = new BotState();
    Point myPos = new Point();
    Point enemyPos = new Point();
    
    Move up = new Move(MoveType.UP);
    Move down = new Move(MoveType.DOWN);
    Move left = new Move(MoveType.LEFT);
    Move right = new Move(MoveType.RIGHT);
    Move pass = new Move(MoveType.PASS);
    
    ArrayList<Move> possibleMoves = new ArrayList();
    
    public MinMax(ArrayList<Move> possibleMoves){
        this.possibleMoves = possibleMoves;
    }
    
    public Move miniMax(Field f, Move lastMove, int currRound, int player, int depth, Move alpha, Move beta){
        currRound = bot.getRoundNumber();
        Move bestMove = new Move(MoveType.getRandomMoveType());
        
        if(depth == 0 || currRound == depth){
            return bestMove;
        }
        
        //if(currRound != 0){
           if(player == f.getMyId()){
               possibleMoves.add(up);
               possibleMoves.add(down);
               possibleMoves.add(left);
               possibleMoves.add(right);
               possibleMoves.add(pass);
               for(Move nextMove : possibleMoves){
                   nextMove = voronoi(f, myPos, enemyPos, possibleMoves);
                   Move lookAheadMove = miniMax(f, nextMove, currRound + 1, f.getEnemyId(), depth - 1, alpha, beta);
                   
                   if(!alpha.equals(lookAheadMove)){
                       alpha = lookAheadMove;
                   }
                   if(!beta.equals(alpha)){
                       return alpha;
                   }
               }
               return alpha; 
           } else{
               possibleMoves.add(up);
               possibleMoves.add(down);
               possibleMoves.add(left);
               possibleMoves.add(right);
               possibleMoves.add(pass);
               for(Move nextMove : possibleMoves){
                   nextMove = voronoi(f, myPos, enemyPos, possibleMoves);
                   Move lookAheadMove = miniMax(f, nextMove, currRound + 1, f.getMyId(), depth - 1, alpha, beta);
                
                   if(!beta.equals(lookAheadMove)){
                      beta = lookAheadMove;
                   }
                   if(!beta.equals(alpha)){
                      return beta;
                   }
               }
        }
        return beta;
           
        //}
        
    }
   
    
    private Move voronoi(Field f, Point myPos, Point enemyPos, ArrayList<Move> possibleMoves){
        int[] values = new int[possibleMoves.size()];
        for(int i = 0; i < possibleMoves.size(); i++){
            Move move = possibleMoves.get(i);
            
            String[][] map1 = f.getField();
            Point next = nextPos(myPos, move);
            map1[myPos.x][myPos.y] = f.leaveWallBehind(myPos);
            map1[next.x][next.y] = f.getMyId() + "";
            String[][] map2 = map1;
            
            int mySpaces = 0;
            int enemySpaces = 0;
            for(int y = 0; y < f.getHeight(); y++){
		for(int x = 0; x < f.getWidth(); x++){
                    Point temp = new Point(x, y);
                    map(myPos, temp, map1, 0); //Map for my dist/point
		    map(enemyPos, temp, map2, 0); //Map for enemy dist/point
                    if(f.isClear(x, y)){
                        int myDist = dist(temp,map1);
			int enemyDist = dist(temp, map2);
			if(myDist < enemyDist){
                            mySpaces++;
                        } 
			else if(enemyDist < myDist){
                            enemySpaces++;
                        }
                    }
                }
            }
            values[i] = mySpaces - enemySpaces;
        }
        
        int maxIndex = 0;
        for (int i = 1; i < values.length; i++) {
            if ((values[i] > values[maxIndex])) {
                maxIndex = i;
            }
        }
        return possibleMoves.get(maxIndex);
    }
    
    public Point nextPos(Point myPos, Move direction){
		Point res = null;
                MoveType lastMoveType = bot.getLastDirection();
                Move lastMove = new Move(lastMoveType);
		try {
			switch(direction.getMoveType()){
			case UP:
				res = new Point(myPos.x, myPos.y-1);
				break;
			case DOWN:
				res = new Point(myPos.x, myPos.y+1);
				break;
			case LEFT:
				res = new Point(myPos.x-1, myPos.y);
				break;
			case RIGHT:
				res = new Point(myPos.x+1, myPos.y);
				break;
			case PASS:
				res = nextPos(myPos, lastMove);
				break;
			default:
				res = myPos;
				break;
			}
		} catch (Exception e) {
			res = null;
		}
		return res;
	}
    
    private void map(Point start, Point target, String[][] map, int dist){
		if(start.equals(target)) {
			map[start.x][start.y] = dist+""; 
			return;
		}
		if(map[start.x][start.y].equals(".")){
			map[start.x][start.y] = dist + "";
                        map(new Point(start.x-1, start.y), target, map, dist++);
			map(new Point(start.x+1, start.y), target, map, dist++);
			map(new Point(start.x, start.y-1), target, map, dist++);
			map(new Point(start.x, start.y+1), target, map, dist++);
		}
		
    }
    
    private int dist(Point target, String[][] map){
		
		String res = map[target.x][target.y];
                int m = 0;
		//if(res.equals)
                try{
                   m = Integer.parseInt(res);
                }catch(NumberFormatException ex){
                }
		return m;
    }
    
}
