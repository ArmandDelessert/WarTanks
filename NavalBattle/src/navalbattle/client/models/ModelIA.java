package navalbattle.client.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import navalbattle.boats.Aircraftcarrier;
import navalbattle.boats.Boat;
import navalbattle.boats.BoatIsOutsideGridException;
import navalbattle.boats.BoatPosition;
import navalbattle.boats.Cruiser;
import navalbattle.boats.Destroyer;
import navalbattle.boats.Submarine;
import navalbattle.client.BoatNotPlacedException;
import navalbattle.client.ClientGrid;
import navalbattle.client.InconsistentGameStateBetweenServerAndClientException;
import navalbattle.client.NavalBattleClient;
import navalbattle.protocol.common.BeaconingParameters;
import navalbattle.protocol.common.BoatQuantity;
import navalbattle.protocol.common.IRemoteMessageListener;
import navalbattle.protocol.common.MapSizeEnum;
import navalbattle.protocol.common.NavalBattleProtocol;
import navalbattle.protocol.messages.common.*;
import navalbattle.server.BoatsAreOverlapping;
import navalbattle.server.HitPosition;

public class ModelIA implements IRemoteMessageListener {
    
    final String ip;
    final String password;
    final NavalBattleConnect.DIGEST_TYPE digest;
    final String username;
    
    final int timeoutConnect = 7; // in seconds
    private ClientGrid idGrid = null;
    private ClientGrid opponentGrid = null;
    private BeaconingParameters parameters;
    private NavalBattleClient client;
    private final double sleepBeforeFiring = 1.2; // in seconds
    
    public ModelIA(String ip, String username, NavalBattleConnect.DIGEST_TYPE digest, String password) {
        this.ip = ip;
        this.username = username;
        this.digest = digest;
        this.password = password;
        
        if (this.password == null && digest == NavalBattleConnect.DIGEST_TYPE.SHARED_SECRET) {
            throw new IllegalArgumentException("Password must be specified when using SHARED_SECRET");
        }
    }
    
    public void connectToServer() {
        this.client = new NavalBattleClient();
        this.client.subscribe(this);
        
        try {
            client.connectToServer(this.ip, timeoutConnect);
        } catch (IOException ex) {
        }
        
        if (client.isConnectedToServer()) {
            this.log("OK: Connected on : " + ip);
            this.sendGetGameParameters();
            
        } else {
            this.log("ERROR: Could not connect to : " + ip);
        }
    }
    
    private void log(String txt) {
        //System.out.println(txt);
    }
    
    private void sendAuth() {
        this.log("Sending connect message");
        
        NavalBattleConnect connect = new NavalBattleConnect();
        connect.setValues(this.username, this.password, this.digest);
        
        this.client.sendMessage(connect);
    }
    
