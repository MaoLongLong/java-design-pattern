package com.csl.command;

/**
 * @author MaoLongLong
 * @date 2021-03-15 14:29:52
 */
public class ConcreteCommandTwo implements Command {

    private final Receiver receiver;

    public ConcreteCommandTwo(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.actionTwo();
    }
}
