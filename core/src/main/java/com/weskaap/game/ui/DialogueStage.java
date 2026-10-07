package com.weskaap.game.ui;

import com.badlogic.gdx.Gdx;
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
import com.weskaap.game.dialogue.DialogueController;
import com.weskaap.game.dialogue.DialogueNode;
import com.weskaap.game.dialogue.DialogueOption;

public class DialogueStage extends Stage {

    private static final float PANEL_MAX_WIDTH = 1100f;
    private static final float PANEL_HEIGHT = 300f;
    private static final float BUTTON_MIN_HEIGHT = 40f;
    private static final float SPEAKER_SCALE = 1.4f;
    private static final float TEXT_SCALE = 1.1f;

    private final DialogueController controller;
    private final Texture panelTexture;
    private final Texture buttonUpTexture;
    private final Texture buttonDownTexture;
    private final Texture buttonOverTexture;
    private final TextButton.TextButtonStyle buttonStyle;
    private final Table table;
    private final Label speakerLabel;
    private final Label textLabel;
    private final Table optionsTable;
    private final TextButton continueButton;

    private String lastNodeId;
    private boolean wasActive;

    public DialogueStage(DialogueController controller, BitmapFont font) {
        super(new ScreenViewport());
        this.controller = controller;

        this.panelTexture = createColorTexture(UiConstants.PANEL_BACKGROUND);
        this.buttonUpTexture = createColorTexture(lighten(UiConstants.PANEL_BACKGROUND, 0.1f));
        this.buttonDownTexture = createColorTexture(UiConstants.PANEL_BORDER);
        this.buttonOverTexture = createColorTexture(lighten(UiConstants.PANEL_BACKGROUND, 0.18f));

        Label.LabelStyle speakerStyle = new Label.LabelStyle(font, UiConstants.SPEAKER_COLOR);
        Label.LabelStyle textStyle = new Label.LabelStyle(font, UiConstants.TEXT_DEFAULT);

        this.buttonStyle = new TextButton.TextButtonStyle();
        this.buttonStyle.font = font;
        this.buttonStyle.fontColor = UiConstants.OPTION_COLOR;
        this.buttonStyle.downFontColor = UiConstants.SPEAKER_COLOR;
        this.buttonStyle.overFontColor = UiConstants.TEXT_DEFAULT;
        this.buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        this.buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));
        this.buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonOverTexture));
        this.buttonStyle.up.setMinHeight(BUTTON_MIN_HEIGHT);
        this.buttonStyle.down.setMinHeight(BUTTON_MIN_HEIGHT);
        this.buttonStyle.over.setMinHeight(BUTTON_MIN_HEIGHT);

        table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        table.pad(UiConstants.PADDING);
        table.setVisible(false);

        speakerLabel = new Label("", speakerStyle);
        speakerLabel.setAlignment(Align.left);
        speakerLabel.setFontScale(SPEAKER_SCALE);

        textLabel = new Label("", textStyle);
        textLabel.setAlignment(Align.left | Align.top);
        textLabel.setFontScale(TEXT_SCALE);
        textLabel.setWrap(true);

        optionsTable = new Table();

        continueButton = new TextButton("[E] Continue", buttonStyle);
        continueButton.pad(10f);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.advance();
            }
        });

        table.add(speakerLabel).left().row();
        table.add(textLabel).growX().expandY().top().left().padTop(UiConstants.SMALL_PADDING).row();
        table.add(optionsTable).growX().left().padTop(UiConstants.SMALL_PADDING).row();
        table.add(continueButton).padTop(UiConstants.SMALL_PADDING).right().row();

        addActor(table);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        refresh();
    }

    @Override
    public void dispose() {
        super.dispose();
        panelTexture.dispose();
        buttonUpTexture.dispose();
        buttonDownTexture.dispose();
        buttonOverTexture.dispose();
    }

    private void refresh() {
        boolean active = controller.isActive();
        if (!active) {
            if (table.isVisible()) {
                table.setVisible(false);
                lastNodeId = null;
                wasActive = false;
            }
            return;
        }

        DialogueNode node = controller.getCurrentNode();
        String nodeId = node != null ? node.getId() : null;
        if (!table.isVisible() || active != wasActive || !java.util.Objects.equals(nodeId, lastNodeId)) {
            wasActive = active;
            lastNodeId = nodeId;
            if (node == null) {
                table.setVisible(false);
                return;
            }

            table.setVisible(true);
            speakerLabel.setText(node.getSpeaker().toUpperCase());
            textLabel.setText(node.getText());
            optionsTable.clear();
            if (Gdx.app != null) {
                Gdx.app.log("DialogueDebug", "refresh: node=" + node.getId() + " speaker=" + node.getSpeaker()
                    + " text=" + node.getText() + " options=" + node.getOptions().size());
                for (DialogueOption option : node.getOptions()) {
                    Gdx.app.log("DialogueDebug", "  option text=" + option.getText()
                        + " next=" + option.getTargetNodeId() + " quest=" + option.getQuestId());
                }
            }

            if (node.hasOptions()) {
                continueButton.setVisible(false);
                int index = 1;
                for (DialogueOption option : node.getOptions()) {
                    final int optionIndex = index - 1;
                    TextButton optionButton = new TextButton(index + ". " + option.getText(), buttonStyle);
                    optionButton.pad(10f);
                    optionButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            controller.selectOption(optionIndex);
                        }
                    });
                    optionsTable.add(optionButton).fillX().padTop(8f).row();
                    index++;
                }
            } else {
                continueButton.setVisible(true);
            }
        }

        if (table.isVisible()) {
            updateLayout();
        }
    }

    private void updateLayout() {
        float screenWidth = getViewport().getScreenWidth();
        float screenHeight = getViewport().getScreenHeight();
        float width = Math.min(PANEL_MAX_WIDTH, Math.max(240f, screenWidth * 0.85f));
        float height = Math.min(PANEL_HEIGHT, screenHeight - UiConstants.PANEL_MARGIN * 4f);
        table.setSize(width, height);
        table.setPosition((screenWidth - width) / 2f, UiConstants.PANEL_MARGIN);
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
        return new Color(
            Math.min(1f, base.r + amount),
            Math.min(1f, base.g + amount),
            Math.min(1f, base.b + amount),
            base.a
        );
    }
}
