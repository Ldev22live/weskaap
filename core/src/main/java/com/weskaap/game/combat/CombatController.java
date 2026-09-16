package com.weskaap.game.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.enemy.PrototypeEnemy;
import com.weskaap.game.player.Hero;

import java.util.List;

public class CombatController {
    public static final int BASIC_ATTACK_DAMAGE = 25;
    public static final float BASIC_ATTACK_COOLDOWN = 0.4f;
    public static final float BASIC_ATTACK_RANGE = 56f;

    private static final float ATTACK_WIDTH = 40f;
    private static final float ATTACK_DISPLAY_DURATION = 0.12f;

    private final Rectangle attackBounds = new Rectangle();
    private float cooldownRemaining;
    private float attackDisplayTimer;
    private boolean attackJustPressed;
    private Combatant lastHit;
    private int hitCount;

    public boolean update(float delta, Hero hero, List<PrototypeEnemy> combatants) {
        cooldownRemaining = Math.max(0f, cooldownRemaining - delta);
        attackDisplayTimer = Math.max(0f, attackDisplayTimer - delta);
        if (!hero.isAlive() || cooldownRemaining > 0f) {
            return false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attackJustPressed = true;
        }
        if (!attackJustPressed) {
            return false;
        }
        attackJustPressed = false;

        cooldownRemaining = BASIC_ATTACK_COOLDOWN;
        attackDisplayTimer = ATTACK_DISPLAY_DURATION;
        lastHit = null;
        hitCount = 0;
        updateAttackBounds(hero);
        int attackDamage = BASIC_ATTACK_DAMAGE + hero.getTotalAttack();
        for (Combatant combatant : combatants) {
            if (combatant.isAlive() && attackBounds.overlaps(combatant.getCombatBounds())) {
                combatant.takeDamage(attackDamage);
                lastHit = combatant;
                hitCount++;
            }
        }
        return true;
    }

    public Rectangle getAttackBounds() {
        return attackBounds;
    }

    public boolean isAttackVisible() {
        return attackDisplayTimer > 0f;
    }

    public Combatant getLastHit() {
        return lastHit;
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getLastAttackDamage() {
        return lastHit != null ? BASIC_ATTACK_DAMAGE : 0;
    }

    private void updateAttackBounds(Hero hero) {
        Rectangle heroBounds = hero.getCollisionBounds();
        Vector2 facing = hero.getFacingDirection();
        if (facing.x > 0f) {
            attackBounds.set(heroBounds.x + heroBounds.width, hero.getY() - ATTACK_WIDTH / 2f,
                BASIC_ATTACK_RANGE, ATTACK_WIDTH);
        } else if (facing.x < 0f) {
            attackBounds.set(heroBounds.x - BASIC_ATTACK_RANGE, hero.getY() - ATTACK_WIDTH / 2f,
                BASIC_ATTACK_RANGE, ATTACK_WIDTH);
        } else if (facing.y > 0f) {
            attackBounds.set(hero.getX() - ATTACK_WIDTH / 2f, heroBounds.y + heroBounds.height,
                ATTACK_WIDTH, BASIC_ATTACK_RANGE);
        } else {
            attackBounds.set(hero.getX() - ATTACK_WIDTH / 2f, heroBounds.y - BASIC_ATTACK_RANGE,
                ATTACK_WIDTH, BASIC_ATTACK_RANGE);
        }
    }
}
