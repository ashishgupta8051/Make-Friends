package com.social.makefriends.model;

public class ChatMessages {
    private String MessageDetails;
    private String MessageTime;
    private String MessageDate;
    private String MessageType;
    private String SenderId;
    private String ReceiverId;
    private String MessageId;
    private String MessageSeenDetails;
    private String FileName;
    private String FileType;
    private String SenderSideMsgDelete;
    private String ReceiverSideMsgDelete;
    private String Forward;

    public ChatMessages() {
    }

    public ChatMessages(String messageDetails, String messageTime, String messageDate, String messageType, String senderId, String receiverId,
                        String messageId, String messageSeenDetails, String fileName,String fileType, String senderSideMsgDelete, String receiverSideMsgDelete,
                        String forward) {
        MessageDetails = messageDetails;
        MessageTime = messageTime;
        MessageDate = messageDate;
        MessageType = messageType;
        SenderId = senderId;
        ReceiverId = receiverId;
        MessageId = messageId;
        MessageSeenDetails = messageSeenDetails;
        FileName = fileName;
        FileType = fileType;
        SenderSideMsgDelete = senderSideMsgDelete;
        ReceiverSideMsgDelete = receiverSideMsgDelete;
        Forward = forward;
    }

    public String getMessageDetails() {
        return MessageDetails;
    }

    public void setMessageDetails(String messageDetails) {
        MessageDetails = messageDetails;
    }

    public String getMessageTime() {
        return MessageTime;
    }

    public void setMessageTime(String messageTime) {
        MessageTime = messageTime;
    }

    public String getMessageDate() {
        return MessageDate;
    }

    public void setMessageDate(String messageDate) {
        MessageDate = messageDate;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getMessageSeenDetails() { return MessageSeenDetails; }

    public void setMessageSeenDetails(String messageSeenDetails) { MessageSeenDetails = messageSeenDetails; }

    public String getFileName() { return FileName; }

    public void setFileName(String fileName) { FileName = fileName; }

    public String getFileType() { return FileType; }

    public void setFileType(String fileType) { FileType = fileType; }

    public String getSenderSideMsgDelete() {
        return SenderSideMsgDelete;
    }

    public void setSenderSideMsgDelete(String senderSideMsgDelete) {
        SenderSideMsgDelete = senderSideMsgDelete;
    }

    public String getReceiverSideMsgDelete() {
        return ReceiverSideMsgDelete;
    }

    public void setReceiverSideMsgDelete(String receiverSideMsgDelete) {
        ReceiverSideMsgDelete = receiverSideMsgDelete;
    }

    public String getForward() {
        return Forward;
    }

    public void setForward(String forward) {
        Forward = forward;
    }
}
