package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.IdentityService;
import net.corda.core.utilities.ProgressTracker;

import java.util.Set;

import java.lang.*;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class EchoInitiator extends FlowLogic<String> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    private final String message;
    private final String counterParty;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public EchoInitiator(String message, String counterParty) {
        this.message = message;
        this.counterParty = counterParty;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        /* ServiceHub: Get hold of the services that the Corda container provides */
        ServiceHub serviceHub = getServiceHub();

        /* IdentityService: Get the IdentityService object that has information about parties & their identities */
        IdentityService identityService = serviceHub.getIdentityService();

        /*  Identity service returns the Party set using the method partiesFromName. Using the boolean flag gives an exact match */
        Set<Party> partySet = identityService.partiesFromName(this.counterParty, true);

        /* if party is not found, we provide below message instead */
        if (partySet.isEmpty()) { return ("Sorry, no Party " + counterParty + " is found"); }

        /* if party is found, we get the value and proceed with the next steps */
        Party receiver = partySet.iterator().next();
        FlowSession otherPartySession = initiateFlow(receiver);
        otherPartySession.send(message);
        System.out.println("Message sent to the counter party: " + ANSI_GREEN + receiver.getName() + ANSI_RESET);
        String outboundMsg = otherPartySession.receive(String.class).unwrap(s -> s);
        return outboundMsg;
    }
}
