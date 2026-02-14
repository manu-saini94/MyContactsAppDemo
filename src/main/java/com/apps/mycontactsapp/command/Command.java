package com.apps.mycontactsapp.command;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Interface for all commands in the application.
 * Supports the Command Pattern.
 */
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
