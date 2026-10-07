package com.weskaap.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.weskaap.game.travel.TravelController;
import com.weskaap.game.travel.TravelDestination;
import com.weskaap.game.travel.TravelResult;
import com.weskaap.game.world.AreaId;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TravelMenuStage extends Stage {
    private final TravelController controller;
    private final Texture panelTexture;
    private final Texture buttonTexture;
    private final Texture buttonOverTexture;
    private final Texture buttonDisabledTexture;
    private final Table panel;
    private final Label resultLabel;
    private final Map<AreaId, TextButton> destinationButtons = new EnumMap<>(AreaId.class);
    private final List<TravelDestination> destinations;
    private boolean wasOpen;
    private int selectionIndex;

    public TravelMenuStage(TravelController controller, BitmapFont font) {
        super(new ScreenViewport());
        this.controller = controller;
        this.destinations = controller.getDestinations();
        panelTexture = createColorTexture(UiConstants.PANEL_BACKGROUND);
        buttonTexture = createColorTexture(lighten(UiConstants.PANEL_BACKGROUND, 0.1f));
        buttonOverTexture = createColorTexture(lighten(UiConstants.PANEL_BACKGROUND, 0.18f));
        buttonDisabledTexture = createColorTexture(new Color(0.16f, 0.16f, 0.18f, 1f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, UiConstants.SPEAKER_COLOR);
        Label.LabelStyle textStyle = new Label.LabelStyle(font, UiConstants.TEXT_DEFAULT);
        TextButton.TextButtonStyle buttonStyle = createButtonStyle(font);

        panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(UiConstants.PADDING * 2f);
        panel.setVisible(false);

        Label title = new Label("TRAVEL", titleStyle);
        title.setFontScale(1.5f);
        title.setAlignment(Align.center);
        panel.add(title).growX().padBottom(UiConstants.PADDING).row();

        for (TravelDestination destination : destinations) {
            TextButton button = new TextButton(destination.getDisplayName(), buttonStyle);
            button.pad(12f);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectDestination(destination.getAreaId());
                }
            });
            destinationButtons.put(destination.getAreaId(), button);
            panel.add(button).width(320f).fillX().padTop(UiConstants.SMALL_PADDING).row();
        }

        TextButton cancel = new TextButton("Cancel", buttonStyle);
        cancel.pad(12f);
        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cancel();
            }
        });
        panel.add(cancel).width(320f).fillX().padTop(UiConstants.PADDING).row();

        resultLabel = new Label("", textStyle);
        resultLabel.setAlignment(Align.center);
        panel.add(resultLabel).width(420f).padTop(UiConstants.SMALL_PADDING).row();

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(panel);
        addActor(root);
    }

    @Override
    public void act(float delta) {
        boolean open = controller.isMenuOpen();
        if (open && !wasOpen) refresh();
        panel.setVisible(open);
        wasOpen = open;
        super.act(delta);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (!controller.isMenuOpen()) return false;
        if (keyCode == Input.Keys.ESCAPE) {
            cancel();
            return true;
        }
        if (keyCode == Input.Keys.UP) {
            selectionIndex = (selectionIndex - 1 + destinations.size()) % destinations.size();
            return true;
        }
        if (keyCode == Input.Keys.DOWN) {
            selectionIndex = (selectionIndex + 1) % destinations.size();
            return true;
        }
        if (keyCode == Input.Keys.ENTER || keyCode == Input.Keys.SPACE) {
            selectDestination(destinations.get(selectionIndex).getAreaId());
            return true;
        }
        return true;
    }

    public TravelResult selectDestination(AreaId areaId) {
        TravelResult result = controller.requestTravel(areaId);
        resultLabel.setText(messageFor(result));
        return result;
    }

    public void cancel() {
        controller.cancel();
        resultLabel.setText("");
    }

    public boolean isDestinationDisabled(AreaId areaId) {
        TextButton button = destinationButtons.get(areaId);
        return button == null || button.isDisabled();
    }

    @Override
    public void dispose() {
        super.dispose();
        panelTexture.dispose();
        buttonTexture.dispose();
        buttonOverTexture.dispose();
        buttonDisabledTexture.dispose();
    }

    private void refresh() {
        selectionIndex = 0;
        resultLabel.setText("");
        for (TravelDestination destination : controller.getDestinations()) {
            boolean current = destination.getAreaId() == controller.getCurrentArea();
            TextButton button = destinationButtons.get(destination.getAreaId());
            button.setDisabled(current || !destination.isUnlocked() || !destination.isImplemented());
            String suffix = current ? " [Current]" : !destination.isUnlocked() ? " [Locked]"
                : !destination.isImplemented() ? " [Not implemented]" : "";
            button.setText(destination.getDisplayName() + suffix);
        }
    }

    private TextButton.TextButtonStyle createButtonStyle(BitmapFont font) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = UiConstants.OPTION_COLOR;
        style.overFontColor = UiConstants.TEXT_DEFAULT;
        style.disabledFontColor = Color.GRAY;
        style.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        style.over = new TextureRegionDrawable(new TextureRegion(buttonOverTexture));
        style.disabled = new TextureRegionDrawable(new TextureRegion(buttonDisabledTexture));
        return style;
    }

    private String messageFor(TravelResult result) {
        switch (result) {
            case LOCKED: return "That destination is locked.";
            case CURRENT_AREA: return "You are already here.";
            case NOT_IMPLEMENTED: return "That area is not implemented yet.";
            case UNKNOWN_DESTINATION: return "Unknown destination.";
            default: return "";
        }
    }

    private static Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private static Color lighten(Color base, float amount) {
        return new Color(Math.min(1f, base.r + amount), Math.min(1f, base.g + amount),
            Math.min(1f, base.b + amount), base.a);
    }
}