    private void placeAndSendBoats() {
        this.log("Placing boats randomly");
        
        MapSizeEnum mapSize = this.parameters.getMapSize();
        //MapSizeEnum mapSize = MapSizeEnum.SMALL;
        this.opponentGrid = new ClientGrid(mapSize);
        BoatQuantity quantitiesMustBe = mapSize.getQuantityOfBoats();
        
        HashMap<Class, Integer> boatClasses = new HashMap<>();
        boatClasses.put(Destroyer.class, 0);
        boatClasses.put(Submarine.class, 0);
        boatClasses.put(Cruiser.class, 0);
        boatClasses.put(Aircraftcarrier.class, 0);
        
        HashMap<Class, Integer> boatLength = new HashMap<>();
        boatLength.put(Destroyer.class, quantitiesMustBe.getDestroyerCount());
        boatLength.put(Submarine.class, quantitiesMustBe.getSubmarineCount());
        boatLength.put(Cruiser.class, quantitiesMustBe.getCruiserCount());
        boatLength.put(Aircraftcarrier.class, quantitiesMustBe.getAircraftCarriersCount());
        
        for (Map.Entry<Class, Integer> boatClass : boatClasses.entrySet()) {
            int boatLen;
            
            try {
                boatLen = boatClass.getKey().getField("LENGTH").getInt(null);
                boatClass.setValue(boatLen);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            }
        }
        
        int mapSizeLen = mapSize.getSize();
        Boolean[][] grid = new Boolean[mapSizeLen][mapSizeLen];
        
        for (int x = 0; x < mapSizeLen; ++x) {
            for (int y = 0; y < mapSizeLen; ++y) {
                grid[x][y] = false;
            }
        }
        
        ArrayList<Boat> allPositions = new ArrayList<>();
        final int arbitraryCellSize = 20;
        
        for (Map.Entry<Class, Integer> l : boatLength.entrySet()) {
            int numberOfThisKind = boatLength.get(l.getKey());
            int len = boatClasses.get(l.getKey());
            
            for (int c = 0; c < numberOfThisKind; ++c) {
                Boat b = null;
                
                try {
                    switch (len) {
                        case 5:
                            b = new Aircraftcarrier(arbitraryCellSize);
                            break;
                        
                        case 4:
                            b = new Cruiser(arbitraryCellSize);
                            break;
                        
                        case 3:
                            b = new Submarine(arbitraryCellSize);
                            break;
                        
                        case 2:
                            b = new Destroyer(arbitraryCellSize);
                            break;
                        
                        default:
                            throw new IllegalArgumentException();
                    }
                } catch (Exception ex) {
                }
                
                BoatPositionWithNewGrid res = this.getPosition(grid, len);
                BoatPosition bp = res.getBp();
                grid = res.getNewGrid();

                /*
                 for (int y = 0; y < grid.length; ++y) {
                 for (int x = 0; x < grid.length; ++x) {
                 System.out.print(grid[x][y] ? " b " : " x ");
                 }

                 System.out.println();
                 }
                 */
                if (res == null) {
                    this.log("ERROR: Cannot place boats : map is too small");
                } else {
                    //System.out.println("Placing a " + b.getClass() + " at position " + bp.getX() + "," + bp.getY() + " " + bp.getOrientation());
                    b.setPosition(bp.getX(), bp.getY(), bp.getOrientation());
                    allPositions.add(b);
                }
            }
        }
        try {
            this.idGrid = new ClientGrid(mapSize);
            this.idGrid.positionBoats(allPositions);
        } catch (BoatsAreOverlapping ex) {
            Logger.getLogger(ModelIA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BoatIsOutsideGridException ex) {
            Logger.getLogger(ModelIA.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Sending boat positions to the server
        ArrayList<BoatPosition> pos = new ArrayList<>();
        
        for (Boat boat : allPositions) {
            pos.add(boat.getPosition());
        }
        
        NavalBattlePositionMyBoats positionMyBoats = new NavalBattlePositionMyBoats();
        positionMyBoats.setValues(pos);
        this.client.sendMessage(positionMyBoats);
    }
    
    private BoatPositionWithNewGrid tryPositionHorizontally(Boolean[][] grid, int length, int x, int y) {
        // Can we put it horizontally ?
        if (x + length <= grid.length) {
            boolean canPlaceHere = true;
            
            for (int a = 0; a < length; ++a) {
                if (grid[x + a][y]) {
                    // Nope
                    canPlaceHere = false;
                    break;
                }
            }
            
            if (canPlaceHere) {
                for (int a = 0; a < length; ++a) {
                    grid[x + a][y] = true;
                }
                
                return new BoatPositionWithNewGrid(new BoatPosition(x, y, NavalBattleProtocol.BOAT_ORIENTATION.HORIZONTAL, length), grid);
            }
        }
        
        return null;
    }
    
    private BoatPositionWithNewGrid tryPositionVertically(Boolean[][] grid, int length, int x, int y) {
        // Can we put it vertically ?
        if (y + length <= grid.length) {
            boolean canPlaceHere = true;
            
            for (int a = 0; a < length; ++a) {
                if (grid[x][y + a]) {
                    // Nope
                    canPlaceHere = false;
                    break;
                }
            }
            
            if (canPlaceHere) {
                for (int a = 0; a < length; ++a) {
                    grid[x][y + a] = true;
                }
                
                return new BoatPositionWithNewGrid(new BoatPosition(x, y, NavalBattleProtocol.BOAT_ORIENTATION.VERTICAL, length), grid);
            }
        }
        
        return null;
    }
    
    private BoatPositionWithNewGrid getPosition(Boolean[][] grid, int length) {
        Random rng = new Random();
        int nextX, nextY;
        BoatPositionWithNewGrid t;
        
        while (true) {
            nextX = rng.nextInt(grid.length);
            nextY = rng.nextInt(grid.length);
            
            if (rng.nextBoolean()) {
                t = this.tryPositionHorizontally(grid, length, nextX, nextY);
                if (t != null) {
                    return t;
                }
                t = this.tryPositionVertically(grid, length, nextX, nextY);
                if (t != null) {
                    return t;
                }
            } else {
                t = this.tryPositionVertically(grid, length, nextX, nextY);
                if (t != null) {
                    return t;
                }
                t = this.tryPositionHorizontally(grid, length, nextX, nextY);
                if (t != null) {
                    return t;
                }
            }
        }
    }
    
    private boolean canFire(NavalBattleProtocol.COORDINATE_TYPE cell) {
        switch (cell) {
            case DAMAGED:
            case SINKED:
            case SATELLITE:
            case MINE:
            case REAVEALED_HAS_NO_SHIP:
            case FIRED_BUT_DID_NOT_DAMAGE_ANYTHING:
            default:
                return false;
            
            case NOTHING:
            case REAVEALED_HAS_SHIP:
                return true;
        }
        
    }
    
    private void chooseSpotFireAndFire() {
        HitPosition spotFire = this.chooseSpotFire();
        
        NavalBattleAttackCoordinate attackMessage = new NavalBattleAttackCoordinate();
        attackMessage.setValues(spotFire.getX(), spotFire.getY());
        this.client.sendMessage(attackMessage);
    }
    
    private HitPosition chooseSpotFire() {
        NavalBattleProtocol.COORDINATE_TYPE[][] grid = this.opponentGrid.getGridForDrawing();
        Random rng = new Random();

        // Check if we have cells which have been revealed and contain a ship
        for (int x = 0; x < grid.length; ++x) {
            for (int y = 0; y < grid.length; ++y) {
                if (grid[x][y] == NavalBattleProtocol.COORDINATE_TYPE.REAVEALED_HAS_SHIP) {
                    return new HitPosition(x, y);
                }
            }
        }
        
        for (int x = 0; x < grid.length; ++x) {
            for (int y = 0; y < grid.length; ++y) {
                // Check horizontally
                boolean lastWasDamaged = false;
                
                if (x + 5 <= grid.length) {
                    // From left to right
                    for (int xv = 0; xv < 5; ++xv) {
                        NavalBattleProtocol.COORDINATE_TYPE current = grid[x + xv][y];
                        boolean currentIsDamaged = (current == NavalBattleProtocol.COORDINATE_TYPE.DAMAGED);
                        
                        if (canFire(current) && !currentIsDamaged && lastWasDamaged) {
                            return new HitPosition(x + xv, y);
                        }
                        
                        lastWasDamaged = currentIsDamaged;
                    }
                }
                
                if (x >= 5) {
                    lastWasDamaged = false;

                    // From right to left
                    for (int xv = 0; xv < 5; ++xv) {
                        NavalBattleProtocol.COORDINATE_TYPE current = grid[x - xv][y];
                        boolean currentIsDamaged = (current == NavalBattleProtocol.COORDINATE_TYPE.DAMAGED);
                        
                        if (canFire(current) && !currentIsDamaged && lastWasDamaged) {
                            return new HitPosition(x - xv, y);
                        }
                        
                        lastWasDamaged = currentIsDamaged;
                    }
                }

                // Check vertically
                if (y + 5 <= grid.length) {
                    // From top to bottom
                    for (int yv = 0; yv < 5; ++yv) {
                        NavalBattleProtocol.COORDINATE_TYPE current = grid[x][y + yv];
                        boolean currentIsDamaged = (current == NavalBattleProtocol.COORDINATE_TYPE.DAMAGED);
                        
                        if (canFire(current) && !currentIsDamaged && lastWasDamaged) {
                            return new HitPosition(x, y + yv);
                        }
                        
                        lastWasDamaged = currentIsDamaged;
                    }
                }
                
                if (y >= 5) {
                    // From bottom to top
                    for (int yv = 0; yv < 5; ++yv) {
                        NavalBattleProtocol.COORDINATE_TYPE current = grid[x][y - yv];
                        boolean currentIsDamaged = (current == NavalBattleProtocol.COORDINATE_TYPE.DAMAGED);
                        
                        if (canFire(current) && !currentIsDamaged && lastWasDamaged) {
                            return new HitPosition(x, y - yv);
                        }
                        
                        lastWasDamaged = currentIsDamaged;
                    }
                }
            }
        }

        // No position : fire randomly
        while (true) {
            int randomX = rng.nextInt(grid.length);
            int randomY = rng.nextInt(grid.length);
            
            if (this.canFire(grid[randomX][randomY])) {
                return new HitPosition(randomX, randomY);
            }
        }
    }
    
    private void sendGetGameParameters() {
        this.log("Sending get game parameters");
        
        NavalBattleGameParameters getParams = new NavalBattleGameParameters();
        this.client.sendMessage(getParams);
    }
    
    @Override
    public void onMessageReceived(NOTIFICATION_TYPE type, NavalBattleMessage message) {
        switch (type) {
            case MESSAGE:
                
                Class classMess = message.getClass();
                
                if (classMess == NavalBattleConnectResponse.class) {
                    NavalBattleConnectResponse casted = (NavalBattleConnectResponse) message;
                    NavalBattleConnectResponse.RESULT result = casted.getResult();
                    
                    if (result == NavalBattleConnectResponse.RESULT.AUTHENTICATED || result == NavalBattleConnectResponse.RESULT.CONNECTED) {
                        this.log("OK: Authenticated");

                        // We wait for NavalBattleGameReadyToStart
                    } else {
                        this.log("ERROR: Can not authenticate");
                    }
                } else if (classMess == NavalBattleGameParametersResponse.class) {
                    NavalBattleGameParametersResponse casted = (NavalBattleGameParametersResponse) message;
                    this.parameters = casted.getParameters();
                    this.sendAuth();
                } else if (classMess == NavalBattleEndOfGame.class) {
                    
                } else if (classMess == NavalBattlePositionMyBoatsResponse.class) {
                    NavalBattlePositionMyBoatsResponse casted = (NavalBattlePositionMyBoatsResponse) message;
                    
                    if (casted.getResult() == NavalBattlePositionMyBoatsResponse.RESULT.SUCCESS) {
                        this.log("OK: Boat positions accepted by the server");

                        // We wait for NavalBattleGameStart
                    } else {
                        this.log("ERROR: Boat positions not accepted by the server");
                    }
                } else if (classMess == NavalBattleGameReadyToStart.class) {
                    // Position your boats

                    this.placeAndSendBoats();
                } else if (classMess == NavalBattleGameStart.class) {
                    // Attacking /being attacked
                    NavalBattleGameStart casted = (NavalBattleGameStart) message;

                    // Who will start ?
                    if (casted.getUsernameFirstToPlay().equals(this.username)) {
                        // It's me, fire!
                        this.chooseSpotFireAndFire();
                    } else {
                        // Wait for NavalBattleAttackedCoordinate
                    }
                    
                } else if (classMess == NavalBattleChatReceive.class) {
                    // We received a chat message
                } else if (classMess == NavalBattleAttackCoordinateResponse.class) {
                    // Damages we have done
                    NavalBattleAttackCoordinateResponse casted = (NavalBattleAttackCoordinateResponse) message;
                    
                    if (casted.getResult() == NavalBattleAttackCoordinateResponse.RESULT.SUCCESS) {
                        this.opponentGrid.receivedFireFromOpponentSimpleDesign(casted.getAllCoordinatesHit());
                    } else {
                        // error
                        // TO-DO
                    }
                    
                } else if (classMess == NavalBattleAttackedCoordinate.class) {
                    // We are being attacked
                    NavalBattleAttackedCoordinate casted = (NavalBattleAttackedCoordinate) message;
                    
                    try {
                        this.idGrid.receivedFireFromOpponent(casted.getAllCoordinatesHit());
                    } catch (InconsistentGameStateBetweenServerAndClientException | BoatNotPlacedException ex) {
                        Logger.getLogger(ModelIA.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    try {
                        Thread.sleep((long)(this.sleepBeforeFiring * 1000));
                    } catch (InterruptedException ex) {
                    }

                    // Fire!
                    this.chooseSpotFireAndFire();
                    
                } else if (classMess == NavalBattleDisconnect.class) {
                    this.client.disconnect();
                    this.connectToServer();
                }
                
                break;
            
            case BEEN_DISCONNECTED:
                break;
        }
    }
}
