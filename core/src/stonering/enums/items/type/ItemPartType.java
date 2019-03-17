package stonering.enums.items.type;

import stonering.entity.local.crafting.CommonComponent;

/**
 * Specifies crafting info for one itemPart.
 *
 * @author Alexander on 27.10.2018.
 */
public class ItemPartType extends CommonComponent {
    private String title;
    private boolean required = true; // item parts are required by default

    //TODO allowed materials and volumes.

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}