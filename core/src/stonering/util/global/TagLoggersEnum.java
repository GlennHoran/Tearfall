package stonering.util.global;


import java.util.List;

/**
 * Enumeration of application-wide loggers which are destinquished by tags and could be enabled or disabled.
 *
 * @author Alexander Kuzyakov
 */
public enum TagLoggersEnum {
    TASKS("task"),
    UI("ui"),
    PATH("path"),
    LOADING("loading"),
    GENERAL("general"),
    BUILDING("building"),
    ITEMS("items");

    private static TaggedLogger logger = new TaggedLogger();
    private String tag;
    private String tagWord;
    private boolean enabled;

    /**
     * Updates statuses of loggers. Only loggers specified in tag list will be enabled after this.
     *
     * @param tags list of tags to enable.
     */
    public static void enabletags(List<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            for (TagLoggersEnum logger : TagLoggersEnum.values()) {
                logger.setEnabled(tags.contains(logger.getTag()));
            }
        }
    }

    /**
     * Enables all loggers.
     */
    public static void enableAll() {
        for (TagLoggersEnum logger : TagLoggersEnum.values()) {
            logger.setEnabled(true);
        }
    }

    TagLoggersEnum(String tag) {
        this.tag = tag;
        tagWord = "[" + tag.toUpperCase() + "]";
        enabled = false;
    }

    public String getTag() {
        return tag;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Method of a logger. If logger is enabled, logs given message prefixed with tag of given logger.
     *
     * @param message
     */
    public void log(String message) {
        if (enabled) logger.log(tagWord, message);
    }

    /**
     * Method of a logger. If logger is enabled, logs given message prefixed with 'debug' word and tag of given logger.
     *
     * @param message
     */
    public void logDebug(String message) {
        if (enabled) logger.debug(tagWord, message);
    }

    public void logWarn(String message) {
        if (enabled) logger.debug(tagWord, message);
    }

    public void logError(String message) {
        if (enabled) logger.error(tagWord, message);
    }
}