package firstiteration;

import battlecode.common.*;

public class PathingUtils {
    /**
     * Gets the closest location from a list of MapLocations
     * @param currentLocation
     * @param otherLocations
     * @return
     */

    private static Direction bugDirection = null;


    public static void moveDirectlyTowards(RobotController rc, MapLocation loc) throws GameActionException {
        MapLocation currLocation = rc.getLocation();
        Direction dirToLoc = currLocation.directionTo(loc);
        for(Direction dir: MapLocationUtils.getBestDirections(dirToLoc)){
            if(rc.canMove(dir)){
                rc.move(dir);
            }
        }
    }

    public static void smartExplore(RobotController rc, MapLocation startLocation) throws GameActionException{
        boolean hasMoved = false;
        MapLocation currLoc = rc.getLocation();
        if(currLoc.x == 0 || currLoc.x == rc.getMapWidth()-1 || currLoc.y == 0 || currLoc.y == rc.getMapHeight()-1){
            RobotPlayer.startLocation = currLoc;
        }
        while(!hasMoved){
            Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
            MapLocation nextLocation = currLoc.add(dir);
            if(startLocation.distanceSquaredTo(currLoc)<= startLocation.distanceSquaredTo(nextLocation) && rc.canMove(dir)){
                rc.move(dir);
                hasMoved = true;
            }
        }
    }

    public static boolean canMoveTowards(RobotController rc, MapLocation target){
        if (!rc.isMovementReady()) {
            // If our cooldown is too high, then don't even bother!
            // There is nothing we can do anyway.
            return false;
        }
        MapLocation currentLocation = rc.getLocation();
        // if (currentLocation == target) // this is BAD! see Lecture 2 for why.
        if (currentLocation.equals(target)) {
            // We're already at our goal! Nothing to do either.
            return false;
        }
        return true;
    }

    public static void moveTowards(RobotController rc, MapLocation target) throws GameActionException {
        if (!rc.isMovementReady()) {
            // If our cooldown is too high, then don't even bother!
            // There is nothing we can do anyway.
            return ;
        }

        MapLocation currentLocation = rc.getLocation();
        // if (currentLocation == target) // this is BAD! see Lecture 2 for why.
        if (currentLocation.equals(target)) {
            // We're already at our goal! Nothing to do either.
            return;
        }

        Direction d = currentLocation.directionTo(target);
        if (rc.canMove(d) && !isObstacle(rc, d)) {
            // Easy case of Bug 0!
            // No obstacle in the way, so let's just go straight for it!
            rc.move(d);
            bugDirection = null;
        } else {
            // Hard case of Bug 0 :<
            // There is an obstacle in the way, so we're gonna have to go around it.
            if (bugDirection == null) {
                // If we don't know what we're trying to do
                // make something up
                // And, what better than to pick as the direction we want to go in
                // the best direction towards the goal?
                bugDirection = d;
            }
            // Now, try to actually go around the obstacle
            // using bugDirection!
            // Repeat 8 times to try all 8 possible directions.
            for (int i = 0; i < 8; i++) {
                if (rc.canMove(bugDirection) && !isObstacle(rc, bugDirection)) {
                    rc.move(bugDirection);
                    bugDirection = bugDirection.rotateLeft();
                    break;
                } else {
                    bugDirection = bugDirection.rotateRight();
                }
            }
        }
    }

    private static boolean isObstacle(RobotController rc, Direction d) throws GameActionException {
        MapLocation adjacentLocation = rc.getLocation().add(d);
        int rubbleOnLocation = rc.senseRubble(adjacentLocation);
        return rubbleOnLocation > 75;
    }

}
