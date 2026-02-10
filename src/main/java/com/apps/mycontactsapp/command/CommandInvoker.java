package com.apps.mycontactsapp.command;

import java.util.Stack;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Invoker class for managing commands and Undo/Redo operations.
 */
public class CommandInvoker {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    public void executeCommand(Command command) throws ValidationException {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack on new operation
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        } else {
            System.out.println("Nothing to undo.");
        }
    }

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
