package com.apps.mycontactsapp.command;

import java.util.Stack;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Invoker class for managing commands and Undo/Redo operations.
 */
public class CommandInvoker {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * Executes a command and pushes it to the undo stack.
     * Clears the redo stack.
     * 
     * @param command the command to execute.
     * @throws ValidationException if execution fails.
     */
    public void executeCommand(Command command) throws ValidationException {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack on new operation
    }

    /**
     * Undoes the last executed command.
     * Pushes the command to the redo stack.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    /**
     * Redoes the last undone command.
     * 
     * @throws ValidationException if execution fails.
     */
    public void redo() throws ValidationException {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        } else {
            System.out.println("Nothing to redo.");
        }
    }
}
