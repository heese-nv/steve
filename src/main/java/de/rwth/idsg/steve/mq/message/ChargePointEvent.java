package de.rwth.idsg.steve.mq.message;

/**
 * A message triggered by the charge point.
 *
 * @author ralf.heese
 */
public interface ChargePointEvent {

    /**
     * @return message ID provided by the charge point
     */
    String getMessageId();

    /**
     * Set the message ID  provided by the charge point.
     *
     * @param messageId
     *         message ID
     */
    void setMessageId(String messageId);
}
