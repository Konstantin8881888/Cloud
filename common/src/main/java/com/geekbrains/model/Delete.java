package com.geekbrains.model;

import lombok.Getter;

@Getter
public class Delete implements CloudMessage
{
    private final String fileName;

    public Delete(String fileName)
    {
        this.fileName = fileName;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.DELETE ;
    }
}
