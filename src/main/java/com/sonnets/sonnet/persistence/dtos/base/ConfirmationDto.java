package com.sonnets.sonnet.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Josh Harkem
 */
public class ConfirmationDto {
    @NotNull
    private Long itemId;
    @NotEmpty
    private String itemType;
    @NotNull
    private boolean isConfirmed;
    private String message;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ConfirmationDto{" +
                "itemId=" + itemId +
                ", itemType='" + itemType + '\'' +
                ", isConfirmed=" + isConfirmed +
                ", message='" + message + '\'' +
                '}';
    }
}
