package org.superheroes.team;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Team {
    public static Team named(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }

    private String name;
}
