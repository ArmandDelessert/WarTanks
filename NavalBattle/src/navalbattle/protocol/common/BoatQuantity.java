package navalbattle.protocol.common;


public class BoatQuantity {
    
    // D'ont forget to update the equals and hashCode methods
    final int aircraftCarriersCount;
    final int cruiserCount;
    final int destroyerCount;
    final int submarineCount;

    public BoatQuantity(int aircraftCarriersCount, int cruiserCount, int destroyerCount, int submarineCount) {
        this.aircraftCarriersCount = aircraftCarriersCount;
        this.cruiserCount = cruiserCount;
        this.destroyerCount = destroyerCount;
        this.submarineCount = submarineCount;
    }

    public int getAircraftCarriersCount() {
        return aircraftCarriersCount;
    }

    public int getCruiserCount() {
        return cruiserCount;
    }

    public int getDestroyerCount() {
        return destroyerCount;
    }

    public int getSubmarineCount() {
        return submarineCount;
    }
    
    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof BoatQuantity))
            return false;
       
        if (obj == this)
            return true;

        BoatQuantity cst = (BoatQuantity) obj;
        return this.getAircraftCarriersCount() == cst.getAircraftCarriersCount() &&
                this.getCruiserCount() == cst.getCruiserCount() &&
                this.getDestroyerCount() == cst.getDestroyerCount() &&
                this.getSubmarineCount() == cst.getSubmarineCount();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.aircraftCarriersCount;
        hash = 79 * hash + this.cruiserCount;
        hash = 79 * hash + this.destroyerCount;
        hash = 79 * hash + this.submarineCount;
        return hash;
    }
}
