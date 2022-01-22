package firstiteration;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class MapLocationUtils {
    public static int mapLocationToInt(RobotController rc, MapLocation mapLocation){
        return mapLocation.y * rc.getMapWidth() + mapLocation.x + 1;
    }

    public static MapLocation intToMapLocation(RobotController rc, int location){
        location -=1;
        return new MapLocation(location%rc.getMapWidth(), location/rc.getMapWidth());
    }

    public static Direction[] getBestDirections(Direction d){
        switch (d){
            case EAST:
                return new Direction[]{Direction.EAST, Direction.NORTHEAST, Direction.SOUTHEAST, Direction.NORTH, Direction.SOUTH, Direction.NORTHWEST, Direction.SOUTHWEST, Direction.WEST};
            case WEST:
                return new Direction[]{Direction.WEST, Direction.NORTHWEST, Direction.SOUTHWEST, Direction.NORTH, Direction.SOUTH, Direction.NORTHEAST, Direction.SOUTHEAST, Direction.EAST};
            case NORTH:
                return new Direction[]{Direction.NORTH, Direction.NORTHWEST, Direction.NORTHEAST, Direction.WEST, Direction.EAST, Direction.SOUTHWEST, Direction.SOUTHEAST, Direction.SOUTH};
            case SOUTH:
                return new Direction[]{Direction.SOUTH, Direction.SOUTHWEST, Direction.SOUTHEAST, Direction.WEST, Direction.EAST, Direction.NORTHWEST, Direction.NORTHEAST, Direction.NORTH};
            case NORTHEAST:
                return new Direction[]{Direction.NORTHEAST, Direction.NORTH, Direction.EAST, Direction.NORTHWEST, Direction.SOUTHEAST, Direction.WEST, Direction.SOUTH, Direction.SOUTHWEST};
            case NORTHWEST:
                return new Direction[]{Direction.NORTHWEST, Direction.NORTH, Direction.WEST, Direction.NORTHEAST, Direction.SOUTHWEST, Direction.EAST, Direction.SOUTHWEST, Direction.SOUTHEAST};
            case SOUTHEAST:
                return new Direction[]{Direction.SOUTHEAST, Direction.SOUTH, Direction.EAST, Direction.NORTHEAST, Direction.SOUTHWEST, Direction.NORTH, Direction.WEST, Direction.NORTHWEST};
            case SOUTHWEST:
                return new Direction[]{Direction.SOUTHWEST, Direction.SOUTH, Direction.WEST, Direction.SOUTHEAST, Direction.NORTHWEST, Direction.EAST, Direction.NORTH, Direction.NORTHEAST};

        }
        return RobotPlayer.directions;
    }
}
