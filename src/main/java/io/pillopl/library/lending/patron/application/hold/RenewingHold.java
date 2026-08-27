package io.pillopl.library.lending.patron.application.hold;

import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.commons.commands.Result;
import io.pillopl.library.lending.book.model.BookOnHold;
import io.pillopl.library.lending.patron.model.PatronId;
import io.pillopl.library.lending.patron.model.Patrons;
import io.pillopl.library.lending.patron.model.PatronEvent.BookHoldExtended;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

import static io.pillopl.library.commons.commands.Result.Success;

@AllArgsConstructor
public class RenewingHold {

    private final FindBookOnHold findBookOnHold;
    private final Patrons patronRepository;

    public Try<Result> renew(@NonNull RenewHoldCommand command) {
        return Try.of(() -> {
            BookOnHold hold = findBookOnHold
                    .findBookOnHold(command.getBookId(), command.getPatronId())
                    .getOrElseThrow(() -> new IllegalArgumentException("Cannot find book on hold with Id: " + command.getBookId().getBookId()));
            if (!hold.by(command.getPatronId()) || !hold.getHoldPlacedAt().equals(command.getLibraryBranchId())) {
                throw new IllegalArgumentException("The hold belongs to another patron or library branch");
            }
            if (hold.getHoldTill() == null ||
                    !command.getNewHoldTill().isAfter(command.getTimestamp()) ||
                    !command.getNewHoldTill().isAfter(hold.getHoldTill())) {
                throw new IllegalArgumentException("A hold can only be extended to a later time");
            }
            patronRepository.publish(new BookHoldExtended(
                    command.getTimestamp(),
                    command.getPatronId().getPatronId(),
                    command.getBookId().getBookId(),
                    command.getLibraryBranchId().getLibraryBranchId(),
                    command.getNewHoldTill()));
            return Success;
        });
    }
}
