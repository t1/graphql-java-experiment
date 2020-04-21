package org.superheroes.hero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ForbiddenException;

@RequestScoped
@AllArgsConstructor @NoArgsConstructor(force = true)
public @Data class ShieldClearance {
    @SuppressWarnings("unused")
    public enum Level {
        PUBLIC, INTERNAL, SECRET, TOP_SECRET
    }

    private Level level;

    public void mustBe(Level requiredLevel) {
        if (level == null)
            throw new NullPointerException("clearance was not initialized");
        if (level.ordinal() < requiredLevel.ordinal())
            throw new ForbiddenException("user has not the required clearance " + requiredLevel + " but " + level);
    }
}
