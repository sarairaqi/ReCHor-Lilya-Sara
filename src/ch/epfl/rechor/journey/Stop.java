package ch.epfl.rechor.journey;
import ch.epfl.rechor.Preconditions;
import java.util.Objects;

// arret de transport public
public record Stop(String name, String platformName, double longitude, double latitude) {

public Stop {
    Objects.requireNonNull(name, "Le nom de l'arret ne peut pas etre nul");

    Preconditions.checkArgument(longitude>=-180 && longitude<=180) ;{
    }

    Preconditions.checkArgument(latitude>=-90 && latitude<=90) ;{
    }
}

}
