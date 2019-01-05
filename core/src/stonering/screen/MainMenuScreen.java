package stonering.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import stonering.TearFall;

import java.io.File;

/**
 * Main screen screen of the game.
 *
 * @author Alexander Kuzyakov on 02.04.2017.
 */
public class MainMenuScreen extends SimpleScreen {
    private TearFall game;
    private Stage stage;
    private Table menuTable;

    private TextButton createWorldButton;  // available always
    private TextButton startGameButton;    // available when world with no settlements present
    private TextButton loadGameButton;     // available when savegame exists
    private TextButton aboutButton;        // available always
    private TextButton quitButton;         // available always

    public MainMenuScreen(TearFall game) {
        this.game = game;
    }

    @Override
    public void show() {
        reset();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    /**
     * Creates stage for this screen
     */
    public void init() {
        stage = new Stage();
        stage.setDebugAll(true);
        Container container = createContainer();
        container.setActor(createTable());
        stage.addActor(container);
        stage.addListener(createKeyListener());
    }

    /**
     * Creates container that fills whole screen.
     */
    private Container createContainer() {
        Container container = new Container();
        container.setFillParent(true);
        container.left().bottom();
//        container.setBackground(new TextureRegionDrawable(
//                new TextureRegion(new Texture("sprites/ui_back.png"), 0, 0, 100, 100)), false);
        return container;
    }

    private Table createTable() {
        menuTable = new Table();
        menuTable.defaults().height(30).width(300).pad(10, 0, 0, 0);
        menuTable.pad(0, 10, 10, 10);
        menuTable.setBackground(new TextureRegionDrawable(
                new TextureRegion(new Texture("sprites/ui_back.png"), 0, 0, 100, 100)));
        createWorldButton = new TextButton("C: Create world", game.getSkin());
        createWorldButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.switchWorldGenMenu();
            }
        });
        menuTable.add(createWorldButton).row();

        if (worldExist()) {
            startGameButton = new TextButton("E: Start game", game.getSkin());
            startGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.switchWorldsSelectMenu();
                }
            });
            menuTable.add(startGameButton).row();

            loadGameButton = new TextButton("L: Load game", game.getSkin());
            menuTable.add(loadGameButton).row();
        }

        aboutButton = new TextButton("A: About", game.getSkin());
        menuTable.add(aboutButton).row();

        quitButton = new TextButton("Q: Quit", game.getSkin());
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        menuTable.add(quitButton);
        return menuTable;
    }

    private InputListener createKeyListener() {
        return new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.E: {
                        if (startGameButton != null) startGameButton.toggle();
                        return true;
                    }
                    case Input.Keys.Q: {
                        quitButton.toggle();
                        return true;
                    }
                    case Input.Keys.L: {
                        loadGameButton.toggle();
                        return true;
                    }
                    case Input.Keys.C: {
                        createWorldButton.toggle();
                        return true;
                    }
                    case Input.Keys.A: {
                        aboutButton.toggle();
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public void resize(int width, int height) {
        reset();
        Gdx.input.setInputProcessor(stage);
    }

    public void reset() {
        if (stage != null) stage.dispose();
        init();
    }

    private boolean worldExist() {
        File file = new File("saves");
        return file.exists() && file.listFiles() != null;
    }
}
