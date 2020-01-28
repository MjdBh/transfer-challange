package com.revolut.challenge.repository;

import com.revolut.challenge.jooq.Tables;
import com.revolut.challenge.jooq.tables.records.TransferRecord;
import com.revolut.challenge.model.Transfer;
import com.revolut.challenge.model.enumeration.TransferStatusType;
import org.jooq.DSLContext;

import java.util.Optional;

public class TransferRepository {

    private final DSLContext dataContext;

    public TransferRepository(DSLContext dataContext) {
        this.dataContext = dataContext;
    }

    public Optional<Transfer> getById(Long id) {
        return dataContext.selectFrom(Tables.TRANSFER)
                .where(Tables.TRANSFER.ID.eq(id))
                .fetchOptionalInto(Transfer.class);
    }

    public TransferRecord save(Transfer transfer) {
        return dataContext.insertInto(Tables.TRANSFER)
                .set(Tables.TRANSFER.ID, transfer.getId())
                .set(Tables.TRANSFER.FROM_ACCOUNT, transfer.getFromAccount())
                .set(Tables.TRANSFER.TO_ACCOUNT, transfer.getToAccount())
                .set(Tables.TRANSFER.CREATE_DATETIME, transfer.getCreateDatetime())
                .set(Tables.TRANSFER.TRANSFER_STATUS_TYPE, transfer.getTransferStatusType())
                .returning(Tables.TRANSFER.ID).fetchOne();
    }

    public int updateState(Long id, TransferStatusType transferStatusType) {
        return dataContext.update(Tables.TRANSFER)
                .set(Tables.TRANSFER.TRANSFER_STATUS_TYPE, transferStatusType)
                .where(Tables.TRANSFER.ID.eq(id))
                .execute();
    }

    public Optional<Transfer> getTransferByAccounts(long fromAccount, long toAccount, TransferStatusType transferStatusType) {
        return dataContext.selectFrom(Tables.TRANSFER)
                .where(Tables.TRANSFER.FROM_ACCOUNT.eq(fromAccount))
                .and(Tables.TRANSFER.TO_ACCOUNT.eq(toAccount))
                .and(Tables.TRANSFER.TRANSFER_STATUS_TYPE.eq(transferStatusType))
                .fetchOptionalInto(Transfer.class);
    }
}
