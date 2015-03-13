package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ist.fenixframework.Atomic;
import pt.ulisboa.tecnico.bubbledocs.exceptions.BubbledocsException;

// add needed import declarations

public abstract class BubbledocsService {

    @Atomic
    public final void execute() throws BubbledocsException {
        dispatch();
    }

    protected abstract void dispatch() throws BubbledocsException;
}
