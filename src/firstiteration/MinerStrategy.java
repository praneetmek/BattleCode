package firstiteration;

import battlecode.common.*;

public class MinerStrategy {

    public static void runMiner(RobotController rc) throws GameActionException {
        //Below is example code, don't worry about it or actually bother
        boolean hasActed = false;
        //mine at locations around me
        String s = "Round "+rc.getRoundNum();
        MapLocation me = rc.getLocation();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineGold(mineLocation)) {
                    hasActed = true;
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > 1) {
                    hasActed = true;
                    rc.mineLead(mineLocation);
                }
            }
        }
        //if i can see gold, move to the gold
        int visionRadiusSquared = rc.getType().visionRadiusSquared;
        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(visionRadiusSquared);
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(visionRadiusSquared);
        if(goldLocations.length > 0) {
            MapLocation maxGoldLocation = goldLocations[0];
            int highestGold = rc.senseGold(maxGoldLocation);
            for(MapLocation goldLocation:goldLocations){
                int goldAtSpot = rc.senseGold(goldLocation);
                if (goldAtSpot > highestGold){
                    maxGoldLocation = goldLocation;
                    highestGold = goldAtSpot;
                }
            }
            PathingUtils.moveTowards(rc,maxGoldLocation);
            hasActed = true;


        }
        else if(leadLocations.length>0){
            MapLocation maxLeadLocation = leadLocations[0];
            int highestLead = rc.senseLead(maxLeadLocation);
            for(MapLocation leadLocation:leadLocations){
                int leadAtSpot = rc.senseLead(leadLocation);
                if (leadAtSpot > highestLead){
                    maxLeadLocation = leadLocation;
                    highestLead = leadAtSpot;
                }
            }
            s+="Highest Lead Value: " + highestLead + " at "+ maxLeadLocation.toString();
            if(highestLead> 5) {
                PathingUtils.moveTowards(rc, maxLeadLocation);
                hasActed = true;
            }
        }
        if(!hasActed){
            PathingUtils.smartExplore(rc, RobotPlayer.startLocation);
        }
        rc.setIndicatorString(s);

    }
}
