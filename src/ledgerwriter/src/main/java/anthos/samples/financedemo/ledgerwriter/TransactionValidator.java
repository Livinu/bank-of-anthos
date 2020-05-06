/*
 * Copyright 2020, Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package anthos.samples.financedemo.ledgerwriter;

import org.springframework.stereotype.Component;

/**
 * Validator to authenticate transaction.
 *
 * Functions to validate transaction details before adding to the ledger.
 */
@Component
public class TransactionValidator {

    public static final String
            EXCEPTION_MESSAGE_INVALID_NUMBER = "invalid account details";
    public static final String
            EXCEPTION_MESSAGE_NOT_AUTHENTICATED = "sender not authenticated";
    public static final String
            EXCEPTION_MESSAGE_SEND_TO_SELF = "can't send to self";
    public static final String
            EXCEPTION_MESSAGE_INVALID_AMOUNT = "invalid amount";


    /**
     *   - Ensure sender is the same user authenticated by auth token
     *   - Ensure account and routing numbers are in the correct format
     *   - Ensure sender and receiver are different accounts
     *   - Ensure amount is positive
     *
     * @param authedAccount  the currently authenticated user account
     * @param transaction    the transaction object
     * @param bearerToken    the token used to authenticate request
     *
     * @throws IllegalArgumentException  on validation error
     */
    public void validateTransaction(String localRoutingNum, String authedAcct,
                                     Transaction transaction)
            throws IllegalArgumentException {
        final String fromAcct = transaction.getFromAccountNum();
        final String fromRoute = transaction.getFromRoutingNum();
        final String toAcct = transaction.getToAccountNum();
        final String toRoute = transaction.getToRoutingNum();
        final Integer amount = transaction.getAmount();

        // Validate account and routing numbers.
        if (!LedgerWriterController.ACCT_REGEX.matcher(fromAcct).matches()
                || !LedgerWriterController.ACCT_REGEX.matcher(toAcct).matches()
                || !LedgerWriterController.ROUTE_REGEX.matcher(
                        fromRoute).matches()
                || !LedgerWriterController.ROUTE_REGEX.matcher(
                        toRoute).matches()) {
            throw new IllegalArgumentException(
                    EXCEPTION_MESSAGE_INVALID_NUMBER);
        }
        // If this is an internal transaction,
        // ensure it originated from the authenticated user.
        if (fromRoute.equals(localRoutingNum) && !fromAcct.equals(authedAcct)) {
            throw new IllegalArgumentException(
                    EXCEPTION_MESSAGE_NOT_AUTHENTICATED);
        }
        // Ensure sender isn't receiver.
        if (fromAcct.equals(toAcct) && fromRoute.equals(toRoute)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_SEND_TO_SELF);
        }
        // Ensure amount is valid value.
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    EXCEPTION_MESSAGE_INVALID_AMOUNT);
        }
    }
}
