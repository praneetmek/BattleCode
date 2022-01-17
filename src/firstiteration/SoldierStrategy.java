package firstiteration;

import battlecode.common.*;

public class SoldierStrategy {
    public static void runSoldier(RobotController rc, RobotInfo[] nearbyEnemies) throws GameActionException {
        //Below is example code, don't worry about it or actually bother

        //mine at locations around me
        int visionRadiusSquared = rc.getType().visionRadiusSquared;

        boolean hasActed = false;
        //path towards the archon
        int archonLocation = rc.readSharedArray(0);
        if(archonLocation != 0){
            MapLocation archonMapLocation = MapLocationUtils.intToMapLocation(rc, archonLocation);
            if(rc.canSenseLocation(archonMapLocation) && !rc.canSenseRobotAtLocation(archonMapLocation)){
                rc.writeSharedArray(0,0);
            }
            else{
                if(rc.canAttack(archonMapLocation)){
                    rc.attack(archonMapLocation);
                    hasActed = true;
                }
                else{
                    if(!PathingUtils.moveTowards(rc, archonMapLocation)){
                        for (RobotInfo enemy:nearbyEnemies){
                            if(rc.canAttack(enemy.getLocation())){
                                rc.setIndicatorString("can attack enemy");
                                rc.attack(enemy.getLocation());
                                hasActed = true;
                            }
                        }
                    }
                    hasActed = true;
                }
            }

        }
        else{
            for (RobotInfo enemy:nearbyEnemies){
                if(rc.canAttack(enemy.getLocation())){
                    rc.setIndicatorString("can attack enemy");
                    rc.attack(enemy.getLocation());
                    hasActed = true;
                }
            }
        }
        //otherwise just move randomly
        if(!hasActed){
            PathingUtils.smartExplore(rc, RobotPlayer.startLocation);
        }
    }
}
