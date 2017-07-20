package stonering.menu.ui_components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.lwjgl.openal.AL;

/**
 * Created by Alexander on 31.05.2017.
 */
public class LabeledProgressBar extends Table {
    private Label label;
    private ProgressBar progressBar;
    private Table table;


    public LabeledProgressBar(Skin skin) {
        super(skin);
        label = new Label("", skin);
        progressBar= new ProgressBar(0,100,1,false,skin);
        this.align(Align.left);
        this.add(label);
        this.row();
        this.add(progressBar);
    }

    public LabeledProgressBar(String text,Skin skin) {
        this(skin);
        label.setText(text);
    }

    public void setText(String text) {
        label.setText(text);
    }

    public  void setValue(int value) {
        progressBar.setValue(value);
    }
}