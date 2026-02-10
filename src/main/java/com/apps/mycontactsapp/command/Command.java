package com.apps.mycontactsapp.command;

import com.apps.mycontactsapp.exceptions.ValidationException;

public interface Command {
    void execute() throws ValidationException;

    void undo();
}
