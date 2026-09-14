package com.weskaap.game.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.weskaap.game.player.Hero;
import com.weskaap.game.world.CollisionResolver;

import java.util.List;

public class EnemyAiController {
    public static final float AGGRO_RANGE = 220f;
    public static final float STOP_DISTANCE = 48f;
    public static final float ENEMY_SPEED = 60f;
    public static final float ATTACK_RANGE = 48f;
    public static final int ATTACK_DAMAGE = 10;
    public static final float ATTACK_COOLDOWN = 1.0f;

    public enum State {
        IDLE, CHASING
    }

    private final PrototypeEnemy enemy;
    private final Vector2 direction = new Vector2();
    private State state = State.IDLE;
    private float attackCooldownRemaining;
    private int lastAttackDamage;

    public EnemyAiController(PrototypeEnemy enemy) {
        this.enemy = enemy;
    }

    public State getState() {
        return state;
    }

    public PrototypeEnemy getEnemy() {
        return enemy;
    }

    public void update(float delta, Hero hero, List<Rectangle> obstacles, float worldWidth, float worldHeight) {
        lastAttackDamage = 0;
        attackCooldownRemaining = Math.max(0f, attackCooldownRemaining - delta);

        if (!enemy.isAlive() || !hero.isAlive()) {
            state = State.IDLE;
            return;
        }

        float distance = enemy.getPosition().dst(hero.getPosition());
        if (distance > AGGRO_RANGE) {
            state = State.IDLE;
            return;
        }

        state = State.CHASING;
        if (distance > STOP_DISTANCE) {
            direction.set(hero.getPosition()).sub(enemy.getPosition()).nor();
            float moveDistance = Math.min(ENEMY_SPEED * delta, distance - STOP_DISTANCE);
            direction.scl(moveDistance);
            Rectangle bounds = enemy.getCombatBounds();
            CollisionResolver.move(bounds, direction.x, direction.y, obstacles, worldWidth, worldHeight);
            enemy.setPosition(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
        }

        if (distance <= ATTACK_RANGE && attackCooldownRemaining <= 0f) {
            int damage = Math.max(1, ATTACK_DAMAGE - hero.getTotalArmour());
            hero.takeDamage(damage);
            lastAttackDamage = damage;
            attackCooldownRemaining = ATTACK_COOLDOWN;
        }
    }

    public int getLastAttackDamage() {
        return lastAttackDamage;
    }

    public float getDistanceToHero(Hero hero) {
        return enemy.getPosition().dst(hero.getPosition());
    }
}
