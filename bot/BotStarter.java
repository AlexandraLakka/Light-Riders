// // Copyright 2016 riddles.io (developers@riddles.io)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import algorithm.MinMax;
import field.Field;
import java.awt.Point;
import java.util.ArrayList;
import move.Move;
import move.MoveType;

/**
 * bot.BotStarter
 * 
 * Magic happens here. You should edit this file, or more specifically
 * the makeTurn() method to make your bot do more than random moves.
 * 
 * @author Jim van Eeden <jim@riddles.io>
 */

public class BotStarter {

	/**
     * Edit this method to make your bot smarter.
	 * Currently returns a random, but valid move.
     * @return a Move object
     */
    public Move doMove(BotState state) {
		//MoveType moveType;
		MoveType lastMoveType = state.getLastDirection();
                Move lastMove = new Move(lastMoveType);
                Move nextMove = null;
                Field field = state.getField();
                ArrayList<Move> possibleMoves = new ArrayList();
                MinMax minMax = new MinMax(possibleMoves);
                Point myPosition = field.getMyPosition();
                Point nextP = minMax.nextPos(myPosition, lastMove);
                Move up = new Move(MoveType.UP);
                Move down = new Move(MoveType.DOWN);
                Move left = new Move(MoveType.LEFT);
                Move right = new Move(MoveType.RIGHT);
                Move pass = new Move(MoveType.PASS);
                int roundNumber = state.getRoundNumber();

		if (roundNumber == 0) { // first move
                        //go towards the nearest wall
                        int[] distToWalls = new int[4];
                        distToWalls[0] = myPosition.y;//up
                        distToWalls[1] = field.getHeight() - myPosition.y;//down
                        distToWalls[2] = myPosition.x;//left
                        distToWalls[3] = field.getWidth() - myPosition.x;//right
                        
                        int shortestDist = distToWalls[2];
                        MoveType desiredDirection = MoveType.getRandomExcluding(MoveType.PASS);
                        for(int i = 0; i < distToWalls.length; i++){
                            if(distToWalls[i] < shortestDist){
                                shortestDist = distToWalls[i];
                                desiredDirection.equals(i);
                            }
                            switch(desiredDirection){
                                case UP:
                                    nextMove= up;
                                    break;
                                case DOWN:
                                    nextMove = down;
                                    break;
                                case LEFT:
                                    nextMove = left;
                                    break;
                                case RIGHT:
                                    nextMove = right;
                                    break;
                                default:
                                    nextMove = left;
                                    break;
                            }
                        }
			//moveType = MoveType.getRandomExcluding(MoveType.PASS);
                        state.setLastDirection(nextMove.getMoveType());
		} else {
                        possibleMoves.add(up);
                        possibleMoves.add(down);
                        possibleMoves.add(left);
                        possibleMoves.add(right);
                        possibleMoves.add(pass);
                        
                        //MoveType opposite = lastMoveType.getOpposite();
                        
                        Move alpha = new Move(MoveType.getRandomMoveType());
                        Move beta = new Move(MoveType.getRandomMoveType());
                 
                        
                        //Remove current and opposite direction
			switch(lastMoveType.getOpposite()){
			case UP:
				possibleMoves.remove(up);
				possibleMoves.remove(down);
			case DOWN:
				possibleMoves.remove(up);
				possibleMoves.remove(down);
				break;
			case LEFT:
				possibleMoves.remove(left);
				possibleMoves.remove(right);
				break;
			case RIGHT:
				possibleMoves.remove(left);
				possibleMoves.remove(right);
				break;
			case PASS:
				break;
			default:
				break;
			}

			//Check if any immediate neighbor square is blocked (wall or edge or enemy player)
			if(!field.isClear(myPosition.x, myPosition.y-1)) possibleMoves.remove(up);
			if(!field.isClear(myPosition.x, myPosition.y+1)) possibleMoves.remove(down);
			if(!field.isClear(myPosition.x-1, myPosition.y)) possibleMoves.remove(left);
			if(!field.isClear(myPosition.x+1, myPosition.y)) possibleMoves.remove(right);
                        
			//If heading towards wall remove pass
                        if(possibleMoves.contains(pass)){
                            switch(lastMoveType){
                                case UP:
                                    if(!field.isClear(myPosition.x, myPosition.y - 1)){
                                        possibleMoves.remove(pass);
                                    }
                                    break;
                                case DOWN:
                                    if(!field.isClear(myPosition.x, myPosition.y + 1)){
                                        possibleMoves.remove(pass);
                                    }
                                    break;
                                case RIGHT:
                                    if(!field.isClear(myPosition.x + 1, myPosition.y)){
                                        possibleMoves.remove(pass);
                                    }
                                    break;
                                case LEFT:
                                    if(!field.isClear(myPosition.x - 1, myPosition.y)){
                                        possibleMoves.remove(pass);
                                    }
                                    break;
                                default:
                                    
                            }
                        }
                        
                        //Dead end avoidance if heading into a dead end
			if(nextP != null){
				if(possibleMoves.size() >= 2 && field.deadEnd(nextP.x, nextP.y)){
                                    possibleMoves.remove(pass);
                                }
			}
                        nextMove = minMax.miniMax(field, lastMove, roundNumber, field.getMyId(), 6, alpha, beta);
			//moveType = MoveType.getRandomExcluding(opposite);
		}
                /*
		if (moveType != MoveType.PASS) {
                    state.setLastDirection(moveType);
                }
                */
		return nextMove;
    }
    
	/**
     * Main method for the bot. Creates a parser and runs it.
     * @param args
     */
 	public static void main(String[] args) {
 		BotParser parser = new BotParser(new BotStarter());
 		parser.run();
 	}
 }
