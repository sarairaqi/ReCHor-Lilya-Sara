package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

// voyage
public record Journey(List<Leg> legs) {
    public sealed interface Leg {
        public abstract Stop depStop();
        public abstract Stop arrStop();
        public abstract LocalDateTime depTime();
        public abstract LocalDateTime arrTime();
        public abstract List<IntermediateStop> intermediateStops();

        default Duration duration() {
            return Duration.between(depTime(), arrTime());}



        // implements leg?

        public record IntermediateStop(Stop stop,LocalDateTime arrTime, LocalDateTime depTime ) {
            public IntermediateStop {
            requireNonNull(stop);
            Preconditions.checkArgument(!depTime.isBefore(arrTime));}

        }

        public record Transport(Stop depstop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime, List<IntermediateStop> intermediateStops, Vehicle vehicle, String route, String destination) implements Leg{
            public Transport {
                requireNonNull(depstop);
                requireNonNull(arrStop);
                requireNonNull(depTime);
                requireNonNull(arrTime);
                requireNonNull(vehicle);
                requireNonNull(route);
                requireNonNull(destination);
                Preconditions.checkArgument(!arrTime.isBefore(depTime));

                // Par exemple, il n'est pas nécessaire d'utiliser requireNonNull pour intermediateStops puisque cet argument est passé à copyOf, qui lèvera elle-même une exception si cet argument est null.

            intermediateStops= List.copyOf(intermediateStops); //garantie l'immuabilité de la classe
            }

            @Override
            public Stop depStop() {
                return depstop;
            }
        }

        public record Foot(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime) implements Leg{
            public Foot {
                requireNonNull(depStop);
                requireNonNull(arrStop);
                requireNonNull(depTime);
                requireNonNull(arrTime);
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
            }

            public List<IntermediateStop> intermediateStops() {
                return List.of(); // retourne une liste vide
            }

            public boolean isTransfer() {
                return depStop.name().equals(arrStop.name());
            }

            // Soyez attentifs au fait qu'une étape à pied entre deux arrêts différents peut être considérée comme un changement si ces arrêts ont le même nom. Par exemple, une étape entre la voie 70 et la voie 1 de la gare de Lausanne est un changement, même si les arrêts sont différents. Seul le fait que le nom de (la gare de) ces deux arrêts soit identique compte.
        }
    }

    public Journey {
        requireNonNull(legs);
        Preconditions.checkArgument(!(legs.isEmpty()));

        boolean previousIsFoot = (legs.get(0) instanceof Leg.Foot);

        for (int i=1; i<legs.size(); i++) {
            Leg current= legs.get(i);
            Leg previous= legs.get(i-1);

            boolean currentIsFoot = (current instanceof Leg.Foot);
            Preconditions.checkArgument(
                    currentIsFoot != previousIsFoot);
            previousIsFoot = currentIsFoot;

            Preconditions.checkArgument(current.depStop().equals(previous.arrStop()));
            Preconditions.checkArgument(!current.depTime().isBefore(previous.arrTime()));
        }
        legs= List.copyOf(legs);
    }

    public Stop depStop() {
        return legs.get(0).depStop();
    }
    public Stop arrStop() {
        return legs.get(legs.size()-1).arrStop();
    }
    public LocalDateTime depTime(){
        return legs.get(0).depTime();
    }
    public LocalDateTime arrTime(){
        return legs.get(legs.size()-1).arrTime();
    }
    public Duration duration() {
        return Duration.between(depTime(), arrTime());
    }


}
