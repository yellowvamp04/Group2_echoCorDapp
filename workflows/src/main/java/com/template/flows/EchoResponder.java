package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;

//import java.lang.*;

// ******************
// * Responder flow *
// ******************
@InitiatedBy(EchoInitiator.class)
public class EchoResponder extends FlowLogic<String> {
    private FlowSession counterPartySession;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";

    public EchoResponder(FlowSession counterPartySession) {
        this.counterPartySession = counterPartySession;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        /* get the inbound message */
        String inboundMsg = counterPartySession.receive(String.class).unwrap(s -> s);

        /* reverse the inbound message */
        StringBuilder reversedMsg = new StringBuilder();
        reversedMsg.append(inboundMsg);
        reversedMsg = reversedMsg.reverse();

        /* send back the result and show on console */
        counterPartySession.send(reversedMsg.toString());
        System.out.println("Message received from counter party: " + ANSI_GREEN + counterPartySession.getCounterparty().getName() + ANSI_RESET);
        System.out.println("Message received: " + ANSI_YELLOW + inboundMsg + ANSI_RESET);
        System.out.println("Reversed Message: " + reversedMsg.toString());
        return reversedMsg.toString();
    }
}
