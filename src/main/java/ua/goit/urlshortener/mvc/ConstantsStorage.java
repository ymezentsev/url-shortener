package ua.goit.urlshortener.mvc;

public final class ConstantsStorage {
    private ConstantsStorage() {
        throw new IllegalStateException("Utility class");
    }

    public static final String MODEL_INDEX = "index";
    public static final String MODEL_EDIT = "edit";
    public static final String MODEL_CREATE = "create";
    public static final String MODEL_REGISTER = "register";
    public static final String MODEL_LOGIN = "login";
    public static final String MODEL_SUCCESS = "success";
    public static final String MODEL_ALL_GUEST = "all-guest";
    public static final String MODEL_ALL_USER = "all-user";
    public static final String MODEL_ADMIN_URLS = "admin-urls";
    public static final String MODEL_ADMIN_USERS = "admin-users";
    public static final String ATTRIBUTE_USERNAME = "username";
    public static final String ATTRIBUTE_USERS = "users";
    public static final String ATTRIBUTE_USER_URLS = "userUrls";
    public static final String ATTRIBUTE_USER_URLS_INACTIVE = "userUrlsInactive";
    public static final String ATTRIBUTE_URLS = "urls";
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_FROM_ADMIN_PAGE = "fromAdminPage";
    public static final String ATTRIBUTE_ERRORS = "errors";
}
