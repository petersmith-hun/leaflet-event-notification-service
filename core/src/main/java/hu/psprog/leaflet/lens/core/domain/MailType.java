package hu.psprog.leaflet.lens.core.domain;

/**
 * Available mail types.
 *
 * @author Peter Smith
 */
public enum MailType {

    /**
     * Notification sent to an author/admin when a new comment is posted for an article.
     */
    COMMENT_NOTIFICATION,

    /**
     * Notification sent to the admin when a contact request is submitted.
     */
    CONTACT_REQUEST,

    /**
     * Notification sent to a user when they request password reset.
     */
    PW_RESET_REQUEST,

    /**
     * Notification sent to a user when a previously requested password reset is successfully completed.
     */
    PW_RESET_CONFIRMATION,

    /**
     * Notification sent to a user when they successfully sign up.
     */
    SIGNUP_CONFIRMATION,

    /**
     * Notification sent to the admin when a system component successfully started up.
     */
    SYSTEM_STARTUP
}
