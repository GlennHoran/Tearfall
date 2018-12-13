package stonering.menu.new_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import stonering.TearFall;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import stonering.entity.world.World;
import stonering.menu.ui_components.MiniMap;
import stonering.menu.ui_components.WorldListItem;
import stonering.menu.utils.WorldSaver;

import java.io.File;

/**
 * Screen for selecting world file for game start.
 *
 * @author Alexander Kuzyakov on 14.04.2017.
 */
public class SelectWorldScreen implements Screen {
    private World world;
    private TearFall game;
    private Stage stage;
    private List<WorldListItem> worldList;
    private MiniMap minimap;

    public SelectWorldScreen(TearFall game) {
        this.game = game;
    }

    public void init() {
        stage.setDebugAll(true);
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.defaults().fill().expandY().space(10);
        rootTable.pad(10).align(Align.bottomLeft);
        rootTable.add(createMenuTable());
        rootTable.add(createMinimap()).expandX();
        stage.addActor(rootTable);
    }

    public Array<WorldListItem> getWorldListItems() {
        File root = new File("saves");
        Array<WorldListItem> list = new Array<>();
        if (root.exists()) {
            for (File file : root.listFiles()) {
                if(file.listFiles().length > 0) {
                    list.add(new WorldListItem(file.getName(), file));
                }
            }
        }
        return list;
    }

    public void checkInput() {

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage = new Stage();
        init();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT | Gdx.gl20.GL_DEPTH_BUFFER_BIT);
        checkInput();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.dispose();
        stage = new Stage();
        init();
        Gdx.input.setInputProcessor(stage);
    }

    private Table createMenuTable() {
        Table menuTable = new Table();
        menuTable.defaults().prefHeight(30).prefWidth(300).padBottom(10).minWidth(300);
        menuTable.align(Align.bottomLeft);

        menuTable.add(new Label("Select world:", game.getSkin()));
        menuTable.row();

        menuTable.add(createWorldList());
        menuTable.row();

        menuTable.add().expandY();
        menuTable.row();
        if(worldList.getItems().size > 0) {
            TextButton proceedButton = new TextButton("Proceed", game.getSkin());
            proceedButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.switchLocationSelectMenu(getWorld());
                }
            });
            menuTable.add(proceedButton);
            menuTable.row();
        }

        TextButton backButton = new TextButton("Back", game.getSkin());
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.switchMainMenu();
            }
        });
        menuTable.add(backButton).colspan(2).pad(0);

        return menuTable;
    }

    private Table createMinimap() {
        minimap = new MiniMap(new Texture("sprites/map_tiles.png"));
        WorldListItem item = worldList.getSelected();
        if (item != null) {
            World world = new WorldSaver().loadWorld(item.getTitle());
            setWorld(world);
            minimap.setWorld(world);
        }
        return minimap;
    }

    private List<WorldListItem> createWorldList() {
        worldList = new List<>(game.getSkin());
        worldList.setItems(getWorldListItems());
        worldList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                World world = new WorldSaver().loadWorld(((List<WorldListItem>) actor).getSelected().getTitle());
                setWorld(world);
                minimap.setWorld(world);
            }
        });
        if (worldList.getItems().size > 0) {
            worldList.setSelected(worldList.getItems().get(0));
        }
        return worldList;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

    public Stage getStage() {
        return stage;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}