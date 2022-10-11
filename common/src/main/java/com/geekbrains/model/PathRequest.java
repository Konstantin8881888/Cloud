package com.geekbrains.model;

import lombok.Getter;

@Getter
public class PathRequest implements CloudMessage
{

    private final String pathName;

    public PathRequest(String pathName)
    {
        this.pathName = pathName;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.PATH_REQUEST;
    }
}
