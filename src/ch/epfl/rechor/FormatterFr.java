package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

public final class FormatterFr {

    private FormatterFr() {}

    public static String formatDuration(Duration duration) {
        long totalMinutes = duration.toMinutes();
        if (totalMinutes < 60) {
            // ex: "12 min"
            return totalMinutes + " min";
        } else {
            // ex: "1 h 3 min"
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            return hours + " h " + minutes + " min";
        }
    }

    public static String formatTime (LocalDateTime dateTime) {
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                // => heure, 1..2 chiffres, sign style normal (jamais négatif)
                .appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
                .appendLiteral('h')
                // => minutes, 2 chiffres obligatoirement
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();

        return timeFormatter.format(dateTime);    }

    public static String formatPlatformName(Stop stop) {
        String p = stop.platformName();
        if (p == null || p.isEmpty()) {
            return "";
        }
        if (Character.isDigit(p.charAt(0))) {
            return "voie " + p;
        } else {
            return "quai " + p;
        }    }

    public static String formatLeg(Journey.Leg.Foot footLeg) {
        String d = formatDuration(footLeg.duration());
        if (footLeg.isTransfer()) {
            return "changement (" + d + ")";
        } else {
            return "trajet à pied (" + d + ")";
        }    }

    public static String formatLeg(Journey.Leg.Transport Leg) {
        StringBuilder sb = new StringBuilder();

        // 1) heure départ: "HHhMM"
        sb.append(formatTime(Leg.depTime())).append(" ");

        // 2) nom arrêt départ
        sb.append(Leg.depStop().name());

        // 3) éventuellement la voie/quai de départ, si non vide
        String depPlatform = formatPlatformName(Leg.depStop());
        if (!depPlatform.isEmpty()) {
            sb.append(" (").append(depPlatform).append(")");
        }

        // 4) " → "
        sb.append(" → ");

        // 5) nom arrêt d'arrivée
        sb.append(Leg.arrStop().name());

        // 6) "(arr. HHhMM [voie/quai])"
        sb.append(" (arr. ").append(formatTime(Leg.arrTime()));
        String arrPlatform = formatPlatformName(Leg.arrStop());
        if (!arrPlatform.isEmpty()) {
            sb.append(" ").append(arrPlatform);
        }
        sb.append(")");

        return sb.toString();    }

    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        return transportLeg.route() + " Direction " + transportLeg.destination();
    }
}
//fmt.format(LocalDate.of(2025, Month.FEBRUARY, 18)); pour obtenir la chaîne 18/2/2025.