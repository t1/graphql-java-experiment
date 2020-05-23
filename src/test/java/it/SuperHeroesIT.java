package it;

import io.smallrye.graphql.client.typesafe.api.GraphQlClientBuilder;
import io.smallrye.graphql.client.typesafe.api.GraphQlClientException;
import io.smallrye.graphql.client.typesafe.api.Header;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;
import org.superheroes.hero.ShieldClearance;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.superheroes.hero.ShieldClearance.Level.SECRET;
import static org.superheroes.hero.ShieldClearance.Level.TOP_SECRET;

public class SuperHeroesIT {
    @SuppressWarnings("unused")
    public static ShieldClearance.Level clearance() { return clearance; }

    private static ShieldClearance.Level clearance = TOP_SECRET;

    private final SuperHeroesApi api = GraphQlClientBuilder.newBuilder().build(SuperHeroesApi.class);

    @Header(name = "S.H.I.E.L.D.-Clearance", method = "clearance")
    public interface SuperHeroesApi {
        List<SuperHero> heroesIn(String location);

        SuperHero findHeroByName(String name);

        @Query("findHeroByName")
        SuperHeroWithTeams findHeroWithTeamsByName(String name);

        @Query("findHeroByName")
        SuperHeroWithRealName findHeroWithRealNameByName(String name);

        @Query("findHeroByName")
        SuperHeroWithLocation findHeroWithLocationByName(String name);

        @SuppressWarnings("UnusedReturnValue")
        SuperHeroWithLocation heroes();

        Team team(String name);

        List<Team> teams();

        @Mutation("createNewHero") SuperHero add(SuperHero newHero);
    }

    @Getter @Setter @ToString @Builder @AllArgsConstructor @NoArgsConstructor
    public static class SuperHero {
        private String name;
        private @Singular List<String> superPowers;
    }

    @Getter @Setter @ToString
    public static class SuperHeroWithRealName {
        private String name;
        @Name("realName") private String secretIdentity;
    }

    @Getter @Setter @ToString
    public static class SuperHeroWithLocation {
        private String name;
        private String currentLocation;
    }

    @Getter @Setter @ToString
    public static class SuperHeroWithTeams {
        private String name;
        private List<String> superPowers;
        private List<TeamWithoutMembers> teamAffiliations;
    }

    @Getter @Setter @ToString
    public static class Team {
        private String name;
        private List<SuperHero> heroes;
    }

    @Getter @Setter @ToString
    public static class TeamWithoutMembers {
        private String name;
        private int size;
    }


    @Test void shouldGetIronMan() {
        SuperHero ironMan = api.findHeroByName("Iron Man");

        then(ironMan.name).isEqualTo("Iron Man");
        then(ironMan.superPowers).containsExactly("wealth", "engineering");
    }

    @Test void shouldGetSpiderManWithRealName() {
        SuperHeroWithRealName spiderMan = api.findHeroWithRealNameByName("Spider Man");

        then(spiderMan.name).isEqualTo("Spider Man");
        then(spiderMan.secretIdentity).isEqualTo("Peter Parker");
    }

    @Test void shouldFailToGetSpiderManWithRealNameWithoutClearance() {
        clearance = SECRET;

        GraphQlClientException thrown = catchThrowableOfType(() -> api.findHeroWithRealNameByName("Spider Man"), GraphQlClientException.class);

        then(thrown).hasMessageContaining("user has not the required clearance TOP_SECRET but SECRET");
    }

    @Test void shouldLocateSpiderMan() {
        SuperHeroWithLocation spiderMan = api.findHeroWithLocationByName("Spider Man");

        then(spiderMan.name).isEqualTo("Spider Man");
        then(spiderMan.currentLocation).isEqualTo("New York, NY");
    }

    @Test void shouldFailToLocateWolverine() {
        GraphQlClientException thrown = catchThrowableOfType(() -> api.findHeroWithLocationByName("Wolverine"), GraphQlClientException.class);

        then(thrown).hasMessageContaining("Unable to determine location for Wolverine");
    }

    @Test void shouldFailToLocateAllHeroes() {
        GraphQlClientException thrown = catchThrowableOfType(api::heroes, GraphQlClientException.class);

        then(thrown).hasMessageContaining("Unable to determine location for Wolverine");
        // then(thrown.getData()). somehow contains(
        //     "{\"name\":\"Iron Man\",\"currentLocation\":\"Los Angeles, CA\"}," +
        //     "{\"name\":\"Spider Man\",\"currentLocation\":\"New York, NY\"}," +
        //     "{\"name\":\"Starlord\",\"currentLocation\":\"Los Angeles, CA\"}," +
        //     "{\"name\":\"Wolverine\",\"currentLocation\":null}");
    }

    @Test void shouldGetIronManWithTeams() {
        SuperHeroWithTeams ironMan = api.findHeroWithTeamsByName("Iron Man");

        then(ironMan.name).isEqualTo("Iron Man");
        then(ironMan.superPowers).containsExactly("wealth", "engineering");
        then(ironMan.teamAffiliations).hasSize(1);
        then(ironMan.teamAffiliations.get(0).name).isEqualTo("Avengers");
        then(ironMan.teamAffiliations.get(0).size).isEqualTo(3);
    }

    @Test void shouldGetAllTeams() {
        List<Team> teams = api.teams();

        then(teams.size()).isEqualTo(3);
        thenIsAvengers(teams.get(0));
    }

    @Test void shouldGetTeamByName() {
        Team avengers = api.team("Avengers");

        thenIsAvengers(avengers);
    }

    private void thenIsAvengers(Team avengers) {
        then(avengers.name).isEqualTo("Avengers");
        then(avengers.heroes).hasSize(3);
        then(avengers.heroes.get(0).name).isEqualTo("Iron Man");
    }

    @Test void shouldGetSuperHeroesInOuterSpace() {
        List<SuperHero> found = api.heroesIn("Outer Space");

        then(found.size()).isEqualTo(1);
        then(found.get(0).name).isEqualTo("Starlord");
    }

    @Test void shouldAddSuperHero() {
        SuperHero created = api.add(SuperHero.builder().name("Groot").build());

        then(created.name).isEqualTo("Groot");
        then(created.superPowers).containsExactly();
    }
}
