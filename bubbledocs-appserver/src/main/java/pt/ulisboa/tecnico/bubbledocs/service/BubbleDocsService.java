package pt.ulisboa.tecnico.bubbledocs.service;

import pt.ist.fenixframework.Atomic;

// add needed import declarations

public abstract class BubbleDocsService {

    @Atomic
    public final void execute() throws BubbleDocsException {
        dispatch();
    }

    protected abstract void dispatch() throws BubbleDocsException;
}
