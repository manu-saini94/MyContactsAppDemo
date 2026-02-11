package com.apps.mycontactsapp.command;

import com.apps.mycontactsapp.exceptions.ValidationException;

public interface Command {
    /**
     * Executes the command logic.
     * 
     * @throws ValidationException if execution fails.
     */
    void execute() throws ValidationException;

    /**
     * Undoes the command logic, reverting the state.
     */
    void undo();
}
